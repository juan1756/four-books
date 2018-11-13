package com.uade.edu.ar.web.rest;

import com.uade.edu.ar.FourbooksApp;

import com.uade.edu.ar.domain.Recomendacion;
import com.uade.edu.ar.repository.RecomendacionRepository;
import com.uade.edu.ar.repository.search.RecomendacionSearchRepository;
import com.uade.edu.ar.web.rest.errors.ExceptionTranslator;

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

import java.util.Collections;
import java.util.List;


import static com.uade.edu.ar.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the RecomendacionResource REST controller.
 *
 * @see RecomendacionResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = FourbooksApp.class)
public class RecomendacionResourceIntTest {

    private static final String DEFAULT_USER = "AAAAAAAAAA";
    private static final String UPDATED_USER = "BBBBBBBBBB";

    private static final String DEFAULT_ISBN = "AAAAAAAAAA";
    private static final String UPDATED_ISBN = "BBBBBBBBBB";

    @Autowired
    private RecomendacionRepository recomendacionRepository;

    /**
     * This repository is mocked in the com.uade.edu.ar.repository.search test package.
     *
     * @see com.uade.edu.ar.repository.search.RecomendacionSearchRepositoryMockConfiguration
     */
    @Autowired
    private RecomendacionSearchRepository mockRecomendacionSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    private MockMvc restRecomendacionMockMvc;

    private Recomendacion recomendacion;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final RecomendacionResource recomendacionResource = new RecomendacionResource(recomendacionRepository, mockRecomendacionSearchRepository);
        this.restRecomendacionMockMvc = MockMvcBuilders.standaloneSetup(recomendacionResource)
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
    public static Recomendacion createEntity() {
        Recomendacion recomendacion = new Recomendacion()
            .user(DEFAULT_USER)
            .isbn(DEFAULT_ISBN);
        return recomendacion;
    }

    @Before
    public void initTest() {
        recomendacionRepository.deleteAll();
        recomendacion = createEntity();
    }

