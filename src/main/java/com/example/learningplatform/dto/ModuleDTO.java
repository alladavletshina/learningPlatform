package com.example.learningplatform.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ModuleDTO {
    private Long id;
    private String title;
    private String description;
    private Integer orderIndex;
    private Long courseId;
    private List<LessonDTO> lessons = new ArrayList<>();
}