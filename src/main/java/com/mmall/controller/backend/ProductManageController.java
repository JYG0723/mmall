package com.mmall.controller.backend;

import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCodeEnum;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.pojo.User;
import com.mmall.service.IFileService;
import com.mmall.service.IProductService;
import com.mmall.service.IUserService;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.ProductDetailVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
     * @param httpSession session
     * @param product     商品实体
     * @return 高复用响应对象
     */
    @RequestMapping(value = "save.do")
    @ResponseBody
    public ServerResponse productSave(HttpSession httpSession, Product product) {
        User user = (User) httpSession.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCodeEnum.NEED_LOGIN.getCode(), "用户未登录，请登录管理员");
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
     * @param httpSession session
     * @param productId   产品id
     * @param status      要修改的状态
     * @return 高复用响应对象
     */
    @RequestMapping(value = "set_sale_status.do")
    @ResponseBody
    public ServerResponse setSaleStatus(HttpSession httpSession, Integer productId, Integer status) {
        User user = (User) httpSession.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCodeEnum.NEED_LOGIN.getCode(), "用户未登录，请登录管理员");
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
     * @param httpSession session
     * @param productId   产品id
     * @return T为ProductDetailVo的高复用对象
     */
    @RequestMapping(value = "detail.do")
    @ResponseBody
    public ServerResponse<ProductDetailVO> getDetail(HttpSession httpSession, Integer productId) {
        User user = (User) httpSession.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCodeEnum.NEED_LOGIN.getCode(), "用户未登录，请登录管理员");
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
     * @param httpSession session
     * @return PageHelper封装好的携带分页结果的PageInfo对象
     */
    @RequestMapping(value = "list.do")
    @ResponseBody
    public ServerResponse<PageInfo> getList(HttpSession httpSession,
                                            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        User user = (User) httpSession.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCodeEnum.NEED_LOGIN.getCode(), "用户未登录，请登录管理员");
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
     * @param httpSession session
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
            return ServerResponse.createByErrorCodeMessage(ResponseCodeEnum.NEED_LOGIN.getCode(), "用户未登录，请登录管理员");
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
     * @param httpSession   session
     * @param multipartFile 上传的文件
     * @return 上产文件在服务器的uri以及url
     */
    @RequestMapping(value = "upload.do")
    @ResponseBody
    public ServerResponse<Map<String, String>> upload(HttpSession httpSession, @RequestParam(value = "upload_file",
            required = false) MultipartFile multipartFile, HttpServletRequest httpServletRequest) {
        User user = (User) httpSession.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCodeEnum.NEED_LOGIN.getCode(),
                    "用户未登录，请登录管理员");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            // 这个路径上传完之后会创建到 发布的webapp目录下，和WEB-INF同级
            // 创建文件夹这种操作不应该依赖于业务(手动创建)。应该通过代码来创建
            // getRealPath 返回str所指文件的绝对路径。只要工程中有该文件，就会检索到
            String path = httpServletRequest.getSession().getServletContext().getRealPath("upload");

            System.out.println("文件上传到的文件夹:" + path);
            // targetFileName 服务器上真实存在的文件名
            String targetFileName = iFileService.upload(multipartFile, path);
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFileName;
            System.out.println("文件的外链:" + url);

            Map<String, String> fileMap = Maps.newHashMap();
            fileMap.put("uri", targetFileName);
            fileMap.put("url", url);
            return ServerResponse.createBySuccess(fileMap);
        } else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }

    /**
     * 富文本图片的上传
     *
     * @param httpSession        session
     * @param multipartFile      上传的文件
     * @param httpServletRequest request
     * @return simditor规定的map接口
     */
    @RequestMapping(value = "richtext_img_upload.do")
    @ResponseBody
    public Map richtextImgUpload(HttpSession httpSession, @RequestParam(value = "upload_file", required = false)
            MultipartFile multipartFile, HttpServletRequest httpServletRequest,
                                 HttpServletResponse httpServletResponse) {
        Map resultMap = Maps.newHashMap();
        User user = (User) httpSession.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            resultMap.put("success", false);
            resultMap.put("msg", "用户未登录，请登录管理员");
            return resultMap;
        }
        // 富文本中对于返回值有自己的要求，我们使用的是simditor所以按照simditor要求的返回。
        // 参考 http://simditor.tower.im/docs/doc-config.html
        /*{
            "success": true/false,
                "msg": "error message", # optional
            "file_path": "[real file path]"
        }*/
        if (iUserService.checkAdminRole(user).isSuccess()) {
            // 这个路径上传完之后会创建到 发布的webapp目录下，和WEB-INF同级
            // 创建文件夹这种操作不应该依赖于业务(手动创建)。应该通过代码来创建
            String path = httpServletRequest.getServletContext().getRealPath("upload");
            String targetFileName = iFileService.upload(multipartFile, path);
            if (StringUtils.isBlank(targetFileName)) {
                resultMap.put("success", false);
                resultMap.put("msg", "上传失败");
                return resultMap;
            }
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFileName;
            resultMap.put("success", true);
            resultMap.put("msg", "上传成功");
            resultMap.put("file_path", url);
            // simditor要求后端上传成功之后返回的head值
            httpServletResponse.addHeader("Access-Control-Allow-Headers", "X-File-Name");
            return resultMap;
        } else {
            resultMap.put("success", false);
            resultMap.put("msg", "无权限操作");
            return resultMap;
        }
    }


}
