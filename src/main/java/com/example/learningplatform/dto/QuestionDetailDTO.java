package com.example.learningplatform.dto;

import com.example.learningplatform.entity.enums.QuestionType;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class QuestionDetailDTO {
    private Long id;
    private String text;
    private QuestionType type;
    private Integer points;
    private List<AnswerOptionDTO> options = new ArrayList<>();
}