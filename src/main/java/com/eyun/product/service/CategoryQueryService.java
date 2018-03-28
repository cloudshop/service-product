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

import com.eyun.product.domain.Category;
import com.eyun.product.domain.*; // for static metamodels
import com.eyun.product.repository.CategoryRepository;
import com.eyun.product.service.dto.CategoryCriteria;

import com.eyun.product.service.dto.CategoryDTO;
import com.eyun.product.service.mapper.CategoryMapper;

/**
 * Service for executing complex queries for Category entities in the database.
 * The main input is a {@link CategoryCriteria} which get's converted to {@link Specifications},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link CategoryDTO} or a {@link Page} of {@link CategoryDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class CategoryQueryService extends QueryService<Category> {

    private final Logger log = LoggerFactory.getLogger(CategoryQueryService.class);


    private final CategoryRepository categoryRepository;

    private final CategoryMapper categoryMapper;

    public CategoryQueryService(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    /**
     * Return a {@link List} of {@link CategoryDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<CategoryDTO> findByCriteria(CategoryCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specifications<Category> specification = createSpecification(criteria);
        return categoryMapper.toDto(categoryRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link CategoryDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<CategoryDTO> findByCriteria(CategoryCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specifications<Category> specification = createSpecification(criteria);
        final Page<Category> result = categoryRepository.findAll(specification, page);
        return result.map(categoryMapper::toDto);
    }

    /**
     * Function to convert CategoryCriteria to a {@link Specifications}
     */
    private Specifications<Category> createSpecification(CategoryCriteria criteria) {
        Specifications<Category> specification = Specifications.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), Category_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Category_.name));
            }
            if (criteria.getParentId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getParentId(), Category_.parentId));
            }
            if (criteria.getIsParent() != null) {
                specification = specification.and(buildSpecification(criteria.getIsParent(), Category_.isParent));
            }
            if (criteria.getSort() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getSort(), Category_.sort));
            }
            if (criteria.getTarget() != null) {
                specification = specification.and(buildStringSpecification(criteria.getTarget(), Category_.target));
            }
            if (criteria.getTargetType() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getTargetType(), Category_.targetType));
            }
            if (criteria.getCreatedTime() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedTime(), Category_.createdTime));
            }
            if (criteria.getUpdatedTime() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getUpdatedTime(), Category_.updatedTime));
            }
            if (criteria.getDeleted() != null) {
                specification = specification.and(buildSpecification(criteria.getDeleted(), Category_.deleted));
            }
            if (criteria.getCategoryGrade() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCategoryGrade(), Category_.categoryGrade));
            }
        }
        return specification;
    }

}
