package com.eyun.product.service.impl;

import com.eyun.product.service.SkuImgService;
import com.eyun.product.domain.SkuImg;
import com.eyun.product.repository.SkuImgRepository;
import com.eyun.product.service.dto.SkuImgDTO;
import com.eyun.product.service.mapper.SkuImgMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * Service Implementation for managing SkuImg.
 */
@Service
@Transactional
public class SkuImgServiceImpl implements SkuImgService {

    private final Logger log = LoggerFactory.getLogger(SkuImgServiceImpl.class);

    private final SkuImgRepository skuImgRepository;

    private final SkuImgMapper skuImgMapper;

    public SkuImgServiceImpl(SkuImgRepository skuImgRepository, SkuImgMapper skuImgMapper) {
        this.skuImgRepository = skuImgRepository;
        this.skuImgMapper = skuImgMapper;
    }

    /**
     * Save a skuImg.
     *
     * @param skuImgDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public SkuImgDTO save(SkuImgDTO skuImgDTO) {
        log.debug("Request to save SkuImg : {}", skuImgDTO);
        SkuImg skuImg = skuImgMapper.toEntity(skuImgDTO);
        skuImg = skuImgRepository.save(skuImg);
        return skuImgMapper.toDto(skuImg);
    }

    /**
     * Get all the skuImgs.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<SkuImgDTO> findAll(Pageable pageable) {
        log.debug("Request to get all SkuImgs");
        return skuImgRepository.findAll(pageable)
            .map(skuImgMapper::toDto);
    }

    /**
     * Get one skuImg by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public SkuImgDTO findOne(Long id) {
        log.debug("Request to get SkuImg : {}", id);
        SkuImg skuImg = skuImgRepository.findOne(id);
        return skuImgMapper.toDto(skuImg);
    }

    @Override
    public List<SkuImg> findSkuImageBySkuId(Long skuId) {
        List<SkuImg> list=skuImgRepository.findAllBySkuId(skuId);
        return list;
    }

    /**
     * Delete the skuImg by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete SkuImg : {}", id);
        skuImgRepository.delete(id);
    }
}
