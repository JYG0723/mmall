package com.mmall.controller.portal;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCodeEnum;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Iterator;
import java.util.Map;

/**
 * @author: Ji YongGuang.
 * @date: 16:59 2017/12/22.
 */
@Controller
@RequestMapping(value = "/order/")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private IOrderService iOrderService;

    @RequestMapping(value = "create.do")
    @ResponseBody
    public ServerResponse create(HttpSession httpSession, Integer shippingId) {
        User user = (User) httpSession.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCodeEnum.NEED_LOGIN.getCode(), ResponseCodeEnum
                    .NEED_LOGIN.getDesc());
        }
        return iOrderService.createOrder(user.getId(), shippingId);
    }

    @RequestMapping(value = "cancel.do")
    @ResponseBody
    public ServerResponse cancel(HttpSession httpSession, Long orderNo) {
        User user = (User) httpSession.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCodeEnum.NEED_LOGIN.getCode(), ResponseCodeEnum
                    .NEED_LOGIN.getDesc());
        }
        return iOrderService.cancel(user.getId(), orderNo);
    }

    @RequestMapping(value = "get_order_cart_product.do")
    @ResponseBody
    public ServerResponse getOrderCartProduct(HttpSession httpSession) {// 右上角购物车数量/以及订单确认页面(主要)
        User user = (User) httpSession.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCodeEnum.NEED_LOGIN.getCode(), ResponseCodeEnum
                    .NEED_LOGIN.getDesc());
        }
        return iOrderService.getOrderCartProduct(user.getId());
    }

    @RequestMapping(value = "detail.do")
    @ResponseBody
    public ServerResponse detail(HttpSession httpSession, Long orderNo) {// 订单填写页面
        User user = (User) httpSession.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCodeEnum.NEED_LOGIN.getCode(), ResponseCodeEnum
                    .NEED_LOGIN.getDesc());
        }
        return iOrderService.getOrderDetail(user.getId(), orderNo);
    }

    @RequestMapping(value = "list.do")
    @ResponseBody
    public ServerResponse list(HttpSession httpSession,
                               @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                               @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        User user = (User) httpSession.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCodeEnum.NEED_LOGIN.getCode(), ResponseCodeEnum
                    .NEED_LOGIN.getDesc());
        }
        return iOrderService.getOrderList(user.getId(), pageNum, pageSize);
    }

    @RequestMapping(value = "pay.do")
    @ResponseBody
    public ServerResponse<Map<String, String>> pay(HttpSession httpSession, Long orderNo, HttpServletRequest request) {
        User user = (User) httpSession.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCodeEnum.NEED_LOGIN.getCode(), ResponseCodeEnum
                    .NEED_LOGIN.getDesc());
        }
        String path = request.getSession().getServletContext().getRealPath("upload");
        return iOrderService.pay(orderNo, user.getId(), path);
    }

    @RequestMapping(value = "alipay_callback.do")
    @ResponseBody
    public Object alipayCallback(HttpServletRequest request) {
        Map<String, String> params = Maps.newHashMap();
        Map requestParameterMap = request.getParameterMap();

        for (Iterator iter = requestParameterMap.keySet().iterator(); iter.hasNext(); ) {
            String parameterName = (String) iter.next();
            String[] parameterValues = (String[]) requestParameterMap.get(parameterName);
            String valueStr = "";
            for (int i = 0; i < parameterValues.length; i++) {
                valueStr = (i == parameterValues.length - 1) ? valueStr + parameterValues[i] : valueStr +
                        parameterValues[i] + ",";
            }
            params.put(parameterName, valueStr);
        }
        logger.info("支付宝回调:sign:{},tradte_status:{},参数:{}", params.get("sign"), params.get("trade_status"), params
                .toString());

        params.remove("sign_type");
        try {// 验签
            boolean alipayRSACheckedV2 = AlipaySignature.rsaCheckV2(params, Configs.getAlipayPublicKey(), "utf-8",
                    Configs
                            .getSignType());

            if (!alipayRSACheckedV2) {
                return ServerResponse.createByErrorMessage("非法请求,验证不通过,再恶意请求我就报警找网警了");
            }
        } catch (AlipayApiException e) {
            logger.error("支付宝回调异常", e);
        }
        // 验证各种数据的正确性
        ServerResponse serverResponse = iOrderService.alipayCallback(params);
        if (serverResponse.isSuccess()) {
            return Const.AlipayClallback.RESPONSE_SUCCESS;
        }
        return Const.AlipayClallback.RESPONSE_FAILED;
    }

    @RequestMapping(value = "query_order_pay_status.do")
    @ResponseBody
    public ServerResponse<Boolean> queryOrderPayStatus(HttpSession httpSession, Long orderNo) {
        User user = (User) httpSession.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCodeEnum.NEED_LOGIN.getCode(), ResponseCodeEnum
                    .NEED_LOGIN.getDesc());
        }
        ServerResponse serverResponse = iOrderService.queryOrderPayStatus(user.getId(), orderNo);
        if (serverResponse.isSuccess()) {
            return ServerResponse.createBySuccess(true);
        }
        return ServerResponse.createBySuccess(false);
    }

}
