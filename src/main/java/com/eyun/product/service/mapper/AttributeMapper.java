package com.eyun.product.service.mapper;

import com.eyun.product.domain.*;
import com.eyun.product.service.dto.AttributeDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity Attribute and its DTO AttributeDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface AttributeMapper extends EntityMapper<AttributeDTO, Attribute> {



    default Attribute fromId(Long id) {
        if (id == null) {
            return null;
        }
        Attribute attribute = new Attribute();
        attribute.setId(id);
        return attribute;
    }
}
