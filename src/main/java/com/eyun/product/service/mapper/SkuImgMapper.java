package com.eyun.product.service.mapper;

import com.eyun.product.domain.*;
import com.eyun.product.service.dto.SkuImgDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity SkuImg and its DTO SkuImgDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface SkuImgMapper extends EntityMapper<SkuImgDTO, SkuImg> {



    default SkuImg fromId(Long id) {
        if (id == null) {
            return null;
        }
        SkuImg skuImg = new SkuImg();
        skuImg.setId(id);
        return skuImg;
    }
}
