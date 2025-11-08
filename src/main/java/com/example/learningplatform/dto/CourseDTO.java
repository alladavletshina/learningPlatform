package com.example.learningplatform.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class CourseDTO {
    private Long id;
    private String title;
    private String description;
    private Integer duration;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal price;
    private Boolean isPublished;
    private Long categoryId;
    private String categoryName;
    private Long teacherId;
    private String teacherName;
    private List<ModuleDTO> modules = new ArrayList<>();
    private List<String> tags = new ArrayList<>();

}