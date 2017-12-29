package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.mmall.common.ServerResponse;
import com.mmall.dao.ShippingMapper;
import com.mmall.pojo.Shipping;
import com.mmall.service.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author: Ji YongGuang.
 * @date: 18:13 2017/12/15.
 */
@Service(value = "iShippingService")
public class ShippingServiceImpl implements IShippingService {

    @Autowired
    private ShippingMapper shippingMapper;

    @Override
    public ServerResponse<Map<String, Integer>> add(Integer userId, Shipping shipping) {
        shipping.setUserId(userId);
        // Mapper配置文件中的sql方法中自动填充了shipping对象的id属性
        int count = shippingMapper.insert(shipping);
        if (count > 0) {
            Map<String, Integer> result = Maps.newHashMap();
            result.put("shippingId", shipping.getId());
            return ServerResponse.createBySuccess("添加地址成功", result);
        }
        return ServerResponse.createByErrorMessage("添加地址失败");
    }

    @Override
    public ServerResponse<String> del(Integer userId, Integer shippingId) {
        int count = shippingMapper.deleteByShippingIduserId(userId, shippingId);
        if (count > 0) {
            return ServerResponse.createBySuccess("删除地址成功");
        }
        return ServerResponse.createByErrorMessage("删除地址失败");
    }

    @Override
    public ServerResponse update(Integer userId, Shipping shipping) {
        // 防止横向越权，也就是从当前登录用户里面进行更改，有DML操作的时候需要将用户id带上
        shipping.setUserId(userId);
        int count = shippingMapper.updateByShipping(shipping);
        if (count > 0) {
            return ServerResponse.createBySuccessMessage("更新地址成功");
        }
        return ServerResponse.createByErrorMessage("更新地址失败");
    }

    @Override
    public ServerResponse<Shipping> select(Integer userId, Integer shippingId) {
        Shipping shipping = shippingMapper.selectByShippingIdUserId(userId, shippingId);
        if (shipping == null) {
            return ServerResponse.createByErrorMessage("无法查询到该地址信息");
        }
        return ServerResponse.createBySuccess("查询地址信息成功", shipping);
    }

    @Override
    public ServerResponse<PageInfo> list(Integer userId, int pageNum, int PageSize) {
        PageHelper.startPage(pageNum, PageSize);
        List<Shipping> shippingList = shippingMapper.selectByUserId(userId);
        /*if (shippingList == null) {
            return ServerResponse.createByErrorMessage("该用户并没有地址信息");
        }*/
        PageInfo pageInfo = new PageInfo(shippingList);
        return ServerResponse.createBySuccess(pageInfo);
    }

}
