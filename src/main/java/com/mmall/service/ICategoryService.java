package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;

import java.util.List;

/**
 * @author : Ji YongGuang.
 * @date : 13:48 2017/11/18.
 * @desc: 分类业务
 */
public interface ICategoryService {

    /**
     * 添加分类
     *
     * @param categoryName 分类名
     * @param parentId     父分类id
     * @return 高复用响应对象
     */
    ServerResponse addCategory(String categoryName, Integer parentId);

    /**
     * 更新分类名
     *
     * @param categoryId   分类id
     * @param categoryName 新分类名
     * @return 高复用响应对象
     */
    ServerResponse updateCategoryName(Integer categoryId, String categoryName);

    /**
     * 获取当前分类结点的一级平行子节点
     *
     * @param categoryId 分类id
     * @return 高复用响应对象
     */
    ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId);

    /**
     * 递归的获取当前分类节点的所有子节点
     *
     * @param categoryId 分类的id
     * @return 高复用响应对象
     */
    ServerResponse<List<Integer>> seleceCategoryAndChildrenById(Integer categoryId);
}
