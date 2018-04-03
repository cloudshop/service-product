package com.eyun.product.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.eyun.product.service.ProductImgService;
import com.eyun.product.web.rest.errors.BadRequestAlertException;
import com.eyun.product.web.rest.util.HeaderUtil;
import com.eyun.product.web.rest.util.PaginationUtil;
import com.eyun.product.service.dto.ProductImgDTO;
import com.eyun.product.service.dto.ProductImgCriteria;
import com.eyun.product.service.ProductImgQueryService;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing ProductImg.
 */
@RestController
@RequestMapping("/api")
public class ProductImgResource {

    private final Logger log = LoggerFactory.getLogger(ProductImgResource.class);

    private static final String ENTITY_NAME = "productImg";

    private final ProductImgService productImgService;

    private final ProductImgQueryService productImgQueryService;

    public ProductImgResource(ProductImgService productImgService, ProductImgQueryService productImgQueryService) {
        this.productImgService = productImgService;
        this.productImgQueryService = productImgQueryService;
    }

    /**
     * POST  /product-imgs : Create a new productImg.
     *
     * @param productImgDTO the productImgDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new productImgDTO, or with status 400 (Bad Request) if the productImg has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/product-imgs")
    @Timed
    public ResponseEntity<ProductImgDTO> createProductImg(@RequestBody ProductImgDTO productImgDTO) throws URISyntaxException {
        log.debug("REST request to save ProductImg : {}", productImgDTO);
        if (productImgDTO.getId() != null) {
            throw new BadRequestAlertException("A new productImg cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ProductImgDTO result = productImgService.save(productImgDTO);
        return ResponseEntity.created(new URI("/api/product-imgs/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /product-imgs : Updates an existing productImg.
     *
     * @param productImgDTO the productImgDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated productImgDTO,
     * or with status 400 (Bad Request) if the productImgDTO is not valid,
     * or with status 500 (Internal Server Error) if the productImgDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/product-imgs")
    @Timed
    public ResponseEntity<ProductImgDTO> updateProductImg(@RequestBody ProductImgDTO productImgDTO) throws URISyntaxException {
        log.debug("REST request to update ProductImg : {}", productImgDTO);
        if (productImgDTO.getId() == null) {
            return createProductImg(productImgDTO);
        }
        ProductImgDTO result = productImgService.save(productImgDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, productImgDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /product-imgs : get all the productImgs.
     *
     * @param pageable the pagination information
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of productImgs in body
     */
    @GetMapping("/product-imgs")
    @Timed
    public ResponseEntity<List<ProductImgDTO>> getAllProductImgs(ProductImgCriteria criteria, Pageable pageable) {
        log.debug("REST request to get ProductImgs by criteria: {}", criteria);
        Page<ProductImgDTO> page = productImgQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/product-imgs");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /product-imgs/:id : get the "id" productImg.
     *
     * @param id the id of the productImgDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the productImgDTO, or with status 404 (Not Found)
     */
    @GetMapping("/product-imgs/{id}")
    @Timed
    public ResponseEntity<ProductImgDTO> getProductImg(@PathVariable Long id) {
        log.debug("REST request to get ProductImg : {}", id);
        ProductImgDTO productImgDTO = productImgService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(productImgDTO));
    }

    /**
     * DELETE  /product-imgs/:id : delete the "id" productImg.
     *
     * @param id the id of the productImgDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/product-imgs/{id}")
    @Timed
    public ResponseEntity<Void> deleteProductImg(@PathVariable Long id) {
        log.debug("REST request to delete ProductImg : {}", id);
        productImgService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
