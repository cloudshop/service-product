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
    @Query(value = "SELECT ifnull(sku.id, \"\") AS id, ifnull(p.shop_id, \"\") AS shopid, ifnull(sku.sku_name, \"\") AS productName, ifnull(sku.price, \"\") AS price, ifnull(sku.sku_code, \"\") AS skuCode, IFNULL(p.details, \"\") AS details, IFNULL(sku.transfer,\"\") AS transfer,IFNULL( GROUP_CONCAT(img.img_url), \"\" ) AS url FROM product p LEFT JOIN product_sku sku ON p.id = sku.product_id LEFT JOIN sku_img img ON sku.id = img.sku_id WHERE p.id = :id AND sku. STATUS = 0 AND sku.count > 0 LIMIT 0, 1",nativeQuery = true)
    public Map findProductById(@Param("id") Long id);

    /*获取商品全部属性*/
    @Query(value = "SELECT ifnull(att. NAME, \"\") AS attName, GROUP_CONCAT(v.id,\":\",v.jhi_value)AS attrValue FROM product p LEFT JOIN attribute att ON p.id = att.product_id LEFT JOIN attr_value v ON att.id = v.attr_id WHERE p.id = :id GROUP BY att. NAME",nativeQuery = true)
    public List<Map> findProductAttrById(@Param("id") Long id);

    /*批量获取商品*/
    @Query(value = "SELECT ifnull(sku.id,\"\") AS skuId, ifnull(sku.sku_name,\"\") AS skuName, ifnull(sku.price ,\"\")AS price, ifnull(substring_index(img.img_url, \",\", 1),\"\") AS url FROM product_sku sku LEFT JOIN sku_img img ON sku.id = img.sku_id WHERE sku.id IN (:ids) AND sku.status = 0",nativeQuery = true)
    public List<Map> findProductByIds(@Param("ids")List<Long> ids);

    /*根据商品名称获取商品*/
    public Product findProductByNameAndShopId(String name,Long shopId);

    /*获取店铺商品*/
    @Query(value = "SELECT p.id AS id, p. NAME AS NAME, p.list_price AS listPrice, ifnull(img.img_url, \"\") AS url FROM product p LEFT JOIN product_img img ON p.id = img.product_id LEFT JOIN product_sku sku ON p.id = sku.product_id WHERE p.shop_id = :shopId AND p.deleted = 0 AND sku.count > 0 AND sku. STATUS = 0 LIMIT :start,:size",nativeQuery = true)
    public List<Map> findProductsByShopIdAndDeleted(@Param("shopId")Long shopId,@Param("start")Integer start,@Param("size")Integer size);

    /*首页，分类商品搜索*/
    @Query(value = "SELECT p.id AS productid, p. NAME AS productName, ifnull(p.list_price,\"\") AS listPrice, P.shop_id AS shopId, ifnull(img.img_url,\"\") AS imgUrl FROM product p LEFT JOIN product_img img ON p.id = img.product_id WHERE p. NAME LIKE CONCAT('%',:productName,'%') AND p.deleted = 0",nativeQuery = true)
    public List<Map> findProductByParam(@Param("productName") String productName);

    /*店铺商品搜索*/
    @Query(value = "SELECT p.id AS id, p. NAME AS NAME, p.list_price AS listPrice, ifnull(img.img_url, \"\") AS url FROM product p LEFT JOIN product_img img ON p.id = img.product_id LEFT JOIN product_sku sku ON p.id = sku.product_id WHERE p.shop_id = :shopId AND p.deleted = 0 AND sku.count > 0 AND sku. STATUS = 0 AND p. NAME LIKE CONCAT('%', :productName, '%')",nativeQuery = true)
    public List<Map> shopProductSearch(@Param("shopId")Long shopId,@Param("productName")String productName);
}
