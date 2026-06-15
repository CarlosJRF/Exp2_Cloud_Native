package com.learningPlatform.registration.controller;

import com.learningPlatform.registration.model.Curso;
import com.learningPlatform.registration.repository.CursoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @RestController indica que esta clase responderá a peticiones web devolviendo datos (generalmente JSON).
 * @RequestMapping("/api/cursos") define la ruta base para estos Endpoints.
 */
@RestController
@RequestMapping("/api/cursos")
public class CursoController {

    // @Autowired inyecta nuestro repositorio para que podamos usarlo aquí
    @Autowired
    private CursoRepository cursoRepository;

    /**
     * Endpoint para consultar la lista de cursos disponibles.
     * Tipo: GET
     * Ruta: http://localhost:8080/api/cursos
     */
    @GetMapping
    public List<Curso> obtenerCursos() {
        // Retorna todos los cursos encontrados en la base de datos
        return cursoRepository.findAll(); 
    }

    /**
     * Endpoint para agregar nuevos cursos a la oferta educativa.
     * Tipo: POST
     * Ruta: http://localhost:8080/api/cursos
     * Se debe enviar un objeto JSON en el cuerpo de la petición.
     */
    @PostMapping
    public Curso agregarCurso(@RequestBody Curso curso) {
        // Guarda el curso en la base de datos y lo devuelve con su ID generado
        return cursoRepository.save(curso);
    }
}
