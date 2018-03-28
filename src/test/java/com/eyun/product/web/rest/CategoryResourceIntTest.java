package com.eyun.product.web.rest;

import com.eyun.product.ProductApp;

import com.eyun.product.config.SecurityBeanOverrideConfiguration;

import com.eyun.product.domain.Category;
import com.eyun.product.repository.CategoryRepository;
import com.eyun.product.service.CategoryService;
import com.eyun.product.service.dto.CategoryDTO;
import com.eyun.product.service.mapper.CategoryMapper;
import com.eyun.product.web.rest.errors.ExceptionTranslator;
import com.eyun.product.service.dto.CategoryCriteria;
import com.eyun.product.service.CategoryQueryService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static com.eyun.product.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the CategoryResource REST controller.
 *
 * @see CategoryResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ProductApp.class, SecurityBeanOverrideConfiguration.class})
public class CategoryResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Long DEFAULT_PARENT_ID = 1L;
    private static final Long UPDATED_PARENT_ID = 2L;

    private static final Boolean DEFAULT_IS_PARENT = false;
    private static final Boolean UPDATED_IS_PARENT = true;

    private static final Integer DEFAULT_SORT = 1;
    private static final Integer UPDATED_SORT = 2;

    private static final String DEFAULT_TARGET = "AAAAAAAAAA";
    private static final String UPDATED_TARGET = "BBBBBBBBBB";

    private static final Integer DEFAULT_TARGET_TYPE = 1;
    private static final Integer UPDATED_TARGET_TYPE = 2;

    private static final Instant DEFAULT_CREATED_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Boolean DEFAULT_DELETED = false;
    private static final Boolean UPDATED_DELETED = true;

    private static final Integer DEFAULT_CATEGORY_GRADE = 1;
    private static final Integer UPDATED_CATEGORY_GRADE = 2;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryQueryService categoryQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restCategoryMockMvc;

    private Category category;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final CategoryResource categoryResource = new CategoryResource(categoryService, categoryQueryService);
        this.restCategoryMockMvc = MockMvcBuilders.standaloneSetup(categoryResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Category createEntity(EntityManager em) {
        Category category = new Category()
            .name(DEFAULT_NAME)
            .parentId(DEFAULT_PARENT_ID)
            .isParent(DEFAULT_IS_PARENT)
            .sort(DEFAULT_SORT)
            .target(DEFAULT_TARGET)
            .targetType(DEFAULT_TARGET_TYPE)
            .createdTime(DEFAULT_CREATED_TIME)
            .updatedTime(DEFAULT_UPDATED_TIME)
            .deleted(DEFAULT_DELETED)
            .categoryGrade(DEFAULT_CATEGORY_GRADE);
        return category;
    }

    @Before
    public void initTest() {
        category = createEntity(em);
    }

    @Test
    @Transactional
    public void createCategory() throws Exception {
        int databaseSizeBeforeCreate = categoryRepository.findAll().size();

        // Create the Category
        CategoryDTO categoryDTO = categoryMapper.toDto(category);
        restCategoryMockMvc.perform(post("/api/categories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(categoryDTO)))
            .andExpect(status().isCreated());

        // Validate the Category in the database
        List<Category> categoryList = categoryRepository.findAll();
        assertThat(categoryList).hasSize(databaseSizeBeforeCreate + 1);
        Category testCategory = categoryList.get(categoryList.size() - 1);
        assertThat(testCategory.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testCategory.getParentId()).isEqualTo(DEFAULT_PARENT_ID);
        assertThat(testCategory.isIsParent()).isEqualTo(DEFAULT_IS_PARENT);
        assertThat(testCategory.getSort()).isEqualTo(DEFAULT_SORT);
        assertThat(testCategory.getTarget()).isEqualTo(DEFAULT_TARGET);
        assertThat(testCategory.getTargetType()).isEqualTo(DEFAULT_TARGET_TYPE);
        assertThat(testCategory.getCreatedTime()).isEqualTo(DEFAULT_CREATED_TIME);
        assertThat(testCategory.getUpdatedTime()).isEqualTo(DEFAULT_UPDATED_TIME);
        assertThat(testCategory.isDeleted()).isEqualTo(DEFAULT_DELETED);
        assertThat(testCategory.getCategoryGrade()).isEqualTo(DEFAULT_CATEGORY_GRADE);
    }

    @Test
    @Transactional
    public void createCategoryWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = categoryRepository.findAll().size();

        // Create the Category with an existing ID
        category.setId(1L);
        CategoryDTO categoryDTO = categoryMapper.toDto(category);

        // An entity with an existing ID cannot be created, so this API call must fail
        restCategoryMockMvc.perform(post("/api/categories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(categoryDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Category in the database
        List<Category> categoryList = categoryRepository.findAll();
        assertThat(categoryList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkParentIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = categoryRepository.findAll().size();
        // set the field null
        category.setParentId(null);

        // Create the Category, which fails.
        CategoryDTO categoryDTO = categoryMapper.toDto(category);

        restCategoryMockMvc.perform(post("/api/categories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(categoryDTO)))
            .andExpect(status().isBadRequest());

        List<Category> categoryList = categoryRepository.findAll();
        assertThat(categoryList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllCategories() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList
        restCategoryMockMvc.perform(get("/api/categories?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(category.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].parentId").value(hasItem(DEFAULT_PARENT_ID.intValue())))
            .andExpect(jsonPath("$.[*].isParent").value(hasItem(DEFAULT_IS_PARENT.booleanValue())))
            .andExpect(jsonPath("$.[*].sort").value(hasItem(DEFAULT_SORT)))
            .andExpect(jsonPath("$.[*].target").value(hasItem(DEFAULT_TARGET.toString())))
            .andExpect(jsonPath("$.[*].targetType").value(hasItem(DEFAULT_TARGET_TYPE)))
            .andExpect(jsonPath("$.[*].createdTime").value(hasItem(DEFAULT_CREATED_TIME.toString())))
            .andExpect(jsonPath("$.[*].updatedTime").value(hasItem(DEFAULT_UPDATED_TIME.toString())))
            .andExpect(jsonPath("$.[*].deleted").value(hasItem(DEFAULT_DELETED.booleanValue())))
            .andExpect(jsonPath("$.[*].categoryGrade").value(hasItem(DEFAULT_CATEGORY_GRADE)));
    }

    @Test
    @Transactional
    public void getCategory() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get the category
        restCategoryMockMvc.perform(get("/api/categories/{id}", category.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(category.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.parentId").value(DEFAULT_PARENT_ID.intValue()))
            .andExpect(jsonPath("$.isParent").value(DEFAULT_IS_PARENT.booleanValue()))
            .andExpect(jsonPath("$.sort").value(DEFAULT_SORT))
            .andExpect(jsonPath("$.target").value(DEFAULT_TARGET.toString()))
            .andExpect(jsonPath("$.targetType").value(DEFAULT_TARGET_TYPE))
            .andExpect(jsonPath("$.createdTime").value(DEFAULT_CREATED_TIME.toString()))
            .andExpect(jsonPath("$.updatedTime").value(DEFAULT_UPDATED_TIME.toString()))
            .andExpect(jsonPath("$.deleted").value(DEFAULT_DELETED.booleanValue()))
            .andExpect(jsonPath("$.categoryGrade").value(DEFAULT_CATEGORY_GRADE));
    }

    @Test
    @Transactional
    public void getAllCategoriesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where name equals to DEFAULT_NAME
        defaultCategoryShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the categoryList where name equals to UPDATED_NAME
        defaultCategoryShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllCategoriesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where name in DEFAULT_NAME or UPDATED_NAME
        defaultCategoryShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the categoryList where name equals to UPDATED_NAME
        defaultCategoryShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllCategoriesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where name is not null
        defaultCategoryShouldBeFound("name.specified=true");

        // Get all the categoryList where name is null
        defaultCategoryShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    public void getAllCategoriesByParentIdIsEqualToSomething() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where parentId equals to DEFAULT_PARENT_ID
        defaultCategoryShouldBeFound("parentId.equals=" + DEFAULT_PARENT_ID);

        // Get all the categoryList where parentId equals to UPDATED_PARENT_ID
        defaultCategoryShouldNotBeFound("parentId.equals=" + UPDATED_PARENT_ID);
    }

    @Test
    @Transactional
    public void getAllCategoriesByParentIdIsInShouldWork() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where parentId in DEFAULT_PARENT_ID or UPDATED_PARENT_ID
        defaultCategoryShouldBeFound("parentId.in=" + DEFAULT_PARENT_ID + "," + UPDATED_PARENT_ID);

        // Get all the categoryList where parentId equals to UPDATED_PARENT_ID
        defaultCategoryShouldNotBeFound("parentId.in=" + UPDATED_PARENT_ID);
    }

    @Test
    @Transactional
    public void getAllCategoriesByParentIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where parentId is not null
        defaultCategoryShouldBeFound("parentId.specified=true");

        // Get all the categoryList where parentId is null
        defaultCategoryShouldNotBeFound("parentId.specified=false");
    }

    @Test
    @Transactional
    public void getAllCategoriesByParentIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where parentId greater than or equals to DEFAULT_PARENT_ID
        defaultCategoryShouldBeFound("parentId.greaterOrEqualThan=" + DEFAULT_PARENT_ID);

        // Get all the categoryList where parentId greater than or equals to UPDATED_PARENT_ID
        defaultCategoryShouldNotBeFound("parentId.greaterOrEqualThan=" + UPDATED_PARENT_ID);
    }

    @Test
    @Transactional
    public void getAllCategoriesByParentIdIsLessThanSomething() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where parentId less than or equals to DEFAULT_PARENT_ID
        defaultCategoryShouldNotBeFound("parentId.lessThan=" + DEFAULT_PARENT_ID);

        // Get all the categoryList where parentId less than or equals to UPDATED_PARENT_ID
        defaultCategoryShouldBeFound("parentId.lessThan=" + UPDATED_PARENT_ID);
    }


    @Test
    @Transactional
    public void getAllCategoriesByIsParentIsEqualToSomething() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where isParent equals to DEFAULT_IS_PARENT
        defaultCategoryShouldBeFound("isParent.equals=" + DEFAULT_IS_PARENT);

        // Get all the categoryList where isParent equals to UPDATED_IS_PARENT
        defaultCategoryShouldNotBeFound("isParent.equals=" + UPDATED_IS_PARENT);
    }

    @Test
    @Transactional
    public void getAllCategoriesByIsParentIsInShouldWork() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where isParent in DEFAULT_IS_PARENT or UPDATED_IS_PARENT
        defaultCategoryShouldBeFound("isParent.in=" + DEFAULT_IS_PARENT + "," + UPDATED_IS_PARENT);

        // Get all the categoryList where isParent equals to UPDATED_IS_PARENT
        defaultCategoryShouldNotBeFound("isParent.in=" + UPDATED_IS_PARENT);
    }

    @Test
    @Transactional
    public void getAllCategoriesByIsParentIsNullOrNotNull() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where isParent is not null
        defaultCategoryShouldBeFound("isParent.specified=true");

        // Get all the categoryList where isParent is null
        defaultCategoryShouldNotBeFound("isParent.specified=false");
    }

    @Test
    @Transactional
    public void getAllCategoriesBySortIsEqualToSomething() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where sort equals to DEFAULT_SORT
        defaultCategoryShouldBeFound("sort.equals=" + DEFAULT_SORT);

        // Get all the categoryList where sort equals to UPDATED_SORT
        defaultCategoryShouldNotBeFound("sort.equals=" + UPDATED_SORT);
    }

    @Test
    @Transactional
    public void getAllCategoriesBySortIsInShouldWork() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where sort in DEFAULT_SORT or UPDATED_SORT
        defaultCategoryShouldBeFound("sort.in=" + DEFAULT_SORT + "," + UPDATED_SORT);

        // Get all the categoryList where sort equals to UPDATED_SORT
        defaultCategoryShouldNotBeFound("sort.in=" + UPDATED_SORT);
    }

    @Test
    @Transactional
    public void getAllCategoriesBySortIsNullOrNotNull() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where sort is not null
        defaultCategoryShouldBeFound("sort.specified=true");

        // Get all the categoryList where sort is null
        defaultCategoryShouldNotBeFound("sort.specified=false");
    }

    @Test
    @Transactional
    public void getAllCategoriesBySortIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where sort greater than or equals to DEFAULT_SORT
        defaultCategoryShouldBeFound("sort.greaterOrEqualThan=" + DEFAULT_SORT);

        // Get all the categoryList where sort greater than or equals to UPDATED_SORT
        defaultCategoryShouldNotBeFound("sort.greaterOrEqualThan=" + UPDATED_SORT);
    }

    @Test
    @Transactional
    public void getAllCategoriesBySortIsLessThanSomething() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where sort less than or equals to DEFAULT_SORT
        defaultCategoryShouldNotBeFound("sort.lessThan=" + DEFAULT_SORT);

        // Get all the categoryList where sort less than or equals to UPDATED_SORT
        defaultCategoryShouldBeFound("sort.lessThan=" + UPDATED_SORT);
    }


    @Test
    @Transactional
    public void getAllCategoriesByTargetIsEqualToSomething() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where target equals to DEFAULT_TARGET
        defaultCategoryShouldBeFound("target.equals=" + DEFAULT_TARGET);

        // Get all the categoryList where target equals to UPDATED_TARGET
        defaultCategoryShouldNotBeFound("target.equals=" + UPDATED_TARGET);
    }

    @Test
    @Transactional
    public void getAllCategoriesByTargetIsInShouldWork() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where target in DEFAULT_TARGET or UPDATED_TARGET
        defaultCategoryShouldBeFound("target.in=" + DEFAULT_TARGET + "," + UPDATED_TARGET);

        // Get all the categoryList where target equals to UPDATED_TARGET
        defaultCategoryShouldNotBeFound("target.in=" + UPDATED_TARGET);
    }

    @Test
    @Transactional
    public void getAllCategoriesByTargetIsNullOrNotNull() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where target is not null
        defaultCategoryShouldBeFound("target.specified=true");

        // Get all the categoryList where target is null
        defaultCategoryShouldNotBeFound("target.specified=false");
    }

    @Test
    @Transactional
    public void getAllCategoriesByTargetTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where targetType equals to DEFAULT_TARGET_TYPE
        defaultCategoryShouldBeFound("targetType.equals=" + DEFAULT_TARGET_TYPE);

        // Get all the categoryList where targetType equals to UPDATED_TARGET_TYPE
        defaultCategoryShouldNotBeFound("targetType.equals=" + UPDATED_TARGET_TYPE);
    }

    @Test
    @Transactional
    public void getAllCategoriesByTargetTypeIsInShouldWork() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where targetType in DEFAULT_TARGET_TYPE or UPDATED_TARGET_TYPE
        defaultCategoryShouldBeFound("targetType.in=" + DEFAULT_TARGET_TYPE + "," + UPDATED_TARGET_TYPE);

        // Get all the categoryList where targetType equals to UPDATED_TARGET_TYPE
        defaultCategoryShouldNotBeFound("targetType.in=" + UPDATED_TARGET_TYPE);
    }

    @Test
    @Transactional
    public void getAllCategoriesByTargetTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where targetType is not null
        defaultCategoryShouldBeFound("targetType.specified=true");

        // Get all the categoryList where targetType is null
        defaultCategoryShouldNotBeFound("targetType.specified=false");
    }

    @Test
    @Transactional
    public void getAllCategoriesByTargetTypeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where targetType greater than or equals to DEFAULT_TARGET_TYPE
        defaultCategoryShouldBeFound("targetType.greaterOrEqualThan=" + DEFAULT_TARGET_TYPE);

        // Get all the categoryList where targetType greater than or equals to UPDATED_TARGET_TYPE
        defaultCategoryShouldNotBeFound("targetType.greaterOrEqualThan=" + UPDATED_TARGET_TYPE);
    }

    @Test
    @Transactional
    public void getAllCategoriesByTargetTypeIsLessThanSomething() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where targetType less than or equals to DEFAULT_TARGET_TYPE
        defaultCategoryShouldNotBeFound("targetType.lessThan=" + DEFAULT_TARGET_TYPE);

        // Get all the categoryList where targetType less than or equals to UPDATED_TARGET_TYPE
        defaultCategoryShouldBeFound("targetType.lessThan=" + UPDATED_TARGET_TYPE);
    }


    @Test
    @Transactional
    public void getAllCategoriesByCreatedTimeIsEqualToSomething() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where createdTime equals to DEFAULT_CREATED_TIME
        defaultCategoryShouldBeFound("createdTime.equals=" + DEFAULT_CREATED_TIME);

        // Get all the categoryList where createdTime equals to UPDATED_CREATED_TIME
        defaultCategoryShouldNotBeFound("createdTime.equals=" + UPDATED_CREATED_TIME);
    }

    @Test
    @Transactional
    public void getAllCategoriesByCreatedTimeIsInShouldWork() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where createdTime in DEFAULT_CREATED_TIME or UPDATED_CREATED_TIME
        defaultCategoryShouldBeFound("createdTime.in=" + DEFAULT_CREATED_TIME + "," + UPDATED_CREATED_TIME);

        // Get all the categoryList where createdTime equals to UPDATED_CREATED_TIME
        defaultCategoryShouldNotBeFound("createdTime.in=" + UPDATED_CREATED_TIME);
    }

    @Test
    @Transactional
    public void getAllCategoriesByCreatedTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where createdTime is not null
        defaultCategoryShouldBeFound("createdTime.specified=true");

        // Get all the categoryList where createdTime is null
        defaultCategoryShouldNotBeFound("createdTime.specified=false");
    }

    @Test
    @Transactional
    public void getAllCategoriesByUpdatedTimeIsEqualToSomething() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where updatedTime equals to DEFAULT_UPDATED_TIME
        defaultCategoryShouldBeFound("updatedTime.equals=" + DEFAULT_UPDATED_TIME);

        // Get all the categoryList where updatedTime equals to UPDATED_UPDATED_TIME
        defaultCategoryShouldNotBeFound("updatedTime.equals=" + UPDATED_UPDATED_TIME);
    }

    @Test
    @Transactional
    public void getAllCategoriesByUpdatedTimeIsInShouldWork() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where updatedTime in DEFAULT_UPDATED_TIME or UPDATED_UPDATED_TIME
        defaultCategoryShouldBeFound("updatedTime.in=" + DEFAULT_UPDATED_TIME + "," + UPDATED_UPDATED_TIME);

        // Get all the categoryList where updatedTime equals to UPDATED_UPDATED_TIME
        defaultCategoryShouldNotBeFound("updatedTime.in=" + UPDATED_UPDATED_TIME);
    }

    @Test
    @Transactional
    public void getAllCategoriesByUpdatedTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where updatedTime is not null
        defaultCategoryShouldBeFound("updatedTime.specified=true");

        // Get all the categoryList where updatedTime is null
        defaultCategoryShouldNotBeFound("updatedTime.specified=false");
    }

    @Test
    @Transactional
    public void getAllCategoriesByDeletedIsEqualToSomething() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where deleted equals to DEFAULT_DELETED
        defaultCategoryShouldBeFound("deleted.equals=" + DEFAULT_DELETED);

        // Get all the categoryList where deleted equals to UPDATED_DELETED
        defaultCategoryShouldNotBeFound("deleted.equals=" + UPDATED_DELETED);
    }

    @Test
    @Transactional
    public void getAllCategoriesByDeletedIsInShouldWork() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where deleted in DEFAULT_DELETED or UPDATED_DELETED
        defaultCategoryShouldBeFound("deleted.in=" + DEFAULT_DELETED + "," + UPDATED_DELETED);

        // Get all the categoryList where deleted equals to UPDATED_DELETED
        defaultCategoryShouldNotBeFound("deleted.in=" + UPDATED_DELETED);
    }

    @Test
    @Transactional
    public void getAllCategoriesByDeletedIsNullOrNotNull() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where deleted is not null
        defaultCategoryShouldBeFound("deleted.specified=true");

        // Get all the categoryList where deleted is null
        defaultCategoryShouldNotBeFound("deleted.specified=false");
    }

    @Test
    @Transactional
    public void getAllCategoriesByCategoryGradeIsEqualToSomething() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where categoryGrade equals to DEFAULT_CATEGORY_GRADE
        defaultCategoryShouldBeFound("categoryGrade.equals=" + DEFAULT_CATEGORY_GRADE);

        // Get all the categoryList where categoryGrade equals to UPDATED_CATEGORY_GRADE
        defaultCategoryShouldNotBeFound("categoryGrade.equals=" + UPDATED_CATEGORY_GRADE);
    }

    @Test
    @Transactional
    public void getAllCategoriesByCategoryGradeIsInShouldWork() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where categoryGrade in DEFAULT_CATEGORY_GRADE or UPDATED_CATEGORY_GRADE
        defaultCategoryShouldBeFound("categoryGrade.in=" + DEFAULT_CATEGORY_GRADE + "," + UPDATED_CATEGORY_GRADE);

        // Get all the categoryList where categoryGrade equals to UPDATED_CATEGORY_GRADE
        defaultCategoryShouldNotBeFound("categoryGrade.in=" + UPDATED_CATEGORY_GRADE);
    }

    @Test
    @Transactional
    public void getAllCategoriesByCategoryGradeIsNullOrNotNull() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where categoryGrade is not null
        defaultCategoryShouldBeFound("categoryGrade.specified=true");

        // Get all the categoryList where categoryGrade is null
        defaultCategoryShouldNotBeFound("categoryGrade.specified=false");
    }

    @Test
    @Transactional
    public void getAllCategoriesByCategoryGradeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where categoryGrade greater than or equals to DEFAULT_CATEGORY_GRADE
        defaultCategoryShouldBeFound("categoryGrade.greaterOrEqualThan=" + DEFAULT_CATEGORY_GRADE);

        // Get all the categoryList where categoryGrade greater than or equals to UPDATED_CATEGORY_GRADE
        defaultCategoryShouldNotBeFound("categoryGrade.greaterOrEqualThan=" + UPDATED_CATEGORY_GRADE);
    }

    @Test
    @Transactional
    public void getAllCategoriesByCategoryGradeIsLessThanSomething() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where categoryGrade less than or equals to DEFAULT_CATEGORY_GRADE
        defaultCategoryShouldNotBeFound("categoryGrade.lessThan=" + DEFAULT_CATEGORY_GRADE);

        // Get all the categoryList where categoryGrade less than or equals to UPDATED_CATEGORY_GRADE
        defaultCategoryShouldBeFound("categoryGrade.lessThan=" + UPDATED_CATEGORY_GRADE);
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultCategoryShouldBeFound(String filter) throws Exception {
        restCategoryMockMvc.perform(get("/api/categories?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(category.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].parentId").value(hasItem(DEFAULT_PARENT_ID.intValue())))
            .andExpect(jsonPath("$.[*].isParent").value(hasItem(DEFAULT_IS_PARENT.booleanValue())))
            .andExpect(jsonPath("$.[*].sort").value(hasItem(DEFAULT_SORT)))
            .andExpect(jsonPath("$.[*].target").value(hasItem(DEFAULT_TARGET.toString())))
            .andExpect(jsonPath("$.[*].targetType").value(hasItem(DEFAULT_TARGET_TYPE)))
            .andExpect(jsonPath("$.[*].createdTime").value(hasItem(DEFAULT_CREATED_TIME.toString())))
            .andExpect(jsonPath("$.[*].updatedTime").value(hasItem(DEFAULT_UPDATED_TIME.toString())))
            .andExpect(jsonPath("$.[*].deleted").value(hasItem(DEFAULT_DELETED.booleanValue())))
            .andExpect(jsonPath("$.[*].categoryGrade").value(hasItem(DEFAULT_CATEGORY_GRADE)));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultCategoryShouldNotBeFound(String filter) throws Exception {
        restCategoryMockMvc.perform(get("/api/categories?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }


    @Test
    @Transactional
    public void getNonExistingCategory() throws Exception {
        // Get the category
        restCategoryMockMvc.perform(get("/api/categories/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateCategory() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);
        int databaseSizeBeforeUpdate = categoryRepository.findAll().size();

        // Update the category
        Category updatedCategory = categoryRepository.findOne(category.getId());
        // Disconnect from session so that the updates on updatedCategory are not directly saved in db
        em.detach(updatedCategory);
        updatedCategory
            .name(UPDATED_NAME)
            .parentId(UPDATED_PARENT_ID)
            .isParent(UPDATED_IS_PARENT)
            .sort(UPDATED_SORT)
            .target(UPDATED_TARGET)
            .targetType(UPDATED_TARGET_TYPE)
            .createdTime(UPDATED_CREATED_TIME)
            .updatedTime(UPDATED_UPDATED_TIME)
            .deleted(UPDATED_DELETED)
            .categoryGrade(UPDATED_CATEGORY_GRADE);
        CategoryDTO categoryDTO = categoryMapper.toDto(updatedCategory);

        restCategoryMockMvc.perform(put("/api/categories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(categoryDTO)))
            .andExpect(status().isOk());

        // Validate the Category in the database
        List<Category> categoryList = categoryRepository.findAll();
        assertThat(categoryList).hasSize(databaseSizeBeforeUpdate);
        Category testCategory = categoryList.get(categoryList.size() - 1);
        assertThat(testCategory.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCategory.getParentId()).isEqualTo(UPDATED_PARENT_ID);
        assertThat(testCategory.isIsParent()).isEqualTo(UPDATED_IS_PARENT);
        assertThat(testCategory.getSort()).isEqualTo(UPDATED_SORT);
        assertThat(testCategory.getTarget()).isEqualTo(UPDATED_TARGET);
        assertThat(testCategory.getTargetType()).isEqualTo(UPDATED_TARGET_TYPE);
        assertThat(testCategory.getCreatedTime()).isEqualTo(UPDATED_CREATED_TIME);
        assertThat(testCategory.getUpdatedTime()).isEqualTo(UPDATED_UPDATED_TIME);
        assertThat(testCategory.isDeleted()).isEqualTo(UPDATED_DELETED);
        assertThat(testCategory.getCategoryGrade()).isEqualTo(UPDATED_CATEGORY_GRADE);
    }

    @Test
    @Transactional
    public void updateNonExistingCategory() throws Exception {
        int databaseSizeBeforeUpdate = categoryRepository.findAll().size();

        // Create the Category
        CategoryDTO categoryDTO = categoryMapper.toDto(category);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restCategoryMockMvc.perform(put("/api/categories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(categoryDTO)))
            .andExpect(status().isCreated());

        // Validate the Category in the database
        List<Category> categoryList = categoryRepository.findAll();
        assertThat(categoryList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteCategory() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);
        int databaseSizeBeforeDelete = categoryRepository.findAll().size();

        // Get the category
        restCategoryMockMvc.perform(delete("/api/categories/{id}", category.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Category> categoryList = categoryRepository.findAll();
        assertThat(categoryList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Category.class);
        Category category1 = new Category();
        category1.setId(1L);
        Category category2 = new Category();
        category2.setId(category1.getId());
        assertThat(category1).isEqualTo(category2);
        category2.setId(2L);
        assertThat(category1).isNotEqualTo(category2);
        category1.setId(null);
        assertThat(category1).isNotEqualTo(category2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CategoryDTO.class);
        CategoryDTO categoryDTO1 = new CategoryDTO();
        categoryDTO1.setId(1L);
        CategoryDTO categoryDTO2 = new CategoryDTO();
        assertThat(categoryDTO1).isNotEqualTo(categoryDTO2);
        categoryDTO2.setId(categoryDTO1.getId());
        assertThat(categoryDTO1).isEqualTo(categoryDTO2);
        categoryDTO2.setId(2L);
        assertThat(categoryDTO1).isNotEqualTo(categoryDTO2);
        categoryDTO1.setId(null);
        assertThat(categoryDTO1).isNotEqualTo(categoryDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(categoryMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(categoryMapper.fromId(null)).isNull();
    }
}
