package com.example.learningplatform.dto;

import com.example.learningplatform.entity.enums.EnrollmentStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EnrollmentDTO {
    private Long id;
    private LocalDateTime enrollDate;
    private EnrollmentStatus status;
    private Integer progress;
    private Long studentId;
    private String studentName;
    private Long courseId;
    private String courseTitle;
}