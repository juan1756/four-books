package com.uade.edu.ar.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.uade.edu.ar.domain.Libro;
import com.uade.edu.ar.domain.Recomendacion;
import com.uade.edu.ar.repository.RecomendacionRepository;
import com.uade.edu.ar.repository.search.RecomendacionSearchRepository;
import com.uade.edu.ar.web.rest.errors.BadRequestAlertException;
import com.uade.edu.ar.web.rest.util.HeaderUtil;

import io.github.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing Recomendacion.
 */
@RestController
@RequestMapping("/api")
public class RecomendacionResource {

    private final Logger log = LoggerFactory.getLogger(RecomendacionResource.class);

    private static final String ENTITY_NAME = "recomendacion";

    private final RecomendacionRepository recomendacionRepository;

    private final RecomendacionSearchRepository recomendacionSearchRepository;

    public RecomendacionResource(RecomendacionRepository recomendacionRepository, RecomendacionSearchRepository recomendacionSearchRepository) {
        this.recomendacionRepository = recomendacionRepository;
        this.recomendacionSearchRepository = recomendacionSearchRepository;
    }

    /**
     * POST  /recomendacions : Create a new recomendacion.
     *
     * @param recomendacion the recomendacion to create
     * @return the ResponseEntity with status 201 (Created) and with body the new recomendacion, or with status 400 (Bad Request) if the recomendacion has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/recomendacions")
    @Timed
    public ResponseEntity<Recomendacion> createRecomendacion(@RequestBody Libro libro) throws URISyntaxException {
        log.debug("REST request to save Recomendacion : {}", libro);
        if (libro.getId() == null) {
            throw new BadRequestAlertException("No se puede recomendar un libro que no existe", ENTITY_NAME, "idnotexists");
        }
        
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        Recomendacion recomendacion = new Recomendacion();
        recomendacion.setIsbn(libro.getIsbn());
        recomendacion.setUser(user.getUsername());
        
        Recomendacion result = recomendacionRepository.save(recomendacion);
        recomendacionSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/recomendacions/" + result.getId()))
            .headers(HeaderUtil.createRecomendacionCreationAlert(result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /recomendacions : Updates an existing recomendacion.
     *
     * @param recomendacion the recomendacion to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated recomendacion,
     * or with status 400 (Bad Request) if the recomendacion is not valid,
     * or with status 500 (Internal Server Error) if the recomendacion couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/recomendacions")
    @Timed
    public ResponseEntity<Recomendacion> updateRecomendacion(@Valid @RequestBody Recomendacion recomendacion) throws URISyntaxException {
        log.debug("REST request to update Recomendacion : {}", recomendacion);
        if (recomendacion.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Recomendacion result = recomendacionRepository.save(recomendacion);
        recomendacionSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, recomendacion.getId().toString()))
            .body(result);
    }

    /**
     * GET  /recomendacions : get all the recomendacions.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of recomendacions in body
     */
    @GetMapping("/recomendacions")
    @Timed
    public List<Recomendacion> getAllRecomendacions() {
        log.debug("REST request to get all Recomendacions");
        return recomendacionRepository.findAll();
    }

    /**
     * GET  /recomendacions/:id : get the "id" recomendacion.
     *
     * @param id the id of the recomendacion to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the recomendacion, or with status 404 (Not Found)
     */
    @GetMapping("/recomendacions/{id}")
    @Timed
    public ResponseEntity<Recomendacion> getRecomendacion(@PathVariable String id) {
        log.debug("REST request to get Recomendacion : {}", id);
        Optional<Recomendacion> recomendacion = recomendacionRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(recomendacion);
    }

    /**
     * DELETE  /recomendacions/:id : delete the "id" recomendacion.
     *
     * @param id the id of the recomendacion to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/recomendacions/{id}")
    @Timed
    public ResponseEntity<Void> deleteRecomendacion(@PathVariable String id) {
        log.debug("REST request to delete Recomendacion : {}", id);
        
        recomendacionRepository.deleteById(id);
        recomendacionSearchRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    /**
     * SEARCH  /_search/recomendacions?query=:query : search for the recomendacion corresponding
     * to the query.
     *
     * @param query the query of the recomendacion search
     * @return the result of the search
     */
    @GetMapping("/_search/recomendacions")
    @Timed
    public List<Recomendacion> searchRecomendacions(@RequestParam String query) {
        log.debug("REST request to search Recomendacions for query {}", query);
        return StreamSupport
            .stream(recomendacionSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }

}
