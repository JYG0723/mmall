package com.mmall.controller.portal;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.service.IProductService;
import com.mmall.vo.ProductDetailVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author: Ji YongGuang.
 * @date: 23:02 2017/12/12.
 */
@Controller
@RequestMapping(value = "/product")
public class ProductController {

    @Autowired
    private IProductService iProductService;

    /**
     * 查看商品详情
     * @param productId 商品id
     * @return
     */
    @RequestMapping(value = "detail.do")
    @ResponseBody
    public ServerResponse<ProductDetailVO> detail(Integer productId) {
        return iProductService.getProductDetail(productId);
    }

    /**
     * 查询商品
     *
     * @param keyword    关键字
     * @param categoryId 分类id
     * @param pageNum    当前页数
     * @param pageSize   页面容量
     * @param orderBy    排序方式
     * @return ProductListVO
     */
    @RequestMapping(value = "list.do")
    @ResponseBody
    public ServerResponse<PageInfo> list(@RequestParam(value = "keyword", required = false) String keyword,
                                         @RequestParam(value = "categoryId", required = false) Integer categoryId,
                                         @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                         @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                         @RequestParam(value = "orderBy", defaultValue = "") String orderBy) {
        return iProductService.getProductByKeyWordCategory(keyword, categoryId, pageNum, pageSize, orderBy);
    }
}
