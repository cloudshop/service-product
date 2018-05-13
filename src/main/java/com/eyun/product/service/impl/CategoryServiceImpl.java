package com.eyun.product.service.impl;

import com.eyun.product.service.CategoryService;
import com.eyun.product.domain.Category;
import com.eyun.product.repository.CategoryRepository;
import com.eyun.product.service.dto.CategoryDTO;
import com.eyun.product.service.mapper.CategoryMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Service Implementation for managing Category.
 */
@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final Logger log = LoggerFactory.getLogger(CategoryServiceImpl.class);

    private final CategoryRepository categoryRepository;

    private final CategoryMapper categoryMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }
    @Override
    public Map getCategory(Long id)throws Exception{
        Map result=new HashMap();
        List<Map<String,String>> category=categoryRepository.getRootCategory();
        result.put("firstCategory",category);
        List<Map<String,String>>subList=categoryRepository.getSubCategory(id);
        List<Map<String,String>> secondList=new ArrayList<Map<String,String>>();
        if (!subList.isEmpty()&&subList.size()>0){
            String secondId="";
            Map second=new HashMap();
            List subIdlist=new ArrayList();
            List<Map<String,String>> thirdList=new ArrayList<Map<String,String>>();
            for (Map<String,String> map:subList){
                if (!secondId.equals(String.valueOf(map.get("secondid")))){
                    secondId=String.valueOf(map.get("secondid"));
                    second=new HashMap();
                    thirdList=new ArrayList<>();
                }
                second.put("secondId",secondId);
                second.put("secondName",map.get("secondname"));
                Map third=new HashMap();
                third.put("thirdid",String.valueOf(map.get("thirdid")));
                third.put("parentid",String.valueOf(map.get("parentid")));
                third.put("thirdName",map.get("thirdname"));
                third.put("target",map.get("target"));
                third.put("target_type",map.get("target_type"));
                third.put("logo",map.get("logo"));
                thirdList.add(third);
                second.put("thirdCategory",thirdList);
                if (!subIdlist.contains(secondId)){
                    subIdlist.add(secondId);
                    secondList.add(second);
                }
            }
        }
        result.put("secondCategory",secondList);
        return result;
    }
    /**
     * Save a category.
     *
     * @param categoryDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public CategoryDTO save(CategoryDTO categoryDTO) {
        log.debug("Request to save Category : {}", categoryDTO);
        Category category = categoryMapper.toEntity(categoryDTO);
        category = categoryRepository.save(category);
        return categoryMapper.toDto(category);
    }

    /**
     * Get all the categories.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<CategoryDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Categories");
        return categoryRepository.findAll(pageable)
            .map(categoryMapper::toDto);
    }

    /**
     * Get one category by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public CategoryDTO findOne(Long id) {
        log.debug("Request to get Category : {}", id);
        Category category = categoryRepository.findOne(id);
        return categoryMapper.toDto(category);
    }

    /**
     * Delete the category by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Category : {}", id);
        categoryRepository.delete(id);
    }
}
