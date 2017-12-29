package com.mmall.vo;

import com.mmall.pojo.OrderItem;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Ji YongGuang.
 * @date 18:04 2017/12/27.
 */
public class OrderProductVO {

    private List<OrderItemVO> orderItemVOList;

    private BigDecimal totalPrice;

    private String imageHost;

    public List<OrderItemVO> getOrderItemVOList() {
        return orderItemVOList;
    }

    public void setOrderItemVOList(List<OrderItemVO> orderItemVOList) {
        this.orderItemVOList = orderItemVOList;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getImageHost() {
        return imageHost;
    }

    public void setImageHost(String imageHost) {
        this.imageHost = imageHost;
    }
}
