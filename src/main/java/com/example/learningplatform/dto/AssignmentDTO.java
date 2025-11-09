package com.example.learningplatform.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AssignmentDTO {
    private Long id;

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    private String title;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    @Future(message = "Due date must be in the future")
    private LocalDateTime dueDate;

    @Min(value = 1, message = "Max score must be at least 1")
    @Max(value = 1000, message = "Max score must not exceed 1000")
    private Integer maxScore;

    @NotNull(message = "Lesson ID is required")
    private Long lessonId;

    private String lessonTitle;
}