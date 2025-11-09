package com.example.learningplatform.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class CourseDTO {
    private Long id;

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    private String title;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    @Min(value = 1, message = "Duration must be at least 1 hour")
    private Integer duration;

    @FutureOrPresent(message = "Start date must be in the present or future")
    private LocalDate startDate;

    @Future(message = "End date must be in the future")
    private LocalDate endDate;

    @DecimalMin(value = "0.0", message = "Price must be positive")
    private BigDecimal price;

    private Boolean isPublished;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    private String categoryName;

    @NotNull(message = "Teacher ID is required")
    private Long teacherId;

    private String teacherName;

    private List<ModuleDTO> modules = new ArrayList<>();

    private List<@NotBlank(message = "Tag cannot be blank") String> tags = new ArrayList<>();
}