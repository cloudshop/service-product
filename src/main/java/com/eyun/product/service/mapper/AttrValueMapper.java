package com.eyun.product.service.mapper;

import com.eyun.product.domain.*;
import com.eyun.product.service.dto.AttrValueDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity AttrValue and its DTO AttrValueDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface AttrValueMapper extends EntityMapper<AttrValueDTO, AttrValue> {



    default AttrValue fromId(Long id) {
        if (id == null) {
            return null;
        }
        AttrValue attrValue = new AttrValue();
        attrValue.setId(id);
        return attrValue;
    }
}
