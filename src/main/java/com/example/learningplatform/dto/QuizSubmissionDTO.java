package com.example.learningplatform.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class QuizSubmissionDTO {
    private Long id;
    private Integer score;
    private LocalDateTime takenAt;
    private Long quizId;
    private String quizTitle;
    private Long studentId;
    private String studentName;
}