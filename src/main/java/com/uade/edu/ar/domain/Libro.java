package com.uade.edu.ar.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.io.Serializable;
import java.util.Objects;

/**
 * A Libro.
 */
@Document(collection = "libro")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "libro")
public class Libro implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Field("isbn")
    private String isbn;

    @Field("titulo")
    private String titulo;

    @Field("editorial")
    private String editorial;

    @Field("edicion")
    private String edicion;

    @Field("activo")
    private Boolean activo;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIsbn() {
        return isbn;
    }

    public Libro isbn(String isbn) {
        this.isbn = isbn;
        return this;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitulo() {
        return titulo;
    }

    public Libro titulo(String titulo) {
        this.titulo = titulo;
        return this;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getEditorial() {
        return editorial;
    }

    public Libro editorial(String editorial) {
        this.editorial = editorial;
        return this;
    }

    public void setEditorial(String editorial) {
        this.editorial = editorial;
    }

    public String getEdicion() {
        return edicion;
    }

    public Libro edicion(String edicion) {
        this.edicion = edicion;
        return this;
    }

    public void setEdicion(String edicion) {
        this.edicion = edicion;
    }

    public Boolean isActivo() {
        return activo;
    }

    public Libro activo(Boolean activo) {
        this.activo = activo;
        return this;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Libro libro = (Libro) o;
        if (libro.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), libro.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Libro{" +
            "id=" + getId() +
            ", isbn='" + getIsbn() + "'" +
            ", titulo='" + getTitulo() + "'" +
            ", editorial='" + getEditorial() + "'" +
            ", edicion='" + getEdicion() + "'" +
            ", activo='" + isActivo() + "'" +
            "}";
    }
}
