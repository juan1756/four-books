package com.uade.edu.ar.web.rest;

import com.uade.edu.ar.FourbooksApp;

import com.uade.edu.ar.domain.Libro;
import com.uade.edu.ar.repository.LibroRepository;
import com.uade.edu.ar.repository.search.LibroSearchRepository;
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
 * Test class for the LibroResource REST controller.
 *
 * @see LibroResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = FourbooksApp.class)
public class LibroResourceIntTest {

    private static final String DEFAULT_ISBN = "AAAAAAAAAA";
    private static final String UPDATED_ISBN = "BBBBBBBBBB";

    private static final String DEFAULT_TITULO = "AAAAAAAAAA";
    private static final String UPDATED_TITULO = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPCION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPCION = "BBBBBBBBBB";

    private static final String DEFAULT_EDITORIAL = "AAAAAAAAAA";
    private static final String UPDATED_EDITORIAL = "BBBBBBBBBB";

    private static final String DEFAULT_EDICION = "AAAAAAAAAA";
    private static final String UPDATED_EDICION = "BBBBBBBBBB";

    @Autowired
    private LibroRepository libroRepository;

    /**
     * This repository is mocked in the com.uade.edu.ar.repository.search test package.
     *
     * @see com.uade.edu.ar.repository.search.LibroSearchRepositoryMockConfiguration
     */
    @Autowired
    private LibroSearchRepository mockLibroSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    private MockMvc restLibroMockMvc;

    private Libro libro;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final LibroResource libroResource = new LibroResource(libroRepository, mockLibroSearchRepository);
        this.restLibroMockMvc = MockMvcBuilders.standaloneSetup(libroResource)
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
    public static Libro createEntity() {
        Libro libro = new Libro()
            .isbn(DEFAULT_ISBN)
            .titulo(DEFAULT_TITULO)
            .descripcion(DEFAULT_DESCRIPCION)
            .editorial(DEFAULT_EDITORIAL)
            .edicion(DEFAULT_EDICION);
        return libro;
    }

    @Before
    public void initTest() {
        libroRepository.deleteAll();
        libro = createEntity();
    }

    @Test
    public void createLibro() throws Exception {
        int databaseSizeBeforeCreate = libroRepository.findAll().size();

        // Create the Libro
        restLibroMockMvc.perform(post("/api/libros")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(libro)))
            .andExpect(status().isCreated());

        // Validate the Libro in the database
        List<Libro> libroList = libroRepository.findAll();
        assertThat(libroList).hasSize(databaseSizeBeforeCreate + 1);
        Libro testLibro = libroList.get(libroList.size() - 1);
        assertThat(testLibro.getIsbn()).isEqualTo(DEFAULT_ISBN);
        assertThat(testLibro.getTitulo()).isEqualTo(DEFAULT_TITULO);
        assertThat(testLibro.getDescripcion()).isEqualTo(DEFAULT_DESCRIPCION);
        assertThat(testLibro.getEditorial()).isEqualTo(DEFAULT_EDITORIAL);
        assertThat(testLibro.getEdicion()).isEqualTo(DEFAULT_EDICION);

