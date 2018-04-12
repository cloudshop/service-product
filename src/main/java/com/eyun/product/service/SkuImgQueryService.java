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

import com.eyun.product.domain.SkuImg;
import com.eyun.product.domain.*; // for static metamodels
import com.eyun.product.repository.SkuImgRepository;
import com.eyun.product.service.dto.SkuImgCriteria;

import com.eyun.product.service.dto.SkuImgDTO;
import com.eyun.product.service.mapper.SkuImgMapper;

/**
 * Service for executing complex queries for SkuImg entities in the database.
 * The main input is a {@link SkuImgCriteria} which get's converted to {@link Specifications},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link SkuImgDTO} or a {@link Page} of {@link SkuImgDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class SkuImgQueryService extends QueryService<SkuImg> {

    private final Logger log = LoggerFactory.getLogger(SkuImgQueryService.class);


    private final SkuImgRepository skuImgRepository;

    private final SkuImgMapper skuImgMapper;

    public SkuImgQueryService(SkuImgRepository skuImgRepository, SkuImgMapper skuImgMapper) {
        this.skuImgRepository = skuImgRepository;
        this.skuImgMapper = skuImgMapper;
    }

    /**
     * Return a {@link List} of {@link SkuImgDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<SkuImgDTO> findByCriteria(SkuImgCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specifications<SkuImg> specification = createSpecification(criteria);
        return skuImgMapper.toDto(skuImgRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link SkuImgDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<SkuImgDTO> findByCriteria(SkuImgCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specifications<SkuImg> specification = createSpecification(criteria);
        final Page<SkuImg> result = skuImgRepository.findAll(specification, page);
        return result.map(skuImgMapper::toDto);
    }

    /**
     * Function to convert SkuImgCriteria to a {@link Specifications}
     */
    private Specifications<SkuImg> createSpecification(SkuImgCriteria criteria) {
        Specifications<SkuImg> specification = Specifications.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), SkuImg_.id));
            }
            if (criteria.getSkuId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getSkuId(), SkuImg_.skuId));
            }
            if (criteria.getImgUrl() != null) {
                specification = specification.and(buildStringSpecification(criteria.getImgUrl(), SkuImg_.imgUrl));
            }
            if (criteria.getType() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getType(), SkuImg_.type));
            }
            if (criteria.getRank() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getRank(), SkuImg_.rank));
            }
            if (criteria.getCreatedTime() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedTime(), SkuImg_.createdTime));
            }
            if (criteria.getUpdatedTime() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getUpdatedTime(), SkuImg_.updatedTime));
            }
            if (criteria.getDeleted() != null) {
                specification = specification.and(buildSpecification(criteria.getDeleted(), SkuImg_.deleted));
            }
        }
        return specification;
    }

}
