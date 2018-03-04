package com.mmall.service.impl;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCodeEnum;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CartMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Cart;
import com.mmall.pojo.Product;
import com.mmall.service.ICartService;
import com.mmall.util.BigDecimalUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.CartProductVO;
import com.mmall.vo.CartVO;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Ji YongGuang.
 * @date 22:53 2017/12/13.
 * @desc: Service层需要注意 public方法应该声明到private方法前
 */
@Service(value = "iCarService")
public class CartServiceImpl implements ICartService {

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;

    @Override
    public ServerResponse<CartVO> add(Integer userId, Integer productId, Integer count) {
        if (productId == null || count == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCodeEnum.ILLEGAL_ARGUEMENT.getCode(),
                    ResponseCodeEnum.ILLEGAL_ARGUEMENT.getDesc());
        }

        Cart cart = cartMapper.selectCartByUserIdProductId(userId, productId);
        if (cart == null) {// 购物车中还没有加入该商品
            Product product = productMapper.selectByPrimaryKey(productId);
            if (product != null && product.getStatus().intValue() == 1) {
                Cart cartItem = new Cart();
                cartItem.setUserId(userId);
                cartItem.setProductId(productId);
                // 这里商品第一次添加到购物车的时候可能出现Quantity数量大于Product的实际Stock的。所以展示CartVO的时候需要进行判断
                cartItem.setQuantity(count);
                // 新加入购物车的商品已经默认是选中状态









                cartItem.setChecked(Const.Cart.CHECKED);
                cartMapper.insert(cartItem);
            } else {// 如果该商品不存在
                return ServerResponse.createByErrorCodeMessage(ResponseCodeEnum.ILLEGAL_ARGUEMENT.getCode(),
                        "当前商品不存在或已下架");
            }
        } else {// 购物车中已存在该商品，叠加
            count = cart.getQuantity() + count;
            cart.setQuantity(count);
            cartMapper.updateByPrimaryKeySelective(cart);
        }
        return this.list(userId);
    }

    @Override
    public ServerResponse<CartVO> update(Integer userId, Integer productId, Integer count) {
        if (productId == null || count == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCodeEnum.ILLEGAL_ARGUEMENT.getCode(),
                    ResponseCodeEnum.ILLEGAL_ARGUEMENT.getDesc());
        }
        Cart cart = (Cart) cartMapper.selectCartByUserIdProductId(userId, productId);
        if (cart != null) {
            cart.setQuantity(count);
        }
        // 更新购物车字段
        cartMapper.updateByPrimaryKey(cart);
        return this.list(userId);
    }

    @Override
    public ServerResponse<CartVO> deleteProduct(Integer userId, String productIds) {
        // 看一下productIds 如果为空会怎么样，是否是空指针异常。如果是的话，什么情况下会出现isEmpty的情况
        List<String> productList = Splitter.on(",").splitToList(productIds);
        if (CollectionUtils.isEmpty(productList)) {
            return ServerResponse.createByErrorCodeMessage(ResponseCodeEnum.ILLEGAL_ARGUEMENT.getCode(),
                    ResponseCodeEnum.ILLEGAL_ARGUEMENT.getDesc());
        }
        cartMapper.deleteCartByUserIdProductIds(userId, productList);
        return this.list(userId);
    }

    @Override
    public ServerResponse<CartVO> list(Integer userId) {
        // 这里就不需要做分页了
        CartVO cartVO = this.getCartVOLimit(userId);
        return ServerResponse.createBySuccess(cartVO);
    }

    @Override
    public ServerResponse<CartVO> selectOrUnSelect(Integer userId, Integer productId, Integer checked) {
        cartMapper.checkedOrUncheckedProduct(userId, productId, checked);
        return this.list(userId);
    }

    @Override
    public ServerResponse<Integer> getCartProductCount(Integer userId) {
        if (userId == null) {
            return ServerResponse.createBySuccess(0);
        }
        // count 购物车商品总数量
        int count = cartMapper.getCartProductCount(userId);
        return ServerResponse.createBySuccess(count);
    }

    /**
     * 返回对应用户的 CartVO 购物车对象
     * 核心方法。供各种方法调用
     * 购物车本身封装成一个ViewObject -> CartVO，购物车里的加入的每个商品对象实体也要封装成一个ViewObject -> CartProductVO
     *
     * @param userId
     * @return
     */
    private CartVO getCartVOLimit(Integer userId) {
        CartVO cartVO = new CartVO();
        List<Cart> cartList = cartMapper.selectCartByUserId(userId);
        List<CartProductVO> cartProductVOList = Lists.newArrayList();

        // 购物车总价
        BigDecimal cartTotalPrice = new BigDecimal("0");
        if (CollectionUtils.isNotEmpty(cartList)) {
            for (Cart cartItem : cartList) {// 遍历购物车中商品
                // 包装CartProductVO的 购物车属性
                CartProductVO cartProductVO = new CartProductVO();
                cartProductVO.setId(cartItem.getId());
                cartProductVO.setUserId(cartItem.getUserId());
                cartProductVO.setProductId(cartItem.getProductId());

                // 包装CartProductVO的 商品属性
                Product product = productMapper.selectByPrimaryKey(cartItem.getProductId());
                if (product != null) {// 判断 购物车中该商品是否存在
                    cartProductVO.setProductMainImage(product.getMainImage());
                    cartProductVO.setProductName(product.getName());
                    cartProductVO.setProductSubtitle(product.getSubtitle());
                    cartProductVO.setProductStatus(product.getStatus());
                    cartProductVO.setProductPrice(product.getPrice());
                    cartProductVO.setProductStock(product.getStock());

                    int buyLimitCount = 0;// 限制购买数量
                    if (product.getStock() >= cartItem.getQuantity()) {// 判断库存
                        // 库存充足的时候
                        buyLimitCount = cartItem.getQuantity();
                        cartProductVO.setLimitQuantity(Const.Cart.LIMIT_COUNT_SUCCESS);// 该字段表示限定数量内购买
                    } else {// 库存不足的时候
                        buyLimitCount = product.getStock();
                        cartProductVO.setLimitQuantity(Const.Cart.LIMIT_COUNT_FAIL);// 非限定数量内购买
                        // 购物车中更新有效库存 -> 防止直接访问接口，跳过前台数量限制，往数据库中插入了非法数据
                        // 所以向前台展示的时候需要判断数据合法性。同时要更新掉数据库中的非法数据
                        Cart cartForQuantity = new Cart();
                        cartForQuantity.setId(cartItem.getId());
                        cartForQuantity.setQuantity(buyLimitCount);
                        cartMapper.updateByPrimaryKeySelective(cartForQuantity);
                    }
                    cartProductVO.setQuantity(buyLimitCount);

                    // 计算该商品的总价
                    cartProductVO.setProductTotalPrice(BigDecimalUtil.mul(
                            cartProductVO.getProductPrice().doubleValue(),
                            cartProductVO.getQuantity().doubleValue()
                    ));
                    cartProductVO.setProductChecked(cartItem.getChecked());// 是否选中
                }

                if (cartItem.getChecked() == Const.Cart.CHECKED) {
                    //如果已经勾选,增加到整个的购物车总价中
                    cartTotalPrice = BigDecimalUtil.add(
                            cartTotalPrice.doubleValue(),
                            cartProductVO.getProductTotalPrice().doubleValue()
                    );
                }
                // 每填充完一个cartProductVO就录入cartProductVOList
                cartProductVOList.add(cartProductVO);
            }
        }
        cartVO.setCartProductVoList(cartProductVOList);
        cartVO.setCartTotalPrice(cartTotalPrice);
        cartVO.setAllChecked(this.getAllCheckedStatus(userId));
        cartVO.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        return cartVO;
    }

    /**
     * 根据userId查看该用户购物车中商品是否全选中
     *
     * @param userId
     * @return
     */
    private boolean getAllCheckedStatus(Integer userId) {
        if (userId == null) {
            return false;
        }
        return cartMapper.selectCartProductCheckedStatusByUserId(userId) == 0; // =0 代表没有未勾选的，即全勾选了
    }

}
