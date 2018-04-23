package com.eyun.product.repository;

import com.eyun.product.domain.SkuImg;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;

import java.util.List;


/**
 * Spring Data JPA repository for the SkuImg entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SkuImgRepository extends JpaRepository<SkuImg, Long>, JpaSpecificationExecutor<SkuImg> {

    public List<SkuImg> findAllBySkuId(Long skuId);
}
