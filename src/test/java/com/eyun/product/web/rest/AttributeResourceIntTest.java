package com.eyun.product.web.rest;

import com.eyun.product.ProductApp;

import com.eyun.product.config.SecurityBeanOverrideConfiguration;

import com.eyun.product.domain.Attribute;
import com.eyun.product.repository.AttributeRepository;
import com.eyun.product.service.AttributeService;
import com.eyun.product.service.dto.AttributeDTO;
import com.eyun.product.service.mapper.AttributeMapper;
import com.eyun.product.web.rest.errors.ExceptionTranslator;
import com.eyun.product.service.dto.AttributeCriteria;
import com.eyun.product.service.AttributeQueryService;

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
 * Test class for the AttributeResource REST controller.
 *
 * @see AttributeResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ProductApp.class, SecurityBeanOverrideConfiguration.class})
public class AttributeResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Long DEFAULT_PRODUCT_ID = 1L;
    private static final Long UPDATED_PRODUCT_ID = 2L;

    private static final Integer DEFAULT_STATUS = 1;
    private static final Integer UPDATED_STATUS = 2;

    private static final Instant DEFAULT_CREATED_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Integer DEFAULT_DELETED = 1;
    private static final Integer UPDATED_DELETED = 2;

    private static final Integer DEFAULT_RANK = 1;
    private static final Integer UPDATED_RANK = 2;

    @Autowired
    private AttributeRepository attributeRepository;

    @Autowired
    private AttributeMapper attributeMapper;

    @Autowired
    private AttributeService attributeService;

    @Autowired
    private AttributeQueryService attributeQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restAttributeMockMvc;

    private Attribute attribute;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final AttributeResource attributeResource = new AttributeResource(attributeService, attributeQueryService);
        this.restAttributeMockMvc = MockMvcBuilders.standaloneSetup(attributeResource)
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
    public static Attribute createEntity(EntityManager em) {
        Attribute attribute = new Attribute()
            .name(DEFAULT_NAME)
            .productId(DEFAULT_PRODUCT_ID)
            .status(DEFAULT_STATUS)
            .createdTime(DEFAULT_CREATED_TIME)
            .updatedTime(DEFAULT_UPDATED_TIME)
            .deleted(DEFAULT_DELETED)
            .rank(DEFAULT_RANK);
        return attribute;
    }

    @Before
    public void initTest() {
        attribute = createEntity(em);
    }

    @Test
    @Transactional
    public void createAttribute() throws Exception {
        int databaseSizeBeforeCreate = attributeRepository.findAll().size();

        // Create the Attribute
        AttributeDTO attributeDTO = attributeMapper.toDto(attribute);
        restAttributeMockMvc.perform(post("/api/attributes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(attributeDTO)))
            .andExpect(status().isCreated());

        // Validate the Attribute in the database
        List<Attribute> attributeList = attributeRepository.findAll();
        assertThat(attributeList).hasSize(databaseSizeBeforeCreate + 1);
        Attribute testAttribute = attributeList.get(attributeList.size() - 1);
        assertThat(testAttribute.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testAttribute.getProductId()).isEqualTo(DEFAULT_PRODUCT_ID);
        assertThat(testAttribute.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testAttribute.getCreatedTime()).isEqualTo(DEFAULT_CREATED_TIME);
        assertThat(testAttribute.getUpdatedTime()).isEqualTo(DEFAULT_UPDATED_TIME);
        assertThat(testAttribute.getDeleted()).isEqualTo(DEFAULT_DELETED);
        assertThat(testAttribute.getRank()).isEqualTo(DEFAULT_RANK);
    }

    @Test
    @Transactional
    public void createAttributeWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = attributeRepository.findAll().size();

        // Create the Attribute with an existing ID
        attribute.setId(1L);
        AttributeDTO attributeDTO = attributeMapper.toDto(attribute);

        // An entity with an existing ID cannot be created, so this API call must fail
        restAttributeMockMvc.perform(post("/api/attributes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(attributeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Attribute in the database
        List<Attribute> attributeList = attributeRepository.findAll();
        assertThat(attributeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = attributeRepository.findAll().size();
        // set the field null
        attribute.setName(null);

        // Create the Attribute, which fails.
        AttributeDTO attributeDTO = attributeMapper.toDto(attribute);

        restAttributeMockMvc.perform(post("/api/attributes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(attributeDTO)))
            .andExpect(status().isBadRequest());

        List<Attribute> attributeList = attributeRepository.findAll();
        assertThat(attributeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkProductIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = attributeRepository.findAll().size();
        // set the field null
        attribute.setProductId(null);

        // Create the Attribute, which fails.
        AttributeDTO attributeDTO = attributeMapper.toDto(attribute);

        restAttributeMockMvc.perform(post("/api/attributes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(attributeDTO)))
            .andExpect(status().isBadRequest());

        List<Attribute> attributeList = attributeRepository.findAll();
        assertThat(attributeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = attributeRepository.findAll().size();
        // set the field null
        attribute.setStatus(null);

        // Create the Attribute, which fails.
        AttributeDTO attributeDTO = attributeMapper.toDto(attribute);

        restAttributeMockMvc.perform(post("/api/attributes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(attributeDTO)))
            .andExpect(status().isBadRequest());

        List<Attribute> attributeList = attributeRepository.findAll();
        assertThat(attributeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllAttributes() throws Exception {
        // Initialize the database
        attributeRepository.saveAndFlush(attribute);

        // Get all the attributeList
        restAttributeMockMvc.perform(get("/api/attributes?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(attribute.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].productId").value(hasItem(DEFAULT_PRODUCT_ID.intValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
            .andExpect(jsonPath("$.[*].createdTime").value(hasItem(DEFAULT_CREATED_TIME.toString())))
            .andExpect(jsonPath("$.[*].updatedTime").value(hasItem(DEFAULT_UPDATED_TIME.toString())))
            .andExpect(jsonPath("$.[*].deleted").value(hasItem(DEFAULT_DELETED)))
            .andExpect(jsonPath("$.[*].rank").value(hasItem(DEFAULT_RANK)));
    }

    @Test
    @Transactional
    public void getAttribute() throws Exception {
        // Initialize the database
        attributeRepository.saveAndFlush(attribute);

        // Get the attribute
        restAttributeMockMvc.perform(get("/api/attributes/{id}", attribute.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(attribute.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.productId").value(DEFAULT_PRODUCT_ID.intValue()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS))
            .andExpect(jsonPath("$.createdTime").value(DEFAULT_CREATED_TIME.toString()))
            .andExpect(jsonPath("$.updatedTime").value(DEFAULT_UPDATED_TIME.toString()))
            .andExpect(jsonPath("$.deleted").value(DEFAULT_DELETED))
            .andExpect(jsonPath("$.rank").value(DEFAULT_RANK));
    }

    @Test
    @Transactional
    public void getAllAttributesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        attributeRepository.saveAndFlush(attribute);

        // Get all the attributeList where name equals to DEFAULT_NAME
        defaultAttributeShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the attributeList where name equals to UPDATED_NAME
        defaultAttributeShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllAttributesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        attributeRepository.saveAndFlush(attribute);

        // Get all the attributeList where name in DEFAULT_NAME or UPDATED_NAME
        defaultAttributeShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the attributeList where name equals to UPDATED_NAME
        defaultAttributeShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllAttributesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        attributeRepository.saveAndFlush(attribute);

        // Get all the attributeList where name is not null
        defaultAttributeShouldBeFound("name.specified=true");

        // Get all the attributeList where name is null
        defaultAttributeShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    public void getAllAttributesByProductIdIsEqualToSomething() throws Exception {
        // Initialize the database
        attributeRepository.saveAndFlush(attribute);

        // Get all the attributeList where productId equals to DEFAULT_PRODUCT_ID
        defaultAttributeShouldBeFound("productId.equals=" + DEFAULT_PRODUCT_ID);

        // Get all the attributeList where productId equals to UPDATED_PRODUCT_ID
        defaultAttributeShouldNotBeFound("productId.equals=" + UPDATED_PRODUCT_ID);
    }

    @Test
    @Transactional
    public void getAllAttributesByProductIdIsInShouldWork() throws Exception {
        // Initialize the database
        attributeRepository.saveAndFlush(attribute);

        // Get all the attributeList where productId in DEFAULT_PRODUCT_ID or UPDATED_PRODUCT_ID
        defaultAttributeShouldBeFound("productId.in=" + DEFAULT_PRODUCT_ID + "," + UPDATED_PRODUCT_ID);

        // Get all the attributeList where productId equals to UPDATED_PRODUCT_ID
        defaultAttributeShouldNotBeFound("productId.in=" + UPDATED_PRODUCT_ID);
    }

    @Test
    @Transactional
    public void getAllAttributesByProductIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        attributeRepository.saveAndFlush(attribute);

        // Get all the attributeList where productId is not null
        defaultAttributeShouldBeFound("productId.specified=true");

        // Get all the attributeList where productId is null
        defaultAttributeShouldNotBeFound("productId.specified=false");
    }

    @Test
    @Transactional
    public void getAllAttributesByProductIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        attributeRepository.saveAndFlush(attribute);

        // Get all the attributeList where productId greater than or equals to DEFAULT_PRODUCT_ID
        defaultAttributeShouldBeFound("productId.greaterOrEqualThan=" + DEFAULT_PRODUCT_ID);

        // Get all the attributeList where productId greater than or equals to UPDATED_PRODUCT_ID
        defaultAttributeShouldNotBeFound("productId.greaterOrEqualThan=" + UPDATED_PRODUCT_ID);
    }

    @Test
    @Transactional
    public void getAllAttributesByProductIdIsLessThanSomething() throws Exception {
        // Initialize the database
        attributeRepository.saveAndFlush(attribute);

        // Get all the attributeList where productId less than or equals to DEFAULT_PRODUCT_ID
        defaultAttributeShouldNotBeFound("productId.lessThan=" + DEFAULT_PRODUCT_ID);

        // Get all the attributeList where productId less than or equals to UPDATED_PRODUCT_ID
        defaultAttributeShouldBeFound("productId.lessThan=" + UPDATED_PRODUCT_ID);
    }


    @Test
    @Transactional
    public void getAllAttributesByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        attributeRepository.saveAndFlush(attribute);

        // Get all the attributeList where status equals to DEFAULT_STATUS
        defaultAttributeShouldBeFound("status.equals=" + DEFAULT_STATUS);

        // Get all the attributeList where status equals to UPDATED_STATUS
        defaultAttributeShouldNotBeFound("status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void getAllAttributesByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        attributeRepository.saveAndFlush(attribute);

        // Get all the attributeList where status in DEFAULT_STATUS or UPDATED_STATUS
        defaultAttributeShouldBeFound("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS);

        // Get all the attributeList where status equals to UPDATED_STATUS
        defaultAttributeShouldNotBeFound("status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void getAllAttributesByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        attributeRepository.saveAndFlush(attribute);

        // Get all the attributeList where status is not null
        defaultAttributeShouldBeFound("status.specified=true");

        // Get all the attributeList where status is null
        defaultAttributeShouldNotBeFound("status.specified=false");
    }

    @Test
    @Transactional
    public void getAllAttributesByStatusIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        attributeRepository.saveAndFlush(attribute);

        // Get all the attributeList where status greater than or equals to DEFAULT_STATUS
        defaultAttributeShouldBeFound("status.greaterOrEqualThan=" + DEFAULT_STATUS);

        // Get all the attributeList where status greater than or equals to UPDATED_STATUS
        defaultAttributeShouldNotBeFound("status.greaterOrEqualThan=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void getAllAttributesByStatusIsLessThanSomething() throws Exception {
        // Initialize the database
        attributeRepository.saveAndFlush(attribute);

        // Get all the attributeList where status less than or equals to DEFAULT_STATUS
        defaultAttributeShouldNotBeFound("status.lessThan=" + DEFAULT_STATUS);

        // Get all the attributeList where status less than or equals to UPDATED_STATUS
        defaultAttributeShouldBeFound("status.lessThan=" + UPDATED_STATUS);
    }


    @Test
    @Transactional
    public void getAllAttributesByCreatedTimeIsEqualToSomething() throws Exception {
        // Initialize the database
        attributeRepository.saveAndFlush(attribute);

        // Get all the attributeList where createdTime equals to DEFAULT_CREATED_TIME
        defaultAttributeShouldBeFound("createdTime.equals=" + DEFAULT_CREATED_TIME);

        // Get all the attributeList where createdTime equals to UPDATED_CREATED_TIME
        defaultAttributeShouldNotBeFound("createdTime.equals=" + UPDATED_CREATED_TIME);
    }

    @Test
    @Transactional
    public void getAllAttributesByCreatedTimeIsInShouldWork() throws Exception {
        // Initialize the database
        attributeRepository.saveAndFlush(attribute);

        // Get all the attributeList where createdTime in DEFAULT_CREATED_TIME or UPDATED_CREATED_TIME
        defaultAttributeShouldBeFound("createdTime.in=" + DEFAULT_CREATED_TIME + "," + UPDATED_CREATED_TIME);

        // Get all the attributeList where createdTime equals to UPDATED_CREATED_TIME
        defaultAttributeShouldNotBeFound("createdTime.in=" + UPDATED_CREATED_TIME);
    }

    @Test
    @Transactional
    public void getAllAttributesByCreatedTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        attributeRepository.saveAndFlush(attribute);

        // Get all the attributeList where createdTime is not null
        defaultAttributeShouldBeFound("createdTime.specified=true");

        // Get all the attributeList where createdTime is null
        defaultAttributeShouldNotBeFound("createdTime.specified=false");
    }

    @Test
    @Transactional
    public void getAllAttributesByUpdatedTimeIsEqualToSomething() throws Exception {
        // Initialize the database
        attributeRepository.saveAndFlush(attribute);

        // Get all the attributeList where updatedTime equals to DEFAULT_UPDATED_TIME
        defaultAttributeShouldBeFound("updatedTime.equals=" + DEFAULT_UPDATED_TIME);

        // Get all the attributeList where updatedTime equals to UPDATED_UPDATED_TIME
        defaultAttributeShouldNotBeFound("updatedTime.equals=" + UPDATED_UPDATED_TIME);
    }

    @Test
    @Transactional
    public void getAllAttributesByUpdatedTimeIsInShouldWork() throws Exception {
        // Initialize the database
        attributeRepository.saveAndFlush(attribute);

        // Get all the attributeList where updatedTime in DEFAULT_UPDATED_TIME or UPDATED_UPDATED_TIME
        defaultAttributeShouldBeFound("updatedTime.in=" + DEFAULT_UPDATED_TIME + "," + UPDATED_UPDATED_TIME);

        // Get all the attributeList where updatedTime equals to UPDATED_UPDATED_TIME
        defaultAttributeShouldNotBeFound("updatedTime.in=" + UPDATED_UPDATED_TIME);
    }

    @Test
    @Transactional
    public void getAllAttributesByUpdatedTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        attributeRepository.saveAndFlush(attribute);

        // Get all the attributeList where updatedTime is not null
        defaultAttributeShouldBeFound("updatedTime.specified=true");

        // Get all the attributeList where updatedTime is null
        defaultAttributeShouldNotBeFound("updatedTime.specified=false");
    }

    @Test
    @Transactional
    public void getAllAttributesByDeletedIsEqualToSomething() throws Exception {
        // Initialize the database
        attributeRepository.saveAndFlush(attribute);

        // Get all the attributeList where deleted equals to DEFAULT_DELETED
        defaultAttributeShouldBeFound("deleted.equals=" + DEFAULT_DELETED);

        // Get all the attributeList where deleted equals to UPDATED_DELETED
        defaultAttributeShouldNotBeFound("deleted.equals=" + UPDATED_DELETED);
    }

    @Test
    @Transactional
    public void getAllAttributesByDeletedIsInShouldWork() throws Exception {
        // Initialize the database
        attributeRepository.saveAndFlush(attribute);

        // Get all the attributeList where deleted in DEFAULT_DELETED or UPDATED_DELETED
        defaultAttributeShouldBeFound("deleted.in=" + DEFAULT_DELETED + "," + UPDATED_DELETED);

        // Get all the attributeList where deleted equals to UPDATED_DELETED
        defaultAttributeShouldNotBeFound("deleted.in=" + UPDATED_DELETED);
    }

    @Test
    @Transactional
    public void getAllAttributesByDeletedIsNullOrNotNull() throws Exception {
        // Initialize the database
        attributeRepository.saveAndFlush(attribute);

        // Get all the attributeList where deleted is not null
        defaultAttributeShouldBeFound("deleted.specified=true");

        // Get all the attributeList where deleted is null
        defaultAttributeShouldNotBeFound("deleted.specified=false");
    }

    @Test
    @Transactional
    public void getAllAttributesByDeletedIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        attributeRepository.saveAndFlush(attribute);

        // Get all the attributeList where deleted greater than or equals to DEFAULT_DELETED
        defaultAttributeShouldBeFound("deleted.greaterOrEqualThan=" + DEFAULT_DELETED);

        // Get all the attributeList where deleted greater than or equals to UPDATED_DELETED
        defaultAttributeShouldNotBeFound("deleted.greaterOrEqualThan=" + UPDATED_DELETED);
    }

    @Test
    @Transactional
    public void getAllAttributesByDeletedIsLessThanSomething() throws Exception {
        // Initialize the database
        attributeRepository.saveAndFlush(attribute);

        // Get all the attributeList where deleted less than or equals to DEFAULT_DELETED
        defaultAttributeShouldNotBeFound("deleted.lessThan=" + DEFAULT_DELETED);

        // Get all the attributeList where deleted less than or equals to UPDATED_DELETED
        defaultAttributeShouldBeFound("deleted.lessThan=" + UPDATED_DELETED);
    }


    @Test
    @Transactional
    public void getAllAttributesByRankIsEqualToSomething() throws Exception {
        // Initialize the database
        attributeRepository.saveAndFlush(attribute);

        // Get all the attributeList where rank equals to DEFAULT_RANK
        defaultAttributeShouldBeFound("rank.equals=" + DEFAULT_RANK);

        // Get all the attributeList where rank equals to UPDATED_RANK
        defaultAttributeShouldNotBeFound("rank.equals=" + UPDATED_RANK);
    }

    @Test
    @Transactional
    public void getAllAttributesByRankIsInShouldWork() throws Exception {
        // Initialize the database
        attributeRepository.saveAndFlush(attribute);

        // Get all the attributeList where rank in DEFAULT_RANK or UPDATED_RANK
        defaultAttributeShouldBeFound("rank.in=" + DEFAULT_RANK + "," + UPDATED_RANK);

        // Get all the attributeList where rank equals to UPDATED_RANK
        defaultAttributeShouldNotBeFound("rank.in=" + UPDATED_RANK);
    }

    @Test
    @Transactional
    public void getAllAttributesByRankIsNullOrNotNull() throws Exception {
        // Initialize the database
        attributeRepository.saveAndFlush(attribute);

        // Get all the attributeList where rank is not null
        defaultAttributeShouldBeFound("rank.specified=true");

        // Get all the attributeList where rank is null
        defaultAttributeShouldNotBeFound("rank.specified=false");
    }

    @Test
    @Transactional
    public void getAllAttributesByRankIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        attributeRepository.saveAndFlush(attribute);

        // Get all the attributeList where rank greater than or equals to DEFAULT_RANK
        defaultAttributeShouldBeFound("rank.greaterOrEqualThan=" + DEFAULT_RANK);

        // Get all the attributeList where rank greater than or equals to UPDATED_RANK
        defaultAttributeShouldNotBeFound("rank.greaterOrEqualThan=" + UPDATED_RANK);
    }

    @Test
    @Transactional
    public void getAllAttributesByRankIsLessThanSomething() throws Exception {
        // Initialize the database
        attributeRepository.saveAndFlush(attribute);

        // Get all the attributeList where rank less than or equals to DEFAULT_RANK
        defaultAttributeShouldNotBeFound("rank.lessThan=" + DEFAULT_RANK);

        // Get all the attributeList where rank less than or equals to UPDATED_RANK
        defaultAttributeShouldBeFound("rank.lessThan=" + UPDATED_RANK);
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultAttributeShouldBeFound(String filter) throws Exception {
        restAttributeMockMvc.perform(get("/api/attributes?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(attribute.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].productId").value(hasItem(DEFAULT_PRODUCT_ID.intValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
            .andExpect(jsonPath("$.[*].createdTime").value(hasItem(DEFAULT_CREATED_TIME.toString())))
            .andExpect(jsonPath("$.[*].updatedTime").value(hasItem(DEFAULT_UPDATED_TIME.toString())))
            .andExpect(jsonPath("$.[*].deleted").value(hasItem(DEFAULT_DELETED)))
            .andExpect(jsonPath("$.[*].rank").value(hasItem(DEFAULT_RANK)));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultAttributeShouldNotBeFound(String filter) throws Exception {
        restAttributeMockMvc.perform(get("/api/attributes?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }


    @Test
    @Transactional
    public void getNonExistingAttribute() throws Exception {
        // Get the attribute
        restAttributeMockMvc.perform(get("/api/attributes/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateAttribute() throws Exception {
        // Initialize the database
        attributeRepository.saveAndFlush(attribute);
        int databaseSizeBeforeUpdate = attributeRepository.findAll().size();

        // Update the attribute
        Attribute updatedAttribute = attributeRepository.findOne(attribute.getId());
        // Disconnect from session so that the updates on updatedAttribute are not directly saved in db
        em.detach(updatedAttribute);
        updatedAttribute
            .name(UPDATED_NAME)
            .productId(UPDATED_PRODUCT_ID)
            .status(UPDATED_STATUS)
            .createdTime(UPDATED_CREATED_TIME)
            .updatedTime(UPDATED_UPDATED_TIME)
            .deleted(UPDATED_DELETED)
            .rank(UPDATED_RANK);
        AttributeDTO attributeDTO = attributeMapper.toDto(updatedAttribute);

        restAttributeMockMvc.perform(put("/api/attributes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(attributeDTO)))
            .andExpect(status().isOk());

        // Validate the Attribute in the database
        List<Attribute> attributeList = attributeRepository.findAll();
        assertThat(attributeList).hasSize(databaseSizeBeforeUpdate);
        Attribute testAttribute = attributeList.get(attributeList.size() - 1);
        assertThat(testAttribute.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testAttribute.getProductId()).isEqualTo(UPDATED_PRODUCT_ID);
        assertThat(testAttribute.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testAttribute.getCreatedTime()).isEqualTo(UPDATED_CREATED_TIME);
        assertThat(testAttribute.getUpdatedTime()).isEqualTo(UPDATED_UPDATED_TIME);
        assertThat(testAttribute.getDeleted()).isEqualTo(UPDATED_DELETED);
        assertThat(testAttribute.getRank()).isEqualTo(UPDATED_RANK);
    }

    @Test
    @Transactional
    public void updateNonExistingAttribute() throws Exception {
        int databaseSizeBeforeUpdate = attributeRepository.findAll().size();

        // Create the Attribute
        AttributeDTO attributeDTO = attributeMapper.toDto(attribute);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restAttributeMockMvc.perform(put("/api/attributes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(attributeDTO)))
            .andExpect(status().isCreated());

        // Validate the Attribute in the database
        List<Attribute> attributeList = attributeRepository.findAll();
        assertThat(attributeList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteAttribute() throws Exception {
        // Initialize the database
        attributeRepository.saveAndFlush(attribute);
        int databaseSizeBeforeDelete = attributeRepository.findAll().size();

        // Get the attribute
        restAttributeMockMvc.perform(delete("/api/attributes/{id}", attribute.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Attribute> attributeList = attributeRepository.findAll();
        assertThat(attributeList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Attribute.class);
        Attribute attribute1 = new Attribute();
        attribute1.setId(1L);
        Attribute attribute2 = new Attribute();
        attribute2.setId(attribute1.getId());
        assertThat(attribute1).isEqualTo(attribute2);
        attribute2.setId(2L);
        assertThat(attribute1).isNotEqualTo(attribute2);
        attribute1.setId(null);
        assertThat(attribute1).isNotEqualTo(attribute2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(AttributeDTO.class);
        AttributeDTO attributeDTO1 = new AttributeDTO();
        attributeDTO1.setId(1L);
        AttributeDTO attributeDTO2 = new AttributeDTO();
        assertThat(attributeDTO1).isNotEqualTo(attributeDTO2);
        attributeDTO2.setId(attributeDTO1.getId());
        assertThat(attributeDTO1).isEqualTo(attributeDTO2);
        attributeDTO2.setId(2L);
        assertThat(attributeDTO1).isNotEqualTo(attributeDTO2);
        attributeDTO1.setId(null);
        assertThat(attributeDTO1).isNotEqualTo(attributeDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(attributeMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(attributeMapper.fromId(null)).isNull();
    }
}
