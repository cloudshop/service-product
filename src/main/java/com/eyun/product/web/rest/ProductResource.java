package com.eyun.product.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.eyun.product.domain.Product;
import com.eyun.product.service.FeignMercurieClient;
import com.eyun.product.service.ProductService;
import com.eyun.product.service.dto.ProductContentDTO;
import com.eyun.product.service.dto.ProductSeachParam;
import com.eyun.product.web.rest.errors.BadRequestAlertException;
import com.eyun.product.web.rest.util.HeaderUtil;
import com.eyun.product.web.rest.util.PaginationUtil;
import com.eyun.product.service.dto.ProductDTO;
import com.eyun.product.service.dto.ProductCriteria;
import com.eyun.product.service.ProductQueryService;
import io.github.jhipster.web.util.ResponseUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST controller for managing Product.
 */
@Api("商品服务")
@RestController
@RequestMapping("/api")
public class ProductResource {

    private final Logger log = LoggerFactory.getLogger(ProductResource.class);

    private static final String ENTITY_NAME = "product";

    private final ProductService productService;

    private final ProductQueryService productQueryService;

    @Autowired
    FeignMercurieClient feignMercurieClient;

    public ProductResource(ProductService productService, ProductQueryService productQueryService) {
        this.productService = productService;
        this.productQueryService = productQueryService;
    }

    /**
     * POST  /products : Create a new product.
     *
     * @param productDTO the productDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new productDTO, or with status 400 (Bad Request) if the product has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/products")
    @Timed
    public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody ProductDTO productDTO) throws URISyntaxException {
        log.debug("REST request to save Product : {}", productDTO);
        ProductDTO result = productService.save(productDTO);
        return ResponseEntity.created(new URI("/api/products/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    @ApiOperation("商品发布")
    @PostMapping("/product/publish")
    @Timed
    public ResponseEntity pulishProduct(@Valid @RequestBody ProductContentDTO productContentDTO) throws Exception {
        log.debug("REST request to publish Product : {}", productContentDTO);
        try{
            Map mercury = feignMercurieClient.findUserMercuryId();
            productContentDTO.setShopId(Long.valueOf(mercury.get("id").toString()));
        }catch (Exception e){
            e.printStackTrace();
            throw new BadRequestAlertException("获取当前登陆用户店铺失败", "mercuryId", "mercuryIdNotfound");
        }
        List<Map> result = productService.publishProductAndSku(productContentDTO);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(result));
    }

    @ApiOperation("上传sku图片")
    @PostMapping("/product/skuImage")
    @Timed
    public ResponseEntity upLoadskuImage(@Valid @RequestBody List<Map> skuImage) throws Exception {
        String result = productService.upLoadskuImage(skuImage);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(result));
    }


    @ApiOperation("sku列表(商家中心后台)")
    @PostMapping("/product/skuStore")
    @Timed
    public ResponseEntity skuListStore(@RequestBody ProductSeachParam productSeachParam) throws Exception {
        try{
            Map mercury = feignMercurieClient.findUserMercuryId();
            productSeachParam.setShopId(Long.valueOf(mercury.get("id").toString()));
        }catch (Exception e){
            e.printStackTrace();
            throw new BadRequestAlertException("获取当前登陆用户店铺失败", "mercuryId", "mercuryIdNotfound");
        }
        Map result = productService.skuListStore(productSeachParam);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(result));
    }

    /**
     * PUT  /products : Updates an existing product.
     *
     * @param productDTO the productDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated productDTO,
     * or with status 400 (Bad Request) if the productDTO is not valid,
     * or with status 500 (Internal Server Error) if the productDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/products")
    @Timed
    public ResponseEntity<ProductDTO> updateProduct(@Valid @RequestBody ProductDTO productDTO) throws URISyntaxException {
        log.debug("REST request to update Product : {}", productDTO);
        if (productDTO.getId() == null) {
            return createProduct(productDTO);
        }
        ProductDTO result = productService.save(productDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, productDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /products : get all the products.
     *
     * @param pageable the pagination information
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of products in body
     */
    @GetMapping("/products")
    @Timed
    public ResponseEntity<List<ProductDTO>> getAllProducts(ProductCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Products by criteria: {}", criteria);
        Page<ProductDTO> page = productQueryService.findByCriteria(criteria, pageable);

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/products");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /products/:id : get the "id" product.
     *
     * @param id the id of the productDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the productDTO, or with status 404 (Not Found)
     */
    @GetMapping("/products/{id}")
    @Timed
    public ResponseEntity<ProductDTO> getProduct(@PathVariable Long id) {
        log.debug("REST request to get Product : {}", id);
        ProductDTO productDTO = productService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(productDTO));
    }

    @ApiOperation("商品信息")
    @GetMapping("/product/content")
    @Timed
    public ResponseEntity findProductByProductId(@RequestParam("id") Long id) {
        Map result = productService.findProductById(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(result));
    }

    @ApiOperation("商品列表")
    @PostMapping("/product/all")
    @Timed
    public ResponseEntity findProductByCatewgoryId(@RequestBody ProductSeachParam productSeachParam) {
        Map result = productService.findProductByCatewgory(productSeachParam);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(result));
    }

    @ApiOperation("首页,分类商品搜索")
    @PostMapping("/product/search")
    @Timed
    public ResponseEntity findProductByParam(@RequestBody ProductSeachParam productSeachParam) {
        List<Map> resultList = productService.findProductByParam(productSeachParam);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(resultList));
    }

    @ApiOperation("批量获取商品")
    @PostMapping("/product/follow")
    @Timed
    public ResponseEntity findProductsByIds(@NotNull @RequestBody List<Long> ids) {
        List<Map> resultList = productService.findProductByIds(ids);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(resultList));
    }

    @ApiOperation("获取店铺商品")
    @GetMapping("/product/shop")
    @Timed
    public ResponseEntity getAllProductsShop(@RequestParam("shopId") Long shopId) {
        List<Map> list = productService.findProductByShopIdAndDeleted(shopId);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(list));
    }

    @ApiOperation("生成商品sku")
    @PostMapping("/product/initsku")
    @Timed
    public ResponseEntity initSku(@NotNull @RequestBody List<Map> productAttr) throws Exception {
        List list = productService.initSku(productAttr);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(list));
    }

    /**
     * DELETE  /products/:id : delete the "id" product.
     *
     * @param id the id of the productDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/products/{id}")
    @Timed
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        log.debug("REST request to delete Product : {}", id);
        productService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
