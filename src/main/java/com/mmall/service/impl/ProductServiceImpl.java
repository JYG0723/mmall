package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCodeEnum;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Category;
import com.mmall.pojo.Product;
import com.mmall.service.ICategoryService;
import com.mmall.service.IProductService;
import com.mmall.util.DateTimeUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.ProductDetailVO;
import com.mmall.vo.ProductListVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : JiYongGuang
 * @date : 19:14 2017/11/26.
 */
@Service("iProductService")
public class ProductServiceImpl implements IProductService {

    private static final String DEFAULT_IMAGE_HOST = "http://img.happymmall.com/";

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ICategoryService iCategoryService;

    @Override
    public ServerResponse saveOrUpdateProduct(Product product) {
        if (product != null) {
            // 无论更新还是新增操作这一步都必须执行
            if (StringUtils.isNotBlank(product.getSubImages())) {
                String[] subImageArray = product.getSubImages().split(",");
                // 子图数大于1才需要判断
                if (subImageArray.length > 0) {
                    product.setMainImage(subImageArray[0]);
                }
            }

            // 如果id不为空 说明该商品已然存在在数据库中 那么必然是更新操作
            if (product.getId() != null) {
                int rowCount = productMapper.updateByPrimaryKey(product);
                if (rowCount > 0) {
                    return ServerResponse.createBySuccessMessage("更新产品成功");
                }
            } else {
                int rowCount = productMapper.insert(product);
                if (rowCount > 0) {
                    return ServerResponse.createByErrorMessage("新增产品成功");
                }
                return ServerResponse.createByErrorMessage("新增产品失败");
            }
        }
        return ServerResponse.createByErrorMessage("新增或更新产品参数不正确");
    }

