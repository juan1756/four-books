package com.uade.edu.ar.repository;

import com.uade.edu.ar.domain.Autor;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


/**
 * Spring Data MongoDB repository for the Autor entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AutorRepository extends MongoRepository<Autor, String> {

}
