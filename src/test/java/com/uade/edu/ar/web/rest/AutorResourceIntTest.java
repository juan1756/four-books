package com.uade.edu.ar.web.rest;

import com.uade.edu.ar.FourbooksApp;

import com.uade.edu.ar.domain.Autor;
import com.uade.edu.ar.repository.AutorRepository;
import com.uade.edu.ar.repository.search.AutorSearchRepository;
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

import java.time.LocalDate;
import java.time.ZoneId;
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
 * Test class for the AutorResource REST controller.
 *
 * @see AutorResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = FourbooksApp.class)
public class AutorResourceIntTest {

    private static final String DEFAULT_NOMBRE = "AAAAAAAAAA";
    private static final String UPDATED_NOMBRE = "BBBBBBBBBB";

    private static final String DEFAULT_NACIONALIDAD = "AAAAAAAAAA";
    private static final String UPDATED_NACIONALIDAD = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_BIRTH_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_BIRTH_DATE = LocalDate.now(ZoneId.systemDefault());

    @Autowired
    private AutorRepository autorRepository;

    /**
     * This repository is mocked in the com.uade.edu.ar.repository.search test package.
     *
     * @see com.uade.edu.ar.repository.search.AutorSearchRepositoryMockConfiguration
     */
    @Autowired
    private AutorSearchRepository mockAutorSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    private MockMvc restAutorMockMvc;

    private Autor autor;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final AutorResource autorResource = new AutorResource(autorRepository, mockAutorSearchRepository);
        this.restAutorMockMvc = MockMvcBuilders.standaloneSetup(autorResource)
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
    public static Autor createEntity() {
        Autor autor = new Autor()
            .nombre(DEFAULT_NOMBRE)
            .nacionalidad(DEFAULT_NACIONALIDAD)
            .email(DEFAULT_EMAIL)
            .birthDate(DEFAULT_BIRTH_DATE);
        return autor;
    }

    @Before
    public void initTest() {
        autorRepository.deleteAll();
        autor = createEntity();
    }

    @Test
    public void createAutor() throws Exception {
        int databaseSizeBeforeCreate = autorRepository.findAll().size();

        // Create the Autor
        restAutorMockMvc.perform(post("/api/autors")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(autor)))
            .andExpect(status().isCreated());

        // Validate the Autor in the database
        List<Autor> autorList = autorRepository.findAll();
        assertThat(autorList).hasSize(databaseSizeBeforeCreate + 1);
        Autor testAutor = autorList.get(autorList.size() - 1);
        assertThat(testAutor.getNombre()).isEqualTo(DEFAULT_NOMBRE);
        assertThat(testAutor.getNacionalidad()).isEqualTo(DEFAULT_NACIONALIDAD);
        assertThat(testAutor.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testAutor.getBirthDate()).isEqualTo(DEFAULT_BIRTH_DATE);

