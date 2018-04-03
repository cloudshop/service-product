package com.eyun.product.web.rest;

import com.eyun.product.ProductApp;

import com.eyun.product.config.SecurityBeanOverrideConfiguration;

import com.eyun.product.domain.ProductImg;
import com.eyun.product.repository.ProductImgRepository;
import com.eyun.product.service.ProductImgService;
import com.eyun.product.service.dto.ProductImgDTO;
import com.eyun.product.service.mapper.ProductImgMapper;
import com.eyun.product.web.rest.errors.ExceptionTranslator;
import com.eyun.product.service.dto.ProductImgCriteria;
import com.eyun.product.service.ProductImgQueryService;

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
 * Test class for the ProductImgResource REST controller.
 *
 * @see ProductImgResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ProductApp.class, SecurityBeanOverrideConfiguration.class})
public class ProductImgResourceIntTest {

    private static final Long DEFAULT_PRODUCT_ID = 1L;
    private static final Long UPDATED_PRODUCT_ID = 2L;

    private static final String DEFAULT_IMG_URL = "AAAAAAAAAA";
    private static final String UPDATED_IMG_URL = "BBBBBBBBBB";

    private static final Integer DEFAULT_TYPE = 1;
    private static final Integer UPDATED_TYPE = 2;

    private static final Integer DEFAULT_RANK = 1;
    private static final Integer UPDATED_RANK = 2;

    private static final Instant DEFAULT_CREATED_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Boolean DEFAULT_DELETED = false;
    private static final Boolean UPDATED_DELETED = true;

    @Autowired
    private ProductImgRepository productImgRepository;

    @Autowired
    private ProductImgMapper productImgMapper;

    @Autowired
    private ProductImgService productImgService;

    @Autowired
    private ProductImgQueryService productImgQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restProductImgMockMvc;

    private ProductImg productImg;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ProductImgResource productImgResource = new ProductImgResource(productImgService, productImgQueryService);
        this.restProductImgMockMvc = MockMvcBuilders.standaloneSetup(productImgResource)
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
    public static ProductImg createEntity(EntityManager em) {
        ProductImg productImg = new ProductImg()
            .productId(DEFAULT_PRODUCT_ID)
            .imgUrl(DEFAULT_IMG_URL)
            .type(DEFAULT_TYPE)
            .rank(DEFAULT_RANK)
            .createdTime(DEFAULT_CREATED_TIME)
            .updatedTime(DEFAULT_UPDATED_TIME)
            .deleted(DEFAULT_DELETED);
        return productImg;
    }

    @Before
    public void initTest() {
        productImg = createEntity(em);
    }

