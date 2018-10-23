package com.uade.edu.ar.repository.search;

import com.uade.edu.ar.domain.Libro;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Libro entity.
 */
public interface LibroSearchRepository extends ElasticsearchRepository<Libro, String> {
}
