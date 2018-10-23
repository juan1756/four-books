package com.uade.edu.ar.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of LibroSearchRepository to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class LibroSearchRepositoryMockConfiguration {

    @MockBean
    private LibroSearchRepository mockLibroSearchRepository;

}
