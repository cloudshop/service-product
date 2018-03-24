package com.eyun.product.service.impl;

import com.eyun.product.service.ProductSkuService;
import com.eyun.product.domain.ProductSku;
import com.eyun.product.repository.ProductSkuRepository;
import com.eyun.product.service.dto.ProductSkuDTO;
import com.eyun.product.service.mapper.ProductSkuMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Service Implementation for managing ProductSku.
 */
@Service
@Transactional
public class ProductSkuServiceImpl implements ProductSkuService {

    private final Logger log = LoggerFactory.getLogger(ProductSkuServiceImpl.class);

    private final ProductSkuRepository productSkuRepository;

    private final ProductSkuMapper productSkuMapper;

    public ProductSkuServiceImpl(ProductSkuRepository productSkuRepository, ProductSkuMapper productSkuMapper) {
        this.productSkuRepository = productSkuRepository;
        this.productSkuMapper = productSkuMapper;
    }

    /**
     * Save a productSku.
     *
     * @param productSkuDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public ProductSkuDTO save(ProductSkuDTO productSkuDTO) {
        log.debug("Request to save ProductSku : {}", productSkuDTO);
        ProductSku productSku = productSkuMapper.toEntity(productSkuDTO);
        productSku = productSkuRepository.save(productSku);
        return productSkuMapper.toDto(productSku);
    }

    /**
     * Get all the productSkus.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ProductSkuDTO> findAll(Pageable pageable) {
        log.debug("Request to get all ProductSkus");
        return productSkuRepository.findAll(pageable)
            .map(productSkuMapper::toDto);
    }

    /**
     * Get one productSku by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public ProductSkuDTO findOne(Long id) {
        log.debug("Request to get ProductSku : {}", id);
        ProductSku productSku = productSkuRepository.findOne(id);
        return productSkuMapper.toDto(productSku);
    }

    /**
     * Delete the productSku by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete ProductSku : {}", id);
        productSkuRepository.delete(id);
    }
}
