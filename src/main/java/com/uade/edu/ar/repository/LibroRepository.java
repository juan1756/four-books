package com.uade.edu.ar.repository;

import com.uade.edu.ar.domain.Libro;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


/**
 * Spring Data MongoDB repository for the Libro entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LibroRepository extends MongoRepository<Libro, String> {

}
