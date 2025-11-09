package com.example.learningplatform.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class QuizDTO {
    private Long id;

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    private String title;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @Min(value = 1, message = "Time limit must be at least 1 minute")
    @Max(value = 480, message = "Time limit must not exceed 480 minutes")
    private long timeLimit;

    private Long moduleId;
    private String moduleTitle;
    private Long courseId;
    private String courseTitle;
}