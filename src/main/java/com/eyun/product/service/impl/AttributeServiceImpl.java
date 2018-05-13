package com.eyun.product.service.impl;

import com.eyun.product.service.AttributeService;
import com.eyun.product.domain.Attribute;
import com.eyun.product.repository.AttributeRepository;
import com.eyun.product.service.dto.AttributeDTO;
import com.eyun.product.service.mapper.AttributeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Service Implementation for managing Attribute.
 */
@Service
@Transactional
public class AttributeServiceImpl implements AttributeService {

    private final Logger log = LoggerFactory.getLogger(AttributeServiceImpl.class);

    private final AttributeRepository attributeRepository;

    private final AttributeMapper attributeMapper;

    public AttributeServiceImpl(AttributeRepository attributeRepository, AttributeMapper attributeMapper) {
        this.attributeRepository = attributeRepository;
        this.attributeMapper = attributeMapper;
    }

    /**
     * Save a attribute.
     *
     * @param attributeDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public AttributeDTO save(AttributeDTO attributeDTO) {
        log.debug("Request to save Attribute : {}", attributeDTO);
        Attribute attribute = attributeMapper.toEntity(attributeDTO);
        attribute = attributeRepository.save(attribute);
        return attributeMapper.toDto(attribute);
    }

    /**
     * Get all the attributes.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<AttributeDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Attributes");
        return attributeRepository.findAll(pageable)
            .map(attributeMapper::toDto);
    }

    /**
     * Get one attribute by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public AttributeDTO findOne(Long id) {
        log.debug("Request to get Attribute : {}", id);
        Attribute attribute = attributeRepository.findOne(id);
        return attributeMapper.toDto(attribute);
    }

    /**
     * Delete the attribute by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Attribute : {}", id);
        attributeRepository.delete(id);
    }
}
