package com.eyun.product.repository;

import com.eyun.product.domain.Category;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;

import java.util.List;
import java.util.Map;


/**
 * Spring Data JPA repository for the Category entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>, JpaSpecificationExecutor<Category> {

    /*获取分类左侧导航*/
    @Query(value = "SELECT c.id AS firstid,c.name AS firstidName from category c WHERE c.parent_id=0 and c.deleted=0",nativeQuery = true)
    public List<Map<String,String>> getRootCategory();

    /*获取二级和三级分类*/
    @Query(value = "SELECT b.id AS secondid, b. NAME AS secondName, c.id AS thirdid, c.parent_id AS parentid, IFNULL(c.target,\"\") AS target, IFNULL(c.target_type,\"\") AS target_type, IFNULL(c. NAME, \"\") AS thirdName, IFNULL(c.image, \"\") AS logo FROM category a, category b, category c WHERE a.id = b.parent_id AND b.id = c.parent_id AND c.deleted = 0 AND a.id =?1 GROUP BY b.id,c.id",nativeQuery = true)
    public List<Map<String,String>> getSubCategory(Long id);

}
