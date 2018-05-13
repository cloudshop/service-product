package com.eyun.product.web.rest;

import com.eyun.product.ProductApp;

import com.eyun.product.config.SecurityBeanOverrideConfiguration;

import com.eyun.product.domain.SkuImg;
import com.eyun.product.repository.SkuImgRepository;
import com.eyun.product.service.SkuImgService;
import com.eyun.product.service.dto.SkuImgDTO;
import com.eyun.product.service.mapper.SkuImgMapper;
import com.eyun.product.web.rest.errors.ExceptionTranslator;
import com.eyun.product.service.dto.SkuImgCriteria;
import com.eyun.product.service.SkuImgQueryService;

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
 * Test class for the SkuImgResource REST controller.
 *
 * @see SkuImgResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ProductApp.class, SecurityBeanOverrideConfiguration.class})
public class SkuImgResourceIntTest {

    private static final Long DEFAULT_SKU_ID = 1L;
    private static final Long UPDATED_SKU_ID = 2L;

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
    private SkuImgRepository skuImgRepository;

    @Autowired
    private SkuImgMapper skuImgMapper;

    @Autowired
    private SkuImgService skuImgService;

    @Autowired
    private SkuImgQueryService skuImgQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restSkuImgMockMvc;

    private SkuImg skuImg;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final SkuImgResource skuImgResource = new SkuImgResource(skuImgService, skuImgQueryService);
        this.restSkuImgMockMvc = MockMvcBuilders.standaloneSetup(skuImgResource)
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
    public static SkuImg createEntity(EntityManager em) {
        SkuImg skuImg = new SkuImg()
            .skuId(DEFAULT_SKU_ID)
            .imgUrl(DEFAULT_IMG_URL)
            .type(DEFAULT_TYPE)
            .rank(DEFAULT_RANK)
            .createdTime(DEFAULT_CREATED_TIME)
            .updatedTime(DEFAULT_UPDATED_TIME)
            .deleted(DEFAULT_DELETED);
        return skuImg;
    }

    @Before
    public void initTest() {
        skuImg = createEntity(em);
    }

