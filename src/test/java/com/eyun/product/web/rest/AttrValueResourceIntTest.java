package com.eyun.product.web.rest;

import com.eyun.product.ProductApp;

import com.eyun.product.config.SecurityBeanOverrideConfiguration;

import com.eyun.product.domain.AttrValue;
import com.eyun.product.repository.AttrValueRepository;
import com.eyun.product.service.AttrValueService;
import com.eyun.product.service.dto.AttrValueDTO;
import com.eyun.product.service.mapper.AttrValueMapper;
import com.eyun.product.web.rest.errors.ExceptionTranslator;
import com.eyun.product.service.dto.AttrValueCriteria;
import com.eyun.product.service.AttrValueQueryService;

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
 * Test class for the AttrValueResource REST controller.
 *
 * @see AttrValueResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ProductApp.class, SecurityBeanOverrideConfiguration.class})
public class AttrValueResourceIntTest {

    private static final Long DEFAULT_ATTR_ID = 1L;
    private static final Long UPDATED_ATTR_ID = 2L;

    private static final String DEFAULT_VALUE = "AAAAAAAAAA";
    private static final String UPDATED_VALUE = "BBBBBBBBBB";

    private static final String DEFAULT_IMAGE = "AAAAAAAAAA";
    private static final String UPDATED_IMAGE = "BBBBBBBBBB";

    private static final Integer DEFAULT_STATUS = 1;
    private static final Integer UPDATED_STATUS = 2;

    private static final Integer DEFAULT_SORT = 1;
    private static final Integer UPDATED_SORT = 2;

    private static final Instant DEFAULT_CREATED_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Boolean DEFAULT_DELETED = false;
    private static final Boolean UPDATED_DELETED = true;

    @Autowired
    private AttrValueRepository attrValueRepository;

    @Autowired
    private AttrValueMapper attrValueMapper;

    @Autowired
    private AttrValueService attrValueService;

    @Autowired
    private AttrValueQueryService attrValueQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restAttrValueMockMvc;

    private AttrValue attrValue;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final AttrValueResource attrValueResource = new AttrValueResource(attrValueService, attrValueQueryService);
        this.restAttrValueMockMvc = MockMvcBuilders.standaloneSetup(attrValueResource)
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
    public static AttrValue createEntity(EntityManager em) {
        AttrValue attrValue = new AttrValue()
            .attrId(DEFAULT_ATTR_ID)
            .value(DEFAULT_VALUE)
            .image(DEFAULT_IMAGE)
            .status(DEFAULT_STATUS)
            .sort(DEFAULT_SORT)
            .createdTime(DEFAULT_CREATED_TIME)
            .updatedTime(DEFAULT_UPDATED_TIME)
            .deleted(DEFAULT_DELETED);
        return attrValue;
    }

    @Before
    public void initTest() {
        attrValue = createEntity(em);
    }

    @Test
    @Transactional
    public void createAttrValue() throws Exception {
        int databaseSizeBeforeCreate = attrValueRepository.findAll().size();

        // Create the AttrValue
        AttrValueDTO attrValueDTO = attrValueMapper.toDto(attrValue);
        restAttrValueMockMvc.perform(post("/api/attr-values")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(attrValueDTO)))
            .andExpect(status().isCreated());

