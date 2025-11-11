package com.example.learningplatform.repository;

import com.example.learningplatform.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    Optional<Quiz> findByModuleId(Long moduleId);
    List<Quiz> findByCourseId(Long courseId);

    @Query("SELECT q FROM Quiz q LEFT JOIN FETCH q.questions ques LEFT JOIN FETCH ques.options WHERE q.id = :id")
    Optional<Quiz> findByIdWithQuestionsAndOptions(@Param("id") Long id);

    @Query("SELECT q FROM Quiz q LEFT JOIN FETCH q.questions WHERE q.id = :id")
    Optional<Quiz> findByIdWithQuestions(@Param("id") Long id);

    @Query("SELECT q FROM Quiz q LEFT JOIN FETCH q.questions ques LEFT JOIN FETCH ques.options LEFT JOIN FETCH q.quizSubmissions WHERE q.id = :id")
    Optional<Quiz> findByIdWithQuestionsAndSubmissions(@Param("id") Long id);
}