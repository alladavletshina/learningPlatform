package com.example.learningplatform.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class QuizDetailDTO {
    private Long id;
    private String title;
    private String description;
    private Long timeLimit;
    private Long courseId;
    private String courseTitle;
    private Long moduleId;
    private String moduleTitle;
    private Integer maxScore;
    private Integer passingScore;
    private List<QuestionDetailDTO> questions = new ArrayList<>();
}