    @Test
    @Transactional
    public void createSkuImg() throws Exception {
        int databaseSizeBeforeCreate = skuImgRepository.findAll().size();

        // Create the SkuImg
        SkuImgDTO skuImgDTO = skuImgMapper.toDto(skuImg);
        restSkuImgMockMvc.perform(post("/api/sku-imgs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(skuImgDTO)))
            .andExpect(status().isCreated());

        // Validate the SkuImg in the database
        List<SkuImg> skuImgList = skuImgRepository.findAll();
        assertThat(skuImgList).hasSize(databaseSizeBeforeCreate + 1);
        SkuImg testSkuImg = skuImgList.get(skuImgList.size() - 1);
        assertThat(testSkuImg.getSkuId()).isEqualTo(DEFAULT_SKU_ID);
        assertThat(testSkuImg.getImgUrl()).isEqualTo(DEFAULT_IMG_URL);
        assertThat(testSkuImg.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testSkuImg.getRank()).isEqualTo(DEFAULT_RANK);
        assertThat(testSkuImg.getCreatedTime()).isEqualTo(DEFAULT_CREATED_TIME);
        assertThat(testSkuImg.getUpdatedTime()).isEqualTo(DEFAULT_UPDATED_TIME);
        assertThat(testSkuImg.isDeleted()).isEqualTo(DEFAULT_DELETED);
    }

    @Test
    @Transactional
    public void createSkuImgWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = skuImgRepository.findAll().size();

        // Create the SkuImg with an existing ID
        skuImg.setId(1L);
        SkuImgDTO skuImgDTO = skuImgMapper.toDto(skuImg);

        // An entity with an existing ID cannot be created, so this API call must fail
        restSkuImgMockMvc.perform(post("/api/sku-imgs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(skuImgDTO)))
            .andExpect(status().isBadRequest());

        // Validate the SkuImg in the database
        List<SkuImg> skuImgList = skuImgRepository.findAll();
        assertThat(skuImgList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllSkuImgs() throws Exception {
        // Initialize the database
        skuImgRepository.saveAndFlush(skuImg);

        // Get all the skuImgList
        restSkuImgMockMvc.perform(get("/api/sku-imgs?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(skuImg.getId().intValue())))
            .andExpect(jsonPath("$.[*].skuId").value(hasItem(DEFAULT_SKU_ID.intValue())))
            .andExpect(jsonPath("$.[*].imgUrl").value(hasItem(DEFAULT_IMG_URL.toString())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
            .andExpect(jsonPath("$.[*].rank").value(hasItem(DEFAULT_RANK)))
            .andExpect(jsonPath("$.[*].createdTime").value(hasItem(DEFAULT_CREATED_TIME.toString())))
            .andExpect(jsonPath("$.[*].updatedTime").value(hasItem(DEFAULT_UPDATED_TIME.toString())))
            .andExpect(jsonPath("$.[*].deleted").value(hasItem(DEFAULT_DELETED.booleanValue())));
    }

    @Test
    @Transactional
    public void getSkuImg() throws Exception {
        // Initialize the database
        skuImgRepository.saveAndFlush(skuImg);

        // Get the skuImg
        restSkuImgMockMvc.perform(get("/api/sku-imgs/{id}", skuImg.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(skuImg.getId().intValue()))
            .andExpect(jsonPath("$.skuId").value(DEFAULT_SKU_ID.intValue()))
            .andExpect(jsonPath("$.imgUrl").value(DEFAULT_IMG_URL.toString()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE))
            .andExpect(jsonPath("$.rank").value(DEFAULT_RANK))
            .andExpect(jsonPath("$.createdTime").value(DEFAULT_CREATED_TIME.toString()))
            .andExpect(jsonPath("$.updatedTime").value(DEFAULT_UPDATED_TIME.toString()))
            .andExpect(jsonPath("$.deleted").value(DEFAULT_DELETED.booleanValue()));
    }

    @Test
    @Transactional
    public void getAllSkuImgsBySkuIdIsEqualToSomething() throws Exception {
        // Initialize the database
        skuImgRepository.saveAndFlush(skuImg);

        // Get all the skuImgList where skuId equals to DEFAULT_SKU_ID
        defaultSkuImgShouldBeFound("skuId.equals=" + DEFAULT_SKU_ID);

        // Get all the skuImgList where skuId equals to UPDATED_SKU_ID
        defaultSkuImgShouldNotBeFound("skuId.equals=" + UPDATED_SKU_ID);
    }

    @Test
    @Transactional
    public void getAllSkuImgsBySkuIdIsInShouldWork() throws Exception {
        // Initialize the database
        skuImgRepository.saveAndFlush(skuImg);

        // Get all the skuImgList where skuId in DEFAULT_SKU_ID or UPDATED_SKU_ID
        defaultSkuImgShouldBeFound("skuId.in=" + DEFAULT_SKU_ID + "," + UPDATED_SKU_ID);

        // Get all the skuImgList where skuId equals to UPDATED_SKU_ID
        defaultSkuImgShouldNotBeFound("skuId.in=" + UPDATED_SKU_ID);
    }

    @Test
    @Transactional
    public void getAllSkuImgsBySkuIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        skuImgRepository.saveAndFlush(skuImg);

        // Get all the skuImgList where skuId is not null
        defaultSkuImgShouldBeFound("skuId.specified=true");

        // Get all the skuImgList where skuId is null
        defaultSkuImgShouldNotBeFound("skuId.specified=false");
    }

    @Test
    @Transactional
    public void getAllSkuImgsBySkuIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        skuImgRepository.saveAndFlush(skuImg);

        // Get all the skuImgList where skuId greater than or equals to DEFAULT_SKU_ID
        defaultSkuImgShouldBeFound("skuId.greaterOrEqualThan=" + DEFAULT_SKU_ID);

        // Get all the skuImgList where skuId greater than or equals to UPDATED_SKU_ID
        defaultSkuImgShouldNotBeFound("skuId.greaterOrEqualThan=" + UPDATED_SKU_ID);
    }

    @Test
    @Transactional
    public void getAllSkuImgsBySkuIdIsLessThanSomething() throws Exception {
        // Initialize the database
        skuImgRepository.saveAndFlush(skuImg);

        // Get all the skuImgList where skuId less than or equals to DEFAULT_SKU_ID
        defaultSkuImgShouldNotBeFound("skuId.lessThan=" + DEFAULT_SKU_ID);

        // Get all the skuImgList where skuId less than or equals to UPDATED_SKU_ID
        defaultSkuImgShouldBeFound("skuId.lessThan=" + UPDATED_SKU_ID);
    }


    @Test
    @Transactional
    public void getAllSkuImgsByImgUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        skuImgRepository.saveAndFlush(skuImg);

        // Get all the skuImgList where imgUrl equals to DEFAULT_IMG_URL
        defaultSkuImgShouldBeFound("imgUrl.equals=" + DEFAULT_IMG_URL);

        // Get all the skuImgList where imgUrl equals to UPDATED_IMG_URL
        defaultSkuImgShouldNotBeFound("imgUrl.equals=" + UPDATED_IMG_URL);
    }

    @Test
    @Transactional
    public void getAllSkuImgsByImgUrlIsInShouldWork() throws Exception {
        // Initialize the database
        skuImgRepository.saveAndFlush(skuImg);

        // Get all the skuImgList where imgUrl in DEFAULT_IMG_URL or UPDATED_IMG_URL
        defaultSkuImgShouldBeFound("imgUrl.in=" + DEFAULT_IMG_URL + "," + UPDATED_IMG_URL);

        // Get all the skuImgList where imgUrl equals to UPDATED_IMG_URL
        defaultSkuImgShouldNotBeFound("imgUrl.in=" + UPDATED_IMG_URL);
    }

    @Test
    @Transactional
    public void getAllSkuImgsByImgUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        skuImgRepository.saveAndFlush(skuImg);

        // Get all the skuImgList where imgUrl is not null
        defaultSkuImgShouldBeFound("imgUrl.specified=true");

        // Get all the skuImgList where imgUrl is null
        defaultSkuImgShouldNotBeFound("imgUrl.specified=false");
    }

    @Test
    @Transactional
    public void getAllSkuImgsByTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        skuImgRepository.saveAndFlush(skuImg);

        // Get all the skuImgList where type equals to DEFAULT_TYPE
        defaultSkuImgShouldBeFound("type.equals=" + DEFAULT_TYPE);

        // Get all the skuImgList where type equals to UPDATED_TYPE
        defaultSkuImgShouldNotBeFound("type.equals=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    public void getAllSkuImgsByTypeIsInShouldWork() throws Exception {
        // Initialize the database
        skuImgRepository.saveAndFlush(skuImg);

        // Get all the skuImgList where type in DEFAULT_TYPE or UPDATED_TYPE
        defaultSkuImgShouldBeFound("type.in=" + DEFAULT_TYPE + "," + UPDATED_TYPE);

        // Get all the skuImgList where type equals to UPDATED_TYPE
        defaultSkuImgShouldNotBeFound("type.in=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    public void getAllSkuImgsByTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        skuImgRepository.saveAndFlush(skuImg);

        // Get all the skuImgList where type is not null
        defaultSkuImgShouldBeFound("type.specified=true");

        // Get all the skuImgList where type is null
        defaultSkuImgShouldNotBeFound("type.specified=false");
    }

    @Test
    @Transactional
    public void getAllSkuImgsByTypeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        skuImgRepository.saveAndFlush(skuImg);

        // Get all the skuImgList where type greater than or equals to DEFAULT_TYPE
        defaultSkuImgShouldBeFound("type.greaterOrEqualThan=" + DEFAULT_TYPE);

        // Get all the skuImgList where type greater than or equals to UPDATED_TYPE
        defaultSkuImgShouldNotBeFound("type.greaterOrEqualThan=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    public void getAllSkuImgsByTypeIsLessThanSomething() throws Exception {
        // Initialize the database
        skuImgRepository.saveAndFlush(skuImg);

        // Get all the skuImgList where type less than or equals to DEFAULT_TYPE
        defaultSkuImgShouldNotBeFound("type.lessThan=" + DEFAULT_TYPE);

        // Get all the skuImgList where type less than or equals to UPDATED_TYPE
        defaultSkuImgShouldBeFound("type.lessThan=" + UPDATED_TYPE);
    }


    @Test
    @Transactional
    public void getAllSkuImgsByRankIsEqualToSomething() throws Exception {
        // Initialize the database
        skuImgRepository.saveAndFlush(skuImg);

        // Get all the skuImgList where rank equals to DEFAULT_RANK
        defaultSkuImgShouldBeFound("rank.equals=" + DEFAULT_RANK);

        // Get all the skuImgList where rank equals to UPDATED_RANK
        defaultSkuImgShouldNotBeFound("rank.equals=" + UPDATED_RANK);
    }

    @Test
    @Transactional
    public void getAllSkuImgsByRankIsInShouldWork() throws Exception {
        // Initialize the database
        skuImgRepository.saveAndFlush(skuImg);

        // Get all the skuImgList where rank in DEFAULT_RANK or UPDATED_RANK
        defaultSkuImgShouldBeFound("rank.in=" + DEFAULT_RANK + "," + UPDATED_RANK);

        // Get all the skuImgList where rank equals to UPDATED_RANK
        defaultSkuImgShouldNotBeFound("rank.in=" + UPDATED_RANK);
    }

    @Test
    @Transactional
    public void getAllSkuImgsByRankIsNullOrNotNull() throws Exception {
        // Initialize the database
        skuImgRepository.saveAndFlush(skuImg);

        // Get all the skuImgList where rank is not null
        defaultSkuImgShouldBeFound("rank.specified=true");

        // Get all the skuImgList where rank is null
        defaultSkuImgShouldNotBeFound("rank.specified=false");
    }

    @Test
    @Transactional
    public void getAllSkuImgsByRankIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        skuImgRepository.saveAndFlush(skuImg);

        // Get all the skuImgList where rank greater than or equals to DEFAULT_RANK
        defaultSkuImgShouldBeFound("rank.greaterOrEqualThan=" + DEFAULT_RANK);

        // Get all the skuImgList where rank greater than or equals to UPDATED_RANK
        defaultSkuImgShouldNotBeFound("rank.greaterOrEqualThan=" + UPDATED_RANK);
    }

    @Test
    @Transactional
    public void getAllSkuImgsByRankIsLessThanSomething() throws Exception {
        // Initialize the database
        skuImgRepository.saveAndFlush(skuImg);

        // Get all the skuImgList where rank less than or equals to DEFAULT_RANK
        defaultSkuImgShouldNotBeFound("rank.lessThan=" + DEFAULT_RANK);

        // Get all the skuImgList where rank less than or equals to UPDATED_RANK
        defaultSkuImgShouldBeFound("rank.lessThan=" + UPDATED_RANK);
    }


    @Test
    @Transactional
    public void getAllSkuImgsByCreatedTimeIsEqualToSomething() throws Exception {
        // Initialize the database
        skuImgRepository.saveAndFlush(skuImg);

        // Get all the skuImgList where createdTime equals to DEFAULT_CREATED_TIME
        defaultSkuImgShouldBeFound("createdTime.equals=" + DEFAULT_CREATED_TIME);

        // Get all the skuImgList where createdTime equals to UPDATED_CREATED_TIME
        defaultSkuImgShouldNotBeFound("createdTime.equals=" + UPDATED_CREATED_TIME);
    }

    @Test
    @Transactional
    public void getAllSkuImgsByCreatedTimeIsInShouldWork() throws Exception {
        // Initialize the database
        skuImgRepository.saveAndFlush(skuImg);

        // Get all the skuImgList where createdTime in DEFAULT_CREATED_TIME or UPDATED_CREATED_TIME
        defaultSkuImgShouldBeFound("createdTime.in=" + DEFAULT_CREATED_TIME + "," + UPDATED_CREATED_TIME);

        // Get all the skuImgList where createdTime equals to UPDATED_CREATED_TIME
        defaultSkuImgShouldNotBeFound("createdTime.in=" + UPDATED_CREATED_TIME);
    }

    @Test
    @Transactional
    public void getAllSkuImgsByCreatedTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        skuImgRepository.saveAndFlush(skuImg);

        // Get all the skuImgList where createdTime is not null
        defaultSkuImgShouldBeFound("createdTime.specified=true");

        // Get all the skuImgList where createdTime is null
        defaultSkuImgShouldNotBeFound("createdTime.specified=false");
    }

    @Test
    @Transactional
    public void getAllSkuImgsByUpdatedTimeIsEqualToSomething() throws Exception {
        // Initialize the database
        skuImgRepository.saveAndFlush(skuImg);

        // Get all the skuImgList where updatedTime equals to DEFAULT_UPDATED_TIME
        defaultSkuImgShouldBeFound("updatedTime.equals=" + DEFAULT_UPDATED_TIME);

        // Get all the skuImgList where updatedTime equals to UPDATED_UPDATED_TIME
        defaultSkuImgShouldNotBeFound("updatedTime.equals=" + UPDATED_UPDATED_TIME);
    }

    @Test
    @Transactional
    public void getAllSkuImgsByUpdatedTimeIsInShouldWork() throws Exception {
        // Initialize the database
        skuImgRepository.saveAndFlush(skuImg);

        // Get all the skuImgList where updatedTime in DEFAULT_UPDATED_TIME or UPDATED_UPDATED_TIME
        defaultSkuImgShouldBeFound("updatedTime.in=" + DEFAULT_UPDATED_TIME + "," + UPDATED_UPDATED_TIME);

        // Get all the skuImgList where updatedTime equals to UPDATED_UPDATED_TIME
        defaultSkuImgShouldNotBeFound("updatedTime.in=" + UPDATED_UPDATED_TIME);
    }

    @Test
    @Transactional
    public void getAllSkuImgsByUpdatedTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        skuImgRepository.saveAndFlush(skuImg);

        // Get all the skuImgList where updatedTime is not null
        defaultSkuImgShouldBeFound("updatedTime.specified=true");

        // Get all the skuImgList where updatedTime is null
        defaultSkuImgShouldNotBeFound("updatedTime.specified=false");
    }

    @Test
    @Transactional
    public void getAllSkuImgsByDeletedIsEqualToSomething() throws Exception {
        // Initialize the database
        skuImgRepository.saveAndFlush(skuImg);

        // Get all the skuImgList where deleted equals to DEFAULT_DELETED
        defaultSkuImgShouldBeFound("deleted.equals=" + DEFAULT_DELETED);

        // Get all the skuImgList where deleted equals to UPDATED_DELETED
        defaultSkuImgShouldNotBeFound("deleted.equals=" + UPDATED_DELETED);
    }

    @Test
    @Transactional
    public void getAllSkuImgsByDeletedIsInShouldWork() throws Exception {
        // Initialize the database
        skuImgRepository.saveAndFlush(skuImg);

        // Get all the skuImgList where deleted in DEFAULT_DELETED or UPDATED_DELETED
        defaultSkuImgShouldBeFound("deleted.in=" + DEFAULT_DELETED + "," + UPDATED_DELETED);

        // Get all the skuImgList where deleted equals to UPDATED_DELETED
        defaultSkuImgShouldNotBeFound("deleted.in=" + UPDATED_DELETED);
    }

    @Test
    @Transactional
    public void getAllSkuImgsByDeletedIsNullOrNotNull() throws Exception {
        // Initialize the database
        skuImgRepository.saveAndFlush(skuImg);

        // Get all the skuImgList where deleted is not null
        defaultSkuImgShouldBeFound("deleted.specified=true");

        // Get all the skuImgList where deleted is null
        defaultSkuImgShouldNotBeFound("deleted.specified=false");
    }
    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultSkuImgShouldBeFound(String filter) throws Exception {
        restSkuImgMockMvc.perform(get("/api/sku-imgs?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(skuImg.getId().intValue())))
            .andExpect(jsonPath("$.[*].skuId").value(hasItem(DEFAULT_SKU_ID.intValue())))
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
    private void defaultSkuImgShouldNotBeFound(String filter) throws Exception {
        restSkuImgMockMvc.perform(get("/api/sku-imgs?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }


    @Test
    @Transactional
    public void getNonExistingSkuImg() throws Exception {
        // Get the skuImg
        restSkuImgMockMvc.perform(get("/api/sku-imgs/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateSkuImg() throws Exception {
        // Initialize the database
        skuImgRepository.saveAndFlush(skuImg);
        int databaseSizeBeforeUpdate = skuImgRepository.findAll().size();

        // Update the skuImg
        SkuImg updatedSkuImg = skuImgRepository.findOne(skuImg.getId());
        // Disconnect from session so that the updates on updatedSkuImg are not directly saved in db
        em.detach(updatedSkuImg);
        updatedSkuImg
            .skuId(UPDATED_SKU_ID)
            .imgUrl(UPDATED_IMG_URL)
            .type(UPDATED_TYPE)
            .rank(UPDATED_RANK)
            .createdTime(UPDATED_CREATED_TIME)
            .updatedTime(UPDATED_UPDATED_TIME)
            .deleted(UPDATED_DELETED);
        SkuImgDTO skuImgDTO = skuImgMapper.toDto(updatedSkuImg);

        restSkuImgMockMvc.perform(put("/api/sku-imgs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(skuImgDTO)))
            .andExpect(status().isOk());

        // Validate the SkuImg in the database
        List<SkuImg> skuImgList = skuImgRepository.findAll();
        assertThat(skuImgList).hasSize(databaseSizeBeforeUpdate);
        SkuImg testSkuImg = skuImgList.get(skuImgList.size() - 1);
        assertThat(testSkuImg.getSkuId()).isEqualTo(UPDATED_SKU_ID);
        assertThat(testSkuImg.getImgUrl()).isEqualTo(UPDATED_IMG_URL);
        assertThat(testSkuImg.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testSkuImg.getRank()).isEqualTo(UPDATED_RANK);
        assertThat(testSkuImg.getCreatedTime()).isEqualTo(UPDATED_CREATED_TIME);
        assertThat(testSkuImg.getUpdatedTime()).isEqualTo(UPDATED_UPDATED_TIME);
        assertThat(testSkuImg.isDeleted()).isEqualTo(UPDATED_DELETED);
    }

    @Test
    @Transactional
    public void updateNonExistingSkuImg() throws Exception {
        int databaseSizeBeforeUpdate = skuImgRepository.findAll().size();

        // Create the SkuImg
        SkuImgDTO skuImgDTO = skuImgMapper.toDto(skuImg);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restSkuImgMockMvc.perform(put("/api/sku-imgs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(skuImgDTO)))
            .andExpect(status().isCreated());

        // Validate the SkuImg in the database
        List<SkuImg> skuImgList = skuImgRepository.findAll();
        assertThat(skuImgList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteSkuImg() throws Exception {
        // Initialize the database
        skuImgRepository.saveAndFlush(skuImg);
        int databaseSizeBeforeDelete = skuImgRepository.findAll().size();

        // Get the skuImg
        restSkuImgMockMvc.perform(delete("/api/sku-imgs/{id}", skuImg.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<SkuImg> skuImgList = skuImgRepository.findAll();
        assertThat(skuImgList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SkuImg.class);
        SkuImg skuImg1 = new SkuImg();
        skuImg1.setId(1L);
        SkuImg skuImg2 = new SkuImg();
        skuImg2.setId(skuImg1.getId());
        assertThat(skuImg1).isEqualTo(skuImg2);
        skuImg2.setId(2L);
        assertThat(skuImg1).isNotEqualTo(skuImg2);
        skuImg1.setId(null);
        assertThat(skuImg1).isNotEqualTo(skuImg2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SkuImgDTO.class);
        SkuImgDTO skuImgDTO1 = new SkuImgDTO();
        skuImgDTO1.setId(1L);
        SkuImgDTO skuImgDTO2 = new SkuImgDTO();
        assertThat(skuImgDTO1).isNotEqualTo(skuImgDTO2);
        skuImgDTO2.setId(skuImgDTO1.getId());
        assertThat(skuImgDTO1).isEqualTo(skuImgDTO2);
        skuImgDTO2.setId(2L);
        assertThat(skuImgDTO1).isNotEqualTo(skuImgDTO2);
        skuImgDTO1.setId(null);
        assertThat(skuImgDTO1).isNotEqualTo(skuImgDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(skuImgMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(skuImgMapper.fromId(null)).isNull();
    }
}
