package com.example.learningplatform.dto;

import lombok.Data;

@Data
public class LessonDTO {
    private Long id;
    private String title;
    private String content;
    private String videoUrl;
    private Integer orderIndex;
    private Long moduleId;
}