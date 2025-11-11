// File: ./src/main/java/com/example/learningplatform/controller/CourseStructureController.java
package com.example.learningplatform.controller;

import com.example.learningplatform.dto.CourseDTO;
import com.example.learningplatform.dto.CreateLessonRequest;
import com.example.learningplatform.dto.CreateModuleRequest;
import com.example.learningplatform.dto.LessonDTO;
import com.example.learningplatform.dto.ModuleDTO;
import com.example.learningplatform.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/courses/{courseId}/structure")
@RequiredArgsConstructor
public class CourseStructureController {

    private final CourseService courseService;

    @PostMapping("/modules")
    public ResponseEntity<ModuleDTO> addModuleToCourse(
            @PathVariable Long courseId,
            @Valid @RequestBody CreateModuleRequest request) {


        if (!courseId.equals(request.getCourseId())) {
            throw new IllegalArgumentException("Course ID in path and request body must match");
        }

        ModuleDTO module = courseService.addModuleToCourse(request);
        return ResponseEntity.ok(module);
    }

    @PostMapping("/modules/{moduleId}/lessons")
    public ResponseEntity<LessonDTO> addLessonToModule(
            @PathVariable Long courseId,
            @PathVariable Long moduleId,
            @Valid @RequestBody CreateLessonRequest request) {

        if (!moduleId.equals(request.getModuleId())) {
            throw new IllegalArgumentException("Module ID in path and request body must match");
        }

        LessonDTO lesson = courseService.addLessonToModule(request);
        return ResponseEntity.ok(lesson);
    }

    @GetMapping("/full")
    public ResponseEntity<CourseDTO> getCourseFullStructure(@PathVariable Long courseId) {
        CourseDTO course = courseService.getCourseFullStructure(courseId);
        return ResponseEntity.ok(course);
    }

    @PutMapping("/modules/{moduleId}/order")
    public ResponseEntity<Void> updateModuleOrder(
            @PathVariable Long courseId,
            @PathVariable Long moduleId,
            @RequestParam Integer order) {

        courseService.updateModuleOrder(moduleId, order);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/lessons/{lessonId}/order")
    public ResponseEntity<Void> updateLessonOrder(
            @PathVariable Long courseId,
            @PathVariable Long lessonId,
            @RequestParam Integer order) {

        courseService.updateLessonOrder(lessonId, order);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/modules/{moduleId}")
    public ResponseEntity<Void> deleteModule(
            @PathVariable Long courseId,
            @PathVariable Long moduleId) {

        courseService.deleteModule(moduleId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/lessons/{lessonId}")
    public ResponseEntity<Void> deleteLesson(
            @PathVariable Long courseId,
            @PathVariable Long lessonId) {

        courseService.deleteLesson(lessonId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/modules/{moduleId}")
    public ResponseEntity<ModuleDTO> updateModule(
            @PathVariable Long courseId,
            @PathVariable Long moduleId,
            @Valid @RequestBody CreateModuleRequest request) {

        if (!courseId.equals(request.getCourseId()) || !moduleId.equals(request.getCourseId())) {
            throw new IllegalArgumentException("Course ID and Module ID must match path variables");
        }

        ModuleDTO updatedModule = courseService.updateModule(moduleId, request);
        return ResponseEntity.ok(updatedModule);
    }

    @PutMapping("/lessons/{lessonId}")
    public ResponseEntity<LessonDTO> updateLesson(
            @PathVariable Long courseId,
            @PathVariable Long lessonId,
            @Valid @RequestBody CreateLessonRequest request) {

        if (!lessonId.equals(request.getModuleId())) {
            throw new IllegalArgumentException("Lesson ID must match path variable");
        }

        LessonDTO updatedLesson = courseService.updateLesson(lessonId, request);
        return ResponseEntity.ok(updatedLesson);
    }

    @GetMapping("/modules/{moduleId}")
    public ResponseEntity<ModuleDTO> getModuleWithLessons(
            @PathVariable Long courseId,
            @PathVariable Long moduleId) {

        ModuleDTO module = courseService.getModuleWithLessons(moduleId);
        return ResponseEntity.ok(module);
    }

    @GetMapping("/lessons/{lessonId}")
    public ResponseEntity<LessonDTO> getLessonWithAssignments(
            @PathVariable Long courseId,
            @PathVariable Long lessonId) {

        LessonDTO lesson = courseService.getLessonWithAssignments(lessonId);
        return ResponseEntity.ok(lesson);
    }
}