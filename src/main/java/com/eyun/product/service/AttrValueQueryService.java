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

import com.eyun.product.domain.AttrValue;
import com.eyun.product.domain.*; // for static metamodels
import com.eyun.product.repository.AttrValueRepository;
import com.eyun.product.service.dto.AttrValueCriteria;

import com.eyun.product.service.dto.AttrValueDTO;
import com.eyun.product.service.mapper.AttrValueMapper;

/**
 * Service for executing complex queries for AttrValue entities in the database.
 * The main input is a {@link AttrValueCriteria} which get's converted to {@link Specifications},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link AttrValueDTO} or a {@link Page} of {@link AttrValueDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class AttrValueQueryService extends QueryService<AttrValue> {

    private final Logger log = LoggerFactory.getLogger(AttrValueQueryService.class);


    private final AttrValueRepository attrValueRepository;

    private final AttrValueMapper attrValueMapper;

    public AttrValueQueryService(AttrValueRepository attrValueRepository, AttrValueMapper attrValueMapper) {
        this.attrValueRepository = attrValueRepository;
        this.attrValueMapper = attrValueMapper;
    }

    /**
     * Return a {@link List} of {@link AttrValueDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<AttrValueDTO> findByCriteria(AttrValueCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specifications<AttrValue> specification = createSpecification(criteria);
        return attrValueMapper.toDto(attrValueRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link AttrValueDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<AttrValueDTO> findByCriteria(AttrValueCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specifications<AttrValue> specification = createSpecification(criteria);
        final Page<AttrValue> result = attrValueRepository.findAll(specification, page);
        return result.map(attrValueMapper::toDto);
    }

    /**
     * Function to convert AttrValueCriteria to a {@link Specifications}
     */
    private Specifications<AttrValue> createSpecification(AttrValueCriteria criteria) {
        Specifications<AttrValue> specification = Specifications.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), AttrValue_.id));
            }
            if (criteria.getAttrId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getAttrId(), AttrValue_.attrId));
            }
            if (criteria.getValue() != null) {
                specification = specification.and(buildStringSpecification(criteria.getValue(), AttrValue_.value));
            }
            if (criteria.getImage() != null) {
                specification = specification.and(buildStringSpecification(criteria.getImage(), AttrValue_.image));
            }
            if (criteria.getStatus() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getStatus(), AttrValue_.status));
            }
            if (criteria.getCreatedTime() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedTime(), AttrValue_.createdTime));
            }
            if (criteria.getUpdatedTime() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getUpdatedTime(), AttrValue_.updatedTime));
            }
            if (criteria.getDeleted() != null) {
                specification = specification.and(buildSpecification(criteria.getDeleted(), AttrValue_.deleted));
            }
            if (criteria.getRank() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getRank(), AttrValue_.rank));
            }
        }
        return specification;
    }

}
