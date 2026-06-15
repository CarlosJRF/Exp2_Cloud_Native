package com.learningPlatform.registration.controller;

import com.learningPlatform.registration.model.Curso;
import com.learningPlatform.registration.model.Inscripcion;
import com.learningPlatform.registration.model.InscripcionRequest;
import com.learningPlatform.registration.repository.CursoRepository;
import com.learningPlatform.registration.repository.InscripcionRepository;
import com.learningPlatform.registration.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@RestController
@RequestMapping("/api/inscripciones")
public class InscripcionController {

    @Autowired
    private InscripcionRepository inscripcionRepository;

    @Autowired
    private CursoRepository cursoRepository;

    // Inyectamos nuestro servicio de S3
    @Autowired
    private S3Service s3Service;

    // --- ENDPOINT ORIGINAL (MODIFICADO PARA SUBIR A S3) ---
    @PostMapping
    public Inscripcion realizarInscripcion(@RequestBody InscripcionRequest request) {
        List<Curso> cursosSeleccionados = cursoRepository.findAllById(request.getCursoIds());
        Double totalAPagar = 0.0;
        StringBuilder detalleCursos = new StringBuilder(); 
        
        for (Curso curso : cursosSeleccionados) {
            totalAPagar += curso.getCosto();
            detalleCursos.append("- ").append(curso.getNombre())
                         .append(" ($").append(curso.getCosto()).append(")\n");
        }

        Inscripcion nuevaInscripcion = new Inscripcion();
        nuevaInscripcion.setNombreEstudiante(request.getNombreEstudiante());
        nuevaInscripcion.setCursos(cursosSeleccionados);
        nuevaInscripcion.setCostoTotal(totalAPagar);
        Inscripcion inscripcionGuardada = inscripcionRepository.save(nuevaInscripcion);

        // Generamos el archivo localmente
        Path rutaArchivoLocal = generarArchivoResumen(inscripcionGuardada, detalleCursos.toString());

        // ¡NUEVO! Subimos el archivo a S3
        if (rutaArchivoLocal != null) {
            s3Service.subirArchivo(inscripcionGuardada.getId(), rutaArchivoLocal);
        }

        return inscripcionGuardada;
    }

    // --- LOS 3 ENDPOINTS ADICIONALES PARA S3 ---

    // 1. DESCARGAR
    @GetMapping("/{id}/resumen/descargar")
    public ResponseEntity<byte[]> descargarResumen(@PathVariable Long id) {
        String nombreArchivo = "resumen_" + id + ".txt";
        byte[] archivoBytes = s3Service.descargarArchivo(id, nombreArchivo);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nombreArchivo + "\"")
                .contentType(MediaType.TEXT_PLAIN)
                .body(archivoBytes);
    }

    // 2. BORRAR
    @DeleteMapping("/{id}/resumen/borrar")
    public ResponseEntity<String> borrarResumen(@PathVariable Long id) {
        String nombreArchivo = "resumen_" + id + ".txt";
        String respuesta = s3Service.borrarArchivo(id, nombreArchivo);
        return ResponseEntity.ok(respuesta);
    }

    // 3. MODIFICAR (En S3, sobreescribimos enviando un archivo nuevo)
    @PutMapping("/{id}/resumen/modificar")
    public ResponseEntity<String> modificarResumen(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        try {
            // Guardamos el archivo subido temporalmente
            Path tempDir = Paths.get("resumenes_locales");
            Path tempFile = tempDir.resolve("resumen_" + id + ".txt");
            Files.copy(file.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);

            // Lo subimos a S3 (Sobreescribe el anterior)
            String respuesta = s3Service.subirArchivo(id, tempFile);
            return ResponseEntity.ok("Archivo modificado correctamente: " + respuesta);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al modificar: " + e.getMessage());
        }
    }

    // Método auxiliar (Actualizado para devolver la ruta)
    private Path generarArchivoResumen(Inscripcion inscripcion, String detalleCursos) {
        try {
            Path rutaCarpeta = Paths.get("resumenes_locales");
            if (!Files.exists(rutaCarpeta)) {
                Files.createDirectories(rutaCarpeta);
            }
            String nombreArchivo = "resumen_" + inscripcion.getId() + ".txt";
            Path rutaArchivo = rutaCarpeta.resolve(nombreArchivo);

            String contenido = "=== RESUMEN DE INSCRIPCIÓN ===\n" +
                               "ID Inscripción: " + inscripcion.getId() + "\n" +
                               "Estudiante: " + inscripcion.getNombreEstudiante() + "\n\n" +
                               "Cursos Inscritos:\n" + detalleCursos + "\n" +
                               "TOTAL A PAGAR: $" + inscripcion.getCostoTotal() + "\n" +
                               "===============================";

            Files.writeString(rutaArchivo, contenido);
            return rutaArchivo;
        } catch (Exception e) {
            System.err.println("Error al generar el archivo físico: " + e.getMessage());
            return null;
        }
    }
}