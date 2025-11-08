package com.example.learningplatform.repository;

import com.example.learningplatform.entity.QuizSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizSubmissionRepository extends JpaRepository<QuizSubmission, Long> {
    Optional<QuizSubmission> findByStudentIdAndQuizId(Long studentId, Long quizId);
    List<QuizSubmission> findByStudentId(Long studentId);
    List<QuizSubmission> findByQuizId(Long quizId);

    @Query("SELECT qs FROM QuizSubmission qs LEFT JOIN FETCH qs.quiz WHERE qs.student.id = :studentId")
    List<QuizSubmission> findByStudentIdWithQuiz(@Param("studentId") Long studentId);

    @Query("SELECT qs FROM QuizSubmission qs LEFT JOIN FETCH qs.student WHERE qs.quiz.id = :quizId")
    List<QuizSubmission> findByQuizIdWithStudent(@Param("quizId") Long quizId);
}