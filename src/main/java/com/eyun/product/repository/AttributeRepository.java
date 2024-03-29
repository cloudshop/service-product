package com.eyun.product.repository;

import com.eyun.product.domain.Attribute;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the Attribute entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AttributeRepository extends JpaRepository<Attribute, Long>, JpaSpecificationExecutor<Attribute> {

    /*获取商品attr*/
    public Attribute findAttributeByProductIdAndNameAndDeleted(Long productId,String name,Integer deleted);
}
