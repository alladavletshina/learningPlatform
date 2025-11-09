package com.example.learningplatform.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ModuleDTO {
    private Long id;

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    private String title;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @Min(value = 1, message = "Order index must be at least 1")
    private Integer orderIndex;

    @NotNull(message = "Course ID is required")
    private Long courseId;

    private List<LessonDTO> lessons = new ArrayList<>();
}