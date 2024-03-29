package com.eyun.product.service;

import com.eyun.product.service.dto.CategoryDTO;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

/**
 * Service Interface for managing Category.
 */
public interface CategoryService {

    /**
     * Save a category.
     *
     * @param categoryDTO the entity to save
     * @return the persisted entity
     */
    CategoryDTO save(CategoryDTO categoryDTO);

    /**
     * Get all the categories.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<CategoryDTO> findAll(Pageable pageable);

    public Map getCategory(Long id)throws Exception;
    /**
     * Get the "id" category.
     *
     * @param id the id of the entity
     * @return the entity
     */
    CategoryDTO findOne(Long id);

    /**
     * Delete the "id" category.
     *
     * @param id the id of the entity
     */
    void delete(Long id);
}
