package com.example.learningplatform.repository;

import com.example.learningplatform.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {
    List<Lesson> findByModuleId(Long moduleId);

    @Query("SELECT l FROM Lesson l LEFT JOIN FETCH l.assignments WHERE l.id = :id")
    Optional<Lesson> findByIdWithAssignments(@Param("id") Long id);

    @Query("SELECT l FROM Lesson l LEFT JOIN FETCH l.assignments WHERE l.module.id = :moduleId ORDER BY l.orderIndex")
    List<Lesson> findByModuleIdWithAssignments(@Param("moduleId") Long moduleId);
}