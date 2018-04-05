package com.eyun.product.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.eyun.product.service.ProductSkuService;
import com.eyun.product.web.rest.errors.BadRequestAlertException;
import com.eyun.product.web.rest.util.HeaderUtil;
import com.eyun.product.web.rest.util.PaginationUtil;
import com.eyun.product.service.dto.ProductSkuDTO;
import com.eyun.product.service.dto.ProductSkuCriteria;
import com.eyun.product.service.ProductSkuQueryService;
import io.github.jhipster.web.util.ResponseUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST controller for managing ProductSku.
 */
@Api("商品sku")
@RestController
@RequestMapping("/api")
public class ProductSkuResource {

    private final Logger log = LoggerFactory.getLogger(ProductSkuResource.class);

    private static final String ENTITY_NAME = "productSku";

    private final ProductSkuService productSkuService;

    private final ProductSkuQueryService productSkuQueryService;

    public ProductSkuResource(ProductSkuService productSkuService, ProductSkuQueryService productSkuQueryService) {
        this.productSkuService = productSkuService;
        this.productSkuQueryService = productSkuQueryService;
    }

    /**
     * POST  /product-skus : Create a new productSku.
     *
     * @param productSkuDTO the productSkuDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new productSkuDTO, or with status 400 (Bad Request) if the productSku has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/product-skus")
    @Timed
    public ResponseEntity<ProductSkuDTO> createProductSku(@Valid @RequestBody ProductSkuDTO productSkuDTO) throws URISyntaxException {
        log.debug("REST request to save ProductSku : {}", productSkuDTO);
        if (productSkuDTO.getId() != null) {
            throw new BadRequestAlertException("A new productSku cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ProductSkuDTO result = productSkuService.save(productSkuDTO);
        return ResponseEntity.created(new URI("/api/product-skus/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }
    @ApiOperation("更新库存")
    @PostMapping("/product-skus/stock/{processtype}")
    @Timed
    public ResponseEntity updateProductSkuCount(@Valid @RequestBody ProductSkuDTO productSkuDTO,@PathVariable Integer processtype) throws Exception {
        log.debug("REST request to update ProductSku : {}", productSkuDTO);
        Map result = productSkuService.updateStockCount(productSkuDTO,processtype);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(result));
    }
    /**
     * PUT  /product-skus : Updates an existing productSku.
     *
     * @param productSkuDTO the productSkuDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated productSkuDTO,
     * or with status 400 (Bad Request) if the productSkuDTO is not valid,
     * or with status 500 (Internal Server Error) if the productSkuDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/product-skus")
    @Timed
    public ResponseEntity<ProductSkuDTO> updateProductSku(@Valid @RequestBody ProductSkuDTO productSkuDTO) throws URISyntaxException {
        log.debug("REST request to update ProductSku : {}", productSkuDTO);
        if (productSkuDTO.getId() == null) {
            return createProductSku(productSkuDTO);
        }
        ProductSkuDTO result = productSkuService.save(productSkuDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, productSkuDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /product-skus : get all the productSkus.
     *
     * @param pageable the pagination information
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of productSkus in body
     */
    @GetMapping("/product-skus")
    @Timed
    public ResponseEntity<List<ProductSkuDTO>> getAllProductSkus(ProductSkuCriteria criteria, Pageable pageable) {
        log.debug("REST request to get ProductSkus by criteria: {}", criteria);
        Page<ProductSkuDTO> page = productSkuQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/product-skus");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /product-skus/:id : get the "id" productSku.
     *
     * @param id the id of the productSkuDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the productSkuDTO, or with status 404 (Not Found)
     */
    @GetMapping("/product-skus/{id}")
    @Timed
    public ResponseEntity<ProductSkuDTO> getProductSku(@PathVariable Long id) {
        log.debug("REST request to get ProductSku : {}", id);
        ProductSkuDTO productSkuDTO = productSkuService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(productSkuDTO));
    }

    /**
     * DELETE  /product-skus/:id : delete the "id" productSku.
     *
     * @param id the id of the productSkuDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/product-skus/{id}")
    @Timed
    public ResponseEntity<Void> deleteProductSku(@PathVariable Long id) {
        log.debug("REST request to delete ProductSku : {}", id);
        productSkuService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
