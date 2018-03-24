package com.eyun.product.web.rest;

import com.eyun.product.ProductApp;

import com.eyun.product.config.SecurityBeanOverrideConfiguration;

import com.eyun.product.domain.Brand;
import com.eyun.product.repository.BrandRepository;
import com.eyun.product.service.BrandService;
import com.eyun.product.service.dto.BrandDTO;
import com.eyun.product.service.mapper.BrandMapper;
import com.eyun.product.web.rest.errors.ExceptionTranslator;
import com.eyun.product.service.dto.BrandCriteria;
import com.eyun.product.service.BrandQueryService;

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
 * Test class for the BrandResource REST controller.
 *
 * @see BrandResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ProductApp.class, SecurityBeanOverrideConfiguration.class})
public class BrandResourceIntTest {

    private static final Long DEFAULT_CATEGORY_ID = 1L;
    private static final Long UPDATED_CATEGORY_ID = 2L;

    private static final String DEFAULT_BRAND_NAME = "AAAAAAAAAA";
    private static final String UPDATED_BRAND_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_BRAND_ALIAS = "AAAAAAAAAA";
    private static final String UPDATED_BRAND_ALIAS = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_LOGO = "AAAAAAAAAA";
    private static final String UPDATED_LOGO = "BBBBBBBBBB";

    private static final Integer DEFAULT_STATUS = 1;
    private static final Integer UPDATED_STATUS = 2;

    private static final Instant DEFAULT_CREATED_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Integer DEFAULT_DELETED = 1;
    private static final Integer UPDATED_DELETED = 2;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private BrandMapper brandMapper;

    @Autowired
    private BrandService brandService;

    @Autowired
    private BrandQueryService brandQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restBrandMockMvc;

    private Brand brand;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final BrandResource brandResource = new BrandResource(brandService, brandQueryService);
        this.restBrandMockMvc = MockMvcBuilders.standaloneSetup(brandResource)
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
    public static Brand createEntity(EntityManager em) {
        Brand brand = new Brand()
            .categoryId(DEFAULT_CATEGORY_ID)
            .brandName(DEFAULT_BRAND_NAME)
            .brandAlias(DEFAULT_BRAND_ALIAS)
            .description(DEFAULT_DESCRIPTION)
            .logo(DEFAULT_LOGO)
            .status(DEFAULT_STATUS)
            .createdTime(DEFAULT_CREATED_TIME)
            .updatedTime(DEFAULT_UPDATED_TIME)
            .deleted(DEFAULT_DELETED);
        return brand;
    }

    @Before
    public void initTest() {
        brand = createEntity(em);
    }

    @Test
    @Transactional
    public void createBrand() throws Exception {
        int databaseSizeBeforeCreate = brandRepository.findAll().size();

        // Create the Brand
        BrandDTO brandDTO = brandMapper.toDto(brand);
        restBrandMockMvc.perform(post("/api/brands")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(brandDTO)))
            .andExpect(status().isCreated());

