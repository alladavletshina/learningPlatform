package com.example.learningplatform.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class CreateCourseRequest {
    private String title;
    private String description;
    private Integer duration;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal price;
    private Long categoryId;
    private Long teacherId;
    private List<String> tags;
}