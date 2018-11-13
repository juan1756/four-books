package com.uade.edu.ar.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.uade.edu.ar.domain.Libro;
import com.uade.edu.ar.domain.Recomendacion;
import com.uade.edu.ar.repository.LibroRepository;
import com.uade.edu.ar.repository.RecomendacionRepository;
import com.uade.edu.ar.repository.search.LibroSearchRepository;
import com.uade.edu.ar.web.rest.errors.BadRequestAlertException;
import com.uade.edu.ar.web.rest.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Libro.
 */
@RestController
@RequestMapping("/api")
public class LibroResource {

    private final Logger log = LoggerFactory.getLogger(LibroResource.class);

    private static final String ENTITY_NAME = "libro";

    private final LibroRepository libroRepository;

    private final LibroSearchRepository libroSearchRepository;
    
    private final RecomendacionRepository recomendacionRepository;

    public LibroResource(RecomendacionRepository recomendacionRepository, LibroRepository libroRepository, LibroSearchRepository libroSearchRepository) {
        this.libroRepository = libroRepository;
        this.recomendacionRepository = recomendacionRepository;
        this.libroSearchRepository = libroSearchRepository;
    }

    /**
     * POST  /libros : Create a new libro.
     *
     * @param libro the libro to create
     * @return the ResponseEntity with status 201 (Created) and with body the new libro, or with status 400 (Bad Request) if the libro has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/libros")
    @Timed
    public ResponseEntity<Libro> createLibro(@RequestBody Libro libro) throws URISyntaxException {
        log.debug("REST request to save Libro : {}", libro);
        if (libro.getId() != null) {
            throw new BadRequestAlertException("A new libro cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Libro result = libroRepository.save(libro);
        libroSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/libros/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /libros : Updates an existing libro.
     *
     * @param libro the libro to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated libro,
     * or with status 400 (Bad Request) if the libro is not valid,
     * or with status 500 (Internal Server Error) if the libro couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/libros")
    @Timed
    public ResponseEntity<Libro> updateLibro(@RequestBody Libro libro) throws URISyntaxException {
        log.debug("REST request to update Libro : {}", libro);
        if (libro.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Libro result = libroRepository.save(libro);
        libroSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, libro.getId().toString()))
            .body(result);
    }

    /**
     * GET  /libros : get all the libros.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of libros in body
     */
    @GetMapping("/libros")
    @Timed
    public List<Libro> getAllLibros() {
        log.debug("REST request to get all Libros");
        
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        List<Recomendacion> recomendaciones = recomendacionRepository.findAllByUser(user.getUsername());
        List<Libro> libros = libroRepository.findAll();
        recomendaciones.forEach(r -> {
        	libros.stream().filter(libro -> {
        		return libro.getIsbn().equals(r.getIsbn());
        	}).findFirst().map(f -> {
        		f.setRecomendado(r.getId());
        		return f;
        	});
        });
        
        return libros;
    }

    /**
     * GET  /libros/:id : get the "id" libro.
     *
     * @param id the id of the libro to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the libro, or with status 404 (Not Found)
     */
    @GetMapping("/libros/{id}")
    @Timed
    public ResponseEntity<Libro> getLibro(@PathVariable String id) {
        log.debug("REST request to get Libro : {}", id);
        Optional<Libro> libro = libroRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(libro);
    }

    /**
     * DELETE  /libros/:id : delete the "id" libro.
     *
     * @param id the id of the libro to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/libros/{id}")
    @Timed
    public ResponseEntity<Void> deleteLibro(@PathVariable String id) {
        log.debug("REST request to delete Libro : {}", id);

        libroRepository.deleteById(id);
        libroSearchRepository.deleteById(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id)).build();
    }

    /**
     * SEARCH  /_search/libros?query=:query : search for the libro corresponding
     * to the query.
     *
     * @param query the query of the libro search
     * @return the result of the search
     */
    @GetMapping("/_search/libros")
    @Timed
    public List<Libro> searchLibros(@RequestParam String query) {
        log.debug("REST request to search Libros for query {}", query);
        return StreamSupport
            .stream(libroSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }

}
