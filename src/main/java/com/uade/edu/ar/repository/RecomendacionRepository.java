package com.uade.edu.ar.repository;

import com.uade.edu.ar.domain.Recomendacion;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


/**
 * Spring Data MongoDB repository for the Recomendacion entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RecomendacionRepository extends MongoRepository<Recomendacion, String> {

	List<Recomendacion> findAllByUser(String user);
	
	int countByIsbn(String isbn);
}
