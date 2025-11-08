package com.example.learningplatform.controller;

import com.example.learningplatform.dto.AssignmentDTO;
import com.example.learningplatform.dto.SubmissionDTO;
import com.example.learningplatform.entity.Assignment;
import com.example.learningplatform.service.AssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
public class AssignmentController {

    private final AssignmentService assignmentService;

    @PostMapping("/lesson/{lessonId}")
    public ResponseEntity<AssignmentDTO> createAssignment(
            @PathVariable Long lessonId,
            @RequestBody Assignment assignment) {
        AssignmentDTO createdAssignment = assignmentService.createAssignment(assignment, lessonId);
        return ResponseEntity.ok(createdAssignment);
    }

    @PostMapping("/{assignmentId}/submit")
    public ResponseEntity<SubmissionDTO> submitAssignment(
            @PathVariable Long assignmentId,
            @RequestParam Long studentId,
            @RequestParam String content) {
        SubmissionDTO submission = assignmentService.submitAssignment(assignmentId, studentId, content);
        return ResponseEntity.ok(submission);
    }

    @PutMapping("/submissions/{submissionId}/grade")
    public ResponseEntity<SubmissionDTO> gradeSubmission(
            @PathVariable Long submissionId,
            @RequestParam Integer score,
            @RequestParam(required = false) String feedback) {
        SubmissionDTO submission = assignmentService.gradeSubmission(submissionId, score, feedback);
        return ResponseEntity.ok(submission);
    }

    @GetMapping("/{assignmentId}/submissions")
    public ResponseEntity<List<SubmissionDTO>> getSubmissionsByAssignment(@PathVariable Long assignmentId) {
        List<SubmissionDTO> submissions = assignmentService.getSubmissionsByAssignment(assignmentId);
        return ResponseEntity.ok(submissions);
    }

    @GetMapping("/student/{studentId}/submissions")
    public ResponseEntity<List<SubmissionDTO>> getSubmissionsByStudent(@PathVariable Long studentId) {
        List<SubmissionDTO> submissions = assignmentService.getSubmissionsByStudent(studentId);
        return ResponseEntity.ok(submissions);
    }
}