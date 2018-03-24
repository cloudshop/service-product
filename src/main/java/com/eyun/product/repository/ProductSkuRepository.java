package com.eyun.product.repository;

import com.eyun.product.domain.ProductSku;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the ProductSku entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProductSkuRepository extends JpaRepository<ProductSku, Long>, JpaSpecificationExecutor<ProductSku> {

}
