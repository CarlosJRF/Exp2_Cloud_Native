package com.learningPlatform.registration.model;

import java.util.List;

/**
 * Esta clase sirve solo para recibir los datos desde el cliente (Postman/Frontend)
 */
public class InscripcionRequest {
    private String nombreEstudiante;
    private List<Long> cursoIds; // Recibimos solo los IDs de los cursos a inscribir

    public String getNombreEstudiante() { return nombreEstudiante; }
    public void setNombreEstudiante(String nombreEstudiante) { this.nombreEstudiante = nombreEstudiante; }

    public List<Long> getCursoIds() { return cursoIds; }
    public void setCursoIds(List<Long> cursoIds) { this.cursoIds = cursoIds; }
}