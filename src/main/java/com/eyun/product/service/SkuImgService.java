package com.eyun.product.service;

import com.eyun.product.domain.SkuImg;
import com.eyun.product.service.dto.SkuImgDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service Interface for managing SkuImg.
 */
public interface SkuImgService {

    /**
     * Save a skuImg.
     *
     * @param skuImgDTO the entity to save
     * @return the persisted entity
     */
    SkuImgDTO save(SkuImgDTO skuImgDTO);

    /**
     * Get all the skuImgs.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<SkuImgDTO> findAll(Pageable pageable);

    /**
     * Get the "id" skuImg.
     *
     * @param id the id of the entity
     * @return the entity
     */
    SkuImgDTO findOne(Long id);

    List<SkuImg> findSkuImageBySkuId(Long skuId);

    /**
     * Delete the "id" skuImg.
     *
     * @param id the id of the entity
     */
    void delete(Long id);
}
