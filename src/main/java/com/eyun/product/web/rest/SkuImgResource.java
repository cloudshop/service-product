package com.eyun.product.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.eyun.product.domain.SkuImg;
import com.eyun.product.service.SkuImgService;
import com.eyun.product.web.rest.errors.BadRequestAlertException;
import com.eyun.product.web.rest.util.HeaderUtil;
import com.eyun.product.web.rest.util.PaginationUtil;
import com.eyun.product.service.dto.SkuImgDTO;
import com.eyun.product.service.dto.SkuImgCriteria;
import com.eyun.product.service.SkuImgQueryService;
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

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing SkuImg.
 */
@Api("sku图片服务")
@RestController
@RequestMapping("/api")
public class SkuImgResource {

    private final Logger log = LoggerFactory.getLogger(SkuImgResource.class);

    private static final String ENTITY_NAME = "skuImg";

    private final SkuImgService skuImgService;

    private final SkuImgQueryService skuImgQueryService;

    public SkuImgResource(SkuImgService skuImgService, SkuImgQueryService skuImgQueryService) {
        this.skuImgService = skuImgService;
        this.skuImgQueryService = skuImgQueryService;
    }

    /**
     * POST  /sku-imgs : Create a new skuImg.
     *
     * @param skuImgDTO the skuImgDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new skuImgDTO, or with status 400 (Bad Request) if the skuImg has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/sku-imgs")
    @Timed
    public ResponseEntity<SkuImgDTO> createSkuImg(@RequestBody SkuImgDTO skuImgDTO) throws URISyntaxException {
        log.debug("REST request to save SkuImg : {}", skuImgDTO);
        if (skuImgDTO.getId() != null) {
            throw new BadRequestAlertException("A new skuImg cannot already have an ID", ENTITY_NAME, "idexists");
        }
        SkuImgDTO result = skuImgService.save(skuImgDTO);
        return ResponseEntity.created(new URI("/api/sku-imgs/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /sku-imgs : Updates an existing skuImg.
     *
     * @param skuImgDTO the skuImgDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated skuImgDTO,
     * or with status 400 (Bad Request) if the skuImgDTO is not valid,
     * or with status 500 (Internal Server Error) if the skuImgDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/sku-imgs")
    @Timed
    public ResponseEntity<SkuImgDTO> updateSkuImg(@RequestBody SkuImgDTO skuImgDTO) throws URISyntaxException {
        log.debug("REST request to update SkuImg : {}", skuImgDTO);
        if (skuImgDTO.getId() == null) {
            return createSkuImg(skuImgDTO);
        }
        SkuImgDTO result = skuImgService.save(skuImgDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, skuImgDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /sku-imgs : get all the skuImgs.
     *
     * @param pageable the pagination information
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of skuImgs in body
     */
    @GetMapping("/sku-imgs")
    @Timed
    public ResponseEntity<List<SkuImgDTO>> getAllSkuImgs(SkuImgCriteria criteria, Pageable pageable) {
        log.debug("REST request to get SkuImgs by criteria: {}", criteria);
        Page<SkuImgDTO> page = skuImgQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/sku-imgs");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /sku-imgs/:id : get the "id" skuImg.
     *
     * @param id the id of the skuImgDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the skuImgDTO, or with status 404 (Not Found)
     */
    @GetMapping("/sku-imgs/{id}")
    @Timed
    public ResponseEntity<SkuImgDTO> getSkuImg(@PathVariable Long id) {
        log.debug("REST request to get SkuImg : {}", id);
        SkuImgDTO skuImgDTO = skuImgService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(skuImgDTO));
    }

    @ApiOperation("获取sku图片")
    @GetMapping("/skuimgs/{skuId}")
    @Timed
    public ResponseEntity getSkuImgs(@PathVariable Long skuId) {
        List<SkuImg> result= skuImgService.findSkuImageBySkuId(skuId);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(result));
    }
    /**
     * DELETE  /sku-imgs/:id : delete the "id" skuImg.
     *
     * @param id the id of the skuImgDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/sku-imgs/{id}")
    @Timed
    public ResponseEntity<Void> deleteSkuImg(@PathVariable Long id) {
        log.debug("REST request to delete SkuImg : {}", id);
        skuImgService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
