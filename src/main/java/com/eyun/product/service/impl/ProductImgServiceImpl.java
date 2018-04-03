package com.eyun.product.service.impl;

import com.eyun.product.service.ProductImgService;
import com.eyun.product.domain.ProductImg;
import com.eyun.product.repository.ProductImgRepository;
import com.eyun.product.service.dto.ProductImgDTO;
import com.eyun.product.service.mapper.ProductImgMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Service Implementation for managing ProductImg.
 */
@Service
@Transactional
public class ProductImgServiceImpl implements ProductImgService {

    private final Logger log = LoggerFactory.getLogger(ProductImgServiceImpl.class);

    private final ProductImgRepository productImgRepository;

    private final ProductImgMapper productImgMapper;

    public ProductImgServiceImpl(ProductImgRepository productImgRepository, ProductImgMapper productImgMapper) {
        this.productImgRepository = productImgRepository;
        this.productImgMapper = productImgMapper;
    }

    /**
     * Save a productImg.
     *
     * @param productImgDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public ProductImgDTO save(ProductImgDTO productImgDTO) {
        log.debug("Request to save ProductImg : {}", productImgDTO);
        ProductImg productImg = productImgMapper.toEntity(productImgDTO);
        productImg = productImgRepository.save(productImg);
        return productImgMapper.toDto(productImg);
    }

    /**
     * Get all the productImgs.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ProductImgDTO> findAll(Pageable pageable) {
        log.debug("Request to get all ProductImgs");
        return productImgRepository.findAll(pageable)
            .map(productImgMapper::toDto);
    }

    /**
     * Get one productImg by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public ProductImgDTO findOne(Long id) {
        log.debug("Request to get ProductImg : {}", id);
        ProductImg productImg = productImgRepository.findOne(id);
        return productImgMapper.toDto(productImg);
    }

    /**
     * Delete the productImg by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete ProductImg : {}", id);
        productImgRepository.delete(id);
    }
}
