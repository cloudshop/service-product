package com.eyun.product.repository;

import com.eyun.product.domain.ProductSku;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;

import java.util.List;
import java.util.Map;


/**
 * Spring Data JPA repository for the ProductSku entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProductSkuRepository extends JpaRepository<ProductSku, Long>, JpaSpecificationExecutor<ProductSku> {

     public ProductSku findByAttrString(String attrString);

     /*获取sku的productId*/
     @Query(value = "SELECT sku.product_id AS productid FROM product_sku sku WHERE sku.product_id = ( SELECT sku.product_id FROM product_sku sku WHERE sku.id =:skuId ) AND sku. STATUS = 0",nativeQuery = true)
     public List<Map> findProductIdBySkuId(@Param("skuId")Long skuId);
}
