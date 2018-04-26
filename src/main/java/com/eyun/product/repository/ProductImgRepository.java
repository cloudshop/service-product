package com.eyun.product.repository;

import com.eyun.product.domain.ProductImg;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the ProductImg entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProductImgRepository extends JpaRepository<ProductImg, Long>, JpaSpecificationExecutor<ProductImg> {

    public ProductImg findByProductId(Long productId);
}
