package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.pojo.Category;
import com.mmall.service.ICategoryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * @作者: Ji YongGuang.
 * @修改时间: 13:48 2017/11/18.
 * @功能描述:
 */
@Service("iCategoryService")
public class CategoryServiceImpl implements ICategoryService {

    private Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public ServerResponse addCategory(String categoryName, Integer parentId) {
        if (parentId == null || StringUtils.isBlank(categoryName)) {
            return ServerResponse.createByErrorMessage("添加品类参数错误");
        }
        Category category = new Category();
        category.setName(categoryName);
        category.setParentId(parentId);
        category.setStatus(true); // 设置这个分类当前是可用的

        int resultCount = categoryMapper.insert(category);
        if (resultCount > 0) {
            return ServerResponse.createBySuccessMessage("添加商品成功");
        }
        return ServerResponse.createByErrorMessage("添加商品失败");
    }

    @Override
    public ServerResponse updateCategoryName(Integer categoryId, String categoryName) {
        if (categoryId == null || StringUtils.isBlank(categoryName)) {
            return ServerResponse.createByErrorMessage("更新品类参数错误");
        }
        Category category = new Category();
        category.setId(categoryId);
        category.setName(categoryName);

        int resultCount = categoryMapper.updateByPrimaryKeySelective(category);
        if (resultCount > 0) {
            return ServerResponse.createBySuccessMessage("更新品类名字成功");
        }
        return ServerResponse.createByErrorMessage("更新品类名字失败");
    }

    @Override
    public ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId) {
        List<Category> categories = categoryMapper.selectCategoryChildrenByParentId(categoryId);
        if (CollectionUtils.isEmpty(categories)) {
            // 该方法不仅判断了categories是否为空，同时判断了categories是否是一个空的集合
            logger.info("未找到当前分类的子分类");
        }
        return ServerResponse.createBySuccess(categories);
    }

    /**
     * 递归查询本节点的id及孩子节点的id
     *
     * @param categoryId 本节点id
     * @return
     *///   Controller层参数校验的重要性。倘若categoryId为空，Service会做许多不必要的操作
    @Override
    public ServerResponse<List<Integer>> seleceCategoryAndChildrenById(Integer categoryId) {
        Set<Category> categorySet = Sets.newHashSet();
        findChildCategory(categorySet, categoryId);

        List<Integer> categoryList = Lists.newArrayList();
        if (categoryId != null) {
            for (Category categoryItem :
                    categorySet) {
                categoryList.add(categoryItem.getId());// id
            }
        }
        return ServerResponse.createBySuccess(categoryList);
    }

    /**
     * 1. 入参很重要，多留意    统一收纳集合，当前需要遍历的结点id
     * 2. 拿到当前查询的结点
     * 3. 添加到Category集合中 再将其儿子结点遍历添加到集合中
     *
     * @param categorySet
     * @param categoryId
     * @return
     */
    // 递归函数，算出子节点.这里直接调用set集合就可以排重
    private Set<Category> findChildCategory(Set<Category> categorySet, Integer categoryId) {
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if (category != null) {
            categorySet.add(category);
        }
        // mybatis对返回集合的处理是如果没有查到的话，不会返回一个null对象。只是一个被初始化过的空的集合size=0
        // 如果是一些不可预知的方法需要做空判断，不然会报空指针异常
        List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);

        // TODO 退出递归的空判断，该结点下是否有孩子结点。由于返回的集合是Mybatis返回的结果。查不出对象也不会为null只是size为0
        // TODO 正常情况下，如果 categoryList 不是Mybatis返回的结果，而是其他情况，那么需要进行空判断。
        for (Category categoryItem : categoryList) {
            // 这里foreach进行了递归跳出的判断。如果 categoryList 为空那么，该foreach就不会进来，最外层foreach跳出的时候该递归方法结束
            findChildCategory(categorySet, categoryItem.getId());
        }
        return categorySet;
    }

}
