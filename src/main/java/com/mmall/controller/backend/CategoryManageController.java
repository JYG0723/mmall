package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.ICategoryService;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * @作者: Ji YongGuang.
 * @修改时间: 13:31 2017/11/18.
 * @功能描述:
 */
@Controller
@RequestMapping("/manage/category")
public class CategoryManageController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private ICategoryService iCategoryService;

    @RequestMapping(
            value = "add_category.do",
            method = RequestMethod.GET
    )
    @ResponseBody
    public ServerResponse<String> addCategory(HttpSession httpSession, String categoryName,
                                              @RequestParam(value = "patternId", defaultValue = "0") int parentId) {
        User currentUser = (User) httpSession.getAttribute(Const.CURRENT_USER);
        if (currentUser == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录,请登录");
        }
        if (iUserService.checkAdmin(currentUser).isSuccess()) {
            //是管理员
            return iCategoryService.addCategory(categoryName, parentId);
        } else {
            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
        }
    }


    @RequestMapping(
            value = "set_category_name.do",
            method = RequestMethod.GET
    )
    @ResponseBody
    public ServerResponse<String> setCategoryName(HttpSession httpSession, Integer categoryId, String categoryName) {
        User currentUser = (User) httpSession.getAttribute(Const.CURRENT_USER);
        if (currentUser == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录,请登录");
        }
        if (iUserService.checkAdmin(currentUser).isSuccess()) {
            return iCategoryService.updateCategoryName(categoryId, categoryName);
        } else {
            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
        }
    }

    @RequestMapping(
            value = "get_category.do",
            method = RequestMethod.GET
    )
    @ResponseBody
    public ServerResponse getChildrenParallelCategory(HttpSession httpSession, @RequestParam(value =
            "categoryId", defaultValue = "0") Integer categoryId) {// 未限定返回状态
        User currentUser = (User) httpSession.getAttribute(Const.CURRENT_USER);
        if (currentUser == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录,请登录");
        }
        if (iUserService.checkAdmin(currentUser).isSuccess()) {
            // 查询子节点的category信息，并且不递归，保持平级
            return iCategoryService.getChildrenParallelCategory(categoryId);
        } else {
            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
        }
    }

    @RequestMapping(value = "get_deep_category.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse getCategoryAndDeepChildrenCategory(HttpSession httpSession, @RequestParam(value =
            "categoryId", defaultValue = "0") Integer categoryId) {
        User currentUser = (User) httpSession.getAttribute(Const.CURRENT_USER);
        if (currentUser == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录,请登录");
        }
        if (iUserService.checkAdmin(currentUser).isSuccess()) {
            // 查询当前结点的id和递归他子节点的id
            return iCategoryService.seleceCategoryAndChildrenById(categoryId);
        }
        return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
    }

}
