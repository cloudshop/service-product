package com.eyun.product.web.rest;

import com.eyun.product.ProductApp;

import com.eyun.product.config.SecurityBeanOverrideConfiguration;

import com.eyun.product.domain.Product;
import com.eyun.product.repository.ProductRepository;
import com.eyun.product.service.ProductService;
import com.eyun.product.service.dto.ProductDTO;
import com.eyun.product.service.mapper.ProductMapper;
import com.eyun.product.web.rest.errors.ExceptionTranslator;
import com.eyun.product.service.dto.ProductCriteria;
import com.eyun.product.service.ProductQueryService;

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
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static com.eyun.product.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the ProductResource REST controller.
 *
 * @see ProductResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ProductApp.class, SecurityBeanOverrideConfiguration.class})
public class ProductResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Long DEFAULT_BRAND_ID = 1L;
    private static final Long UPDATED_BRAND_ID = 2L;

    private static final BigDecimal DEFAULT_LIST_PRICE = new BigDecimal(1);
    private static final BigDecimal UPDATED_LIST_PRICE = new BigDecimal(2);

    private static final Long DEFAULT_SHOP_ID = 1L;
    private static final Long UPDATED_SHOP_ID = 2L;

    private static final Instant DEFAULT_CREATED_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Boolean DEFAULT_DELETED = false;
    private static final Boolean UPDATED_DELETED = true;

    private static final String DEFAULT_DETAILS = "AAAAAAAAAA";
    private static final String UPDATED_DETAILS = "BBBBBBBBBB";

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductQueryService productQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restProductMockMvc;

    private Product product;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ProductResource productResource = new ProductResource(productService, productQueryService);
        this.restProductMockMvc = MockMvcBuilders.standaloneSetup(productResource)
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
    public static Product createEntity(EntityManager em) {
        Product product = new Product()
            .name(DEFAULT_NAME)
            .brandId(DEFAULT_BRAND_ID)
            .listPrice(DEFAULT_LIST_PRICE)
            .shopId(DEFAULT_SHOP_ID)
            .createdTime(DEFAULT_CREATED_TIME)
            .updatedTime(DEFAULT_UPDATED_TIME)
            .deleted(DEFAULT_DELETED)
            .details(DEFAULT_DETAILS);
        return product;
    }

    @Before
    public void initTest() {
        product = createEntity(em);
    }

    @Test
    @Transactional
    public void createProduct() throws Exception {
        int databaseSizeBeforeCreate = productRepository.findAll().size();

        // Create the Product
        ProductDTO productDTO = productMapper.toDto(product);
        restProductMockMvc.perform(post("/api/products")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(productDTO)))
            .andExpect(status().isCreated());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeCreate + 1);
        Product testProduct = productList.get(productList.size() - 1);
        assertThat(testProduct.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testProduct.getBrandId()).isEqualTo(DEFAULT_BRAND_ID);
        assertThat(testProduct.getListPrice()).isEqualTo(DEFAULT_LIST_PRICE);
        assertThat(testProduct.getShopId()).isEqualTo(DEFAULT_SHOP_ID);
        assertThat(testProduct.getCreatedTime()).isEqualTo(DEFAULT_CREATED_TIME);
        assertThat(testProduct.getUpdatedTime()).isEqualTo(DEFAULT_UPDATED_TIME);
        assertThat(testProduct.isDeleted()).isEqualTo(DEFAULT_DELETED);
        assertThat(testProduct.getDetails()).isEqualTo(DEFAULT_DETAILS);
    }

    @Test
    @Transactional
    public void createProductWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = productRepository.findAll().size();

        // Create the Product with an existing ID
        product.setId(1L);
        ProductDTO productDTO = productMapper.toDto(product);

        // An entity with an existing ID cannot be created, so this API call must fail
        restProductMockMvc.perform(post("/api/products")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(productDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = productRepository.findAll().size();
        // set the field null
        product.setName(null);

        // Create the Product, which fails.
        ProductDTO productDTO = productMapper.toDto(product);

        restProductMockMvc.perform(post("/api/products")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(productDTO)))
            .andExpect(status().isBadRequest());

        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkBrandIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = productRepository.findAll().size();
        // set the field null
        product.setBrandId(null);

        // Create the Product, which fails.
        ProductDTO productDTO = productMapper.toDto(product);

        restProductMockMvc.perform(post("/api/products")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(productDTO)))
            .andExpect(status().isBadRequest());

        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllProducts() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList
        restProductMockMvc.perform(get("/api/products?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(product.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].brandId").value(hasItem(DEFAULT_BRAND_ID.intValue())))
            .andExpect(jsonPath("$.[*].listPrice").value(hasItem(DEFAULT_LIST_PRICE.intValue())))
            .andExpect(jsonPath("$.[*].shopId").value(hasItem(DEFAULT_SHOP_ID.intValue())))
            .andExpect(jsonPath("$.[*].createdTime").value(hasItem(DEFAULT_CREATED_TIME.toString())))
            .andExpect(jsonPath("$.[*].updatedTime").value(hasItem(DEFAULT_UPDATED_TIME.toString())))
            .andExpect(jsonPath("$.[*].deleted").value(hasItem(DEFAULT_DELETED.booleanValue())))
            .andExpect(jsonPath("$.[*].details").value(hasItem(DEFAULT_DETAILS.toString())));
    }

    @Test
    @Transactional
    public void getProduct() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get the product
        restProductMockMvc.perform(get("/api/products/{id}", product.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(product.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.brandId").value(DEFAULT_BRAND_ID.intValue()))
            .andExpect(jsonPath("$.listPrice").value(DEFAULT_LIST_PRICE.intValue()))
            .andExpect(jsonPath("$.shopId").value(DEFAULT_SHOP_ID.intValue()))
            .andExpect(jsonPath("$.createdTime").value(DEFAULT_CREATED_TIME.toString()))
            .andExpect(jsonPath("$.updatedTime").value(DEFAULT_UPDATED_TIME.toString()))
            .andExpect(jsonPath("$.deleted").value(DEFAULT_DELETED.booleanValue()))
            .andExpect(jsonPath("$.details").value(DEFAULT_DETAILS.toString()));
    }

    @Test
    @Transactional
    public void getAllProductsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where name equals to DEFAULT_NAME
        defaultProductShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the productList where name equals to UPDATED_NAME
        defaultProductShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllProductsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where name in DEFAULT_NAME or UPDATED_NAME
        defaultProductShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the productList where name equals to UPDATED_NAME
        defaultProductShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllProductsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where name is not null
        defaultProductShouldBeFound("name.specified=true");

        // Get all the productList where name is null
        defaultProductShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    public void getAllProductsByBrandIdIsEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where brandId equals to DEFAULT_BRAND_ID
        defaultProductShouldBeFound("brandId.equals=" + DEFAULT_BRAND_ID);

        // Get all the productList where brandId equals to UPDATED_BRAND_ID
        defaultProductShouldNotBeFound("brandId.equals=" + UPDATED_BRAND_ID);
    }

    @Test
    @Transactional
    public void getAllProductsByBrandIdIsInShouldWork() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where brandId in DEFAULT_BRAND_ID or UPDATED_BRAND_ID
        defaultProductShouldBeFound("brandId.in=" + DEFAULT_BRAND_ID + "," + UPDATED_BRAND_ID);

        // Get all the productList where brandId equals to UPDATED_BRAND_ID
        defaultProductShouldNotBeFound("brandId.in=" + UPDATED_BRAND_ID);
    }

    @Test
    @Transactional
    public void getAllProductsByBrandIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where brandId is not null
        defaultProductShouldBeFound("brandId.specified=true");

        // Get all the productList where brandId is null
        defaultProductShouldNotBeFound("brandId.specified=false");
    }

    @Test
    @Transactional
    public void getAllProductsByBrandIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where brandId greater than or equals to DEFAULT_BRAND_ID
        defaultProductShouldBeFound("brandId.greaterOrEqualThan=" + DEFAULT_BRAND_ID);

        // Get all the productList where brandId greater than or equals to UPDATED_BRAND_ID
        defaultProductShouldNotBeFound("brandId.greaterOrEqualThan=" + UPDATED_BRAND_ID);
    }

    @Test
    @Transactional
    public void getAllProductsByBrandIdIsLessThanSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where brandId less than or equals to DEFAULT_BRAND_ID
        defaultProductShouldNotBeFound("brandId.lessThan=" + DEFAULT_BRAND_ID);

        // Get all the productList where brandId less than or equals to UPDATED_BRAND_ID
        defaultProductShouldBeFound("brandId.lessThan=" + UPDATED_BRAND_ID);
    }


    @Test
    @Transactional
    public void getAllProductsByListPriceIsEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where listPrice equals to DEFAULT_LIST_PRICE
        defaultProductShouldBeFound("listPrice.equals=" + DEFAULT_LIST_PRICE);

        // Get all the productList where listPrice equals to UPDATED_LIST_PRICE
        defaultProductShouldNotBeFound("listPrice.equals=" + UPDATED_LIST_PRICE);
    }

    @Test
    @Transactional
    public void getAllProductsByListPriceIsInShouldWork() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where listPrice in DEFAULT_LIST_PRICE or UPDATED_LIST_PRICE
        defaultProductShouldBeFound("listPrice.in=" + DEFAULT_LIST_PRICE + "," + UPDATED_LIST_PRICE);

        // Get all the productList where listPrice equals to UPDATED_LIST_PRICE
        defaultProductShouldNotBeFound("listPrice.in=" + UPDATED_LIST_PRICE);
    }

    @Test
    @Transactional
    public void getAllProductsByListPriceIsNullOrNotNull() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where listPrice is not null
        defaultProductShouldBeFound("listPrice.specified=true");

        // Get all the productList where listPrice is null
        defaultProductShouldNotBeFound("listPrice.specified=false");
    }

    @Test
    @Transactional
    public void getAllProductsByShopIdIsEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where shopId equals to DEFAULT_SHOP_ID
        defaultProductShouldBeFound("shopId.equals=" + DEFAULT_SHOP_ID);

        // Get all the productList where shopId equals to UPDATED_SHOP_ID
        defaultProductShouldNotBeFound("shopId.equals=" + UPDATED_SHOP_ID);
    }

    @Test
    @Transactional
    public void getAllProductsByShopIdIsInShouldWork() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where shopId in DEFAULT_SHOP_ID or UPDATED_SHOP_ID
        defaultProductShouldBeFound("shopId.in=" + DEFAULT_SHOP_ID + "," + UPDATED_SHOP_ID);

        // Get all the productList where shopId equals to UPDATED_SHOP_ID
        defaultProductShouldNotBeFound("shopId.in=" + UPDATED_SHOP_ID);
    }

    @Test
    @Transactional
    public void getAllProductsByShopIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where shopId is not null
        defaultProductShouldBeFound("shopId.specified=true");

        // Get all the productList where shopId is null
        defaultProductShouldNotBeFound("shopId.specified=false");
    }

    @Test
    @Transactional
    public void getAllProductsByShopIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where shopId greater than or equals to DEFAULT_SHOP_ID
        defaultProductShouldBeFound("shopId.greaterOrEqualThan=" + DEFAULT_SHOP_ID);

        // Get all the productList where shopId greater than or equals to UPDATED_SHOP_ID
        defaultProductShouldNotBeFound("shopId.greaterOrEqualThan=" + UPDATED_SHOP_ID);
    }

    @Test
    @Transactional
    public void getAllProductsByShopIdIsLessThanSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where shopId less than or equals to DEFAULT_SHOP_ID
        defaultProductShouldNotBeFound("shopId.lessThan=" + DEFAULT_SHOP_ID);

        // Get all the productList where shopId less than or equals to UPDATED_SHOP_ID
        defaultProductShouldBeFound("shopId.lessThan=" + UPDATED_SHOP_ID);
    }


    @Test
    @Transactional
    public void getAllProductsByCreatedTimeIsEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where createdTime equals to DEFAULT_CREATED_TIME
        defaultProductShouldBeFound("createdTime.equals=" + DEFAULT_CREATED_TIME);

        // Get all the productList where createdTime equals to UPDATED_CREATED_TIME
        defaultProductShouldNotBeFound("createdTime.equals=" + UPDATED_CREATED_TIME);
    }

    @Test
    @Transactional
    public void getAllProductsByCreatedTimeIsInShouldWork() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where createdTime in DEFAULT_CREATED_TIME or UPDATED_CREATED_TIME
        defaultProductShouldBeFound("createdTime.in=" + DEFAULT_CREATED_TIME + "," + UPDATED_CREATED_TIME);

        // Get all the productList where createdTime equals to UPDATED_CREATED_TIME
        defaultProductShouldNotBeFound("createdTime.in=" + UPDATED_CREATED_TIME);
    }

    @Test
    @Transactional
    public void getAllProductsByCreatedTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where createdTime is not null
        defaultProductShouldBeFound("createdTime.specified=true");

        // Get all the productList where createdTime is null
        defaultProductShouldNotBeFound("createdTime.specified=false");
    }

    @Test
    @Transactional
    public void getAllProductsByUpdatedTimeIsEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where updatedTime equals to DEFAULT_UPDATED_TIME
        defaultProductShouldBeFound("updatedTime.equals=" + DEFAULT_UPDATED_TIME);

        // Get all the productList where updatedTime equals to UPDATED_UPDATED_TIME
        defaultProductShouldNotBeFound("updatedTime.equals=" + UPDATED_UPDATED_TIME);
    }

    @Test
    @Transactional
    public void getAllProductsByUpdatedTimeIsInShouldWork() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where updatedTime in DEFAULT_UPDATED_TIME or UPDATED_UPDATED_TIME
        defaultProductShouldBeFound("updatedTime.in=" + DEFAULT_UPDATED_TIME + "," + UPDATED_UPDATED_TIME);

        // Get all the productList where updatedTime equals to UPDATED_UPDATED_TIME
        defaultProductShouldNotBeFound("updatedTime.in=" + UPDATED_UPDATED_TIME);
    }

    @Test
    @Transactional
    public void getAllProductsByUpdatedTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where updatedTime is not null
        defaultProductShouldBeFound("updatedTime.specified=true");

        // Get all the productList where updatedTime is null
        defaultProductShouldNotBeFound("updatedTime.specified=false");
    }

    @Test
    @Transactional
    public void getAllProductsByDeletedIsEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where deleted equals to DEFAULT_DELETED
        defaultProductShouldBeFound("deleted.equals=" + DEFAULT_DELETED);

        // Get all the productList where deleted equals to UPDATED_DELETED
        defaultProductShouldNotBeFound("deleted.equals=" + UPDATED_DELETED);
    }

    @Test
    @Transactional
    public void getAllProductsByDeletedIsInShouldWork() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where deleted in DEFAULT_DELETED or UPDATED_DELETED
        defaultProductShouldBeFound("deleted.in=" + DEFAULT_DELETED + "," + UPDATED_DELETED);

        // Get all the productList where deleted equals to UPDATED_DELETED
        defaultProductShouldNotBeFound("deleted.in=" + UPDATED_DELETED);
    }

    @Test
    @Transactional
    public void getAllProductsByDeletedIsNullOrNotNull() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where deleted is not null
        defaultProductShouldBeFound("deleted.specified=true");

        // Get all the productList where deleted is null
        defaultProductShouldNotBeFound("deleted.specified=false");
    }

    @Test
    @Transactional
    public void getAllProductsByDetailsIsEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where details equals to DEFAULT_DETAILS
        defaultProductShouldBeFound("details.equals=" + DEFAULT_DETAILS);

        // Get all the productList where details equals to UPDATED_DETAILS
        defaultProductShouldNotBeFound("details.equals=" + UPDATED_DETAILS);
    }

    @Test
    @Transactional
    public void getAllProductsByDetailsIsInShouldWork() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where details in DEFAULT_DETAILS or UPDATED_DETAILS
        defaultProductShouldBeFound("details.in=" + DEFAULT_DETAILS + "," + UPDATED_DETAILS);

        // Get all the productList where details equals to UPDATED_DETAILS
        defaultProductShouldNotBeFound("details.in=" + UPDATED_DETAILS);
    }

    @Test
    @Transactional
    public void getAllProductsByDetailsIsNullOrNotNull() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where details is not null
        defaultProductShouldBeFound("details.specified=true");

        // Get all the productList where details is null
        defaultProductShouldNotBeFound("details.specified=false");
    }
    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultProductShouldBeFound(String filter) throws Exception {
        restProductMockMvc.perform(get("/api/products?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(product.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].brandId").value(hasItem(DEFAULT_BRAND_ID.intValue())))
            .andExpect(jsonPath("$.[*].listPrice").value(hasItem(DEFAULT_LIST_PRICE.intValue())))
            .andExpect(jsonPath("$.[*].shopId").value(hasItem(DEFAULT_SHOP_ID.intValue())))
            .andExpect(jsonPath("$.[*].createdTime").value(hasItem(DEFAULT_CREATED_TIME.toString())))
            .andExpect(jsonPath("$.[*].updatedTime").value(hasItem(DEFAULT_UPDATED_TIME.toString())))
            .andExpect(jsonPath("$.[*].deleted").value(hasItem(DEFAULT_DELETED.booleanValue())))
            .andExpect(jsonPath("$.[*].details").value(hasItem(DEFAULT_DETAILS.toString())));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultProductShouldNotBeFound(String filter) throws Exception {
        restProductMockMvc.perform(get("/api/products?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }


    @Test
    @Transactional
    public void getNonExistingProduct() throws Exception {
        // Get the product
        restProductMockMvc.perform(get("/api/products/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateProduct() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);
        int databaseSizeBeforeUpdate = productRepository.findAll().size();

        // Update the product
        Product updatedProduct = productRepository.findOne(product.getId());
        // Disconnect from session so that the updates on updatedProduct are not directly saved in db
        em.detach(updatedProduct);
        updatedProduct
            .name(UPDATED_NAME)
            .brandId(UPDATED_BRAND_ID)
            .listPrice(UPDATED_LIST_PRICE)
            .shopId(UPDATED_SHOP_ID)
            .createdTime(UPDATED_CREATED_TIME)
            .updatedTime(UPDATED_UPDATED_TIME)
            .deleted(UPDATED_DELETED)
            .details(UPDATED_DETAILS);
        ProductDTO productDTO = productMapper.toDto(updatedProduct);

        restProductMockMvc.perform(put("/api/products")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(productDTO)))
            .andExpect(status().isOk());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeUpdate);
        Product testProduct = productList.get(productList.size() - 1);
        assertThat(testProduct.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testProduct.getBrandId()).isEqualTo(UPDATED_BRAND_ID);
        assertThat(testProduct.getListPrice()).isEqualTo(UPDATED_LIST_PRICE);
        assertThat(testProduct.getShopId()).isEqualTo(UPDATED_SHOP_ID);
        assertThat(testProduct.getCreatedTime()).isEqualTo(UPDATED_CREATED_TIME);
        assertThat(testProduct.getUpdatedTime()).isEqualTo(UPDATED_UPDATED_TIME);
        assertThat(testProduct.isDeleted()).isEqualTo(UPDATED_DELETED);
        assertThat(testProduct.getDetails()).isEqualTo(UPDATED_DETAILS);
    }

    @Test
    @Transactional
    public void updateNonExistingProduct() throws Exception {
        int databaseSizeBeforeUpdate = productRepository.findAll().size();

        // Create the Product
        ProductDTO productDTO = productMapper.toDto(product);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restProductMockMvc.perform(put("/api/products")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(productDTO)))
            .andExpect(status().isCreated());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteProduct() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);
        int databaseSizeBeforeDelete = productRepository.findAll().size();

        // Get the product
        restProductMockMvc.perform(delete("/api/products/{id}", product.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Product.class);
        Product product1 = new Product();
        product1.setId(1L);
        Product product2 = new Product();
        product2.setId(product1.getId());
        assertThat(product1).isEqualTo(product2);
        product2.setId(2L);
        assertThat(product1).isNotEqualTo(product2);
        product1.setId(null);
        assertThat(product1).isNotEqualTo(product2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProductDTO.class);
        ProductDTO productDTO1 = new ProductDTO();
        productDTO1.setId(1L);
        ProductDTO productDTO2 = new ProductDTO();
        assertThat(productDTO1).isNotEqualTo(productDTO2);
        productDTO2.setId(productDTO1.getId());
        assertThat(productDTO1).isEqualTo(productDTO2);
        productDTO2.setId(2L);
        assertThat(productDTO1).isNotEqualTo(productDTO2);
        productDTO1.setId(null);
        assertThat(productDTO1).isNotEqualTo(productDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(productMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(productMapper.fromId(null)).isNull();
    }
}
