package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.vo.CartVO;

/**
 * @author: Ji YongGuang.
 * @date: 22:53 2017/12/13.
 */
public interface ICartService {

    ServerResponse<CartVO> add(Integer userId, Integer productId, Integer count);

    ServerResponse<CartVO> update(Integer userId, Integer productId, Integer count);

    ServerResponse<CartVO> deleteProduct(Integer userId, String productIds);

    ServerResponse<CartVO> list(Integer userId);

    ServerResponse<CartVO> selectOrUnSelect(Integer userId, Integer productId, Integer checked);

    ServerResponse<Integer> getCartProductCount(Integer userId);
}