        // Validate the AttrValue in the database
        List<AttrValue> attrValueList = attrValueRepository.findAll();
        assertThat(attrValueList).hasSize(databaseSizeBeforeCreate + 1);
        AttrValue testAttrValue = attrValueList.get(attrValueList.size() - 1);
        assertThat(testAttrValue.getAttrId()).isEqualTo(DEFAULT_ATTR_ID);
        assertThat(testAttrValue.getValue()).isEqualTo(DEFAULT_VALUE);
        assertThat(testAttrValue.getImage()).isEqualTo(DEFAULT_IMAGE);
        assertThat(testAttrValue.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testAttrValue.getSort()).isEqualTo(DEFAULT_SORT);
        assertThat(testAttrValue.getCreatedTime()).isEqualTo(DEFAULT_CREATED_TIME);
        assertThat(testAttrValue.getUpdatedTime()).isEqualTo(DEFAULT_UPDATED_TIME);
        assertThat(testAttrValue.isDeleted()).isEqualTo(DEFAULT_DELETED);
    }

    @Test
    @Transactional
    public void createAttrValueWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = attrValueRepository.findAll().size();

        // Create the AttrValue with an existing ID
        attrValue.setId(1L);
        AttrValueDTO attrValueDTO = attrValueMapper.toDto(attrValue);

        // An entity with an existing ID cannot be created, so this API call must fail
        restAttrValueMockMvc.perform(post("/api/attr-values")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(attrValueDTO)))
            .andExpect(status().isBadRequest());

        // Validate the AttrValue in the database
        List<AttrValue> attrValueList = attrValueRepository.findAll();
        assertThat(attrValueList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkAttrIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = attrValueRepository.findAll().size();
        // set the field null
        attrValue.setAttrId(null);

        // Create the AttrValue, which fails.
        AttrValueDTO attrValueDTO = attrValueMapper.toDto(attrValue);

        restAttrValueMockMvc.perform(post("/api/attr-values")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(attrValueDTO)))
            .andExpect(status().isBadRequest());

        List<AttrValue> attrValueList = attrValueRepository.findAll();
        assertThat(attrValueList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllAttrValues() throws Exception {
        // Initialize the database
        attrValueRepository.saveAndFlush(attrValue);

        // Get all the attrValueList
        restAttrValueMockMvc.perform(get("/api/attr-values?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(attrValue.getId().intValue())))
            .andExpect(jsonPath("$.[*].attrId").value(hasItem(DEFAULT_ATTR_ID.intValue())))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE.toString())))
            .andExpect(jsonPath("$.[*].image").value(hasItem(DEFAULT_IMAGE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
            .andExpect(jsonPath("$.[*].sort").value(hasItem(DEFAULT_SORT)))
            .andExpect(jsonPath("$.[*].createdTime").value(hasItem(DEFAULT_CREATED_TIME.toString())))
            .andExpect(jsonPath("$.[*].updatedTime").value(hasItem(DEFAULT_UPDATED_TIME.toString())))
            .andExpect(jsonPath("$.[*].deleted").value(hasItem(DEFAULT_DELETED.booleanValue())));
    }

    @Test
    @Transactional
    public void getAttrValue() throws Exception {
        // Initialize the database
        attrValueRepository.saveAndFlush(attrValue);

        // Get the attrValue
        restAttrValueMockMvc.perform(get("/api/attr-values/{id}", attrValue.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(attrValue.getId().intValue()))
            .andExpect(jsonPath("$.attrId").value(DEFAULT_ATTR_ID.intValue()))
            .andExpect(jsonPath("$.value").value(DEFAULT_VALUE.toString()))
            .andExpect(jsonPath("$.image").value(DEFAULT_IMAGE.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS))
            .andExpect(jsonPath("$.sort").value(DEFAULT_SORT))
            .andExpect(jsonPath("$.createdTime").value(DEFAULT_CREATED_TIME.toString()))
            .andExpect(jsonPath("$.updatedTime").value(DEFAULT_UPDATED_TIME.toString()))
            .andExpect(jsonPath("$.deleted").value(DEFAULT_DELETED.booleanValue()));
    }

    @Test
    @Transactional
    public void getAllAttrValuesByAttrIdIsEqualToSomething() throws Exception {
        // Initialize the database
        attrValueRepository.saveAndFlush(attrValue);

        // Get all the attrValueList where attrId equals to DEFAULT_ATTR_ID
        defaultAttrValueShouldBeFound("attrId.equals=" + DEFAULT_ATTR_ID);

        // Get all the attrValueList where attrId equals to UPDATED_ATTR_ID
        defaultAttrValueShouldNotBeFound("attrId.equals=" + UPDATED_ATTR_ID);
    }

    @Test
    @Transactional
    public void getAllAttrValuesByAttrIdIsInShouldWork() throws Exception {
        // Initialize the database
        attrValueRepository.saveAndFlush(attrValue);

        // Get all the attrValueList where attrId in DEFAULT_ATTR_ID or UPDATED_ATTR_ID
        defaultAttrValueShouldBeFound("attrId.in=" + DEFAULT_ATTR_ID + "," + UPDATED_ATTR_ID);

        // Get all the attrValueList where attrId equals to UPDATED_ATTR_ID
        defaultAttrValueShouldNotBeFound("attrId.in=" + UPDATED_ATTR_ID);
    }

    @Test
    @Transactional
    public void getAllAttrValuesByAttrIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        attrValueRepository.saveAndFlush(attrValue);

        // Get all the attrValueList where attrId is not null
        defaultAttrValueShouldBeFound("attrId.specified=true");

        // Get all the attrValueList where attrId is null
        defaultAttrValueShouldNotBeFound("attrId.specified=false");
    }

    @Test
    @Transactional
    public void getAllAttrValuesByAttrIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        attrValueRepository.saveAndFlush(attrValue);

        // Get all the attrValueList where attrId greater than or equals to DEFAULT_ATTR_ID
        defaultAttrValueShouldBeFound("attrId.greaterOrEqualThan=" + DEFAULT_ATTR_ID);

        // Get all the attrValueList where attrId greater than or equals to UPDATED_ATTR_ID
        defaultAttrValueShouldNotBeFound("attrId.greaterOrEqualThan=" + UPDATED_ATTR_ID);
    }

    @Test
    @Transactional
    public void getAllAttrValuesByAttrIdIsLessThanSomething() throws Exception {
        // Initialize the database
        attrValueRepository.saveAndFlush(attrValue);

        // Get all the attrValueList where attrId less than or equals to DEFAULT_ATTR_ID
        defaultAttrValueShouldNotBeFound("attrId.lessThan=" + DEFAULT_ATTR_ID);

        // Get all the attrValueList where attrId less than or equals to UPDATED_ATTR_ID
        defaultAttrValueShouldBeFound("attrId.lessThan=" + UPDATED_ATTR_ID);
    }


    @Test
    @Transactional
    public void getAllAttrValuesByValueIsEqualToSomething() throws Exception {
        // Initialize the database
        attrValueRepository.saveAndFlush(attrValue);

        // Get all the attrValueList where value equals to DEFAULT_VALUE
        defaultAttrValueShouldBeFound("value.equals=" + DEFAULT_VALUE);

        // Get all the attrValueList where value equals to UPDATED_VALUE
        defaultAttrValueShouldNotBeFound("value.equals=" + UPDATED_VALUE);
    }

    @Test
    @Transactional
    public void getAllAttrValuesByValueIsInShouldWork() throws Exception {
        // Initialize the database
        attrValueRepository.saveAndFlush(attrValue);

        // Get all the attrValueList where value in DEFAULT_VALUE or UPDATED_VALUE
        defaultAttrValueShouldBeFound("value.in=" + DEFAULT_VALUE + "," + UPDATED_VALUE);

        // Get all the attrValueList where value equals to UPDATED_VALUE
        defaultAttrValueShouldNotBeFound("value.in=" + UPDATED_VALUE);
    }

    @Test
    @Transactional
    public void getAllAttrValuesByValueIsNullOrNotNull() throws Exception {
        // Initialize the database
        attrValueRepository.saveAndFlush(attrValue);

        // Get all the attrValueList where value is not null
        defaultAttrValueShouldBeFound("value.specified=true");

        // Get all the attrValueList where value is null
        defaultAttrValueShouldNotBeFound("value.specified=false");
    }

    @Test
    @Transactional
    public void getAllAttrValuesByImageIsEqualToSomething() throws Exception {
        // Initialize the database
        attrValueRepository.saveAndFlush(attrValue);

        // Get all the attrValueList where image equals to DEFAULT_IMAGE
        defaultAttrValueShouldBeFound("image.equals=" + DEFAULT_IMAGE);

        // Get all the attrValueList where image equals to UPDATED_IMAGE
        defaultAttrValueShouldNotBeFound("image.equals=" + UPDATED_IMAGE);
    }

    @Test
    @Transactional
    public void getAllAttrValuesByImageIsInShouldWork() throws Exception {
        // Initialize the database
        attrValueRepository.saveAndFlush(attrValue);

        // Get all the attrValueList where image in DEFAULT_IMAGE or UPDATED_IMAGE
        defaultAttrValueShouldBeFound("image.in=" + DEFAULT_IMAGE + "," + UPDATED_IMAGE);

        // Get all the attrValueList where image equals to UPDATED_IMAGE
        defaultAttrValueShouldNotBeFound("image.in=" + UPDATED_IMAGE);
    }

    @Test
    @Transactional
    public void getAllAttrValuesByImageIsNullOrNotNull() throws Exception {
        // Initialize the database
        attrValueRepository.saveAndFlush(attrValue);

        // Get all the attrValueList where image is not null
        defaultAttrValueShouldBeFound("image.specified=true");

        // Get all the attrValueList where image is null
        defaultAttrValueShouldNotBeFound("image.specified=false");
    }

    @Test
    @Transactional
    public void getAllAttrValuesByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        attrValueRepository.saveAndFlush(attrValue);

        // Get all the attrValueList where status equals to DEFAULT_STATUS
        defaultAttrValueShouldBeFound("status.equals=" + DEFAULT_STATUS);

        // Get all the attrValueList where status equals to UPDATED_STATUS
        defaultAttrValueShouldNotBeFound("status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void getAllAttrValuesByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        attrValueRepository.saveAndFlush(attrValue);

        // Get all the attrValueList where status in DEFAULT_STATUS or UPDATED_STATUS
        defaultAttrValueShouldBeFound("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS);

        // Get all the attrValueList where status equals to UPDATED_STATUS
        defaultAttrValueShouldNotBeFound("status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void getAllAttrValuesByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        attrValueRepository.saveAndFlush(attrValue);

        // Get all the attrValueList where status is not null
        defaultAttrValueShouldBeFound("status.specified=true");

        // Get all the attrValueList where status is null
        defaultAttrValueShouldNotBeFound("status.specified=false");
    }

    @Test
    @Transactional
    public void getAllAttrValuesByStatusIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        attrValueRepository.saveAndFlush(attrValue);

        // Get all the attrValueList where status greater than or equals to DEFAULT_STATUS
        defaultAttrValueShouldBeFound("status.greaterOrEqualThan=" + DEFAULT_STATUS);

        // Get all the attrValueList where status greater than or equals to UPDATED_STATUS
        defaultAttrValueShouldNotBeFound("status.greaterOrEqualThan=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void getAllAttrValuesByStatusIsLessThanSomething() throws Exception {
        // Initialize the database
        attrValueRepository.saveAndFlush(attrValue);

        // Get all the attrValueList where status less than or equals to DEFAULT_STATUS
        defaultAttrValueShouldNotBeFound("status.lessThan=" + DEFAULT_STATUS);

        // Get all the attrValueList where status less than or equals to UPDATED_STATUS
        defaultAttrValueShouldBeFound("status.lessThan=" + UPDATED_STATUS);
    }


    @Test
    @Transactional
    public void getAllAttrValuesBySortIsEqualToSomething() throws Exception {
        // Initialize the database
        attrValueRepository.saveAndFlush(attrValue);

        // Get all the attrValueList where sort equals to DEFAULT_SORT
        defaultAttrValueShouldBeFound("sort.equals=" + DEFAULT_SORT);

        // Get all the attrValueList where sort equals to UPDATED_SORT
        defaultAttrValueShouldNotBeFound("sort.equals=" + UPDATED_SORT);
    }

    @Test
    @Transactional
    public void getAllAttrValuesBySortIsInShouldWork() throws Exception {
        // Initialize the database
        attrValueRepository.saveAndFlush(attrValue);

        // Get all the attrValueList where sort in DEFAULT_SORT or UPDATED_SORT
        defaultAttrValueShouldBeFound("sort.in=" + DEFAULT_SORT + "," + UPDATED_SORT);

        // Get all the attrValueList where sort equals to UPDATED_SORT
        defaultAttrValueShouldNotBeFound("sort.in=" + UPDATED_SORT);
    }

    @Test
    @Transactional
    public void getAllAttrValuesBySortIsNullOrNotNull() throws Exception {
        // Initialize the database
        attrValueRepository.saveAndFlush(attrValue);

        // Get all the attrValueList where sort is not null
        defaultAttrValueShouldBeFound("sort.specified=true");

        // Get all the attrValueList where sort is null
        defaultAttrValueShouldNotBeFound("sort.specified=false");
    }

    @Test
    @Transactional
    public void getAllAttrValuesBySortIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        attrValueRepository.saveAndFlush(attrValue);

        // Get all the attrValueList where sort greater than or equals to DEFAULT_SORT
        defaultAttrValueShouldBeFound("sort.greaterOrEqualThan=" + DEFAULT_SORT);

        // Get all the attrValueList where sort greater than or equals to UPDATED_SORT
        defaultAttrValueShouldNotBeFound("sort.greaterOrEqualThan=" + UPDATED_SORT);
    }

    @Test
    @Transactional
    public void getAllAttrValuesBySortIsLessThanSomething() throws Exception {
        // Initialize the database
        attrValueRepository.saveAndFlush(attrValue);

        // Get all the attrValueList where sort less than or equals to DEFAULT_SORT
        defaultAttrValueShouldNotBeFound("sort.lessThan=" + DEFAULT_SORT);

        // Get all the attrValueList where sort less than or equals to UPDATED_SORT
        defaultAttrValueShouldBeFound("sort.lessThan=" + UPDATED_SORT);
    }


    @Test
    @Transactional
    public void getAllAttrValuesByCreatedTimeIsEqualToSomething() throws Exception {
        // Initialize the database
        attrValueRepository.saveAndFlush(attrValue);

        // Get all the attrValueList where createdTime equals to DEFAULT_CREATED_TIME
        defaultAttrValueShouldBeFound("createdTime.equals=" + DEFAULT_CREATED_TIME);

        // Get all the attrValueList where createdTime equals to UPDATED_CREATED_TIME
        defaultAttrValueShouldNotBeFound("createdTime.equals=" + UPDATED_CREATED_TIME);
    }

    @Test
    @Transactional
    public void getAllAttrValuesByCreatedTimeIsInShouldWork() throws Exception {
        // Initialize the database
        attrValueRepository.saveAndFlush(attrValue);

        // Get all the attrValueList where createdTime in DEFAULT_CREATED_TIME or UPDATED_CREATED_TIME
        defaultAttrValueShouldBeFound("createdTime.in=" + DEFAULT_CREATED_TIME + "," + UPDATED_CREATED_TIME);

        // Get all the attrValueList where createdTime equals to UPDATED_CREATED_TIME
        defaultAttrValueShouldNotBeFound("createdTime.in=" + UPDATED_CREATED_TIME);
    }

    @Test
    @Transactional
    public void getAllAttrValuesByCreatedTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        attrValueRepository.saveAndFlush(attrValue);

        // Get all the attrValueList where createdTime is not null
        defaultAttrValueShouldBeFound("createdTime.specified=true");

        // Get all the attrValueList where createdTime is null
        defaultAttrValueShouldNotBeFound("createdTime.specified=false");
    }

    @Test
    @Transactional
    public void getAllAttrValuesByUpdatedTimeIsEqualToSomething() throws Exception {
        // Initialize the database
        attrValueRepository.saveAndFlush(attrValue);

        // Get all the attrValueList where updatedTime equals to DEFAULT_UPDATED_TIME
        defaultAttrValueShouldBeFound("updatedTime.equals=" + DEFAULT_UPDATED_TIME);

        // Get all the attrValueList where updatedTime equals to UPDATED_UPDATED_TIME
        defaultAttrValueShouldNotBeFound("updatedTime.equals=" + UPDATED_UPDATED_TIME);
    }

    @Test
    @Transactional
    public void getAllAttrValuesByUpdatedTimeIsInShouldWork() throws Exception {
        // Initialize the database
        attrValueRepository.saveAndFlush(attrValue);

        // Get all the attrValueList where updatedTime in DEFAULT_UPDATED_TIME or UPDATED_UPDATED_TIME
        defaultAttrValueShouldBeFound("updatedTime.in=" + DEFAULT_UPDATED_TIME + "," + UPDATED_UPDATED_TIME);

        // Get all the attrValueList where updatedTime equals to UPDATED_UPDATED_TIME
        defaultAttrValueShouldNotBeFound("updatedTime.in=" + UPDATED_UPDATED_TIME);
    }

    @Test
    @Transactional
    public void getAllAttrValuesByUpdatedTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        attrValueRepository.saveAndFlush(attrValue);

        // Get all the attrValueList where updatedTime is not null
        defaultAttrValueShouldBeFound("updatedTime.specified=true");

        // Get all the attrValueList where updatedTime is null
        defaultAttrValueShouldNotBeFound("updatedTime.specified=false");
    }

    @Test
    @Transactional
    public void getAllAttrValuesByDeletedIsEqualToSomething() throws Exception {
        // Initialize the database
        attrValueRepository.saveAndFlush(attrValue);

        // Get all the attrValueList where deleted equals to DEFAULT_DELETED
        defaultAttrValueShouldBeFound("deleted.equals=" + DEFAULT_DELETED);

        // Get all the attrValueList where deleted equals to UPDATED_DELETED
        defaultAttrValueShouldNotBeFound("deleted.equals=" + UPDATED_DELETED);
    }

    @Test
    @Transactional
    public void getAllAttrValuesByDeletedIsInShouldWork() throws Exception {
        // Initialize the database
        attrValueRepository.saveAndFlush(attrValue);

        // Get all the attrValueList where deleted in DEFAULT_DELETED or UPDATED_DELETED
        defaultAttrValueShouldBeFound("deleted.in=" + DEFAULT_DELETED + "," + UPDATED_DELETED);

        // Get all the attrValueList where deleted equals to UPDATED_DELETED
        defaultAttrValueShouldNotBeFound("deleted.in=" + UPDATED_DELETED);
    }

    @Test
    @Transactional
    public void getAllAttrValuesByDeletedIsNullOrNotNull() throws Exception {
        // Initialize the database
        attrValueRepository.saveAndFlush(attrValue);

        // Get all the attrValueList where deleted is not null
        defaultAttrValueShouldBeFound("deleted.specified=true");

        // Get all the attrValueList where deleted is null
        defaultAttrValueShouldNotBeFound("deleted.specified=false");
    }
    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultAttrValueShouldBeFound(String filter) throws Exception {
        restAttrValueMockMvc.perform(get("/api/attr-values?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(attrValue.getId().intValue())))
            .andExpect(jsonPath("$.[*].attrId").value(hasItem(DEFAULT_ATTR_ID.intValue())))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE.toString())))
            .andExpect(jsonPath("$.[*].image").value(hasItem(DEFAULT_IMAGE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
            .andExpect(jsonPath("$.[*].sort").value(hasItem(DEFAULT_SORT)))
            .andExpect(jsonPath("$.[*].createdTime").value(hasItem(DEFAULT_CREATED_TIME.toString())))
            .andExpect(jsonPath("$.[*].updatedTime").value(hasItem(DEFAULT_UPDATED_TIME.toString())))
            .andExpect(jsonPath("$.[*].deleted").value(hasItem(DEFAULT_DELETED.booleanValue())));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultAttrValueShouldNotBeFound(String filter) throws Exception {
        restAttrValueMockMvc.perform(get("/api/attr-values?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }


    @Test
    @Transactional
    public void getNonExistingAttrValue() throws Exception {
        // Get the attrValue
        restAttrValueMockMvc.perform(get("/api/attr-values/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateAttrValue() throws Exception {
        // Initialize the database
        attrValueRepository.saveAndFlush(attrValue);
        int databaseSizeBeforeUpdate = attrValueRepository.findAll().size();

        // Update the attrValue
        AttrValue updatedAttrValue = attrValueRepository.findOne(attrValue.getId());
        // Disconnect from session so that the updates on updatedAttrValue are not directly saved in db
        em.detach(updatedAttrValue);
        updatedAttrValue
            .attrId(UPDATED_ATTR_ID)
            .value(UPDATED_VALUE)
            .image(UPDATED_IMAGE)
            .status(UPDATED_STATUS)
            .sort(UPDATED_SORT)
            .createdTime(UPDATED_CREATED_TIME)
            .updatedTime(UPDATED_UPDATED_TIME)
            .deleted(UPDATED_DELETED);
        AttrValueDTO attrValueDTO = attrValueMapper.toDto(updatedAttrValue);

        restAttrValueMockMvc.perform(put("/api/attr-values")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(attrValueDTO)))
            .andExpect(status().isOk());

        // Validate the AttrValue in the database
        List<AttrValue> attrValueList = attrValueRepository.findAll();
        assertThat(attrValueList).hasSize(databaseSizeBeforeUpdate);
        AttrValue testAttrValue = attrValueList.get(attrValueList.size() - 1);
        assertThat(testAttrValue.getAttrId()).isEqualTo(UPDATED_ATTR_ID);
        assertThat(testAttrValue.getValue()).isEqualTo(UPDATED_VALUE);
        assertThat(testAttrValue.getImage()).isEqualTo(UPDATED_IMAGE);
        assertThat(testAttrValue.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testAttrValue.getSort()).isEqualTo(UPDATED_SORT);
        assertThat(testAttrValue.getCreatedTime()).isEqualTo(UPDATED_CREATED_TIME);
        assertThat(testAttrValue.getUpdatedTime()).isEqualTo(UPDATED_UPDATED_TIME);
        assertThat(testAttrValue.isDeleted()).isEqualTo(UPDATED_DELETED);
    }

    @Test
    @Transactional
    public void updateNonExistingAttrValue() throws Exception {
        int databaseSizeBeforeUpdate = attrValueRepository.findAll().size();

        // Create the AttrValue
        AttrValueDTO attrValueDTO = attrValueMapper.toDto(attrValue);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restAttrValueMockMvc.perform(put("/api/attr-values")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(attrValueDTO)))
            .andExpect(status().isCreated());

        // Validate the AttrValue in the database
        List<AttrValue> attrValueList = attrValueRepository.findAll();
        assertThat(attrValueList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteAttrValue() throws Exception {
        // Initialize the database
        attrValueRepository.saveAndFlush(attrValue);
        int databaseSizeBeforeDelete = attrValueRepository.findAll().size();

        // Get the attrValue
        restAttrValueMockMvc.perform(delete("/api/attr-values/{id}", attrValue.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<AttrValue> attrValueList = attrValueRepository.findAll();
        assertThat(attrValueList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AttrValue.class);
        AttrValue attrValue1 = new AttrValue();
        attrValue1.setId(1L);
        AttrValue attrValue2 = new AttrValue();
        attrValue2.setId(attrValue1.getId());
        assertThat(attrValue1).isEqualTo(attrValue2);
        attrValue2.setId(2L);
        assertThat(attrValue1).isNotEqualTo(attrValue2);
        attrValue1.setId(null);
        assertThat(attrValue1).isNotEqualTo(attrValue2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(AttrValueDTO.class);
        AttrValueDTO attrValueDTO1 = new AttrValueDTO();
        attrValueDTO1.setId(1L);
        AttrValueDTO attrValueDTO2 = new AttrValueDTO();
        assertThat(attrValueDTO1).isNotEqualTo(attrValueDTO2);
        attrValueDTO2.setId(attrValueDTO1.getId());
        assertThat(attrValueDTO1).isEqualTo(attrValueDTO2);
        attrValueDTO2.setId(2L);
        assertThat(attrValueDTO1).isNotEqualTo(attrValueDTO2);
        attrValueDTO1.setId(null);
        assertThat(attrValueDTO1).isNotEqualTo(attrValueDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(attrValueMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(attrValueMapper.fromId(null)).isNull();
    }
}
