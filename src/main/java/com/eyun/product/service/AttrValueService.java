package com.eyun.product.service;

import com.eyun.product.service.dto.AttrValueDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing AttrValue.
 */
public interface AttrValueService {

    /**
     * Save a attrValue.
     *
     * @param attrValueDTO the entity to save
     * @return the persisted entity
     */
    AttrValueDTO save(AttrValueDTO attrValueDTO);

    /**
     * Get all the attrValues.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<AttrValueDTO> findAll(Pageable pageable);

    /**
     * Get the "id" attrValue.
     *
     * @param id the id of the entity
     * @return the entity
     */
    AttrValueDTO findOne(Long id);

    /**
     * Delete the "id" attrValue.
     *
     * @param id the id of the entity
     */
    void delete(Long id);
}
