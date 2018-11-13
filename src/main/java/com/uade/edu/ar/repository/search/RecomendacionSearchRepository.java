package com.uade.edu.ar.repository.search;

import com.uade.edu.ar.domain.Recomendacion;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Recomendacion entity.
 */
public interface RecomendacionSearchRepository extends ElasticsearchRepository<Recomendacion, String> {
}
