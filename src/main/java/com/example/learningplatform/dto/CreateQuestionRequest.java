package com.example.learningplatform.dto;

import com.example.learningplatform.entity.enums.QuestionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class CreateQuestionRequest {

    @NotBlank(message = "Question text is required")
    @Size(min = 5, max = 1000, message = "Question text must be between 5 and 1000 characters")
    private String text;

    @NotNull(message = "Question type is required")
    private QuestionType type;

    @NotNull(message = "Points are required")
    private Integer points = 1;

    @NotNull(message = "Options are required")
    @Size(min = 2, message = "Question must have at least 2 options")
    private List<CreateAnswerOptionRequest> options;
}