    @Test
    public void createRecomendacion() throws Exception {
        int databaseSizeBeforeCreate = recomendacionRepository.findAll().size();

        // Create the Recomendacion
        restRecomendacionMockMvc.perform(post("/api/recomendacions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(recomendacion)))
            .andExpect(status().isCreated());

        // Validate the Recomendacion in the database
        List<Recomendacion> recomendacionList = recomendacionRepository.findAll();
        assertThat(recomendacionList).hasSize(databaseSizeBeforeCreate + 1);
        Recomendacion testRecomendacion = recomendacionList.get(recomendacionList.size() - 1);
        assertThat(testRecomendacion.getUser()).isEqualTo(DEFAULT_USER);
        assertThat(testRecomendacion.getIsbn()).isEqualTo(DEFAULT_ISBN);

        // Validate the Recomendacion in Elasticsearch
        verify(mockRecomendacionSearchRepository, times(1)).save(testRecomendacion);
    }

    @Test
    public void createRecomendacionWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = recomendacionRepository.findAll().size();

        // Create the Recomendacion with an existing ID
        recomendacion.setId("existing_id");

        // An entity with an existing ID cannot be created, so this API call must fail
        restRecomendacionMockMvc.perform(post("/api/recomendacions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(recomendacion)))
            .andExpect(status().isBadRequest());

        // Validate the Recomendacion in the database
        List<Recomendacion> recomendacionList = recomendacionRepository.findAll();
        assertThat(recomendacionList).hasSize(databaseSizeBeforeCreate);

        // Validate the Recomendacion in Elasticsearch
        verify(mockRecomendacionSearchRepository, times(0)).save(recomendacion);
    }

    @Test
    public void checkUserIsRequired() throws Exception {
        int databaseSizeBeforeTest = recomendacionRepository.findAll().size();
        // set the field null
        recomendacion.setUser(null);

        // Create the Recomendacion, which fails.

        restRecomendacionMockMvc.perform(post("/api/recomendacions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(recomendacion)))
            .andExpect(status().isBadRequest());

        List<Recomendacion> recomendacionList = recomendacionRepository.findAll();
        assertThat(recomendacionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void checkIsbnIsRequired() throws Exception {
        int databaseSizeBeforeTest = recomendacionRepository.findAll().size();
        // set the field null
        recomendacion.setIsbn(null);

        // Create the Recomendacion, which fails.

        restRecomendacionMockMvc.perform(post("/api/recomendacions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(recomendacion)))
            .andExpect(status().isBadRequest());

        List<Recomendacion> recomendacionList = recomendacionRepository.findAll();
        assertThat(recomendacionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void getAllRecomendacions() throws Exception {
        // Initialize the database
        recomendacionRepository.save(recomendacion);

        // Get all the recomendacionList
        restRecomendacionMockMvc.perform(get("/api/recomendacions?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(recomendacion.getId())))
            .andExpect(jsonPath("$.[*].user").value(hasItem(DEFAULT_USER.toString())))
            .andExpect(jsonPath("$.[*].isbn").value(hasItem(DEFAULT_ISBN.toString())));
    }
    
    @Test
    public void getRecomendacion() throws Exception {
        // Initialize the database
        recomendacionRepository.save(recomendacion);

        // Get the recomendacion
        restRecomendacionMockMvc.perform(get("/api/recomendacions/{id}", recomendacion.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(recomendacion.getId()))
            .andExpect(jsonPath("$.user").value(DEFAULT_USER.toString()))
            .andExpect(jsonPath("$.isbn").value(DEFAULT_ISBN.toString()));
    }

    @Test
    public void getNonExistingRecomendacion() throws Exception {
        // Get the recomendacion
        restRecomendacionMockMvc.perform(get("/api/recomendacions/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    public void updateRecomendacion() throws Exception {
        // Initialize the database
        recomendacionRepository.save(recomendacion);

        int databaseSizeBeforeUpdate = recomendacionRepository.findAll().size();

        // Update the recomendacion
        Recomendacion updatedRecomendacion = recomendacionRepository.findById(recomendacion.getId()).get();
        updatedRecomendacion
            .user(UPDATED_USER)
            .isbn(UPDATED_ISBN);

        restRecomendacionMockMvc.perform(put("/api/recomendacions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedRecomendacion)))
            .andExpect(status().isOk());

        // Validate the Recomendacion in the database
        List<Recomendacion> recomendacionList = recomendacionRepository.findAll();
        assertThat(recomendacionList).hasSize(databaseSizeBeforeUpdate);
        Recomendacion testRecomendacion = recomendacionList.get(recomendacionList.size() - 1);
        assertThat(testRecomendacion.getUser()).isEqualTo(UPDATED_USER);
        assertThat(testRecomendacion.getIsbn()).isEqualTo(UPDATED_ISBN);

        // Validate the Recomendacion in Elasticsearch
        verify(mockRecomendacionSearchRepository, times(1)).save(testRecomendacion);
    }

    @Test
    public void updateNonExistingRecomendacion() throws Exception {
        int databaseSizeBeforeUpdate = recomendacionRepository.findAll().size();

        // Create the Recomendacion

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRecomendacionMockMvc.perform(put("/api/recomendacions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(recomendacion)))
            .andExpect(status().isBadRequest());

        // Validate the Recomendacion in the database
        List<Recomendacion> recomendacionList = recomendacionRepository.findAll();
        assertThat(recomendacionList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Recomendacion in Elasticsearch
        verify(mockRecomendacionSearchRepository, times(0)).save(recomendacion);
    }

    @Test
    public void deleteRecomendacion() throws Exception {
        // Initialize the database
        recomendacionRepository.save(recomendacion);

        int databaseSizeBeforeDelete = recomendacionRepository.findAll().size();

        // Get the recomendacion
        restRecomendacionMockMvc.perform(delete("/api/recomendacions/{id}", recomendacion.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Recomendacion> recomendacionList = recomendacionRepository.findAll();
        assertThat(recomendacionList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Recomendacion in Elasticsearch
        verify(mockRecomendacionSearchRepository, times(1)).deleteById(recomendacion.getId());
    }

    @Test
    public void searchRecomendacion() throws Exception {
        // Initialize the database
        recomendacionRepository.save(recomendacion);
        when(mockRecomendacionSearchRepository.search(queryStringQuery("id:" + recomendacion.getId())))
            .thenReturn(Collections.singletonList(recomendacion));
        // Search the recomendacion
        restRecomendacionMockMvc.perform(get("/api/_search/recomendacions?query=id:" + recomendacion.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(recomendacion.getId())))
            .andExpect(jsonPath("$.[*].user").value(hasItem(DEFAULT_USER.toString())))
            .andExpect(jsonPath("$.[*].isbn").value(hasItem(DEFAULT_ISBN.toString())));
    }

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Recomendacion.class);
        Recomendacion recomendacion1 = new Recomendacion();
        recomendacion1.setId("id1");
        Recomendacion recomendacion2 = new Recomendacion();
        recomendacion2.setId(recomendacion1.getId());
        assertThat(recomendacion1).isEqualTo(recomendacion2);
        recomendacion2.setId("id2");
        assertThat(recomendacion1).isNotEqualTo(recomendacion2);
        recomendacion1.setId(null);
        assertThat(recomendacion1).isNotEqualTo(recomendacion2);
    }
}
