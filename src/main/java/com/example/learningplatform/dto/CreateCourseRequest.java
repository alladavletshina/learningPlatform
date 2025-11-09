package com.example.learningplatform.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class CreateCourseRequest {
    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    private String title;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    @Min(value = 1, message = "Duration must be at least 1 hour")
    @Max(value = 1000, message = "Duration must not exceed 1000 hours")
    private Integer duration;

    @FutureOrPresent(message = "Start date must be in the present or future")
    private LocalDate startDate;

    @Future(message = "End date must be in the future")
    private LocalDate endDate;

    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @Digits(integer = 6, fraction = 2, message = "Price must have max 6 integer and 2 fraction digits")
    private BigDecimal price;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    @NotNull(message = "Teacher ID is required")
    private Long teacherId;

    private List<@NotBlank(message = "Tag cannot be blank") String> tags;
}