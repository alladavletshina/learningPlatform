package com.example.learningplatform.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CreateQuizRequest {
    @NotBlank
    private String title;

    private String description;

    private long timeLimit = 1800;

    @NotNull
    private Long courseId;

    private Long moduleId;

    private Integer maxScore = 100;

    private Integer passingScore = 60;

    private List<CreateQuestionRequest> questions;
}