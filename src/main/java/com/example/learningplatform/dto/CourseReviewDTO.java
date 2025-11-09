package com.example.learningplatform.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CourseReviewDTO {
    private Long id;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
    private Long courseId;
    private String courseTitle;
    private Long studentId;
    private String studentName;
}