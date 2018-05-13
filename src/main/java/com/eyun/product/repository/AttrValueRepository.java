package com.eyun.product.repository;

import com.eyun.product.domain.AttrValue;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;

import java.util.List;
import java.util.Map;


/**
 * Spring Data JPA repository for the AttrValue entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AttrValueRepository extends JpaRepository<AttrValue, Long>, JpaSpecificationExecutor<AttrValue> {

    public AttrValue findAllByAttrIdAndValue(Long attrId,String value);
}
