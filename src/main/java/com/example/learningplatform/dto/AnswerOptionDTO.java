package com.example.learningplatform.dto;

import lombok.Data;

@Data
public class AnswerOptionDTO {
    private Long id;
    private String text;
    private Boolean isCorrect;
}