        // Validate the Brand in the database
        List<Brand> brandList = brandRepository.findAll();
        assertThat(brandList).hasSize(databaseSizeBeforeCreate + 1);
        Brand testBrand = brandList.get(brandList.size() - 1);
        assertThat(testBrand.getCategoryId()).isEqualTo(DEFAULT_CATEGORY_ID);
        assertThat(testBrand.getBrandName()).isEqualTo(DEFAULT_BRAND_NAME);
        assertThat(testBrand.getBrandAlias()).isEqualTo(DEFAULT_BRAND_ALIAS);
        assertThat(testBrand.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testBrand.getLogo()).isEqualTo(DEFAULT_LOGO);
        assertThat(testBrand.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testBrand.getCreatedTime()).isEqualTo(DEFAULT_CREATED_TIME);
        assertThat(testBrand.getUpdatedTime()).isEqualTo(DEFAULT_UPDATED_TIME);
        assertThat(testBrand.getDeleted()).isEqualTo(DEFAULT_DELETED);
    }

    @Test
    @Transactional
    public void createBrandWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = brandRepository.findAll().size();

        // Create the Brand with an existing ID
        brand.setId(1L);
        BrandDTO brandDTO = brandMapper.toDto(brand);

        // An entity with an existing ID cannot be created, so this API call must fail
        restBrandMockMvc.perform(post("/api/brands")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(brandDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Brand in the database
        List<Brand> brandList = brandRepository.findAll();
        assertThat(brandList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllBrands() throws Exception {
        // Initialize the database
        brandRepository.saveAndFlush(brand);

        // Get all the brandList
        restBrandMockMvc.perform(get("/api/brands?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(brand.getId().intValue())))
            .andExpect(jsonPath("$.[*].categoryId").value(hasItem(DEFAULT_CATEGORY_ID.intValue())))
            .andExpect(jsonPath("$.[*].brandName").value(hasItem(DEFAULT_BRAND_NAME.toString())))
            .andExpect(jsonPath("$.[*].brandAlias").value(hasItem(DEFAULT_BRAND_ALIAS.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].logo").value(hasItem(DEFAULT_LOGO.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
            .andExpect(jsonPath("$.[*].createdTime").value(hasItem(DEFAULT_CREATED_TIME.toString())))
            .andExpect(jsonPath("$.[*].updatedTime").value(hasItem(DEFAULT_UPDATED_TIME.toString())))
            .andExpect(jsonPath("$.[*].deleted").value(hasItem(DEFAULT_DELETED)));
    }

    @Test
    @Transactional
    public void getBrand() throws Exception {
        // Initialize the database
        brandRepository.saveAndFlush(brand);

        // Get the brand
        restBrandMockMvc.perform(get("/api/brands/{id}", brand.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(brand.getId().intValue()))
            .andExpect(jsonPath("$.categoryId").value(DEFAULT_CATEGORY_ID.intValue()))
            .andExpect(jsonPath("$.brandName").value(DEFAULT_BRAND_NAME.toString()))
            .andExpect(jsonPath("$.brandAlias").value(DEFAULT_BRAND_ALIAS.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.logo").value(DEFAULT_LOGO.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS))
            .andExpect(jsonPath("$.createdTime").value(DEFAULT_CREATED_TIME.toString()))
            .andExpect(jsonPath("$.updatedTime").value(DEFAULT_UPDATED_TIME.toString()))
            .andExpect(jsonPath("$.deleted").value(DEFAULT_DELETED));
    }

    @Test
    @Transactional
    public void getAllBrandsByCategoryIdIsEqualToSomething() throws Exception {
        // Initialize the database
        brandRepository.saveAndFlush(brand);

        // Get all the brandList where categoryId equals to DEFAULT_CATEGORY_ID
        defaultBrandShouldBeFound("categoryId.equals=" + DEFAULT_CATEGORY_ID);

        // Get all the brandList where categoryId equals to UPDATED_CATEGORY_ID
        defaultBrandShouldNotBeFound("categoryId.equals=" + UPDATED_CATEGORY_ID);
    }

    @Test
    @Transactional
    public void getAllBrandsByCategoryIdIsInShouldWork() throws Exception {
        // Initialize the database
        brandRepository.saveAndFlush(brand);

        // Get all the brandList where categoryId in DEFAULT_CATEGORY_ID or UPDATED_CATEGORY_ID
        defaultBrandShouldBeFound("categoryId.in=" + DEFAULT_CATEGORY_ID + "," + UPDATED_CATEGORY_ID);

        // Get all the brandList where categoryId equals to UPDATED_CATEGORY_ID
        defaultBrandShouldNotBeFound("categoryId.in=" + UPDATED_CATEGORY_ID);
    }

    @Test
    @Transactional
    public void getAllBrandsByCategoryIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        brandRepository.saveAndFlush(brand);

        // Get all the brandList where categoryId is not null
        defaultBrandShouldBeFound("categoryId.specified=true");

        // Get all the brandList where categoryId is null
        defaultBrandShouldNotBeFound("categoryId.specified=false");
    }

    @Test
    @Transactional
    public void getAllBrandsByCategoryIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        brandRepository.saveAndFlush(brand);

        // Get all the brandList where categoryId greater than or equals to DEFAULT_CATEGORY_ID
        defaultBrandShouldBeFound("categoryId.greaterOrEqualThan=" + DEFAULT_CATEGORY_ID);

        // Get all the brandList where categoryId greater than or equals to UPDATED_CATEGORY_ID
        defaultBrandShouldNotBeFound("categoryId.greaterOrEqualThan=" + UPDATED_CATEGORY_ID);
    }

    @Test
    @Transactional
    public void getAllBrandsByCategoryIdIsLessThanSomething() throws Exception {
        // Initialize the database
        brandRepository.saveAndFlush(brand);

        // Get all the brandList where categoryId less than or equals to DEFAULT_CATEGORY_ID
        defaultBrandShouldNotBeFound("categoryId.lessThan=" + DEFAULT_CATEGORY_ID);

        // Get all the brandList where categoryId less than or equals to UPDATED_CATEGORY_ID
        defaultBrandShouldBeFound("categoryId.lessThan=" + UPDATED_CATEGORY_ID);
    }


    @Test
    @Transactional
    public void getAllBrandsByBrandNameIsEqualToSomething() throws Exception {
        // Initialize the database
        brandRepository.saveAndFlush(brand);

        // Get all the brandList where brandName equals to DEFAULT_BRAND_NAME
        defaultBrandShouldBeFound("brandName.equals=" + DEFAULT_BRAND_NAME);

        // Get all the brandList where brandName equals to UPDATED_BRAND_NAME
        defaultBrandShouldNotBeFound("brandName.equals=" + UPDATED_BRAND_NAME);
    }

    @Test
    @Transactional
    public void getAllBrandsByBrandNameIsInShouldWork() throws Exception {
        // Initialize the database
        brandRepository.saveAndFlush(brand);

        // Get all the brandList where brandName in DEFAULT_BRAND_NAME or UPDATED_BRAND_NAME
        defaultBrandShouldBeFound("brandName.in=" + DEFAULT_BRAND_NAME + "," + UPDATED_BRAND_NAME);

        // Get all the brandList where brandName equals to UPDATED_BRAND_NAME
        defaultBrandShouldNotBeFound("brandName.in=" + UPDATED_BRAND_NAME);
    }

    @Test
    @Transactional
    public void getAllBrandsByBrandNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        brandRepository.saveAndFlush(brand);

        // Get all the brandList where brandName is not null
        defaultBrandShouldBeFound("brandName.specified=true");

        // Get all the brandList where brandName is null
        defaultBrandShouldNotBeFound("brandName.specified=false");
    }

    @Test
    @Transactional
    public void getAllBrandsByBrandAliasIsEqualToSomething() throws Exception {
        // Initialize the database
        brandRepository.saveAndFlush(brand);

        // Get all the brandList where brandAlias equals to DEFAULT_BRAND_ALIAS
        defaultBrandShouldBeFound("brandAlias.equals=" + DEFAULT_BRAND_ALIAS);

        // Get all the brandList where brandAlias equals to UPDATED_BRAND_ALIAS
        defaultBrandShouldNotBeFound("brandAlias.equals=" + UPDATED_BRAND_ALIAS);
    }

    @Test
    @Transactional
    public void getAllBrandsByBrandAliasIsInShouldWork() throws Exception {
        // Initialize the database
        brandRepository.saveAndFlush(brand);

        // Get all the brandList where brandAlias in DEFAULT_BRAND_ALIAS or UPDATED_BRAND_ALIAS
        defaultBrandShouldBeFound("brandAlias.in=" + DEFAULT_BRAND_ALIAS + "," + UPDATED_BRAND_ALIAS);

        // Get all the brandList where brandAlias equals to UPDATED_BRAND_ALIAS
        defaultBrandShouldNotBeFound("brandAlias.in=" + UPDATED_BRAND_ALIAS);
    }

    @Test
    @Transactional
    public void getAllBrandsByBrandAliasIsNullOrNotNull() throws Exception {
        // Initialize the database
        brandRepository.saveAndFlush(brand);

        // Get all the brandList where brandAlias is not null
        defaultBrandShouldBeFound("brandAlias.specified=true");

        // Get all the brandList where brandAlias is null
        defaultBrandShouldNotBeFound("brandAlias.specified=false");
    }

    @Test
    @Transactional
    public void getAllBrandsByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        brandRepository.saveAndFlush(brand);

        // Get all the brandList where description equals to DEFAULT_DESCRIPTION
        defaultBrandShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the brandList where description equals to UPDATED_DESCRIPTION
        defaultBrandShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllBrandsByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        brandRepository.saveAndFlush(brand);

        // Get all the brandList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultBrandShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the brandList where description equals to UPDATED_DESCRIPTION
        defaultBrandShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllBrandsByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        brandRepository.saveAndFlush(brand);

        // Get all the brandList where description is not null
        defaultBrandShouldBeFound("description.specified=true");

        // Get all the brandList where description is null
        defaultBrandShouldNotBeFound("description.specified=false");
    }

    @Test
    @Transactional
    public void getAllBrandsByLogoIsEqualToSomething() throws Exception {
        // Initialize the database
        brandRepository.saveAndFlush(brand);

        // Get all the brandList where logo equals to DEFAULT_LOGO
        defaultBrandShouldBeFound("logo.equals=" + DEFAULT_LOGO);

        // Get all the brandList where logo equals to UPDATED_LOGO
        defaultBrandShouldNotBeFound("logo.equals=" + UPDATED_LOGO);
    }

    @Test
    @Transactional
    public void getAllBrandsByLogoIsInShouldWork() throws Exception {
        // Initialize the database
        brandRepository.saveAndFlush(brand);

        // Get all the brandList where logo in DEFAULT_LOGO or UPDATED_LOGO
        defaultBrandShouldBeFound("logo.in=" + DEFAULT_LOGO + "," + UPDATED_LOGO);

        // Get all the brandList where logo equals to UPDATED_LOGO
        defaultBrandShouldNotBeFound("logo.in=" + UPDATED_LOGO);
    }

    @Test
    @Transactional
    public void getAllBrandsByLogoIsNullOrNotNull() throws Exception {
        // Initialize the database
        brandRepository.saveAndFlush(brand);

        // Get all the brandList where logo is not null
        defaultBrandShouldBeFound("logo.specified=true");

        // Get all the brandList where logo is null
        defaultBrandShouldNotBeFound("logo.specified=false");
    }

    @Test
    @Transactional
    public void getAllBrandsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        brandRepository.saveAndFlush(brand);

        // Get all the brandList where status equals to DEFAULT_STATUS
        defaultBrandShouldBeFound("status.equals=" + DEFAULT_STATUS);

        // Get all the brandList where status equals to UPDATED_STATUS
        defaultBrandShouldNotBeFound("status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void getAllBrandsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        brandRepository.saveAndFlush(brand);

        // Get all the brandList where status in DEFAULT_STATUS or UPDATED_STATUS
        defaultBrandShouldBeFound("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS);

        // Get all the brandList where status equals to UPDATED_STATUS
        defaultBrandShouldNotBeFound("status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void getAllBrandsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        brandRepository.saveAndFlush(brand);

        // Get all the brandList where status is not null
        defaultBrandShouldBeFound("status.specified=true");

        // Get all the brandList where status is null
        defaultBrandShouldNotBeFound("status.specified=false");
    }

    @Test
    @Transactional
    public void getAllBrandsByStatusIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        brandRepository.saveAndFlush(brand);

        // Get all the brandList where status greater than or equals to DEFAULT_STATUS
        defaultBrandShouldBeFound("status.greaterOrEqualThan=" + DEFAULT_STATUS);

        // Get all the brandList where status greater than or equals to UPDATED_STATUS
        defaultBrandShouldNotBeFound("status.greaterOrEqualThan=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void getAllBrandsByStatusIsLessThanSomething() throws Exception {
        // Initialize the database
        brandRepository.saveAndFlush(brand);

        // Get all the brandList where status less than or equals to DEFAULT_STATUS
        defaultBrandShouldNotBeFound("status.lessThan=" + DEFAULT_STATUS);

        // Get all the brandList where status less than or equals to UPDATED_STATUS
        defaultBrandShouldBeFound("status.lessThan=" + UPDATED_STATUS);
    }


    @Test
    @Transactional
    public void getAllBrandsByCreatedTimeIsEqualToSomething() throws Exception {
        // Initialize the database
        brandRepository.saveAndFlush(brand);

        // Get all the brandList where createdTime equals to DEFAULT_CREATED_TIME
        defaultBrandShouldBeFound("createdTime.equals=" + DEFAULT_CREATED_TIME);

        // Get all the brandList where createdTime equals to UPDATED_CREATED_TIME
        defaultBrandShouldNotBeFound("createdTime.equals=" + UPDATED_CREATED_TIME);
    }

    @Test
    @Transactional
    public void getAllBrandsByCreatedTimeIsInShouldWork() throws Exception {
        // Initialize the database
        brandRepository.saveAndFlush(brand);

        // Get all the brandList where createdTime in DEFAULT_CREATED_TIME or UPDATED_CREATED_TIME
        defaultBrandShouldBeFound("createdTime.in=" + DEFAULT_CREATED_TIME + "," + UPDATED_CREATED_TIME);

        // Get all the brandList where createdTime equals to UPDATED_CREATED_TIME
        defaultBrandShouldNotBeFound("createdTime.in=" + UPDATED_CREATED_TIME);
    }

    @Test
    @Transactional
    public void getAllBrandsByCreatedTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        brandRepository.saveAndFlush(brand);

        // Get all the brandList where createdTime is not null
        defaultBrandShouldBeFound("createdTime.specified=true");

        // Get all the brandList where createdTime is null
        defaultBrandShouldNotBeFound("createdTime.specified=false");
    }

    @Test
    @Transactional
    public void getAllBrandsByUpdatedTimeIsEqualToSomething() throws Exception {
        // Initialize the database
        brandRepository.saveAndFlush(brand);

        // Get all the brandList where updatedTime equals to DEFAULT_UPDATED_TIME
        defaultBrandShouldBeFound("updatedTime.equals=" + DEFAULT_UPDATED_TIME);

        // Get all the brandList where updatedTime equals to UPDATED_UPDATED_TIME
        defaultBrandShouldNotBeFound("updatedTime.equals=" + UPDATED_UPDATED_TIME);
    }

    @Test
    @Transactional
    public void getAllBrandsByUpdatedTimeIsInShouldWork() throws Exception {
        // Initialize the database
        brandRepository.saveAndFlush(brand);

        // Get all the brandList where updatedTime in DEFAULT_UPDATED_TIME or UPDATED_UPDATED_TIME
        defaultBrandShouldBeFound("updatedTime.in=" + DEFAULT_UPDATED_TIME + "," + UPDATED_UPDATED_TIME);

        // Get all the brandList where updatedTime equals to UPDATED_UPDATED_TIME
        defaultBrandShouldNotBeFound("updatedTime.in=" + UPDATED_UPDATED_TIME);
    }

    @Test
    @Transactional
    public void getAllBrandsByUpdatedTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        brandRepository.saveAndFlush(brand);

        // Get all the brandList where updatedTime is not null
        defaultBrandShouldBeFound("updatedTime.specified=true");

        // Get all the brandList where updatedTime is null
        defaultBrandShouldNotBeFound("updatedTime.specified=false");
    }

    @Test
    @Transactional
    public void getAllBrandsByDeletedIsEqualToSomething() throws Exception {
        // Initialize the database
        brandRepository.saveAndFlush(brand);

        // Get all the brandList where deleted equals to DEFAULT_DELETED
        defaultBrandShouldBeFound("deleted.equals=" + DEFAULT_DELETED);

        // Get all the brandList where deleted equals to UPDATED_DELETED
        defaultBrandShouldNotBeFound("deleted.equals=" + UPDATED_DELETED);
    }

    @Test
    @Transactional
    public void getAllBrandsByDeletedIsInShouldWork() throws Exception {
        // Initialize the database
        brandRepository.saveAndFlush(brand);

        // Get all the brandList where deleted in DEFAULT_DELETED or UPDATED_DELETED
        defaultBrandShouldBeFound("deleted.in=" + DEFAULT_DELETED + "," + UPDATED_DELETED);

        // Get all the brandList where deleted equals to UPDATED_DELETED
        defaultBrandShouldNotBeFound("deleted.in=" + UPDATED_DELETED);
    }

    @Test
    @Transactional
    public void getAllBrandsByDeletedIsNullOrNotNull() throws Exception {
        // Initialize the database
        brandRepository.saveAndFlush(brand);

        // Get all the brandList where deleted is not null
        defaultBrandShouldBeFound("deleted.specified=true");

        // Get all the brandList where deleted is null
        defaultBrandShouldNotBeFound("deleted.specified=false");
    }

    @Test
    @Transactional
    public void getAllBrandsByDeletedIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        brandRepository.saveAndFlush(brand);

        // Get all the brandList where deleted greater than or equals to DEFAULT_DELETED
        defaultBrandShouldBeFound("deleted.greaterOrEqualThan=" + DEFAULT_DELETED);

        // Get all the brandList where deleted greater than or equals to UPDATED_DELETED
        defaultBrandShouldNotBeFound("deleted.greaterOrEqualThan=" + UPDATED_DELETED);
    }

    @Test
    @Transactional
    public void getAllBrandsByDeletedIsLessThanSomething() throws Exception {
        // Initialize the database
        brandRepository.saveAndFlush(brand);

        // Get all the brandList where deleted less than or equals to DEFAULT_DELETED
        defaultBrandShouldNotBeFound("deleted.lessThan=" + DEFAULT_DELETED);

        // Get all the brandList where deleted less than or equals to UPDATED_DELETED
        defaultBrandShouldBeFound("deleted.lessThan=" + UPDATED_DELETED);
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultBrandShouldBeFound(String filter) throws Exception {
        restBrandMockMvc.perform(get("/api/brands?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(brand.getId().intValue())))
            .andExpect(jsonPath("$.[*].categoryId").value(hasItem(DEFAULT_CATEGORY_ID.intValue())))
            .andExpect(jsonPath("$.[*].brandName").value(hasItem(DEFAULT_BRAND_NAME.toString())))
            .andExpect(jsonPath("$.[*].brandAlias").value(hasItem(DEFAULT_BRAND_ALIAS.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].logo").value(hasItem(DEFAULT_LOGO.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
            .andExpect(jsonPath("$.[*].createdTime").value(hasItem(DEFAULT_CREATED_TIME.toString())))
            .andExpect(jsonPath("$.[*].updatedTime").value(hasItem(DEFAULT_UPDATED_TIME.toString())))
            .andExpect(jsonPath("$.[*].deleted").value(hasItem(DEFAULT_DELETED)));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultBrandShouldNotBeFound(String filter) throws Exception {
        restBrandMockMvc.perform(get("/api/brands?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }


    @Test
    @Transactional
    public void getNonExistingBrand() throws Exception {
        // Get the brand
        restBrandMockMvc.perform(get("/api/brands/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateBrand() throws Exception {
        // Initialize the database
        brandRepository.saveAndFlush(brand);
        int databaseSizeBeforeUpdate = brandRepository.findAll().size();

        // Update the brand
        Brand updatedBrand = brandRepository.findOne(brand.getId());
        // Disconnect from session so that the updates on updatedBrand are not directly saved in db
        em.detach(updatedBrand);
        updatedBrand
            .categoryId(UPDATED_CATEGORY_ID)
            .brandName(UPDATED_BRAND_NAME)
            .brandAlias(UPDATED_BRAND_ALIAS)
            .description(UPDATED_DESCRIPTION)
            .logo(UPDATED_LOGO)
            .status(UPDATED_STATUS)
            .createdTime(UPDATED_CREATED_TIME)
            .updatedTime(UPDATED_UPDATED_TIME)
            .deleted(UPDATED_DELETED);
        BrandDTO brandDTO = brandMapper.toDto(updatedBrand);

        restBrandMockMvc.perform(put("/api/brands")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(brandDTO)))
            .andExpect(status().isOk());

        // Validate the Brand in the database
        List<Brand> brandList = brandRepository.findAll();
        assertThat(brandList).hasSize(databaseSizeBeforeUpdate);
        Brand testBrand = brandList.get(brandList.size() - 1);
        assertThat(testBrand.getCategoryId()).isEqualTo(UPDATED_CATEGORY_ID);
        assertThat(testBrand.getBrandName()).isEqualTo(UPDATED_BRAND_NAME);
        assertThat(testBrand.getBrandAlias()).isEqualTo(UPDATED_BRAND_ALIAS);
        assertThat(testBrand.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testBrand.getLogo()).isEqualTo(UPDATED_LOGO);
        assertThat(testBrand.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testBrand.getCreatedTime()).isEqualTo(UPDATED_CREATED_TIME);
        assertThat(testBrand.getUpdatedTime()).isEqualTo(UPDATED_UPDATED_TIME);
        assertThat(testBrand.getDeleted()).isEqualTo(UPDATED_DELETED);
    }

    @Test
    @Transactional
    public void updateNonExistingBrand() throws Exception {
        int databaseSizeBeforeUpdate = brandRepository.findAll().size();

        // Create the Brand
        BrandDTO brandDTO = brandMapper.toDto(brand);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restBrandMockMvc.perform(put("/api/brands")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(brandDTO)))
            .andExpect(status().isCreated());

        // Validate the Brand in the database
        List<Brand> brandList = brandRepository.findAll();
        assertThat(brandList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteBrand() throws Exception {
        // Initialize the database
        brandRepository.saveAndFlush(brand);
        int databaseSizeBeforeDelete = brandRepository.findAll().size();

        // Get the brand
        restBrandMockMvc.perform(delete("/api/brands/{id}", brand.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Brand> brandList = brandRepository.findAll();
        assertThat(brandList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Brand.class);
        Brand brand1 = new Brand();
        brand1.setId(1L);
        Brand brand2 = new Brand();
        brand2.setId(brand1.getId());
        assertThat(brand1).isEqualTo(brand2);
        brand2.setId(2L);
        assertThat(brand1).isNotEqualTo(brand2);
        brand1.setId(null);
        assertThat(brand1).isNotEqualTo(brand2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(BrandDTO.class);
        BrandDTO brandDTO1 = new BrandDTO();
        brandDTO1.setId(1L);
        BrandDTO brandDTO2 = new BrandDTO();
        assertThat(brandDTO1).isNotEqualTo(brandDTO2);
        brandDTO2.setId(brandDTO1.getId());
        assertThat(brandDTO1).isEqualTo(brandDTO2);
        brandDTO2.setId(2L);
        assertThat(brandDTO1).isNotEqualTo(brandDTO2);
        brandDTO1.setId(null);
        assertThat(brandDTO1).isNotEqualTo(brandDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(brandMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(brandMapper.fromId(null)).isNull();
    }
}
