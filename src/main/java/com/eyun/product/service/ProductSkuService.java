package com.eyun.product.service;

import com.eyun.product.service.dto.ProductSkuDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

/**
 * Service Interface for managing ProductSku.
 */
public interface ProductSkuService {

    /**
     * Save a productSku.
     *
     * @param productSkuDTO the entity to save
     * @return the persisted entity
     */
    ProductSkuDTO save(ProductSkuDTO productSkuDTO);

    Map updateStockCount(ProductSkuDTO productSkuDTO, Integer processType);

    /**
     * Get all the productSkus.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<ProductSkuDTO> findAll(Pageable pageable);

    /**
     * Get the "id" productSku.
     *
     * @param id the id of the entity
     * @return the entity
     */
    ProductSkuDTO findOne(Long id);

    /**
     * Delete the "id" productSku.
     *
     * @param id the id of the entity
     */
    void delete(Long id);
}
