package com.eyun.product.service.impl;

import com.eyun.product.service.AttrValueService;
import com.eyun.product.domain.AttrValue;
import com.eyun.product.repository.AttrValueRepository;
import com.eyun.product.service.dto.AttrValueDTO;
import com.eyun.product.service.mapper.AttrValueMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Service Implementation for managing AttrValue.
 */
@Service
@Transactional
public class AttrValueServiceImpl implements AttrValueService {

    private final Logger log = LoggerFactory.getLogger(AttrValueServiceImpl.class);

    private final AttrValueRepository attrValueRepository;

    private final AttrValueMapper attrValueMapper;

    public AttrValueServiceImpl(AttrValueRepository attrValueRepository, AttrValueMapper attrValueMapper) {
        this.attrValueRepository = attrValueRepository;
        this.attrValueMapper = attrValueMapper;
    }

    /**
     * Save a attrValue.
     *
     * @param attrValueDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public AttrValueDTO save(AttrValueDTO attrValueDTO) {
        log.debug("Request to save AttrValue : {}", attrValueDTO);
        AttrValue attrValue = attrValueMapper.toEntity(attrValueDTO);
        attrValue = attrValueRepository.save(attrValue);
        return attrValueMapper.toDto(attrValue);
    }

    /**
     * Get all the attrValues.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<AttrValueDTO> findAll(Pageable pageable) {
        log.debug("Request to get all AttrValues");
        return attrValueRepository.findAll(pageable)
            .map(attrValueMapper::toDto);
    }

    /**
     * Get one attrValue by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public AttrValueDTO findOne(Long id) {
        log.debug("Request to get AttrValue : {}", id);
        AttrValue attrValue = attrValueRepository.findOne(id);
        return attrValueMapper.toDto(attrValue);
    }

    /**
     * Delete the attrValue by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete AttrValue : {}", id);
        attrValueRepository.delete(id);
    }
}
