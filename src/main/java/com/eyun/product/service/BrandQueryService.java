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

import com.eyun.product.domain.Brand;
import com.eyun.product.domain.*; // for static metamodels
import com.eyun.product.repository.BrandRepository;
import com.eyun.product.service.dto.BrandCriteria;

import com.eyun.product.service.dto.BrandDTO;
import com.eyun.product.service.mapper.BrandMapper;

/**
 * Service for executing complex queries for Brand entities in the database.
 * The main input is a {@link BrandCriteria} which get's converted to {@link Specifications},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link BrandDTO} or a {@link Page} of {@link BrandDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class BrandQueryService extends QueryService<Brand> {

    private final Logger log = LoggerFactory.getLogger(BrandQueryService.class);


    private final BrandRepository brandRepository;

    private final BrandMapper brandMapper;

    public BrandQueryService(BrandRepository brandRepository, BrandMapper brandMapper) {
        this.brandRepository = brandRepository;
        this.brandMapper = brandMapper;
    }

    /**
     * Return a {@link List} of {@link BrandDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<BrandDTO> findByCriteria(BrandCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specifications<Brand> specification = createSpecification(criteria);
        return brandMapper.toDto(brandRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link BrandDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<BrandDTO> findByCriteria(BrandCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specifications<Brand> specification = createSpecification(criteria);
        final Page<Brand> result = brandRepository.findAll(specification, page);
        return result.map(brandMapper::toDto);
    }

    /**
     * Function to convert BrandCriteria to a {@link Specifications}
     */
    private Specifications<Brand> createSpecification(BrandCriteria criteria) {
        Specifications<Brand> specification = Specifications.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), Brand_.id));
            }
            if (criteria.getCategoryId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCategoryId(), Brand_.categoryId));
            }
            if (criteria.getBrandName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getBrandName(), Brand_.brandName));
            }
            if (criteria.getBrandAlias() != null) {
                specification = specification.and(buildStringSpecification(criteria.getBrandAlias(), Brand_.brandAlias));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), Brand_.description));
            }
            if (criteria.getLogo() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLogo(), Brand_.logo));
            }
            if (criteria.getStatus() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getStatus(), Brand_.status));
            }
            if (criteria.getCreatedTime() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedTime(), Brand_.createdTime));
            }
            if (criteria.getUpdatedTime() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getUpdatedTime(), Brand_.updatedTime));
            }
            if (criteria.getDeleted() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDeleted(), Brand_.deleted));
            }
        }
        return specification;
    }

}
