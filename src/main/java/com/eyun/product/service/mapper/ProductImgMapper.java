package com.eyun.product.service.mapper;

import com.eyun.product.domain.*;
import com.eyun.product.service.dto.ProductImgDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity ProductImg and its DTO ProductImgDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface ProductImgMapper extends EntityMapper<ProductImgDTO, ProductImg> {



    default ProductImg fromId(Long id) {
        if (id == null) {
            return null;
        }
        ProductImg productImg = new ProductImg();
        productImg.setId(id);
        return productImg;
    }
}
