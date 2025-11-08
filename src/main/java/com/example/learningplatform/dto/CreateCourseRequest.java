package com.example.learningplatform.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class CreateCourseRequest {
    @NotBlank
    private String title;
    private String description;
    private Integer duration;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal price;
    @NotNull
    private Long categoryId;
    @NotNull
    private Long teacherId;
    private List<String> tags;
}