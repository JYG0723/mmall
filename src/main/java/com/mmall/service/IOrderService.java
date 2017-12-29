package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.vo.OrderVO;

import java.util.Map;

/**
 * @author: Ji YongGuang.
 * @date: 17:12 2017/12/22.
 */
public interface IOrderService {

    ServerResponse<Map<String, String>> pay(Long orderNo, Integer userId, String path);

    ServerResponse alipayCallback(Map<String, String> params);

    ServerResponse queryOrderPayStatus(Integer userId, Long orderNo);

    ServerResponse createOrder(Integer userId, Integer shippingId);

    ServerResponse cancel(Integer userId, Long orderNo);

    ServerResponse getOrderCartProduct(Integer userId);

    ServerResponse<OrderVO> getOrderDetail(Integer userId, Long orderNo);

    ServerResponse getOrderList(Integer userId, int pageNum, int pageSize);

    // backend

    ServerResponse<PageInfo> manageList(int pageNum, int pageSize);

    ServerResponse<OrderVO> manageDetail(Long orderNo);

    ServerResponse<PageInfo> manageSearch(Long orderNo, int pageNum, int pageSize);

    ServerResponse<String> manageSendGoods(Long orderNo);
}
