package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ResponseCodeEnum;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.ICartService;
import com.mmall.vo.CartVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * @author: Ji YongGuang.
 * @date: 22:49 2017/12/13.
 */
@Controller
@RequestMapping(value = "/cart/")
public class CartController {

    @Autowired
    private ICartService iCarService;

    @RequestMapping(value = "list.do")
    @ResponseBody
    public ServerResponse<CartVO> list(HttpSession httpSession) {
        User user = (User) httpSession.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCodeEnum.NEED_LOGIN.getCode(), ResponseCodeEnum
                    .NEED_LOGIN.getDesc());
        }
        return iCarService.list(user.getId());
    }

    @RequestMapping(value = "add.do")
    @ResponseBody
    public ServerResponse<CartVO> add(HttpSession httpSession, Integer userId, Integer productId, Integer count) {
        User user = (User) httpSession.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCodeEnum.NEED_LOGIN.getCode(), ResponseCodeEnum
                    .NEED_LOGIN.getDesc());
        }
        return iCarService.add(user.getId(), productId, count);
    }

    @RequestMapping(value = "update.do")
    @ResponseBody
    public ServerResponse<CartVO> update(HttpSession httpSession, Integer userId, Integer productId, Integer count) {
        User user = (User) httpSession.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCodeEnum.NEED_LOGIN.getCode(), ResponseCodeEnum
                    .NEED_LOGIN.getDesc());
        }
        return iCarService.update(user.getId(), productId, count);
    }

    @RequestMapping(value = "delete_product.do")
    @ResponseBody
    public ServerResponse<CartVO> deleteProduct(HttpSession httpSession, String productIds) {// 传过来是 12,13 逗号分隔
        User user = (User) httpSession.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCodeEnum.NEED_LOGIN.getCode(), ResponseCodeEnum
                    .NEED_LOGIN.getDesc());
        }
        return iCarService.deleteProduct(user.getId(), productIds);
    }

    @RequestMapping(value = "select_all.do")
    @ResponseBody
    public ServerResponse<CartVO> selectAll(HttpSession httpSession) {
        User user = (User) httpSession.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCodeEnum.NEED_LOGIN.getCode(), ResponseCodeEnum
                    .NEED_LOGIN.getDesc());
        }
        return iCarService.selectOrUnSelect(user.getId(), null, Const.Cart.CHECKED);
    }

    @RequestMapping(value = "un_select_all.do")
    @ResponseBody
    public ServerResponse<CartVO> unSelectAll(HttpSession httpSession) {
        User user = (User) httpSession.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCodeEnum.NEED_LOGIN.getCode(), ResponseCodeEnum
                    .NEED_LOGIN.getDesc());
        }
        return iCarService.selectOrUnSelect(user.getId(), null, Const.Cart.UN_CHECKED);
    }

    @RequestMapping(value = "select.do")
    @ResponseBody
    public ServerResponse<CartVO> select(HttpSession httpSession, Integer productId) {
        User user = (User) httpSession.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCodeEnum.NEED_LOGIN.getCode(), ResponseCodeEnum
                    .NEED_LOGIN.getDesc());
        }
        return iCarService.selectOrUnSelect(user.getId(), productId, Const.Cart.CHECKED);
    }

    @RequestMapping(value = "un_select.do")
    @ResponseBody
    public ServerResponse<CartVO> unSelect(HttpSession httpSession, Integer productId) {
        User user = (User) httpSession.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCodeEnum.NEED_LOGIN.getCode(), ResponseCodeEnum
                    .NEED_LOGIN.getDesc());
        }
        return iCarService.selectOrUnSelect(user.getId(), productId, Const.Cart.UN_CHECKED);
    }

    @RequestMapping(value = "get_cart_product_count.do")
    @ResponseBody
    public ServerResponse<Integer> getCartProductCount(HttpSession httpSession) {
        User user = (User) httpSession.getAttribute(Const.CURRENT_USER);
        if (user == null) {// 如果当前用户未登录，购物车数量显示 0
            return ServerResponse.createBySuccess(0);
        }
        return iCarService.getCartProductCount(user.getId());
    }

}
