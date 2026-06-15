package com.learningPlatform.registration.repository;

import com.learningPlatform.registration.model.Curso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Heredamos del JpaRepository, para obtener métodos como save(), findAll(), findById(), etc.
 */
@Repository
public interface CursoRepository extends JpaRepository<Curso, Long> {
}
