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

import com.eyun.product.domain.Attribute;
import com.eyun.product.domain.*; // for static metamodels
import com.eyun.product.repository.AttributeRepository;
import com.eyun.product.service.dto.AttributeCriteria;

import com.eyun.product.service.dto.AttributeDTO;
import com.eyun.product.service.mapper.AttributeMapper;

/**
 * Service for executing complex queries for Attribute entities in the database.
 * The main input is a {@link AttributeCriteria} which get's converted to {@link Specifications},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link AttributeDTO} or a {@link Page} of {@link AttributeDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class AttributeQueryService extends QueryService<Attribute> {

    private final Logger log = LoggerFactory.getLogger(AttributeQueryService.class);


    private final AttributeRepository attributeRepository;

    private final AttributeMapper attributeMapper;

    public AttributeQueryService(AttributeRepository attributeRepository, AttributeMapper attributeMapper) {
        this.attributeRepository = attributeRepository;
        this.attributeMapper = attributeMapper;
    }

    /**
     * Return a {@link List} of {@link AttributeDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<AttributeDTO> findByCriteria(AttributeCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specifications<Attribute> specification = createSpecification(criteria);
        return attributeMapper.toDto(attributeRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link AttributeDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<AttributeDTO> findByCriteria(AttributeCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specifications<Attribute> specification = createSpecification(criteria);
        final Page<Attribute> result = attributeRepository.findAll(specification, page);
        return result.map(attributeMapper::toDto);
    }

    /**
     * Function to convert AttributeCriteria to a {@link Specifications}
     */
    private Specifications<Attribute> createSpecification(AttributeCriteria criteria) {
        Specifications<Attribute> specification = Specifications.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), Attribute_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Attribute_.name));
            }
            if (criteria.getProductId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getProductId(), Attribute_.productId));
            }
            if (criteria.getStatus() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getStatus(), Attribute_.status));
            }
            if (criteria.getCreatedTime() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedTime(), Attribute_.createdTime));
            }
            if (criteria.getUpdatedTime() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getUpdatedTime(), Attribute_.updatedTime));
            }
            if (criteria.getDeleted() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDeleted(), Attribute_.deleted));
            }
            if (criteria.getRank() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getRank(), Attribute_.rank));
            }
        }
        return specification;
    }

}
