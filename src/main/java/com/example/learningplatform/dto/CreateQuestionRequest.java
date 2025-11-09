package com.example.learningplatform.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class CreateQuestionRequest {
    @NotBlank
    private String text;

    @NotBlank
    private String type;

    private Integer points = 1;

    private List<CreateAnswerOptionRequest> options;
}