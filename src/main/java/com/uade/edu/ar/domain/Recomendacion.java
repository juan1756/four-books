package com.uade.edu.ar.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.util.Objects;

/**
 * A Recomendacion.
 */
@Document(collection = "recomendacion")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "recomendacion")
public class Recomendacion implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @NotNull
    @Field("user")
    private String user;

    @NotNull
    @Field("isbn")
    private String isbn;
    
    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public Recomendacion user(String user) {
        this.user = user;
        return this;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getIsbn() {
        return isbn;
    }

    public Recomendacion isbn(String isbn) {
        this.isbn = isbn;
        return this;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }
    
	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Recomendacion recomendacion = (Recomendacion) o;
        if (recomendacion.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), recomendacion.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Recomendacion{" +
            "id=" + getId() +
            ", user='" + getUser() + "'" +
            ", isbn='" + getIsbn() + "'" +
            "}";
    }
}
