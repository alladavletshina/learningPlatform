package com.example.learningplatform.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SubmissionDTO {
    private Long id;
    private String content;
    private LocalDateTime submittedAt;
    private Integer score;
    private String feedback;
    private Long assignmentId;
    private String assignmentTitle;
    private Long studentId;
    private String studentName;
}