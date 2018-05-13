package com.eyun.product.service.mapper;

import com.eyun.product.domain.*;
import com.eyun.product.service.dto.BrandDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity Brand and its DTO BrandDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface BrandMapper extends EntityMapper<BrandDTO, Brand> {



    default Brand fromId(Long id) {
        if (id == null) {
            return null;
        }
        Brand brand = new Brand();
        brand.setId(id);
        return brand;
    }
}
