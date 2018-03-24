package com.eyun.product.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.eyun.product.service.AttributeService;
import com.eyun.product.web.rest.errors.BadRequestAlertException;
import com.eyun.product.web.rest.util.HeaderUtil;
import com.eyun.product.web.rest.util.PaginationUtil;
import com.eyun.product.service.dto.AttributeDTO;
import com.eyun.product.service.dto.AttributeCriteria;
import com.eyun.product.service.AttributeQueryService;
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
 * REST controller for managing Attribute.
 */
@RestController
@RequestMapping("/api")
public class AttributeResource {

    private final Logger log = LoggerFactory.getLogger(AttributeResource.class);

    private static final String ENTITY_NAME = "attribute";

    private final AttributeService attributeService;

    private final AttributeQueryService attributeQueryService;

    public AttributeResource(AttributeService attributeService, AttributeQueryService attributeQueryService) {
        this.attributeService = attributeService;
        this.attributeQueryService = attributeQueryService;
    }

    /**
     * POST  /attributes : Create a new attribute.
     *
     * @param attributeDTO the attributeDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new attributeDTO, or with status 400 (Bad Request) if the attribute has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/attributes")
    @Timed
    public ResponseEntity<AttributeDTO> createAttribute(@Valid @RequestBody AttributeDTO attributeDTO) throws URISyntaxException {
        log.debug("REST request to save Attribute : {}", attributeDTO);
        if (attributeDTO.getId() != null) {
            throw new BadRequestAlertException("A new attribute cannot already have an ID", ENTITY_NAME, "idexists");
        }
        AttributeDTO result = attributeService.save(attributeDTO);
        return ResponseEntity.created(new URI("/api/attributes/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /attributes : Updates an existing attribute.
     *
     * @param attributeDTO the attributeDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated attributeDTO,
     * or with status 400 (Bad Request) if the attributeDTO is not valid,
     * or with status 500 (Internal Server Error) if the attributeDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/attributes")
    @Timed
    public ResponseEntity<AttributeDTO> updateAttribute(@Valid @RequestBody AttributeDTO attributeDTO) throws URISyntaxException {
        log.debug("REST request to update Attribute : {}", attributeDTO);
        if (attributeDTO.getId() == null) {
            return createAttribute(attributeDTO);
        }
        AttributeDTO result = attributeService.save(attributeDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, attributeDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /attributes : get all the attributes.
     *
     * @param pageable the pagination information
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of attributes in body
     */
    @GetMapping("/attributes")
    @Timed
    public ResponseEntity<List<AttributeDTO>> getAllAttributes(AttributeCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Attributes by criteria: {}", criteria);
        Page<AttributeDTO> page = attributeQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/attributes");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /attributes/:id : get the "id" attribute.
     *
     * @param id the id of the attributeDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the attributeDTO, or with status 404 (Not Found)
     */
    @GetMapping("/attributes/{id}")
    @Timed
    public ResponseEntity<AttributeDTO> getAttribute(@PathVariable Long id) {
        log.debug("REST request to get Attribute : {}", id);
        AttributeDTO attributeDTO = attributeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(attributeDTO));
    }

    /**
     * DELETE  /attributes/:id : delete the "id" attribute.
     *
     * @param id the id of the attributeDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/attributes/{id}")
    @Timed
    public ResponseEntity<Void> deleteAttribute(@PathVariable Long id) {
        log.debug("REST request to delete Attribute : {}", id);
        attributeService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
