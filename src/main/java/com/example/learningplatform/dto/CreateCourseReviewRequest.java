package com.example.learningplatform.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateCourseReviewRequest {

    @NotNull
    private Long courseId;

    @NotNull
    @Min(1)
    @Max(5)
    private Integer rating;

    private String comment;
}