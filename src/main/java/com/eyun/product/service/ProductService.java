package com.eyun.product.service;

import com.eyun.product.service.dto.ProductDTO;

import com.eyun.product.service.dto.ProductSeachParam;
import org.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;


/**
 * Service Interface for managing Product.
 */
public interface ProductService {

    /**
     * Save a product.
     *
     * @param productDTO the entity to save
     * @return the persisted entity
     */
    ProductDTO save(ProductDTO productDTO);

    public Map findProductById(Long id);

    public Map findProductByCatewgory(ProductSeachParam productSeachParam);

    public List<Map> findProductByIds(List<Long> ids);

    /**
     * Get all the products.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<ProductDTO> findAll(Pageable pageable);

    /**
     * Get the "id" product.
     *
     * @param id the id of the entity
     * @return the entity
     */
    ProductDTO findOne(Long id);

    /**
     * Delete the "id" product.
     *
     * @param id the id of the entity
     */
    void delete(Long id);
}
