package com.example.learningplatform.repository;

import com.example.learningplatform.entity.CourseReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseReviewRepository extends JpaRepository<CourseReview, Long> {

    List<CourseReview> findByCourseId(Long courseId);
    List<CourseReview> findByStudentId(Long studentId);
    Optional<CourseReview> findByStudentIdAndCourseId(Long studentId, Long courseId);
    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);

    @Query("SELECT AVG(cr.rating) FROM CourseReview cr WHERE cr.course.id = :courseId")
    Double findAverageRatingByCourseId(@Param("courseId") Long courseId);

    @Query("SELECT cr FROM CourseReview cr LEFT JOIN FETCH cr.student WHERE cr.course.id = :courseId")
    List<CourseReview> findByCourseIdWithStudent(@Param("courseId") Long courseId);

    @Query("SELECT cr FROM CourseReview cr LEFT JOIN FETCH cr.course WHERE cr.student.id = :studentId")
    List<CourseReview> findByStudentIdWithCourse(@Param("studentId") Long studentId);

    long countByCourseId(Long courseId);
}