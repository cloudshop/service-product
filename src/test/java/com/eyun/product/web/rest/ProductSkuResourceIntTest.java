package com.eyun.product.web.rest;

import com.eyun.product.ProductApp;

import com.eyun.product.config.SecurityBeanOverrideConfiguration;

import com.eyun.product.domain.ProductSku;
import com.eyun.product.repository.ProductSkuRepository;
import com.eyun.product.service.ProductSkuService;
import com.eyun.product.service.dto.ProductSkuDTO;
import com.eyun.product.service.mapper.ProductSkuMapper;
import com.eyun.product.web.rest.errors.ExceptionTranslator;
import com.eyun.product.service.dto.ProductSkuCriteria;
import com.eyun.product.service.ProductSkuQueryService;

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
 * Test class for the ProductSkuResource REST controller.
 *
 * @see ProductSkuResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ProductApp.class, SecurityBeanOverrideConfiguration.class})
public class ProductSkuResourceIntTest {

    private static final Long DEFAULT_PRODUCT_ID = 1L;
    private static final Long UPDATED_PRODUCT_ID = 2L;

    private static final Integer DEFAULT_COUNT = 0;
    private static final Integer UPDATED_COUNT = 1;

    private static final Integer DEFAULT_PRICE = 0;
    private static final Integer UPDATED_PRICE = 1;

    private static final Integer DEFAULT_STATUS = 1;
    private static final Integer UPDATED_STATUS = 2;

    private static final String DEFAULT_SKU_NAME = "AAAAAAAAAA";
    private static final String UPDATED_SKU_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_SKU_CODE = "AAAAAAAAAA";
    private static final String UPDATED_SKU_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_ATTR_STRING = "AAAAAAAAAA";
    private static final String UPDATED_ATTR_STRING = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Boolean DEFAULT_DELETED = false;
    private static final Boolean UPDATED_DELETED = true;

    @Autowired
    private ProductSkuRepository productSkuRepository;

    @Autowired
    private ProductSkuMapper productSkuMapper;

    @Autowired
    private ProductSkuService productSkuService;

    @Autowired
    private ProductSkuQueryService productSkuQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restProductSkuMockMvc;

    private ProductSku productSku;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ProductSkuResource productSkuResource = new ProductSkuResource(productSkuService, productSkuQueryService);
        this.restProductSkuMockMvc = MockMvcBuilders.standaloneSetup(productSkuResource)
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
    public static ProductSku createEntity(EntityManager em) {
        ProductSku productSku = new ProductSku()
            .productId(DEFAULT_PRODUCT_ID)
            .count(DEFAULT_COUNT)
            .price(DEFAULT_PRICE)
            .status(DEFAULT_STATUS)
            .skuName(DEFAULT_SKU_NAME)
            .skuCode(DEFAULT_SKU_CODE)
            .attrString(DEFAULT_ATTR_STRING)
            .createdTime(DEFAULT_CREATED_TIME)
            .updatedTime(DEFAULT_UPDATED_TIME)
            .deleted(DEFAULT_DELETED);
        return productSku;
    }

    @Before
    public void initTest() {
        productSku = createEntity(em);
    }

    @Test
    @Transactional
    public void createProductSku() throws Exception {
        int databaseSizeBeforeCreate = productSkuRepository.findAll().size();

        // Create the ProductSku
        ProductSkuDTO productSkuDTO = productSkuMapper.toDto(productSku);
        restProductSkuMockMvc.perform(post("/api/product-skus")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(productSkuDTO)))
            .andExpect(status().isCreated());

