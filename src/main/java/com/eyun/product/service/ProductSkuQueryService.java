package com.eyun.product.service;


import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.jhipster.service.QueryService;

import com.eyun.product.domain.ProductSku;
import com.eyun.product.domain.*; // for static metamodels
import com.eyun.product.repository.ProductSkuRepository;
import com.eyun.product.service.dto.ProductSkuCriteria;

import com.eyun.product.service.dto.ProductSkuDTO;
import com.eyun.product.service.mapper.ProductSkuMapper;

/**
 * Service for executing complex queries for ProductSku entities in the database.
 * The main input is a {@link ProductSkuCriteria} which get's converted to {@link Specifications},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ProductSkuDTO} or a {@link Page} of {@link ProductSkuDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ProductSkuQueryService extends QueryService<ProductSku> {

    private final Logger log = LoggerFactory.getLogger(ProductSkuQueryService.class);


    private final ProductSkuRepository productSkuRepository;

    private final ProductSkuMapper productSkuMapper;

    public ProductSkuQueryService(ProductSkuRepository productSkuRepository, ProductSkuMapper productSkuMapper) {
        this.productSkuRepository = productSkuRepository;
        this.productSkuMapper = productSkuMapper;
    }

    /**
     * Return a {@link List} of {@link ProductSkuDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ProductSkuDTO> findByCriteria(ProductSkuCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specifications<ProductSku> specification = createSpecification(criteria);
        return productSkuMapper.toDto(productSkuRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link ProductSkuDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ProductSkuDTO> findByCriteria(ProductSkuCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specifications<ProductSku> specification = createSpecification(criteria);
        final Page<ProductSku> result = productSkuRepository.findAll(specification, page);
        return result.map(productSkuMapper::toDto);
    }

    /**
     * Function to convert ProductSkuCriteria to a {@link Specifications}
     */
    private Specifications<ProductSku> createSpecification(ProductSkuCriteria criteria) {
        Specifications<ProductSku> specification = Specifications.where(null);
        if (criteria != null) {
            /*if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), ProductSku_.id));
            }
            if (criteria.getProductId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getProductId(), ProductSku_.productId));
            }
            if (criteria.getCount() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCount(), ProductSku_.count));
            }
            if (criteria.getPrice() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getPrice(), ProductSku_.price));
            }
            if (criteria.getStatus() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getStatus(), ProductSku_.status));
            }
            if (criteria.getSkuName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getSkuName(), ProductSku_.skuName));
            }
            if (criteria.getSkuCode() != null) {
                specification = specification.and(buildStringSpecification(criteria.getSkuCode(), ProductSku_.skuCode));
            }
            if (criteria.getAttrString() != null) {
                specification = specification.and(buildStringSpecification(criteria.getAttrString(), ProductSku_.attrString));
            }
            if (criteria.getCreatedTime() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedTime(), ProductSku_.createdTime));
            }
            if (criteria.getUpdatedTime() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getUpdatedTime(), ProductSku_.updatedTime));
            }
            if (criteria.getDeleted() != null) {
                specification = specification.and(buildSpecification(criteria.getDeleted(), ProductSku_.deleted));
            }
            if (criteria.getTransfer() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getTransfer(), ProductSku_.transfer));
            }*/
        }
        return specification;
    }

}
