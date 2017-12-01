package com.mmall.controller.backend;

import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.pojo.User;
import com.mmall.service.IFileService;
import com.mmall.service.IProductService;
import com.mmall.service.IUserService;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.ProductDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * @author : Ji YongGuang.
 * @date : 19:00 2017/11/26.
 */
@SuppressWarnings("SpringAutowiredFieldsWarningInspection")
@Controller
@RequestMapping("/manage/product")
public class ProductManageController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private IProductService iProductService;

    @Autowired
    private IFileService iFileService;

    /**
     * 新增商品
     *
     * @param httpSession
     * @param product     商品实体
     * @return 高复用响应对象
     */
    @RequestMapping(value = "save.do")
    @ResponseBody
    public ServerResponse productSave(HttpSession httpSession, Product product) {
        User user = (User) httpSession.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录管理员");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iProductService.saveOrUpdateProduct(product);
        } else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }

    /**
     * 修改产品状态
     *
     * @param httpSession
     * @param productId   产品id
     * @param status      要修改的状态
     * @return
     */
    @RequestMapping(value = "set_sale_status.do")
    @ResponseBody
    public ServerResponse setSaleStatus(HttpSession httpSession, Integer productId, Integer status) {
        User user = (User) httpSession.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录管理员");
        }
        // 如果是继续操作，如果不是跳出
        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iProductService.setSaleStatus(productId, status);
        } else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }

    /**
     * 产品详情
     *
     * @param httpSession
     * @param productId   产品id
     * @return T为ProductDetailVo的高复用对象
     */
    @RequestMapping(value = "detail.do")
    @ResponseBody
    public ServerResponse<ProductDetailVo> getDetail(HttpSession httpSession, Integer productId) {
        User user = (User) httpSession.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录管理员");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iProductService.manageProductDetail(productId);
        } else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }

    /**
     * 产品列表
     * 用到了分页功能，引入了github上分页功能的开源技术包。
     * 里面通过aop的方式对查询的sql语句进行了修改
     *
     * @param httpSession
     * @return PageHelper封装好的携带分页结果的PageInfo对象
     */
    @RequestMapping(value = "list.do")
    @ResponseBody
    public ServerResponse<PageInfo> getList(HttpSession httpSession,
                                            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        User user = (User) httpSession.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录管理员");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            // limit 几个 offset 从第几个开始
            return iProductService.getProductList(pageNum, pageSize);
        } else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }

    /**
     * 查找商品
     *
     * @param httpSession
     * @param productName 商品的名称
     * @param productId   商品的id
     * @param pageNum     第几页
     * @param pageSize    页面展示数量
     * @return PageHelper封装好的携带商品实体的PageInfo对象
     */
    @RequestMapping(value = "search.do")
    @ResponseBody
    public ServerResponse<PageInfo> productSearch(HttpSession httpSession, String productName, Integer productId,
                                                  @RequestParam(value = "pageNmn", defaultValue = "1") Integer pageNum,
                                                  @RequestParam(value = "pageSize", defaultValue = "10") Integer
                                                          pageSize) {
        User user = (User) httpSession.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录管理员");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            // 根据商品的名称或者id查询商品
            return iProductService.searchProduct(productName, productId, pageNum, pageSize);
        } else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }

    /**
     * 文件上传
     * <p/>
     * 该功能需要配合dispatcher-servlet.xml文件中配置的multipartResolver<bean>使用
     *
     * @param httpSession
     * @param multipartFile 上传的文件
     * @param httpServletRequest
     * @return
     */
    @RequestMapping(value = "upload.do")
    @ResponseBody
    public ServerResponse<Map> upload(HttpSession httpSession, MultipartFile multipartFile, HttpServletRequest
            httpServletRequest) {
        // 这个路径上传完之后会创建到 发布的webapp目录下，和WEB-INF同级
        // 创建文件夹这种操作不应该依赖于业务。应该通过代码来创建
        String path = httpServletRequest.getServletContext().getRealPath("upload");
        String targetFileName = iFileService.upload(multipartFile, path);
        String url = PropertiesUtil.getProperty("ftp.server.http.prefix" + targetFileName);

        Map fileMap = Maps.newHashMap();
        fileMap.put("uri", targetFileName);
        fileMap.put("url", url);

        // 打印出来是啥
        System.out.println("上传文件返回的目标文件名:", targetFileName);
        System.out.println("上传的路径", path);
        return ServerResponse.createBySuccess(fileMap);
    }
}