        // Validate the ProductSku in the database
        List<ProductSku> productSkuList = productSkuRepository.findAll();
        assertThat(productSkuList).hasSize(databaseSizeBeforeCreate + 1);
        ProductSku testProductSku = productSkuList.get(productSkuList.size() - 1);
        assertThat(testProductSku.getProductId()).isEqualTo(DEFAULT_PRODUCT_ID);
        assertThat(testProductSku.getCount()).isEqualTo(DEFAULT_COUNT);
        assertThat(testProductSku.getPrice()).isEqualTo(DEFAULT_PRICE);
        assertThat(testProductSku.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testProductSku.getSkuName()).isEqualTo(DEFAULT_SKU_NAME);
        assertThat(testProductSku.getSkuCode()).isEqualTo(DEFAULT_SKU_CODE);
        assertThat(testProductSku.getAttrString()).isEqualTo(DEFAULT_ATTR_STRING);
        assertThat(testProductSku.getCreatedTime()).isEqualTo(DEFAULT_CREATED_TIME);
        assertThat(testProductSku.getUpdatedTime()).isEqualTo(DEFAULT_UPDATED_TIME);
        assertThat(testProductSku.isDeleted()).isEqualTo(DEFAULT_DELETED);
    }

    @Test
    @Transactional
    public void createProductSkuWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = productSkuRepository.findAll().size();

        // Create the ProductSku with an existing ID
        productSku.setId(1L);
        ProductSkuDTO productSkuDTO = productSkuMapper.toDto(productSku);

        // An entity with an existing ID cannot be created, so this API call must fail
        restProductSkuMockMvc.perform(post("/api/product-skus")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(productSkuDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ProductSku in the database
        List<ProductSku> productSkuList = productSkuRepository.findAll();
        assertThat(productSkuList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkProductIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = productSkuRepository.findAll().size();
        // set the field null
        productSku.setProductId(null);

        // Create the ProductSku, which fails.
        ProductSkuDTO productSkuDTO = productSkuMapper.toDto(productSku);

        restProductSkuMockMvc.perform(post("/api/product-skus")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(productSkuDTO)))
            .andExpect(status().isBadRequest());

        List<ProductSku> productSkuList = productSkuRepository.findAll();
        assertThat(productSkuList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkCountIsRequired() throws Exception {
        int databaseSizeBeforeTest = productSkuRepository.findAll().size();
        // set the field null
        productSku.setCount(null);

        // Create the ProductSku, which fails.
        ProductSkuDTO productSkuDTO = productSkuMapper.toDto(productSku);

        restProductSkuMockMvc.perform(post("/api/product-skus")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(productSkuDTO)))
            .andExpect(status().isBadRequest());

        List<ProductSku> productSkuList = productSkuRepository.findAll();
        assertThat(productSkuList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkPriceIsRequired() throws Exception {
        int databaseSizeBeforeTest = productSkuRepository.findAll().size();
        // set the field null
        productSku.setPrice(null);

        // Create the ProductSku, which fails.
        ProductSkuDTO productSkuDTO = productSkuMapper.toDto(productSku);

        restProductSkuMockMvc.perform(post("/api/product-skus")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(productSkuDTO)))
            .andExpect(status().isBadRequest());

        List<ProductSku> productSkuList = productSkuRepository.findAll();
        assertThat(productSkuList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllProductSkus() throws Exception {
        // Initialize the database
        productSkuRepository.saveAndFlush(productSku);

        // Get all the productSkuList
        restProductSkuMockMvc.perform(get("/api/product-skus?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(productSku.getId().intValue())))
            .andExpect(jsonPath("$.[*].productId").value(hasItem(DEFAULT_PRODUCT_ID.intValue())))
            .andExpect(jsonPath("$.[*].count").value(hasItem(DEFAULT_COUNT)))
            .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
            .andExpect(jsonPath("$.[*].skuName").value(hasItem(DEFAULT_SKU_NAME.toString())))
            .andExpect(jsonPath("$.[*].skuCode").value(hasItem(DEFAULT_SKU_CODE.toString())))
            .andExpect(jsonPath("$.[*].attrString").value(hasItem(DEFAULT_ATTR_STRING.toString())))
            .andExpect(jsonPath("$.[*].createdTime").value(hasItem(DEFAULT_CREATED_TIME.toString())))
            .andExpect(jsonPath("$.[*].updatedTime").value(hasItem(DEFAULT_UPDATED_TIME.toString())))
            .andExpect(jsonPath("$.[*].deleted").value(hasItem(DEFAULT_DELETED.booleanValue())));
    }

    @Test
    @Transactional
    public void getProductSku() throws Exception {
        // Initialize the database
        productSkuRepository.saveAndFlush(productSku);

        // Get the productSku
        restProductSkuMockMvc.perform(get("/api/product-skus/{id}", productSku.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(productSku.getId().intValue()))
            .andExpect(jsonPath("$.productId").value(DEFAULT_PRODUCT_ID.intValue()))
            .andExpect(jsonPath("$.count").value(DEFAULT_COUNT))
            .andExpect(jsonPath("$.price").value(DEFAULT_PRICE))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS))
            .andExpect(jsonPath("$.skuName").value(DEFAULT_SKU_NAME.toString()))
            .andExpect(jsonPath("$.skuCode").value(DEFAULT_SKU_CODE.toString()))
            .andExpect(jsonPath("$.attrString").value(DEFAULT_ATTR_STRING.toString()))
            .andExpect(jsonPath("$.createdTime").value(DEFAULT_CREATED_TIME.toString()))
            .andExpect(jsonPath("$.updatedTime").value(DEFAULT_UPDATED_TIME.toString()))
            .andExpect(jsonPath("$.deleted").value(DEFAULT_DELETED.booleanValue()));
    }

    @Test
    @Transactional
    public void getAllProductSkusByProductIdIsEqualToSomething() throws Exception {
        // Initialize the database
        productSkuRepository.saveAndFlush(productSku);

        // Get all the productSkuList where productId equals to DEFAULT_PRODUCT_ID
        defaultProductSkuShouldBeFound("productId.equals=" + DEFAULT_PRODUCT_ID);

        // Get all the productSkuList where productId equals to UPDATED_PRODUCT_ID
        defaultProductSkuShouldNotBeFound("productId.equals=" + UPDATED_PRODUCT_ID);
    }

    @Test
    @Transactional
    public void getAllProductSkusByProductIdIsInShouldWork() throws Exception {
        // Initialize the database
        productSkuRepository.saveAndFlush(productSku);

        // Get all the productSkuList where productId in DEFAULT_PRODUCT_ID or UPDATED_PRODUCT_ID
        defaultProductSkuShouldBeFound("productId.in=" + DEFAULT_PRODUCT_ID + "," + UPDATED_PRODUCT_ID);

        // Get all the productSkuList where productId equals to UPDATED_PRODUCT_ID
        defaultProductSkuShouldNotBeFound("productId.in=" + UPDATED_PRODUCT_ID);
    }

    @Test
    @Transactional
    public void getAllProductSkusByProductIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        productSkuRepository.saveAndFlush(productSku);

        // Get all the productSkuList where productId is not null
        defaultProductSkuShouldBeFound("productId.specified=true");

        // Get all the productSkuList where productId is null
        defaultProductSkuShouldNotBeFound("productId.specified=false");
    }

    @Test
    @Transactional
    public void getAllProductSkusByProductIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        productSkuRepository.saveAndFlush(productSku);

        // Get all the productSkuList where productId greater than or equals to DEFAULT_PRODUCT_ID
        defaultProductSkuShouldBeFound("productId.greaterOrEqualThan=" + DEFAULT_PRODUCT_ID);

        // Get all the productSkuList where productId greater than or equals to UPDATED_PRODUCT_ID
        defaultProductSkuShouldNotBeFound("productId.greaterOrEqualThan=" + UPDATED_PRODUCT_ID);
    }

    @Test
    @Transactional
    public void getAllProductSkusByProductIdIsLessThanSomething() throws Exception {
        // Initialize the database
        productSkuRepository.saveAndFlush(productSku);

        // Get all the productSkuList where productId less than or equals to DEFAULT_PRODUCT_ID
        defaultProductSkuShouldNotBeFound("productId.lessThan=" + DEFAULT_PRODUCT_ID);

        // Get all the productSkuList where productId less than or equals to UPDATED_PRODUCT_ID
        defaultProductSkuShouldBeFound("productId.lessThan=" + UPDATED_PRODUCT_ID);
    }


    @Test
    @Transactional
    public void getAllProductSkusByCountIsEqualToSomething() throws Exception {
        // Initialize the database
        productSkuRepository.saveAndFlush(productSku);

        // Get all the productSkuList where count equals to DEFAULT_COUNT
        defaultProductSkuShouldBeFound("count.equals=" + DEFAULT_COUNT);

        // Get all the productSkuList where count equals to UPDATED_COUNT
        defaultProductSkuShouldNotBeFound("count.equals=" + UPDATED_COUNT);
    }

    @Test
    @Transactional
    public void getAllProductSkusByCountIsInShouldWork() throws Exception {
        // Initialize the database
        productSkuRepository.saveAndFlush(productSku);

        // Get all the productSkuList where count in DEFAULT_COUNT or UPDATED_COUNT
        defaultProductSkuShouldBeFound("count.in=" + DEFAULT_COUNT + "," + UPDATED_COUNT);

        // Get all the productSkuList where count equals to UPDATED_COUNT
        defaultProductSkuShouldNotBeFound("count.in=" + UPDATED_COUNT);
    }

    @Test
    @Transactional
    public void getAllProductSkusByCountIsNullOrNotNull() throws Exception {
        // Initialize the database
        productSkuRepository.saveAndFlush(productSku);

        // Get all the productSkuList where count is not null
        defaultProductSkuShouldBeFound("count.specified=true");

        // Get all the productSkuList where count is null
        defaultProductSkuShouldNotBeFound("count.specified=false");
    }

    @Test
    @Transactional
    public void getAllProductSkusByCountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        productSkuRepository.saveAndFlush(productSku);

        // Get all the productSkuList where count greater than or equals to DEFAULT_COUNT
        defaultProductSkuShouldBeFound("count.greaterOrEqualThan=" + DEFAULT_COUNT);

        // Get all the productSkuList where count greater than or equals to UPDATED_COUNT
        defaultProductSkuShouldNotBeFound("count.greaterOrEqualThan=" + UPDATED_COUNT);
    }

    @Test
    @Transactional
    public void getAllProductSkusByCountIsLessThanSomething() throws Exception {
        // Initialize the database
        productSkuRepository.saveAndFlush(productSku);

        // Get all the productSkuList where count less than or equals to DEFAULT_COUNT
        defaultProductSkuShouldNotBeFound("count.lessThan=" + DEFAULT_COUNT);

        // Get all the productSkuList where count less than or equals to UPDATED_COUNT
        defaultProductSkuShouldBeFound("count.lessThan=" + UPDATED_COUNT);
    }


    @Test
    @Transactional
    public void getAllProductSkusByPriceIsEqualToSomething() throws Exception {
        // Initialize the database
        productSkuRepository.saveAndFlush(productSku);

        // Get all the productSkuList where price equals to DEFAULT_PRICE
        defaultProductSkuShouldBeFound("price.equals=" + DEFAULT_PRICE);

        // Get all the productSkuList where price equals to UPDATED_PRICE
        defaultProductSkuShouldNotBeFound("price.equals=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    public void getAllProductSkusByPriceIsInShouldWork() throws Exception {
        // Initialize the database
        productSkuRepository.saveAndFlush(productSku);

        // Get all the productSkuList where price in DEFAULT_PRICE or UPDATED_PRICE
        defaultProductSkuShouldBeFound("price.in=" + DEFAULT_PRICE + "," + UPDATED_PRICE);

        // Get all the productSkuList where price equals to UPDATED_PRICE
        defaultProductSkuShouldNotBeFound("price.in=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    public void getAllProductSkusByPriceIsNullOrNotNull() throws Exception {
        // Initialize the database
        productSkuRepository.saveAndFlush(productSku);

        // Get all the productSkuList where price is not null
        defaultProductSkuShouldBeFound("price.specified=true");

        // Get all the productSkuList where price is null
        defaultProductSkuShouldNotBeFound("price.specified=false");
    }

    @Test
    @Transactional
    public void getAllProductSkusByPriceIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        productSkuRepository.saveAndFlush(productSku);

        // Get all the productSkuList where price greater than or equals to DEFAULT_PRICE
        defaultProductSkuShouldBeFound("price.greaterOrEqualThan=" + DEFAULT_PRICE);

        // Get all the productSkuList where price greater than or equals to UPDATED_PRICE
        defaultProductSkuShouldNotBeFound("price.greaterOrEqualThan=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    public void getAllProductSkusByPriceIsLessThanSomething() throws Exception {
        // Initialize the database
        productSkuRepository.saveAndFlush(productSku);

        // Get all the productSkuList where price less than or equals to DEFAULT_PRICE
        defaultProductSkuShouldNotBeFound("price.lessThan=" + DEFAULT_PRICE);

        // Get all the productSkuList where price less than or equals to UPDATED_PRICE
        defaultProductSkuShouldBeFound("price.lessThan=" + UPDATED_PRICE);
    }


    @Test
    @Transactional
    public void getAllProductSkusByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        productSkuRepository.saveAndFlush(productSku);

        // Get all the productSkuList where status equals to DEFAULT_STATUS
        defaultProductSkuShouldBeFound("status.equals=" + DEFAULT_STATUS);

        // Get all the productSkuList where status equals to UPDATED_STATUS
        defaultProductSkuShouldNotBeFound("status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void getAllProductSkusByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        productSkuRepository.saveAndFlush(productSku);

        // Get all the productSkuList where status in DEFAULT_STATUS or UPDATED_STATUS
        defaultProductSkuShouldBeFound("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS);

        // Get all the productSkuList where status equals to UPDATED_STATUS
        defaultProductSkuShouldNotBeFound("status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void getAllProductSkusByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        productSkuRepository.saveAndFlush(productSku);

        // Get all the productSkuList where status is not null
        defaultProductSkuShouldBeFound("status.specified=true");

        // Get all the productSkuList where status is null
        defaultProductSkuShouldNotBeFound("status.specified=false");
    }

    @Test
    @Transactional
    public void getAllProductSkusByStatusIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        productSkuRepository.saveAndFlush(productSku);

        // Get all the productSkuList where status greater than or equals to DEFAULT_STATUS
        defaultProductSkuShouldBeFound("status.greaterOrEqualThan=" + DEFAULT_STATUS);

        // Get all the productSkuList where status greater than or equals to UPDATED_STATUS
        defaultProductSkuShouldNotBeFound("status.greaterOrEqualThan=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void getAllProductSkusByStatusIsLessThanSomething() throws Exception {
        // Initialize the database
        productSkuRepository.saveAndFlush(productSku);

        // Get all the productSkuList where status less than or equals to DEFAULT_STATUS
        defaultProductSkuShouldNotBeFound("status.lessThan=" + DEFAULT_STATUS);

        // Get all the productSkuList where status less than or equals to UPDATED_STATUS
        defaultProductSkuShouldBeFound("status.lessThan=" + UPDATED_STATUS);
    }


    @Test
    @Transactional
    public void getAllProductSkusBySkuNameIsEqualToSomething() throws Exception {
        // Initialize the database
        productSkuRepository.saveAndFlush(productSku);

        // Get all the productSkuList where skuName equals to DEFAULT_SKU_NAME
        defaultProductSkuShouldBeFound("skuName.equals=" + DEFAULT_SKU_NAME);

        // Get all the productSkuList where skuName equals to UPDATED_SKU_NAME
        defaultProductSkuShouldNotBeFound("skuName.equals=" + UPDATED_SKU_NAME);
    }

    @Test
    @Transactional
    public void getAllProductSkusBySkuNameIsInShouldWork() throws Exception {
        // Initialize the database
        productSkuRepository.saveAndFlush(productSku);

        // Get all the productSkuList where skuName in DEFAULT_SKU_NAME or UPDATED_SKU_NAME
        defaultProductSkuShouldBeFound("skuName.in=" + DEFAULT_SKU_NAME + "," + UPDATED_SKU_NAME);

        // Get all the productSkuList where skuName equals to UPDATED_SKU_NAME
        defaultProductSkuShouldNotBeFound("skuName.in=" + UPDATED_SKU_NAME);
    }

    @Test
    @Transactional
    public void getAllProductSkusBySkuNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        productSkuRepository.saveAndFlush(productSku);

        // Get all the productSkuList where skuName is not null
        defaultProductSkuShouldBeFound("skuName.specified=true");

        // Get all the productSkuList where skuName is null
        defaultProductSkuShouldNotBeFound("skuName.specified=false");
    }

    @Test
    @Transactional
    public void getAllProductSkusBySkuCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        productSkuRepository.saveAndFlush(productSku);

        // Get all the productSkuList where skuCode equals to DEFAULT_SKU_CODE
        defaultProductSkuShouldBeFound("skuCode.equals=" + DEFAULT_SKU_CODE);

        // Get all the productSkuList where skuCode equals to UPDATED_SKU_CODE
        defaultProductSkuShouldNotBeFound("skuCode.equals=" + UPDATED_SKU_CODE);
    }

    @Test
    @Transactional
    public void getAllProductSkusBySkuCodeIsInShouldWork() throws Exception {
        // Initialize the database
        productSkuRepository.saveAndFlush(productSku);

        // Get all the productSkuList where skuCode in DEFAULT_SKU_CODE or UPDATED_SKU_CODE
        defaultProductSkuShouldBeFound("skuCode.in=" + DEFAULT_SKU_CODE + "," + UPDATED_SKU_CODE);

        // Get all the productSkuList where skuCode equals to UPDATED_SKU_CODE
        defaultProductSkuShouldNotBeFound("skuCode.in=" + UPDATED_SKU_CODE);
    }

    @Test
    @Transactional
    public void getAllProductSkusBySkuCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        productSkuRepository.saveAndFlush(productSku);

        // Get all the productSkuList where skuCode is not null
        defaultProductSkuShouldBeFound("skuCode.specified=true");

        // Get all the productSkuList where skuCode is null
        defaultProductSkuShouldNotBeFound("skuCode.specified=false");
    }

    @Test
    @Transactional
    public void getAllProductSkusByAttrStringIsEqualToSomething() throws Exception {
        // Initialize the database
        productSkuRepository.saveAndFlush(productSku);

        // Get all the productSkuList where attrString equals to DEFAULT_ATTR_STRING
        defaultProductSkuShouldBeFound("attrString.equals=" + DEFAULT_ATTR_STRING);

        // Get all the productSkuList where attrString equals to UPDATED_ATTR_STRING
        defaultProductSkuShouldNotBeFound("attrString.equals=" + UPDATED_ATTR_STRING);
    }

    @Test
    @Transactional
    public void getAllProductSkusByAttrStringIsInShouldWork() throws Exception {
        // Initialize the database
        productSkuRepository.saveAndFlush(productSku);

        // Get all the productSkuList where attrString in DEFAULT_ATTR_STRING or UPDATED_ATTR_STRING
        defaultProductSkuShouldBeFound("attrString.in=" + DEFAULT_ATTR_STRING + "," + UPDATED_ATTR_STRING);

        // Get all the productSkuList where attrString equals to UPDATED_ATTR_STRING
        defaultProductSkuShouldNotBeFound("attrString.in=" + UPDATED_ATTR_STRING);
    }

    @Test
    @Transactional
    public void getAllProductSkusByAttrStringIsNullOrNotNull() throws Exception {
        // Initialize the database
        productSkuRepository.saveAndFlush(productSku);

        // Get all the productSkuList where attrString is not null
        defaultProductSkuShouldBeFound("attrString.specified=true");

        // Get all the productSkuList where attrString is null
        defaultProductSkuShouldNotBeFound("attrString.specified=false");
    }

    @Test
    @Transactional
    public void getAllProductSkusByCreatedTimeIsEqualToSomething() throws Exception {
        // Initialize the database
        productSkuRepository.saveAndFlush(productSku);

        // Get all the productSkuList where createdTime equals to DEFAULT_CREATED_TIME
        defaultProductSkuShouldBeFound("createdTime.equals=" + DEFAULT_CREATED_TIME);

        // Get all the productSkuList where createdTime equals to UPDATED_CREATED_TIME
        defaultProductSkuShouldNotBeFound("createdTime.equals=" + UPDATED_CREATED_TIME);
    }

    @Test
    @Transactional
    public void getAllProductSkusByCreatedTimeIsInShouldWork() throws Exception {
        // Initialize the database
        productSkuRepository.saveAndFlush(productSku);

        // Get all the productSkuList where createdTime in DEFAULT_CREATED_TIME or UPDATED_CREATED_TIME
        defaultProductSkuShouldBeFound("createdTime.in=" + DEFAULT_CREATED_TIME + "," + UPDATED_CREATED_TIME);

        // Get all the productSkuList where createdTime equals to UPDATED_CREATED_TIME
        defaultProductSkuShouldNotBeFound("createdTime.in=" + UPDATED_CREATED_TIME);
    }

    @Test
    @Transactional
    public void getAllProductSkusByCreatedTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        productSkuRepository.saveAndFlush(productSku);

        // Get all the productSkuList where createdTime is not null
        defaultProductSkuShouldBeFound("createdTime.specified=true");

        // Get all the productSkuList where createdTime is null
        defaultProductSkuShouldNotBeFound("createdTime.specified=false");
    }

    @Test
    @Transactional
    public void getAllProductSkusByUpdatedTimeIsEqualToSomething() throws Exception {
        // Initialize the database
        productSkuRepository.saveAndFlush(productSku);

        // Get all the productSkuList where updatedTime equals to DEFAULT_UPDATED_TIME
        defaultProductSkuShouldBeFound("updatedTime.equals=" + DEFAULT_UPDATED_TIME);

        // Get all the productSkuList where updatedTime equals to UPDATED_UPDATED_TIME
        defaultProductSkuShouldNotBeFound("updatedTime.equals=" + UPDATED_UPDATED_TIME);
    }

    @Test
    @Transactional
    public void getAllProductSkusByUpdatedTimeIsInShouldWork() throws Exception {
        // Initialize the database
        productSkuRepository.saveAndFlush(productSku);

        // Get all the productSkuList where updatedTime in DEFAULT_UPDATED_TIME or UPDATED_UPDATED_TIME
        defaultProductSkuShouldBeFound("updatedTime.in=" + DEFAULT_UPDATED_TIME + "," + UPDATED_UPDATED_TIME);

        // Get all the productSkuList where updatedTime equals to UPDATED_UPDATED_TIME
        defaultProductSkuShouldNotBeFound("updatedTime.in=" + UPDATED_UPDATED_TIME);
    }

    @Test
    @Transactional
    public void getAllProductSkusByUpdatedTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        productSkuRepository.saveAndFlush(productSku);

        // Get all the productSkuList where updatedTime is not null
        defaultProductSkuShouldBeFound("updatedTime.specified=true");

        // Get all the productSkuList where updatedTime is null
        defaultProductSkuShouldNotBeFound("updatedTime.specified=false");
    }

    @Test
    @Transactional
    public void getAllProductSkusByDeletedIsEqualToSomething() throws Exception {
        // Initialize the database
        productSkuRepository.saveAndFlush(productSku);

        // Get all the productSkuList where deleted equals to DEFAULT_DELETED
        defaultProductSkuShouldBeFound("deleted.equals=" + DEFAULT_DELETED);

        // Get all the productSkuList where deleted equals to UPDATED_DELETED
        defaultProductSkuShouldNotBeFound("deleted.equals=" + UPDATED_DELETED);
    }

    @Test
    @Transactional
    public void getAllProductSkusByDeletedIsInShouldWork() throws Exception {
        // Initialize the database
        productSkuRepository.saveAndFlush(productSku);

        // Get all the productSkuList where deleted in DEFAULT_DELETED or UPDATED_DELETED
        defaultProductSkuShouldBeFound("deleted.in=" + DEFAULT_DELETED + "," + UPDATED_DELETED);

        // Get all the productSkuList where deleted equals to UPDATED_DELETED
        defaultProductSkuShouldNotBeFound("deleted.in=" + UPDATED_DELETED);
    }

    @Test
    @Transactional
    public void getAllProductSkusByDeletedIsNullOrNotNull() throws Exception {
        // Initialize the database
        productSkuRepository.saveAndFlush(productSku);

        // Get all the productSkuList where deleted is not null
        defaultProductSkuShouldBeFound("deleted.specified=true");

        // Get all the productSkuList where deleted is null
        defaultProductSkuShouldNotBeFound("deleted.specified=false");
    }
    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultProductSkuShouldBeFound(String filter) throws Exception {
        restProductSkuMockMvc.perform(get("/api/product-skus?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(productSku.getId().intValue())))
            .andExpect(jsonPath("$.[*].productId").value(hasItem(DEFAULT_PRODUCT_ID.intValue())))
            .andExpect(jsonPath("$.[*].count").value(hasItem(DEFAULT_COUNT)))
            .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
            .andExpect(jsonPath("$.[*].skuName").value(hasItem(DEFAULT_SKU_NAME.toString())))
            .andExpect(jsonPath("$.[*].skuCode").value(hasItem(DEFAULT_SKU_CODE.toString())))
            .andExpect(jsonPath("$.[*].attrString").value(hasItem(DEFAULT_ATTR_STRING.toString())))
            .andExpect(jsonPath("$.[*].createdTime").value(hasItem(DEFAULT_CREATED_TIME.toString())))
            .andExpect(jsonPath("$.[*].updatedTime").value(hasItem(DEFAULT_UPDATED_TIME.toString())))
            .andExpect(jsonPath("$.[*].deleted").value(hasItem(DEFAULT_DELETED.booleanValue())));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultProductSkuShouldNotBeFound(String filter) throws Exception {
        restProductSkuMockMvc.perform(get("/api/product-skus?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }


    @Test
    @Transactional
    public void getNonExistingProductSku() throws Exception {
        // Get the productSku
        restProductSkuMockMvc.perform(get("/api/product-skus/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateProductSku() throws Exception {
        // Initialize the database
        productSkuRepository.saveAndFlush(productSku);
        int databaseSizeBeforeUpdate = productSkuRepository.findAll().size();

        // Update the productSku
        ProductSku updatedProductSku = productSkuRepository.findOne(productSku.getId());
        // Disconnect from session so that the updates on updatedProductSku are not directly saved in db
        em.detach(updatedProductSku);
        updatedProductSku
            .productId(UPDATED_PRODUCT_ID)
            .count(UPDATED_COUNT)
            .price(UPDATED_PRICE)
            .status(UPDATED_STATUS)
            .skuName(UPDATED_SKU_NAME)
            .skuCode(UPDATED_SKU_CODE)
            .attrString(UPDATED_ATTR_STRING)
            .createdTime(UPDATED_CREATED_TIME)
            .updatedTime(UPDATED_UPDATED_TIME)
            .deleted(UPDATED_DELETED);
        ProductSkuDTO productSkuDTO = productSkuMapper.toDto(updatedProductSku);

        restProductSkuMockMvc.perform(put("/api/product-skus")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(productSkuDTO)))
            .andExpect(status().isOk());

        // Validate the ProductSku in the database
        List<ProductSku> productSkuList = productSkuRepository.findAll();
        assertThat(productSkuList).hasSize(databaseSizeBeforeUpdate);
        ProductSku testProductSku = productSkuList.get(productSkuList.size() - 1);
        assertThat(testProductSku.getProductId()).isEqualTo(UPDATED_PRODUCT_ID);
        assertThat(testProductSku.getCount()).isEqualTo(UPDATED_COUNT);
        assertThat(testProductSku.getPrice()).isEqualTo(UPDATED_PRICE);
        assertThat(testProductSku.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testProductSku.getSkuName()).isEqualTo(UPDATED_SKU_NAME);
        assertThat(testProductSku.getSkuCode()).isEqualTo(UPDATED_SKU_CODE);
        assertThat(testProductSku.getAttrString()).isEqualTo(UPDATED_ATTR_STRING);
        assertThat(testProductSku.getCreatedTime()).isEqualTo(UPDATED_CREATED_TIME);
        assertThat(testProductSku.getUpdatedTime()).isEqualTo(UPDATED_UPDATED_TIME);
        assertThat(testProductSku.isDeleted()).isEqualTo(UPDATED_DELETED);
    }

    @Test
    @Transactional
    public void updateNonExistingProductSku() throws Exception {
        int databaseSizeBeforeUpdate = productSkuRepository.findAll().size();

        // Create the ProductSku
        ProductSkuDTO productSkuDTO = productSkuMapper.toDto(productSku);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restProductSkuMockMvc.perform(put("/api/product-skus")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(productSkuDTO)))
            .andExpect(status().isCreated());

        // Validate the ProductSku in the database
        List<ProductSku> productSkuList = productSkuRepository.findAll();
        assertThat(productSkuList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteProductSku() throws Exception {
        // Initialize the database
        productSkuRepository.saveAndFlush(productSku);
        int databaseSizeBeforeDelete = productSkuRepository.findAll().size();

        // Get the productSku
        restProductSkuMockMvc.perform(delete("/api/product-skus/{id}", productSku.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<ProductSku> productSkuList = productSkuRepository.findAll();
        assertThat(productSkuList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProductSku.class);
        ProductSku productSku1 = new ProductSku();
        productSku1.setId(1L);
        ProductSku productSku2 = new ProductSku();
        productSku2.setId(productSku1.getId());
        assertThat(productSku1).isEqualTo(productSku2);
        productSku2.setId(2L);
        assertThat(productSku1).isNotEqualTo(productSku2);
        productSku1.setId(null);
        assertThat(productSku1).isNotEqualTo(productSku2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProductSkuDTO.class);
        ProductSkuDTO productSkuDTO1 = new ProductSkuDTO();
        productSkuDTO1.setId(1L);
        ProductSkuDTO productSkuDTO2 = new ProductSkuDTO();
        assertThat(productSkuDTO1).isNotEqualTo(productSkuDTO2);
        productSkuDTO2.setId(productSkuDTO1.getId());
        assertThat(productSkuDTO1).isEqualTo(productSkuDTO2);
        productSkuDTO2.setId(2L);
        assertThat(productSkuDTO1).isNotEqualTo(productSkuDTO2);
        productSkuDTO1.setId(null);
        assertThat(productSkuDTO1).isNotEqualTo(productSkuDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(productSkuMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(productSkuMapper.fromId(null)).isNull();
    }
}
