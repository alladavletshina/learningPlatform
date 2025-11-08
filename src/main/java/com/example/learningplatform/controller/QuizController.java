package com.example.learningplatform.controller;

import com.example.learningplatform.dto.QuizDTO;
import com.example.learningplatform.dto.QuizSubmissionDTO;
import com.example.learningplatform.entity.Quiz;
import com.example.learningplatform.service.QuizService;
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
    public ResponseEntity<QuizDTO> createQuiz(@RequestBody Quiz quiz) {
        QuizDTO createdQuiz = quizService.createQuiz(quiz);
        return ResponseEntity.ok(createdQuiz);
    }

    @PostMapping("/{quizId}/submit")
    public ResponseEntity<QuizSubmissionDTO> submitQuiz(
            @PathVariable Long quizId,
            @RequestParam Long studentId,
            @RequestBody Map<Long, Long> answers) {
        QuizSubmissionDTO submission = quizService.submitQuiz(quizId, studentId, answers);
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
}