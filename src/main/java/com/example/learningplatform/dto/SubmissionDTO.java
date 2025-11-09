package com.example.learningplatform.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SubmissionDTO {
    private Long id;

    @NotBlank(message = "Content is required")
    @Size(min = 10, max = 5000, message = "Content must be between 10 and 5000 characters")
    private String content;

    @PastOrPresent(message = "Submission date must be in the past or present")
    private LocalDateTime submittedAt;

    @Min(value = 0, message = "Score must be at least 0")
    private Integer score;

    @Size(max = 1000, message = "Feedback must not exceed 1000 characters")
    private String feedback;

    @NotNull(message = "Assignment ID is required")
    private Long assignmentId;

    private String assignmentTitle;

    @NotNull(message = "Student ID is required")
    private Long studentId;

    private String studentName;
}