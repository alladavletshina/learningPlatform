package com.example.learningplatform.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class CreateQuizRequest {

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    private String title;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    private Long timeLimit = 1800L;

    @NotNull(message = "Course ID is required")
    private Long courseId;

    private Long moduleId;

    private Integer maxScore = 100;

    private Integer passingScore = 60;

    @NotNull(message = "Questions are required")
    @Size(min = 1, message = "Quiz must have at least one question")
    private List<CreateQuestionRequest> questions;
}