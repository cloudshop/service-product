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

     public ProductSku findByProductIdAndAttrString(Long productId,String attrString);

     @Query(value = "SELECT sku.id AS skuId, sku.sku_name AS skuName, sku.count AS count, sku.price AS price, ifnull(sku.status,\"\") AS STATUS, sku.transfer AS transfer, sku.sku_code AS skuCode, IFNULL(sku.created_time, \"\") AS publishTime FROM product p, product_sku sku WHERE p.id = sku.product_id AND p.shop_id = :shopId LIMIT :start,:size",nativeQuery = true)
     public List<Map> findProductSkusByShopId(@Param("shopId") Long shopId,@Param("start") Integer start,@Param("size") Integer size);
}
