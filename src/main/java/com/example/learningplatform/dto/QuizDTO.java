package com.example.learningplatform.dto;

import lombok.Data;

import java.time.Duration;

@Data
public class QuizDTO {
    private Long id;
    private String title;
    private String description;
    private long timeLimit;
    private Long moduleId;
    private String moduleTitle;
    private Long courseId;
    private String courseTitle;

}