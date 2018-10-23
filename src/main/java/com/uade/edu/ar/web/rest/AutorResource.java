package com.uade.edu.ar.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.uade.edu.ar.domain.Autor;
import com.uade.edu.ar.repository.AutorRepository;
import com.uade.edu.ar.repository.search.AutorSearchRepository;
import com.uade.edu.ar.web.rest.errors.BadRequestAlertException;
import com.uade.edu.ar.web.rest.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Autor.
 */
@RestController
@RequestMapping("/api")
public class AutorResource {

    private final Logger log = LoggerFactory.getLogger(AutorResource.class);

    private static final String ENTITY_NAME = "autor";

    private final AutorRepository autorRepository;

    private final AutorSearchRepository autorSearchRepository;

    public AutorResource(AutorRepository autorRepository, AutorSearchRepository autorSearchRepository) {
        this.autorRepository = autorRepository;
        this.autorSearchRepository = autorSearchRepository;
    }

    /**
     * POST  /autors : Create a new autor.
     *
     * @param autor the autor to create
     * @return the ResponseEntity with status 201 (Created) and with body the new autor, or with status 400 (Bad Request) if the autor has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/autors")
    @Timed
    public ResponseEntity<Autor> createAutor(@RequestBody Autor autor) throws URISyntaxException {
        log.debug("REST request to save Autor : {}", autor);
        if (autor.getId() != null) {
            throw new BadRequestAlertException("A new autor cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Autor result = autorRepository.save(autor);
        autorSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/autors/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /autors : Updates an existing autor.
     *
     * @param autor the autor to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated autor,
     * or with status 400 (Bad Request) if the autor is not valid,
     * or with status 500 (Internal Server Error) if the autor couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/autors")
    @Timed
    public ResponseEntity<Autor> updateAutor(@RequestBody Autor autor) throws URISyntaxException {
        log.debug("REST request to update Autor : {}", autor);
        if (autor.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Autor result = autorRepository.save(autor);
        autorSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, autor.getId().toString()))
            .body(result);
    }

    /**
     * GET  /autors : get all the autors.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of autors in body
     */
    @GetMapping("/autors")
    @Timed
    public List<Autor> getAllAutors() {
        log.debug("REST request to get all Autors");
        return autorRepository.findAll();
    }

    /**
     * GET  /autors/:id : get the "id" autor.
     *
     * @param id the id of the autor to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the autor, or with status 404 (Not Found)
     */
    @GetMapping("/autors/{id}")
    @Timed
    public ResponseEntity<Autor> getAutor(@PathVariable String id) {
        log.debug("REST request to get Autor : {}", id);
        Optional<Autor> autor = autorRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(autor);
    }

    /**
     * DELETE  /autors/:id : delete the "id" autor.
     *
     * @param id the id of the autor to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/autors/{id}")
    @Timed
    public ResponseEntity<Void> deleteAutor(@PathVariable String id) {
        log.debug("REST request to delete Autor : {}", id);

        autorRepository.deleteById(id);
        autorSearchRepository.deleteById(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id)).build();
    }

    /**
     * SEARCH  /_search/autors?query=:query : search for the autor corresponding
     * to the query.
     *
     * @param query the query of the autor search
     * @return the result of the search
     */
    @GetMapping("/_search/autors")
    @Timed
    public List<Autor> searchAutors(@RequestParam String query) {
        log.debug("REST request to search Autors for query {}", query);
        return StreamSupport
            .stream(autorSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }

}
