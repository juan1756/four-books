package com.uade.edu.ar.repository.search;

import com.uade.edu.ar.domain.Autor;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Autor entity.
 */
public interface AutorSearchRepository extends ElasticsearchRepository<Autor, String> {
}
