package com.eyun.product.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.eyun.product.service.AttrValueService;
import com.eyun.product.web.rest.errors.BadRequestAlertException;
import com.eyun.product.web.rest.util.HeaderUtil;
import com.eyun.product.web.rest.util.PaginationUtil;
import com.eyun.product.service.dto.AttrValueDTO;
import com.eyun.product.service.dto.AttrValueCriteria;
import com.eyun.product.service.AttrValueQueryService;
import io.github.jhipster.web.util.ResponseUtil;
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
import java.util.Optional;

/**
 * REST controller for managing AttrValue.
 */
@RestController
@RequestMapping("/api")
public class AttrValueResource {

    private final Logger log = LoggerFactory.getLogger(AttrValueResource.class);

    private static final String ENTITY_NAME = "attrValue";

    private final AttrValueService attrValueService;

    private final AttrValueQueryService attrValueQueryService;

    public AttrValueResource(AttrValueService attrValueService, AttrValueQueryService attrValueQueryService) {
        this.attrValueService = attrValueService;
        this.attrValueQueryService = attrValueQueryService;
    }

    /**
     * POST  /attr-values : Create a new attrValue.
     *
     * @param attrValueDTO the attrValueDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new attrValueDTO, or with status 400 (Bad Request) if the attrValue has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/attr-values")
    @Timed
    public ResponseEntity<AttrValueDTO> createAttrValue(@Valid @RequestBody AttrValueDTO attrValueDTO) throws URISyntaxException {
        log.debug("REST request to save AttrValue : {}", attrValueDTO);
        if (attrValueDTO.getId() != null) {
            throw new BadRequestAlertException("A new attrValue cannot already have an ID", ENTITY_NAME, "idexists");
        }
        AttrValueDTO result = attrValueService.save(attrValueDTO);
        return ResponseEntity.created(new URI("/api/attr-values/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /attr-values : Updates an existing attrValue.
     *
     * @param attrValueDTO the attrValueDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated attrValueDTO,
     * or with status 400 (Bad Request) if the attrValueDTO is not valid,
     * or with status 500 (Internal Server Error) if the attrValueDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/attr-values")
    @Timed
    public ResponseEntity<AttrValueDTO> updateAttrValue(@Valid @RequestBody AttrValueDTO attrValueDTO) throws URISyntaxException {
        log.debug("REST request to update AttrValue : {}", attrValueDTO);
        if (attrValueDTO.getId() == null) {
            return createAttrValue(attrValueDTO);
        }
        AttrValueDTO result = attrValueService.save(attrValueDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, attrValueDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /attr-values : get all the attrValues.
     *
     * @param pageable the pagination information
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of attrValues in body
     */
    @GetMapping("/attr-values")
    @Timed
    public ResponseEntity<List<AttrValueDTO>> getAllAttrValues(AttrValueCriteria criteria, Pageable pageable) {
        log.debug("REST request to get AttrValues by criteria: {}", criteria);
        Page<AttrValueDTO> page = attrValueQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/attr-values");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /attr-values/:id : get the "id" attrValue.
     *
     * @param id the id of the attrValueDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the attrValueDTO, or with status 404 (Not Found)
     */
    @GetMapping("/attr-values/{id}")
    @Timed
    public ResponseEntity<AttrValueDTO> getAttrValue(@PathVariable Long id) {
        log.debug("REST request to get AttrValue : {}", id);
        AttrValueDTO attrValueDTO = attrValueService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(attrValueDTO));
    }

    /**
     * DELETE  /attr-values/:id : delete the "id" attrValue.
     *
     * @param id the id of the attrValueDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/attr-values/{id}")
    @Timed
    public ResponseEntity<Void> deleteAttrValue(@PathVariable Long id) {
        log.debug("REST request to delete AttrValue : {}", id);
        attrValueService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
