// File: ./src/main/java/com/example/learningplatform/controller/QuizController.java
package com.example.learningplatform.controller;

import com.example.learningplatform.dto.*;
import com.example.learningplatform.service.QuizService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/quizzes")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;

    @PostMapping
    public ResponseEntity<QuizDTO> createQuiz(@Valid @RequestBody CreateQuizRequest request) {
        QuizDTO createdQuiz = quizService.createQuiz(request);
        return ResponseEntity.ok(createdQuiz);
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<QuizDTO>> getQuizzesByCourse(@PathVariable Long courseId) {
        List<QuizDTO> quizzes = quizService.getQuizzesByCourse(courseId);
        return ResponseEntity.ok(quizzes);
    }

    @GetMapping("/module/{moduleId}")
    public ResponseEntity<QuizDTO> getQuizByModule(@PathVariable Long moduleId) {
        QuizDTO quiz = quizService.getQuizByModule(moduleId);
        return quiz != null ? ResponseEntity.ok(quiz) : ResponseEntity.notFound().build();
    }

    @GetMapping("/{quizId}")
    public ResponseEntity<QuizDetailDTO> getQuizWithQuestions(@PathVariable Long quizId) {
        QuizDetailDTO quiz = quizService.getQuizWithQuestions(quizId);
        return ResponseEntity.ok(quiz);
    }

    @PostMapping("/{quizId}/submit/single")
    public ResponseEntity<QuizSubmissionDTO> submitQuizSingleChoice(
            @PathVariable Long quizId,
            @RequestParam Long studentId,
            @RequestBody Map<Long, Long> answers) {
        QuizSubmissionDTO submission = quizService.submitQuiz(quizId, studentId, answers);
        return ResponseEntity.ok(submission);
    }

    @PostMapping("/{quizId}/submit/multiple")
    public ResponseEntity<QuizSubmissionDTO> submitQuizMultipleChoice(
            @PathVariable Long quizId,
            @RequestParam Long studentId,
            @RequestBody Map<Long, List<Long>> answers) {
        QuizSubmissionDTO submission = quizService.submitQuizWithDetails(quizId, studentId, answers);
        return ResponseEntity.ok(submission);
    }

    @GetMapping("/student/{studentId}/submissions")
    public ResponseEntity<List<QuizSubmissionDTO>> getSubmissionsByStudent(@PathVariable Long studentId) {
        List<QuizSubmissionDTO> submissions = quizService.getQuizSubmissionsByStudent(studentId);
        return ResponseEntity.ok(submissions);
    }

    @GetMapping("/{quizId}/submissions")
    public ResponseEntity<List<QuizSubmissionDTO>> getSubmissionsByQuiz(@PathVariable Long quizId) {
        List<QuizSubmissionDTO> submissions = quizService.getQuizSubmissionsByQuiz(quizId);
        return ResponseEntity.ok(submissions);
    }

    @DeleteMapping("/{quizId}")
    public ResponseEntity<Void> deleteQuiz(@PathVariable Long quizId) {
        quizService.deleteQuiz(quizId);
        return ResponseEntity.noContent().build();
    }
}