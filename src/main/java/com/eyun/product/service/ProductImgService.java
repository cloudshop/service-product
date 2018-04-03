package com.eyun.product.service;

import com.eyun.product.service.dto.ProductImgDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing ProductImg.
 */
public interface ProductImgService {

    /**
     * Save a productImg.
     *
     * @param productImgDTO the entity to save
     * @return the persisted entity
     */
    ProductImgDTO save(ProductImgDTO productImgDTO);

    /**
     * Get all the productImgs.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<ProductImgDTO> findAll(Pageable pageable);

    /**
     * Get the "id" productImg.
     *
     * @param id the id of the entity
     * @return the entity
     */
    ProductImgDTO findOne(Long id);

    /**
     * Delete the "id" productImg.
     *
     * @param id the id of the entity
     */
    void delete(Long id);
}
