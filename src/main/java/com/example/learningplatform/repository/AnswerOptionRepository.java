package com.example.learningplatform.repository;

import com.example.learningplatform.entity.AnswerOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerOptionRepository extends JpaRepository<AnswerOption, Long> {
    List<AnswerOption> findByQuestionId(Long questionId);

    @Modifying
    @Query("DELETE FROM AnswerOption ao WHERE ao.question.id = :questionId")
    void deleteByQuestionId(@Param("questionId") Long questionId);
}