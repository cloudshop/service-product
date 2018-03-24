package com.eyun.product.service;

import com.eyun.product.service.dto.AttributeDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing Attribute.
 */
public interface AttributeService {

    /**
     * Save a attribute.
     *
     * @param attributeDTO the entity to save
     * @return the persisted entity
     */
    AttributeDTO save(AttributeDTO attributeDTO);

    /**
     * Get all the attributes.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<AttributeDTO> findAll(Pageable pageable);

    /**
     * Get the "id" attribute.
     *
     * @param id the id of the entity
     * @return the entity
     */
    AttributeDTO findOne(Long id);

    /**
     * Delete the "id" attribute.
     *
     * @param id the id of the entity
     */
    void delete(Long id);
}
