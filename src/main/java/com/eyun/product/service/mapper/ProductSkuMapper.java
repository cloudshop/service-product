package com.eyun.product.service.mapper;

import com.eyun.product.domain.*;
import com.eyun.product.service.dto.ProductSkuDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity ProductSku and its DTO ProductSkuDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface ProductSkuMapper extends EntityMapper<ProductSkuDTO, ProductSku> {



    default ProductSku fromId(Long id) {
        if (id == null) {
            return null;
        }
        ProductSku productSku = new ProductSku();
        productSku.setId(id);
        return productSku;
    }
}
