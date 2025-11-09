package com.example.learningplatform.dto;

import com.example.learningplatform.entity.enums.EnrollmentStatus;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EnrollmentDTO {
    private Long id;

    @PastOrPresent(message = "Enroll date must be in the past or present")
    private LocalDateTime enrollDate;

    @NotNull(message = "Status is required")
    private EnrollmentStatus status;

    @Min(value = 0, message = "Progress must be at least 0")
    @Max(value = 100, message = "Progress must not exceed 100")
    private Integer progress;

    @NotNull(message = "Student ID is required")
    private Long studentId;

    private String studentName;

    @NotNull(message = "Course ID is required")
    private Long courseId;

    private String courseTitle;
}