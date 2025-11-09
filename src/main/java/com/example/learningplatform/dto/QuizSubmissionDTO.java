package com.example.learningplatform.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class QuizSubmissionDTO {
    private Long id;

    @Min(value = 0, message = "Score must be at least 0")
    private Integer score;

    @PastOrPresent(message = "Taken at date must be in the past or present")
    private LocalDateTime takenAt;

    @NotNull(message = "Quiz ID is required")
    private Long quizId;

    private String quizTitle;

    @NotNull(message = "Student ID is required")
    private Long studentId;

    private String studentName;
}