        // Validate the Libro in Elasticsearch
        verify(mockLibroSearchRepository, times(1)).save(testLibro);
    }

    @Test
    public void createLibroWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = libroRepository.findAll().size();

        // Create the Libro with an existing ID
        libro.setId("existing_id");

        // An entity with an existing ID cannot be created, so this API call must fail
        restLibroMockMvc.perform(post("/api/libros")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(libro)))
            .andExpect(status().isBadRequest());

        // Validate the Libro in the database
        List<Libro> libroList = libroRepository.findAll();
        assertThat(libroList).hasSize(databaseSizeBeforeCreate);

        // Validate the Libro in Elasticsearch
        verify(mockLibroSearchRepository, times(0)).save(libro);
    }

    @Test
    public void getAllLibros() throws Exception {
        // Initialize the database
        libroRepository.save(libro);

        // Get all the libroList
        restLibroMockMvc.perform(get("/api/libros?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(libro.getId())))
            .andExpect(jsonPath("$.[*].isbn").value(hasItem(DEFAULT_ISBN.toString())))
            .andExpect(jsonPath("$.[*].titulo").value(hasItem(DEFAULT_TITULO.toString())))
            .andExpect(jsonPath("$.[*].descripcion").value(hasItem(DEFAULT_DESCRIPCION.toString())))
            .andExpect(jsonPath("$.[*].editorial").value(hasItem(DEFAULT_EDITORIAL.toString())))
            .andExpect(jsonPath("$.[*].edicion").value(hasItem(DEFAULT_EDICION.toString())));
    }
    
    @Test
    public void getLibro() throws Exception {
        // Initialize the database
        libroRepository.save(libro);

        // Get the libro
        restLibroMockMvc.perform(get("/api/libros/{id}", libro.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(libro.getId()))
            .andExpect(jsonPath("$.isbn").value(DEFAULT_ISBN.toString()))
            .andExpect(jsonPath("$.titulo").value(DEFAULT_TITULO.toString()))
            .andExpect(jsonPath("$.descripcion").value(DEFAULT_DESCRIPCION.toString()))
            .andExpect(jsonPath("$.editorial").value(DEFAULT_EDITORIAL.toString()))
            .andExpect(jsonPath("$.edicion").value(DEFAULT_EDICION.toString()));
    }

    @Test
    public void getNonExistingLibro() throws Exception {
        // Get the libro
        restLibroMockMvc.perform(get("/api/libros/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    public void updateLibro() throws Exception {
        // Initialize the database
        libroRepository.save(libro);

        int databaseSizeBeforeUpdate = libroRepository.findAll().size();

        // Update the libro
        Libro updatedLibro = libroRepository.findById(libro.getId()).get();
        updatedLibro
            .isbn(UPDATED_ISBN)
            .titulo(UPDATED_TITULO)
            .descripcion(UPDATED_DESCRIPCION)
            .editorial(UPDATED_EDITORIAL)
            .edicion(UPDATED_EDICION);

        restLibroMockMvc.perform(put("/api/libros")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedLibro)))
            .andExpect(status().isOk());

        // Validate the Libro in the database
        List<Libro> libroList = libroRepository.findAll();
        assertThat(libroList).hasSize(databaseSizeBeforeUpdate);
        Libro testLibro = libroList.get(libroList.size() - 1);
        assertThat(testLibro.getIsbn()).isEqualTo(UPDATED_ISBN);
        assertThat(testLibro.getTitulo()).isEqualTo(UPDATED_TITULO);
        assertThat(testLibro.getDescripcion()).isEqualTo(UPDATED_DESCRIPCION);
        assertThat(testLibro.getEditorial()).isEqualTo(UPDATED_EDITORIAL);
        assertThat(testLibro.getEdicion()).isEqualTo(UPDATED_EDICION);

        // Validate the Libro in Elasticsearch
        verify(mockLibroSearchRepository, times(1)).save(testLibro);
    }

    @Test
    public void updateNonExistingLibro() throws Exception {
        int databaseSizeBeforeUpdate = libroRepository.findAll().size();

        // Create the Libro

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLibroMockMvc.perform(put("/api/libros")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(libro)))
            .andExpect(status().isBadRequest());

        // Validate the Libro in the database
        List<Libro> libroList = libroRepository.findAll();
        assertThat(libroList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Libro in Elasticsearch
        verify(mockLibroSearchRepository, times(0)).save(libro);
    }

    @Test
    public void deleteLibro() throws Exception {
        // Initialize the database
        libroRepository.save(libro);

        int databaseSizeBeforeDelete = libroRepository.findAll().size();

        // Get the libro
        restLibroMockMvc.perform(delete("/api/libros/{id}", libro.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Libro> libroList = libroRepository.findAll();
        assertThat(libroList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Libro in Elasticsearch
        verify(mockLibroSearchRepository, times(1)).deleteById(libro.getId());
    }

    @Test
    public void searchLibro() throws Exception {
        // Initialize the database
        libroRepository.save(libro);
        when(mockLibroSearchRepository.search(queryStringQuery("id:" + libro.getId())))
            .thenReturn(Collections.singletonList(libro));
        // Search the libro
        restLibroMockMvc.perform(get("/api/_search/libros?query=id:" + libro.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(libro.getId())))
            .andExpect(jsonPath("$.[*].isbn").value(hasItem(DEFAULT_ISBN.toString())))
            .andExpect(jsonPath("$.[*].titulo").value(hasItem(DEFAULT_TITULO.toString())))
            .andExpect(jsonPath("$.[*].descripcion").value(hasItem(DEFAULT_DESCRIPCION.toString())))
            .andExpect(jsonPath("$.[*].editorial").value(hasItem(DEFAULT_EDITORIAL.toString())))
            .andExpect(jsonPath("$.[*].edicion").value(hasItem(DEFAULT_EDICION.toString())));
    }

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Libro.class);
        Libro libro1 = new Libro();
        libro1.setId("id1");
        Libro libro2 = new Libro();
        libro2.setId(libro1.getId());
        assertThat(libro1).isEqualTo(libro2);
        libro2.setId("id2");
        assertThat(libro1).isNotEqualTo(libro2);
        libro1.setId(null);
        assertThat(libro1).isNotEqualTo(libro2);
    }
}
