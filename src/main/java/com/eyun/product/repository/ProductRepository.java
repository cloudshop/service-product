package com.eyun.product.repository;

import com.eyun.product.domain.Product;



import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.*;

import java.util.List;
import java.util.Map;


/**
 * Spring Data JPA repository for the Product entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    /*获取商品信息*/
    @Query(value = "SELECT sku.id AS id, p.shop_id AS shopid,sku.sku_name AS productName, sku.price AS price, sku.sku_code AS skuCode,IFNULL(p.details,\"\") AS details FROM product p LEFT JOIN product_sku sku ON p.id = sku.product_id WHERE p.id = :id AND sku.deleted = 0 LIMIT 0,1",nativeQuery = true)
    public Map findProductById(@Param("id") Long id);

    /*获取商品全部属性*/
    @Query(value = "SELECT  ifnull(att.name,\"\") AS attName, ifnull(GROUP_CONCAT(v.jhi_value),\"\") AS attValue FROM product p LEFT JOIN attribute att ON p.id = att.product_id LEFT JOIN attr_value v ON att.id = v.attr_id, (SELECT p.name FROM product p WHERE p.id= :id)t WHERE p.name = t.name GROUP BY att.name",nativeQuery = true)
    public List<Map> findProductAttrById(@Param("id") Long id);

    /*批量获取商品*/
    @Query(value = "SELECT ifnull(sku.id,\"\") AS skuId, ifnull(sku.sku_name,\"\") AS skuName, ifnull(sku.price ,\"\")AS price, ifnull(substring_index(img.img_url, \",\", 1),\"\") AS url FROM product_sku sku LEFT JOIN sku_img img ON sku.id = img.sku_id WHERE sku.id IN (:ids)",nativeQuery = true)
    public List<Map> findProductByIds(@Param("ids")List<Long> ids);

}
