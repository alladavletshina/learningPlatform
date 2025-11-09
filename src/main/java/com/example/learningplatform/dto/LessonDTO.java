package com.example.learningplatform.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

@Data
public class LessonDTO {
    private Long id;

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    private String title;

    @Size(max = 5000, message = "Content must not exceed 5000 characters")
    private String content;

    @URL(message = "Video URL must be valid")
    private String videoUrl;

    @Min(value = 1, message = "Order index must be at least 1")
    private Integer orderIndex;

    @NotNull(message = "Module ID is required")
    private Long moduleId;
}