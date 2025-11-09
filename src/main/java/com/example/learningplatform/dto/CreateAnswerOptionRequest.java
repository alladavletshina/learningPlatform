package com.example.learningplatform.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateAnswerOptionRequest {
    @NotBlank
    private String text;

    private Boolean isCorrect = false;
}