    @Test
    @Transactional
    public void createProductImg() throws Exception {
        int databaseSizeBeforeCreate = productImgRepository.findAll().size();

        // Create the ProductImg
        ProductImgDTO productImgDTO = productImgMapper.toDto(productImg);
        restProductImgMockMvc.perform(post("/api/product-imgs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(productImgDTO)))
            .andExpect(status().isCreated());

        // Validate the ProductImg in the database
        List<ProductImg> productImgList = productImgRepository.findAll();
        assertThat(productImgList).hasSize(databaseSizeBeforeCreate + 1);
        ProductImg testProductImg = productImgList.get(productImgList.size() - 1);
        assertThat(testProductImg.getProductId()).isEqualTo(DEFAULT_PRODUCT_ID);
        assertThat(testProductImg.getImgUrl()).isEqualTo(DEFAULT_IMG_URL);
        assertThat(testProductImg.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testProductImg.getRank()).isEqualTo(DEFAULT_RANK);
        assertThat(testProductImg.getCreatedTime()).isEqualTo(DEFAULT_CREATED_TIME);
        assertThat(testProductImg.getUpdatedTime()).isEqualTo(DEFAULT_UPDATED_TIME);
        assertThat(testProductImg.isDeleted()).isEqualTo(DEFAULT_DELETED);
    }

    @Test
    @Transactional
    public void createProductImgWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = productImgRepository.findAll().size();

        // Create the ProductImg with an existing ID
        productImg.setId(1L);
        ProductImgDTO productImgDTO = productImgMapper.toDto(productImg);

        // An entity with an existing ID cannot be created, so this API call must fail
        restProductImgMockMvc.perform(post("/api/product-imgs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(productImgDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ProductImg in the database
        List<ProductImg> productImgList = productImgRepository.findAll();
        assertThat(productImgList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllProductImgs() throws Exception {
        // Initialize the database
        productImgRepository.saveAndFlush(productImg);

        // Get all the productImgList
        restProductImgMockMvc.perform(get("/api/product-imgs?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(productImg.getId().intValue())))
            .andExpect(jsonPath("$.[*].productId").value(hasItem(DEFAULT_PRODUCT_ID.intValue())))
            .andExpect(jsonPath("$.[*].imgUrl").value(hasItem(DEFAULT_IMG_URL.toString())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
            .andExpect(jsonPath("$.[*].rank").value(hasItem(DEFAULT_RANK)))
            .andExpect(jsonPath("$.[*].createdTime").value(hasItem(DEFAULT_CREATED_TIME.toString())))
            .andExpect(jsonPath("$.[*].updatedTime").value(hasItem(DEFAULT_UPDATED_TIME.toString())))
            .andExpect(jsonPath("$.[*].deleted").value(hasItem(DEFAULT_DELETED.booleanValue())));
    }

    @Test
    @Transactional
    public void getProductImg() throws Exception {
        // Initialize the database
        productImgRepository.saveAndFlush(productImg);

        // Get the productImg
        restProductImgMockMvc.perform(get("/api/product-imgs/{id}", productImg.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(productImg.getId().intValue()))
            .andExpect(jsonPath("$.productId").value(DEFAULT_PRODUCT_ID.intValue()))
            .andExpect(jsonPath("$.imgUrl").value(DEFAULT_IMG_URL.toString()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE))
            .andExpect(jsonPath("$.rank").value(DEFAULT_RANK))
            .andExpect(jsonPath("$.createdTime").value(DEFAULT_CREATED_TIME.toString()))
            .andExpect(jsonPath("$.updatedTime").value(DEFAULT_UPDATED_TIME.toString()))
            .andExpect(jsonPath("$.deleted").value(DEFAULT_DELETED.booleanValue()));
    }

    @Test
    @Transactional
    public void getAllProductImgsByProductIdIsEqualToSomething() throws Exception {
        // Initialize the database
        productImgRepository.saveAndFlush(productImg);

        // Get all the productImgList where productId equals to DEFAULT_PRODUCT_ID
        defaultProductImgShouldBeFound("productId.equals=" + DEFAULT_PRODUCT_ID);

        // Get all the productImgList where productId equals to UPDATED_PRODUCT_ID
        defaultProductImgShouldNotBeFound("productId.equals=" + UPDATED_PRODUCT_ID);
    }

    @Test
    @Transactional
    public void getAllProductImgsByProductIdIsInShouldWork() throws Exception {
        // Initialize the database
        productImgRepository.saveAndFlush(productImg);

        // Get all the productImgList where productId in DEFAULT_PRODUCT_ID or UPDATED_PRODUCT_ID
        defaultProductImgShouldBeFound("productId.in=" + DEFAULT_PRODUCT_ID + "," + UPDATED_PRODUCT_ID);

        // Get all the productImgList where productId equals to UPDATED_PRODUCT_ID
        defaultProductImgShouldNotBeFound("productId.in=" + UPDATED_PRODUCT_ID);
    }

    @Test
    @Transactional
    public void getAllProductImgsByProductIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        productImgRepository.saveAndFlush(productImg);

        // Get all the productImgList where productId is not null
        defaultProductImgShouldBeFound("productId.specified=true");

        // Get all the productImgList where productId is null
        defaultProductImgShouldNotBeFound("productId.specified=false");
    }

    @Test
    @Transactional
    public void getAllProductImgsByProductIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        productImgRepository.saveAndFlush(productImg);

        // Get all the productImgList where productId greater than or equals to DEFAULT_PRODUCT_ID
        defaultProductImgShouldBeFound("productId.greaterOrEqualThan=" + DEFAULT_PRODUCT_ID);

        // Get all the productImgList where productId greater than or equals to UPDATED_PRODUCT_ID
        defaultProductImgShouldNotBeFound("productId.greaterOrEqualThan=" + UPDATED_PRODUCT_ID);
    }

    @Test
    @Transactional
    public void getAllProductImgsByProductIdIsLessThanSomething() throws Exception {
        // Initialize the database
        productImgRepository.saveAndFlush(productImg);

        // Get all the productImgList where productId less than or equals to DEFAULT_PRODUCT_ID
        defaultProductImgShouldNotBeFound("productId.lessThan=" + DEFAULT_PRODUCT_ID);

        // Get all the productImgList where productId less than or equals to UPDATED_PRODUCT_ID
        defaultProductImgShouldBeFound("productId.lessThan=" + UPDATED_PRODUCT_ID);
    }


    @Test
    @Transactional
    public void getAllProductImgsByImgUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        productImgRepository.saveAndFlush(productImg);

        // Get all the productImgList where imgUrl equals to DEFAULT_IMG_URL
        defaultProductImgShouldBeFound("imgUrl.equals=" + DEFAULT_IMG_URL);

        // Get all the productImgList where imgUrl equals to UPDATED_IMG_URL
        defaultProductImgShouldNotBeFound("imgUrl.equals=" + UPDATED_IMG_URL);
    }

    @Test
    @Transactional
    public void getAllProductImgsByImgUrlIsInShouldWork() throws Exception {
        // Initialize the database
        productImgRepository.saveAndFlush(productImg);

        // Get all the productImgList where imgUrl in DEFAULT_IMG_URL or UPDATED_IMG_URL
        defaultProductImgShouldBeFound("imgUrl.in=" + DEFAULT_IMG_URL + "," + UPDATED_IMG_URL);

        // Get all the productImgList where imgUrl equals to UPDATED_IMG_URL
        defaultProductImgShouldNotBeFound("imgUrl.in=" + UPDATED_IMG_URL);
    }

    @Test
    @Transactional
    public void getAllProductImgsByImgUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        productImgRepository.saveAndFlush(productImg);

        // Get all the productImgList where imgUrl is not null
        defaultProductImgShouldBeFound("imgUrl.specified=true");

        // Get all the productImgList where imgUrl is null
        defaultProductImgShouldNotBeFound("imgUrl.specified=false");
    }

    @Test
    @Transactional
    public void getAllProductImgsByTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        productImgRepository.saveAndFlush(productImg);

        // Get all the productImgList where type equals to DEFAULT_TYPE
        defaultProductImgShouldBeFound("type.equals=" + DEFAULT_TYPE);

        // Get all the productImgList where type equals to UPDATED_TYPE
        defaultProductImgShouldNotBeFound("type.equals=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    public void getAllProductImgsByTypeIsInShouldWork() throws Exception {
        // Initialize the database
        productImgRepository.saveAndFlush(productImg);

        // Get all the productImgList where type in DEFAULT_TYPE or UPDATED_TYPE
        defaultProductImgShouldBeFound("type.in=" + DEFAULT_TYPE + "," + UPDATED_TYPE);

        // Get all the productImgList where type equals to UPDATED_TYPE
        defaultProductImgShouldNotBeFound("type.in=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    public void getAllProductImgsByTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        productImgRepository.saveAndFlush(productImg);

        // Get all the productImgList where type is not null
        defaultProductImgShouldBeFound("type.specified=true");

        // Get all the productImgList where type is null
        defaultProductImgShouldNotBeFound("type.specified=false");
    }

    @Test
    @Transactional
    public void getAllProductImgsByTypeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        productImgRepository.saveAndFlush(productImg);

        // Get all the productImgList where type greater than or equals to DEFAULT_TYPE
        defaultProductImgShouldBeFound("type.greaterOrEqualThan=" + DEFAULT_TYPE);

        // Get all the productImgList where type greater than or equals to UPDATED_TYPE
        defaultProductImgShouldNotBeFound("type.greaterOrEqualThan=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    public void getAllProductImgsByTypeIsLessThanSomething() throws Exception {
        // Initialize the database
        productImgRepository.saveAndFlush(productImg);

        // Get all the productImgList where type less than or equals to DEFAULT_TYPE
        defaultProductImgShouldNotBeFound("type.lessThan=" + DEFAULT_TYPE);

        // Get all the productImgList where type less than or equals to UPDATED_TYPE
        defaultProductImgShouldBeFound("type.lessThan=" + UPDATED_TYPE);
    }


    @Test
    @Transactional
    public void getAllProductImgsByRankIsEqualToSomething() throws Exception {
        // Initialize the database
        productImgRepository.saveAndFlush(productImg);

        // Get all the productImgList where rank equals to DEFAULT_RANK
        defaultProductImgShouldBeFound("rank.equals=" + DEFAULT_RANK);

        // Get all the productImgList where rank equals to UPDATED_RANK
        defaultProductImgShouldNotBeFound("rank.equals=" + UPDATED_RANK);
    }

    @Test
    @Transactional
    public void getAllProductImgsByRankIsInShouldWork() throws Exception {
        // Initialize the database
        productImgRepository.saveAndFlush(productImg);

        // Get all the productImgList where rank in DEFAULT_RANK or UPDATED_RANK
        defaultProductImgShouldBeFound("rank.in=" + DEFAULT_RANK + "," + UPDATED_RANK);

        // Get all the productImgList where rank equals to UPDATED_RANK
        defaultProductImgShouldNotBeFound("rank.in=" + UPDATED_RANK);
    }

    @Test
    @Transactional
    public void getAllProductImgsByRankIsNullOrNotNull() throws Exception {
        // Initialize the database
        productImgRepository.saveAndFlush(productImg);

        // Get all the productImgList where rank is not null
        defaultProductImgShouldBeFound("rank.specified=true");

        // Get all the productImgList where rank is null
        defaultProductImgShouldNotBeFound("rank.specified=false");
    }

    @Test
    @Transactional
    public void getAllProductImgsByRankIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        productImgRepository.saveAndFlush(productImg);

        // Get all the productImgList where rank greater than or equals to DEFAULT_RANK
        defaultProductImgShouldBeFound("rank.greaterOrEqualThan=" + DEFAULT_RANK);

        // Get all the productImgList where rank greater than or equals to UPDATED_RANK
        defaultProductImgShouldNotBeFound("rank.greaterOrEqualThan=" + UPDATED_RANK);
    }

    @Test
    @Transactional
    public void getAllProductImgsByRankIsLessThanSomething() throws Exception {
        // Initialize the database
        productImgRepository.saveAndFlush(productImg);

        // Get all the productImgList where rank less than or equals to DEFAULT_RANK
        defaultProductImgShouldNotBeFound("rank.lessThan=" + DEFAULT_RANK);

        // Get all the productImgList where rank less than or equals to UPDATED_RANK
        defaultProductImgShouldBeFound("rank.lessThan=" + UPDATED_RANK);
    }


    @Test
    @Transactional
    public void getAllProductImgsByCreatedTimeIsEqualToSomething() throws Exception {
        // Initialize the database
        productImgRepository.saveAndFlush(productImg);

        // Get all the productImgList where createdTime equals to DEFAULT_CREATED_TIME
        defaultProductImgShouldBeFound("createdTime.equals=" + DEFAULT_CREATED_TIME);

        // Get all the productImgList where createdTime equals to UPDATED_CREATED_TIME
        defaultProductImgShouldNotBeFound("createdTime.equals=" + UPDATED_CREATED_TIME);
    }

    @Test
    @Transactional
    public void getAllProductImgsByCreatedTimeIsInShouldWork() throws Exception {
        // Initialize the database
        productImgRepository.saveAndFlush(productImg);

        // Get all the productImgList where createdTime in DEFAULT_CREATED_TIME or UPDATED_CREATED_TIME
        defaultProductImgShouldBeFound("createdTime.in=" + DEFAULT_CREATED_TIME + "," + UPDATED_CREATED_TIME);

        // Get all the productImgList where createdTime equals to UPDATED_CREATED_TIME
        defaultProductImgShouldNotBeFound("createdTime.in=" + UPDATED_CREATED_TIME);
    }

    @Test
    @Transactional
    public void getAllProductImgsByCreatedTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        productImgRepository.saveAndFlush(productImg);

        // Get all the productImgList where createdTime is not null
        defaultProductImgShouldBeFound("createdTime.specified=true");

        // Get all the productImgList where createdTime is null
        defaultProductImgShouldNotBeFound("createdTime.specified=false");
    }

    @Test
    @Transactional
    public void getAllProductImgsByUpdatedTimeIsEqualToSomething() throws Exception {
        // Initialize the database
        productImgRepository.saveAndFlush(productImg);

        // Get all the productImgList where updatedTime equals to DEFAULT_UPDATED_TIME
        defaultProductImgShouldBeFound("updatedTime.equals=" + DEFAULT_UPDATED_TIME);

        // Get all the productImgList where updatedTime equals to UPDATED_UPDATED_TIME
        defaultProductImgShouldNotBeFound("updatedTime.equals=" + UPDATED_UPDATED_TIME);
    }

    @Test
    @Transactional
    public void getAllProductImgsByUpdatedTimeIsInShouldWork() throws Exception {
        // Initialize the database
        productImgRepository.saveAndFlush(productImg);

        // Get all the productImgList where updatedTime in DEFAULT_UPDATED_TIME or UPDATED_UPDATED_TIME
        defaultProductImgShouldBeFound("updatedTime.in=" + DEFAULT_UPDATED_TIME + "," + UPDATED_UPDATED_TIME);

        // Get all the productImgList where updatedTime equals to UPDATED_UPDATED_TIME
        defaultProductImgShouldNotBeFound("updatedTime.in=" + UPDATED_UPDATED_TIME);
    }

    @Test
    @Transactional
    public void getAllProductImgsByUpdatedTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        productImgRepository.saveAndFlush(productImg);

        // Get all the productImgList where updatedTime is not null
        defaultProductImgShouldBeFound("updatedTime.specified=true");

        // Get all the productImgList where updatedTime is null
        defaultProductImgShouldNotBeFound("updatedTime.specified=false");
    }

    @Test
    @Transactional
    public void getAllProductImgsByDeletedIsEqualToSomething() throws Exception {
        // Initialize the database
        productImgRepository.saveAndFlush(productImg);

        // Get all the productImgList where deleted equals to DEFAULT_DELETED
        defaultProductImgShouldBeFound("deleted.equals=" + DEFAULT_DELETED);

        // Get all the productImgList where deleted equals to UPDATED_DELETED
        defaultProductImgShouldNotBeFound("deleted.equals=" + UPDATED_DELETED);
    }

    @Test
    @Transactional
    public void getAllProductImgsByDeletedIsInShouldWork() throws Exception {
        // Initialize the database
        productImgRepository.saveAndFlush(productImg);

        // Get all the productImgList where deleted in DEFAULT_DELETED or UPDATED_DELETED
        defaultProductImgShouldBeFound("deleted.in=" + DEFAULT_DELETED + "," + UPDATED_DELETED);

        // Get all the productImgList where deleted equals to UPDATED_DELETED
        defaultProductImgShouldNotBeFound("deleted.in=" + UPDATED_DELETED);
    }

    @Test
    @Transactional
    public void getAllProductImgsByDeletedIsNullOrNotNull() throws Exception {
        // Initialize the database
        productImgRepository.saveAndFlush(productImg);

        // Get all the productImgList where deleted is not null
        defaultProductImgShouldBeFound("deleted.specified=true");

        // Get all the productImgList where deleted is null
        defaultProductImgShouldNotBeFound("deleted.specified=false");
    }
    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultProductImgShouldBeFound(String filter) throws Exception {
        restProductImgMockMvc.perform(get("/api/product-imgs?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(productImg.getId().intValue())))
            .andExpect(jsonPath("$.[*].productId").value(hasItem(DEFAULT_PRODUCT_ID.intValue())))
            .andExpect(jsonPath("$.[*].imgUrl").value(hasItem(DEFAULT_IMG_URL.toString())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
            .andExpect(jsonPath("$.[*].rank").value(hasItem(DEFAULT_RANK)))
            .andExpect(jsonPath("$.[*].createdTime").value(hasItem(DEFAULT_CREATED_TIME.toString())))
            .andExpect(jsonPath("$.[*].updatedTime").value(hasItem(DEFAULT_UPDATED_TIME.toString())))
            .andExpect(jsonPath("$.[*].deleted").value(hasItem(DEFAULT_DELETED.booleanValue())));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultProductImgShouldNotBeFound(String filter) throws Exception {
        restProductImgMockMvc.perform(get("/api/product-imgs?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }


    @Test
    @Transactional
    public void getNonExistingProductImg() throws Exception {
        // Get the productImg
        restProductImgMockMvc.perform(get("/api/product-imgs/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateProductImg() throws Exception {
        // Initialize the database
        productImgRepository.saveAndFlush(productImg);
        int databaseSizeBeforeUpdate = productImgRepository.findAll().size();

        // Update the productImg
        ProductImg updatedProductImg = productImgRepository.findOne(productImg.getId());
        // Disconnect from session so that the updates on updatedProductImg are not directly saved in db
        em.detach(updatedProductImg);
        updatedProductImg
            .productId(UPDATED_PRODUCT_ID)
            .imgUrl(UPDATED_IMG_URL)
            .type(UPDATED_TYPE)
            .rank(UPDATED_RANK)
            .createdTime(UPDATED_CREATED_TIME)
            .updatedTime(UPDATED_UPDATED_TIME)
            .deleted(UPDATED_DELETED);
        ProductImgDTO productImgDTO = productImgMapper.toDto(updatedProductImg);

        restProductImgMockMvc.perform(put("/api/product-imgs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(productImgDTO)))
            .andExpect(status().isOk());

        // Validate the ProductImg in the database
        List<ProductImg> productImgList = productImgRepository.findAll();
        assertThat(productImgList).hasSize(databaseSizeBeforeUpdate);
        ProductImg testProductImg = productImgList.get(productImgList.size() - 1);
        assertThat(testProductImg.getProductId()).isEqualTo(UPDATED_PRODUCT_ID);
        assertThat(testProductImg.getImgUrl()).isEqualTo(UPDATED_IMG_URL);
        assertThat(testProductImg.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testProductImg.getRank()).isEqualTo(UPDATED_RANK);
        assertThat(testProductImg.getCreatedTime()).isEqualTo(UPDATED_CREATED_TIME);
        assertThat(testProductImg.getUpdatedTime()).isEqualTo(UPDATED_UPDATED_TIME);
        assertThat(testProductImg.isDeleted()).isEqualTo(UPDATED_DELETED);
    }

    @Test
    @Transactional
    public void updateNonExistingProductImg() throws Exception {
        int databaseSizeBeforeUpdate = productImgRepository.findAll().size();

        // Create the ProductImg
        ProductImgDTO productImgDTO = productImgMapper.toDto(productImg);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restProductImgMockMvc.perform(put("/api/product-imgs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(productImgDTO)))
            .andExpect(status().isCreated());

        // Validate the ProductImg in the database
        List<ProductImg> productImgList = productImgRepository.findAll();
        assertThat(productImgList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteProductImg() throws Exception {
        // Initialize the database
        productImgRepository.saveAndFlush(productImg);
        int databaseSizeBeforeDelete = productImgRepository.findAll().size();

        // Get the productImg
        restProductImgMockMvc.perform(delete("/api/product-imgs/{id}", productImg.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<ProductImg> productImgList = productImgRepository.findAll();
        assertThat(productImgList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProductImg.class);
        ProductImg productImg1 = new ProductImg();
        productImg1.setId(1L);
        ProductImg productImg2 = new ProductImg();
        productImg2.setId(productImg1.getId());
        assertThat(productImg1).isEqualTo(productImg2);
        productImg2.setId(2L);
        assertThat(productImg1).isNotEqualTo(productImg2);
        productImg1.setId(null);
        assertThat(productImg1).isNotEqualTo(productImg2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProductImgDTO.class);
        ProductImgDTO productImgDTO1 = new ProductImgDTO();
        productImgDTO1.setId(1L);
        ProductImgDTO productImgDTO2 = new ProductImgDTO();
        assertThat(productImgDTO1).isNotEqualTo(productImgDTO2);
        productImgDTO2.setId(productImgDTO1.getId());
        assertThat(productImgDTO1).isEqualTo(productImgDTO2);
        productImgDTO2.setId(2L);
        assertThat(productImgDTO1).isNotEqualTo(productImgDTO2);
        productImgDTO1.setId(null);
        assertThat(productImgDTO1).isNotEqualTo(productImgDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(productImgMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(productImgMapper.fromId(null)).isNull();
    }
}
