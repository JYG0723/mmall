package com.mmall.controller.portal;

import com.github.pagehelper.PageInfo;
import com.mmall.common.Const;
import com.mmall.common.ResponseCodeEnum;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Shipping;
import com.mmall.pojo.User;
import com.mmall.service.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * @author: Ji YongGuang.
 * @date: 18:12 2017/12/15.
 */
@Controller
@RequestMapping(value = "/shipping/")
public class ShippingController {

    @Autowired
    private IShippingService iShippingService;

    @RequestMapping(value = "add.do")
    @ResponseBody
    public ServerResponse add(HttpSession httpSession, Shipping shipping) {
        User user = (User) httpSession.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCodeEnum.NEED_LOGIN.getCode(), ResponseCodeEnum
                    .NEED_LOGIN.getDesc());
        }
        return iShippingService.add(user.getId(), shipping);
    }

    @RequestMapping(value = "del.do")
    @ResponseBody
    public ServerResponse del(HttpSession httpSession, Integer shippingId) {
        User user = (User) httpSession.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCodeEnum.NEED_LOGIN.getCode(), ResponseCodeEnum
                    .NEED_LOGIN.getDesc());
        }
        return iShippingService.del(user.getId(), shippingId);
    }

    @RequestMapping(value = "update.do")
    @ResponseBody
    public ServerResponse update(HttpSession httpSession, Shipping shipping) {
        User user = (User) httpSession.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCodeEnum.NEED_LOGIN.getCode(), ResponseCodeEnum
                    .NEED_LOGIN.getDesc());
        }
        return iShippingService.update(user.getId(), shipping);
    }

    @RequestMapping(value = "select.do")
    @ResponseBody
    public ServerResponse select(HttpSession httpSession, Integer shippingId) {
        User user = (User) httpSession.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCodeEnum.NEED_LOGIN.getCode(), ResponseCodeEnum
                    .NEED_LOGIN.getDesc());
        }
        return iShippingService.select(user.getId(), shippingId);
    }

    @RequestMapping(value = "list.do")
    @ResponseBody
    public ServerResponse<PageInfo> list(@RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                         @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                         HttpSession httpSession) {
        User user = (User) httpSession.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCodeEnum.NEED_LOGIN.getCode(), ResponseCodeEnum
                    .NEED_LOGIN.getDesc());
        }
        return iShippingService.list(user.getId(), pageNum, pageSize);
    }

}