    @Override
    public ServerResponse<String> setSaleStatus(Integer productId, Integer status) {
        if (productId == null || status == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCodeEnum.ILLEGAL_ARGUEMENT.getCode(),
                    ResponseCodeEnum.NEED_LOGIN.getDesc());
        }
        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);
        int rowCount = productMapper.updateByPrimaryKeySelective(product);
        if (rowCount > 0) {
            return ServerResponse.createBySuccess("修改产品销售状态成功");
        }
        return ServerResponse.createByErrorMessage("修改产品销售状态失败");
    }

    @Override
    public ServerResponse<ProductDetailVO> manageProductDetail(Integer productId) {
        if (productId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCodeEnum.ILLEGAL_ARGUEMENT.getCode(),
                    ResponseCodeEnum.ILLEGAL_ARGUEMENT.getDesc());
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null) {
            return ServerResponse.createByErrorMessage("产品已下架或者删除");
        }
        // ProductDetailVO 这种Vo类都是 向页面传递 所需要用到的相应的对象的信息来用的。
        // 比如页面需要比Product属性更详细的信息，就会封装一个ProductDetailVo对象。里面不仅包含所需要用到的Product
        // 对象的信息，还会包含其他需要的信息。名字就是需求的名字，比如你需要ProductDetail，你需要ProductList。
        ProductDetailVO productDetailVo = assembleProductDeatilVO(product);
        return ServerResponse.createBySuccess(productDetailVo);
    }

    private ProductDetailVO assembleProductDeatilVO(Product product) {
        ProductDetailVO productDetailVo = new ProductDetailVO();
        productDetailVo.setId(product.getId());
        productDetailVo.setSubtitle(product.getSubtitle());
        productDetailVo.setPrice(product.getPrice());
        // 数据库中只存的图片的uri，不会存url。防止图片服务器更换，图片还可以正常显示，所以对图片服务器地址和图片uri
        // 进行拼接。因为如果图片服务器更改之后，不可能对数据库中现存的image数据进行大范围更改
        productDetailVo.setMainImage(product.getMainImage());
        productDetailVo.setSubImages(product.getSubImages());
        productDetailVo.setCategoryId(product.getCategoryId());
        productDetailVo.setDetail(product.getDetail());
        productDetailVo.setName(product.getName());
        productDetailVo.setStatus(product.getStatus());
        productDetailVo.setStock(product.getStock());
        // 从配置文件中获取ImageHost属性，因为该属性会被很多地方引用。代码和配置隔离，防止图片服务器变更
        productDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix",
                DEFAULT_IMAGE_HOST));
        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        // 如果当前产品的分类的id在数据库category表中没有的话，那么说明插入数据的时候没有选择分类。默认为根节点0下的产品
        if (category == null) {
            // 默认该品类的父结点是根节点
            productDetailVo.setParentCategoryId(0);
        } else {
            productDetailVo.setParentCategoryId(category.getParentId());
        }
        // createTime通过mybatis在db中拿出来的是一个毫秒数。所以需要处理成标准格式
        // 入库的数据，需要规范的指定其属性的类型。比如Product类的时间就必须是Date。因为时间是mybatis的now()函数生成的，
        // 就必须用Date类型来对应。向页面回显的时候就需要将日期类型的数据转化为字符串类型的比如productDetailVo的时间就是
        // String类型的。因为传递向页面的数据都为字符串可以看成是都没有类型的
        productDetailVo.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
        productDetailVo.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));
        return productDetailVo;
    }

    @Override
    public ServerResponse<PageInfo> getProductList(int pageNum, int pageSize) {
        // 官方wiki [https://github.com/pagehelper/Mybatis-PageHelper/blob/master/wikis/zh/HowToUse.md]
        //1.startPage--start  记录一个开始位置
        //2.填充自己的sql查询逻辑 - 此时执行完sql之后的return是分好页的Page对象
        //3.pageHelper-收尾[PageInfo]

        PageHelper.startPage(pageNum, pageSize);
        // List中不需要Product中这么详细的信息。毕竟我们不是需要detail只是一个list
        // 我们在执行sql的时候会被aop进行拦截，然后在原来的sql语句的基础上，添加上分页的语句 limit offset
        List<Product> productList = productMapper.selectList(); // productList 是 Page 类型

        List<ProductListVO> productListVoList = Lists.newArrayList();
        for (Product productItem : productList) {
            ProductListVO productListVo = assembleProductListVO(productItem);
            productListVoList.add(productListVo);
        }
        // 对[分页结果]进行包装,构造方法传入的是经过分页处理的对象.然后对应该List，将其属性剥离下来填充到PageInfo对象中统一返回
        PageInfo pageResult = new PageInfo(productList);
        // 我们需要productList来进行分页，但并不需要返回productList中过多的数据
        // 所以这里我们需要把pageResult这个对象的List属性中存的需要展示的数据进行重置
        pageResult.setList(productListVoList);
        return ServerResponse.createBySuccess(pageResult);
    }

    private ProductListVO assembleProductListVO(Product product) {
        ProductListVO productListVo = new ProductListVO();
        productListVo.setId(product.getId());
        productListVo.setName(product.getName());
        productListVo.setCategoryId(product.getCategoryId());
        productListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix",
                DEFAULT_IMAGE_HOST));
        productListVo.setMainImage(product.getMainImage());
        productListVo.setPrice(product.getPrice());
        productListVo.setSubtitle(product.getSubtitle());
        productListVo.setStatus(product.getStatus());
        return productListVo;
    }

    @Override
    public ServerResponse<PageInfo> searchProduct(String productName, Integer productId, int pageNum, int
            pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        // 如果productName不为空，模糊查询
        if (StringUtils.isNotBlank(productName)) {
            productName = new StringBuilder().append("%").append(productName).append("%").toString();
        }
        List<Product> productList = productMapper.selectByNameAndProductId(productName, productId);
        List<ProductListVO> productListVoList = Lists.newArrayList();
        for (Product p : productList) {
            ProductListVO productListVo = assembleProductListVO(p);
            productListVoList.add(productListVo);
        }
        PageInfo pageInfo = new PageInfo(productList);
        pageInfo.setList(productListVoList);
        return ServerResponse.createBySuccess(pageInfo);
    }

    @Override
    public ServerResponse<ProductDetailVO> getProductDetail(Integer productId) {
        if (productId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCodeEnum.ILLEGAL_ARGUEMENT.getCode(),
                    ResponseCodeEnum.ILLEGAL_ARGUEMENT.getDesc());
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null) {
            return ServerResponse.createByErrorMessage("商品不存在");
        }
        if (product.getStatus() != Const.ProductStatusEnum.ON_SALE.getCode()) {
            return ServerResponse.createByErrorMessage("商品已下架或删除");
        }
        ProductDetailVO productDetailVO = assembleProductDeatilVO(product);
        return ServerResponse.createBySuccess(productDetailVO);
    }

    @Override
    public ServerResponse<PageInfo> getProductByKeyWordCategory(String keyword, Integer categoryId, int pageNum,
                                                                int pageSize, String orderBy) {
        // 如果都为空
        if (StringUtils.isBlank(keyword) && categoryId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCodeEnum.ILLEGAL_ARGUEMENT.getCode(),
                    ResponseCodeEnum.ILLEGAL_ARGUEMENT.getDesc());
        }
        List<Integer> categoryIdlist = new ArrayList<Integer>();
        // 如果categoryId 不为空
        if (categoryId != null) {
            Category category = categoryMapper.selectByPrimaryKey(categoryId);
            // category 不存在且 keyword 没有
            if (category == null && StringUtils.isBlank(keyword)) {
                //没有该分类,并且还没有关键字,这个时候返回一个空的结果集,不报错
                PageHelper.startPage(pageNum, pageSize);
                List<ProductListVO> productListVOList = Lists.newArrayList();
                PageInfo pageInfo = new PageInfo(productListVOList);
                return ServerResponse.createBySuccess(pageInfo);
            }
            // category存在
            if (category != null) {
                // category 存在 - 查询该category下的所有子category包括自己
                categoryIdlist = iCategoryService.seleceCategoryAndChildrenById(category.getId()).getData();
            }
        }
        // 如果 keyword 不为空
        if (StringUtils.isNotBlank(keyword)) {
            keyword = new StringBuilder().append("%").append(keyword).append("%").toString();
        }
        PageHelper.startPage(pageNum, pageSize);
        // 排序处理
        if (StringUtils.isNotBlank(orderBy)) {
            PageHelper.startPage(pageNum, pageSize);
            // 这里Set集合的contains方法是O(1) 而List是O(n) 所以选用set，即便数据少，但是还是以性能为主
            if (Const.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)) {
                String[] orderByArray = orderBy.split("_");
                // (price asc) 的格式
                PageHelper.orderBy(orderByArray[0] + " " + orderByArray[1]);
            }
        }
        List<Product> productList = productMapper.selectByNameAndCategoryIds(StringUtils.isBlank(keyword) ? null :
                keyword, categoryIdlist.size() == 0 ? null : categoryIdlist);

        List<ProductListVO> productListVOList = Lists.newArrayList();
        for (Product p : productList) {
            ProductListVO productListVO = assembleProductListVO(p);
            productListVOList.add(productListVO);
        }

        PageInfo pageInfo = new PageInfo(productList);
        pageInfo.setList(productListVOList);
        return ServerResponse.createBySuccess(pageInfo);
    }
}
