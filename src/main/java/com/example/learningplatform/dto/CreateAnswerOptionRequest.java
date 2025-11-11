package com.example.learningplatform.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateAnswerOptionRequest {

    @NotBlank(message = "Option text is required")
    private String text;

    @NotNull(message = "isCorrect flag is required")
    private Boolean isCorrect = false;
}