        // Validate the Autor in Elasticsearch
        verify(mockAutorSearchRepository, times(1)).save(testAutor);
    }

    @Test
    public void createAutorWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = autorRepository.findAll().size();

        // Create the Autor with an existing ID
        autor.setId("existing_id");

        // An entity with an existing ID cannot be created, so this API call must fail
        restAutorMockMvc.perform(post("/api/autors")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(autor)))
            .andExpect(status().isBadRequest());

        // Validate the Autor in the database
        List<Autor> autorList = autorRepository.findAll();
        assertThat(autorList).hasSize(databaseSizeBeforeCreate);

        // Validate the Autor in Elasticsearch
        verify(mockAutorSearchRepository, times(0)).save(autor);
    }

    @Test
    public void getAllAutors() throws Exception {
        // Initialize the database
        autorRepository.save(autor);

        // Get all the autorList
        restAutorMockMvc.perform(get("/api/autors?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(autor.getId())))
            .andExpect(jsonPath("$.[*].nombre").value(hasItem(DEFAULT_NOMBRE.toString())))
            .andExpect(jsonPath("$.[*].nacionalidad").value(hasItem(DEFAULT_NACIONALIDAD.toString())))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL.toString())))
            .andExpect(jsonPath("$.[*].birthDate").value(hasItem(DEFAULT_BIRTH_DATE.toString())));
    }
    
    @Test
    public void getAutor() throws Exception {
        // Initialize the database
        autorRepository.save(autor);

        // Get the autor
        restAutorMockMvc.perform(get("/api/autors/{id}", autor.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(autor.getId()))
            .andExpect(jsonPath("$.nombre").value(DEFAULT_NOMBRE.toString()))
            .andExpect(jsonPath("$.nacionalidad").value(DEFAULT_NACIONALIDAD.toString()))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL.toString()))
            .andExpect(jsonPath("$.birthDate").value(DEFAULT_BIRTH_DATE.toString()));
    }

    @Test
    public void getNonExistingAutor() throws Exception {
        // Get the autor
        restAutorMockMvc.perform(get("/api/autors/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    public void updateAutor() throws Exception {
        // Initialize the database
        autorRepository.save(autor);

        int databaseSizeBeforeUpdate = autorRepository.findAll().size();

        // Update the autor
        Autor updatedAutor = autorRepository.findById(autor.getId()).get();
        updatedAutor
            .nombre(UPDATED_NOMBRE)
            .nacionalidad(UPDATED_NACIONALIDAD)
            .email(UPDATED_EMAIL)
            .birthDate(UPDATED_BIRTH_DATE);

        restAutorMockMvc.perform(put("/api/autors")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedAutor)))
            .andExpect(status().isOk());

        // Validate the Autor in the database
        List<Autor> autorList = autorRepository.findAll();
        assertThat(autorList).hasSize(databaseSizeBeforeUpdate);
        Autor testAutor = autorList.get(autorList.size() - 1);
        assertThat(testAutor.getNombre()).isEqualTo(UPDATED_NOMBRE);
        assertThat(testAutor.getNacionalidad()).isEqualTo(UPDATED_NACIONALIDAD);
        assertThat(testAutor.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testAutor.getBirthDate()).isEqualTo(UPDATED_BIRTH_DATE);

        // Validate the Autor in Elasticsearch
        verify(mockAutorSearchRepository, times(1)).save(testAutor);
    }

    @Test
    public void updateNonExistingAutor() throws Exception {
        int databaseSizeBeforeUpdate = autorRepository.findAll().size();

        // Create the Autor

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAutorMockMvc.perform(put("/api/autors")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(autor)))
            .andExpect(status().isBadRequest());

        // Validate the Autor in the database
        List<Autor> autorList = autorRepository.findAll();
        assertThat(autorList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Autor in Elasticsearch
        verify(mockAutorSearchRepository, times(0)).save(autor);
    }

    @Test
    public void deleteAutor() throws Exception {
        // Initialize the database
        autorRepository.save(autor);

        int databaseSizeBeforeDelete = autorRepository.findAll().size();

        // Get the autor
        restAutorMockMvc.perform(delete("/api/autors/{id}", autor.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Autor> autorList = autorRepository.findAll();
        assertThat(autorList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Autor in Elasticsearch
        verify(mockAutorSearchRepository, times(1)).deleteById(autor.getId());
    }

    @Test
    public void searchAutor() throws Exception {
        // Initialize the database
        autorRepository.save(autor);
        when(mockAutorSearchRepository.search(queryStringQuery("id:" + autor.getId())))
            .thenReturn(Collections.singletonList(autor));
        // Search the autor
        restAutorMockMvc.perform(get("/api/_search/autors?query=id:" + autor.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(autor.getId())))
            .andExpect(jsonPath("$.[*].nombre").value(hasItem(DEFAULT_NOMBRE.toString())))
            .andExpect(jsonPath("$.[*].nacionalidad").value(hasItem(DEFAULT_NACIONALIDAD.toString())))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL.toString())))
            .andExpect(jsonPath("$.[*].birthDate").value(hasItem(DEFAULT_BIRTH_DATE.toString())));
    }

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Autor.class);
        Autor autor1 = new Autor();
        autor1.setId("id1");
        Autor autor2 = new Autor();
        autor2.setId(autor1.getId());
        assertThat(autor1).isEqualTo(autor2);
        autor2.setId("id2");
        assertThat(autor1).isNotEqualTo(autor2);
        autor1.setId(null);
        assertThat(autor1).isNotEqualTo(autor2);
    }
}
