package com.learningPlatform.registration.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Inscripcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombreEstudiante;
    private Double costoTotal;

    // Relación de muchos a muchos: Una inscripción puede tener varios cursos
    @ManyToMany
    private List<Curso> cursos;

    public Inscripcion() {
    }

    public Inscripcion(String nombreEstudiante, List<Curso> cursos, Double costoTotal) {
        this.nombreEstudiante = nombreEstudiante;
        this.cursos = cursos;
        this.costoTotal = costoTotal;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombreEstudiante() { return nombreEstudiante; }
    public void setNombreEstudiante(String nombreEstudiante) { this.nombreEstudiante = nombreEstudiante; }

    public Double getCostoTotal() { return costoTotal; }
    public void setCostoTotal(Double costoTotal) { this.costoTotal = costoTotal; }

    public List<Curso> getCursos() { return cursos; }
    public void setCursos(List<Curso> cursos) { this.cursos = cursos; }
}