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
    @Query(value = "SELECT p.id AS id, p.shop_id AS shopid,p. NAME AS productName, sku.price AS price, sku.sku_name AS attr, IFNULL(p.details,\"\") AS details FROM product p LEFT JOIN product_sku sku ON p.id = sku.product_id WHERE p.id = :id AND sku.deleted = 0",nativeQuery = true)
    public Map findProductById(@Param("id") Long id);

    /*获取商品全部属性*/
    @Query(value = "SELECT  att.name AS attName, GROUP_CONCAT(v.jhi_value) AS attValue FROM product p LEFT JOIN attribute att ON p.id = att.product_id LEFT JOIN attr_value v ON att.id = v.attr_id, (SELECT p.name FROM product p WHERE p.id= :id)t WHERE p.name = t.name GROUP BY att.name",nativeQuery = true)
    public List<Map> findProductAttrById(@Param("id") Long id);

    /*批量获取商品*/
    @Query(value = "SELECT p.id id, p. NAME AS productName, p.list_price AS Price, b.brand_name AS brandName, GROUP_CONCAT(av.jhi_value SEPARATOR \" \") AS attrValue FROM product p LEFT JOIN brand b ON p.brand_id = b.id LEFT JOIN attribute att ON att.product_id = p.id LEFT JOIN attr_value av ON att.id = av.attr_id WHERE p.id IN (?1) GROUP BY p.id",nativeQuery = true)
    public List findProductByIds(List<Long> ids);

}
