package com.eyun.product.service;


import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.jhipster.service.QueryService;

import com.eyun.product.domain.ProductImg;
import com.eyun.product.domain.*; // for static metamodels
import com.eyun.product.repository.ProductImgRepository;
import com.eyun.product.service.dto.ProductImgCriteria;

import com.eyun.product.service.dto.ProductImgDTO;
import com.eyun.product.service.mapper.ProductImgMapper;

/**
 * Service for executing complex queries for ProductImg entities in the database.
 * The main input is a {@link ProductImgCriteria} which get's converted to {@link Specifications},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ProductImgDTO} or a {@link Page} of {@link ProductImgDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ProductImgQueryService extends QueryService<ProductImg> {

    private final Logger log = LoggerFactory.getLogger(ProductImgQueryService.class);


    private final ProductImgRepository productImgRepository;

    private final ProductImgMapper productImgMapper;

    public ProductImgQueryService(ProductImgRepository productImgRepository, ProductImgMapper productImgMapper) {
        this.productImgRepository = productImgRepository;
        this.productImgMapper = productImgMapper;
    }

    /**
     * Return a {@link List} of {@link ProductImgDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ProductImgDTO> findByCriteria(ProductImgCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specifications<ProductImg> specification = createSpecification(criteria);
        return productImgMapper.toDto(productImgRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link ProductImgDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ProductImgDTO> findByCriteria(ProductImgCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specifications<ProductImg> specification = createSpecification(criteria);
        final Page<ProductImg> result = productImgRepository.findAll(specification, page);
        return result.map(productImgMapper::toDto);
    }

    /**
     * Function to convert ProductImgCriteria to a {@link Specifications}
     */
    private Specifications<ProductImg> createSpecification(ProductImgCriteria criteria) {
        Specifications<ProductImg> specification = Specifications.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), ProductImg_.id));
            }
            if (criteria.getProductId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getProductId(), ProductImg_.productId));
            }
            if (criteria.getImgUrl() != null) {
                specification = specification.and(buildStringSpecification(criteria.getImgUrl(), ProductImg_.imgUrl));
            }
            if (criteria.getType() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getType(), ProductImg_.type));
            }
            if (criteria.getRank() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getRank(), ProductImg_.rank));
            }
            if (criteria.getCreatedTime() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedTime(), ProductImg_.createdTime));
            }
            if (criteria.getUpdatedTime() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getUpdatedTime(), ProductImg_.updatedTime));
            }
            if (criteria.getDeleted() != null) {
                specification = specification.and(buildSpecification(criteria.getDeleted(), ProductImg_.deleted));
            }
        }
        return specification;
    }

}
