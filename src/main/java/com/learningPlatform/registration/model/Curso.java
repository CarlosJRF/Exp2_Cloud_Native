package com.learningPlatform.registration.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/**
 * Esta clase representa la tabla 'Curso' en nuestra base de datos.
 * La etiqueta @Entity le dice a Spring que debe guardarse en Oracle.
 */
@Entity
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Identificador único autoincrementable

    private String nombre;
    private String instructor;
    private Integer duracion; // Representa la duración en horas
    private Double costo;

    // Constructores vacíos y con parámetros (necesarios para JPA)
    public Curso() {
    }

    public Curso(String nombre, String instructor, Integer duracion, Double costo) {
        this.nombre = nombre;
        this.instructor = instructor;
        this.duracion = duracion;
        this.costo = costo;
    }

    // Getters y Setters (Para poder leer y modificar los valores)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getInstructor() { return instructor; }
    public void setInstructor(String instructor) { this.instructor = instructor; }

    public Integer getDuracion() { return duracion; }
    public void setDuracion(Integer duracion) { this.duracion = duracion; }

    public Double getCosto() { return costo; }
    public void setCosto(Double costo) { this.costo = costo; }
}