package com.learningPlatform.registration.controller;

import com.learningPlatform.registration.model.Curso;
import com.learningPlatform.registration.model.Inscripcion;
import com.learningPlatform.registration.model.InscripcionRequest;
import com.learningPlatform.registration.repository.CursoRepository;
import com.learningPlatform.registration.repository.InscripcionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/inscripciones")
public class InscripcionController {

    @Autowired
    private InscripcionRepository inscripcionRepository;

    @Autowired
    private CursoRepository cursoRepository;

    @PostMapping
    public Inscripcion realizarInscripcion(@RequestBody InscripcionRequest request) {
        
        // 1. Buscar los cursos seleccionados por sus IDs
        List<Curso> cursosSeleccionados = cursoRepository.findAllById(request.getCursoIds());

        // 2. Calcular el costo total sumando el costo de cada curso
        Double totalAPagar = 0.0;
        StringBuilder detalleCursos = new StringBuilder(); // Para armar el texto del archivo
        
        for (Curso curso : cursosSeleccionados) {
            totalAPagar += curso.getCosto();
            detalleCursos.append("- ").append(curso.getNombre())
                         .append(" ($").append(curso.getCosto()).append(")\n");
        }

        // 3. Crear y guardar la inscripción en la base de datos
        Inscripcion nuevaInscripcion = new Inscripcion();
        nuevaInscripcion.setNombreEstudiante(request.getNombreEstudiante());
        nuevaInscripcion.setCursos(cursosSeleccionados);
        nuevaInscripcion.setCostoTotal(totalAPagar);
        
        Inscripcion inscripcionGuardada = inscripcionRepository.save(nuevaInscripcion);

        // 4. Generar el archivo físico local (Requisito Semana 1 y 4)
        generarArchivoResumen(inscripcionGuardada, detalleCursos.toString());

        return inscripcionGuardada;
    }

    /**
     * Método auxiliar para generar un archivo .txt físico en el computador
     */
    private void generarArchivoResumen(Inscripcion inscripcion, String detalleCursos) {
        try {
            // Creamos una carpeta llamada "resumenes_locales" en la raíz del proyecto
            Path rutaCarpeta = Paths.get("resumenes_locales");
            if (!Files.exists(rutaCarpeta)) {
                Files.createDirectories(rutaCarpeta);
            }

            // El nombre del archivo incluirá el ID de la inscripción
            String nombreArchivo = "resumen_" + inscripcion.getId() + ".txt";
            Path rutaArchivo = rutaCarpeta.resolve(nombreArchivo);

            // Armamos el contenido del texto
            String contenido = "=== RESUMEN DE INSCRIPCIÓN ===\n" +
                               "ID Inscripción: " + inscripcion.getId() + "\n" +
                               "Estudiante: " + inscripcion.getNombreEstudiante() + "\n\n" +
                               "Cursos Inscritos:\n" + detalleCursos + "\n" +
                               "TOTAL A PAGAR: $" + inscripcion.getCostoTotal() + "\n" +
                               "===============================";

            // Escribimos el archivo físicamente en el disco
            Files.writeString(rutaArchivo, contenido);
            System.out.println("Archivo de resumen generado exitosamente en: " + rutaArchivo.toAbsolutePath());

        } catch (Exception e) {
            System.err.println("Error al generar el archivo físico: " + e.getMessage());
        }
    }
}