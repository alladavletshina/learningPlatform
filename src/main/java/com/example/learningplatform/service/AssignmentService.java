package com.example.learningplatform.service;

import com.example.learningplatform.dto.AssignmentDTO;
import com.example.learningplatform.dto.SubmissionDTO;
import com.example.learningplatform.entity.Assignment;
import com.example.learningplatform.entity.Lesson;
import com.example.learningplatform.entity.Submission;
import com.example.learningplatform.entity.User;
import com.example.learningplatform.exception.ResourceNotFoundException;
import com.example.learningplatform.repository.AssignmentRepository;
import com.example.learningplatform.repository.LessonRepository;
import com.example.learningplatform.repository.SubmissionRepository;
import com.example.learningplatform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final SubmissionRepository submissionRepository;
    private final LessonRepository lessonRepository;
    private final UserRepository userRepository;

    public AssignmentDTO createAssignment(Assignment assignment, Long lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found with id: " + lessonId));

        assignment.setLesson(lesson);
        Assignment savedAssignment = assignmentRepository.save(assignment);
        log.info("Created assignment with id: {}", savedAssignment.getId());
        return convertToDTO(savedAssignment);
    }

    public SubmissionDTO submitAssignment(Long assignmentId, Long studentId, String content) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found with id: " + assignmentId));

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        submissionRepository.findByStudentIdAndAssignmentId(studentId, assignmentId)
                .ifPresent(submission -> {
                    throw new IllegalArgumentException("Student has already submitted this assignment");
                });

        Submission submission = new Submission();
        submission.setAssignment(assignment);
        submission.setStudent(student);
        submission.setContent(content);

        Submission savedSubmission = submissionRepository.save(submission);
        log.info("Student {} submitted assignment {}", studentId, assignmentId);
        return convertToSubmissionDTO(savedSubmission);
    }

    public SubmissionDTO gradeSubmission(Long submissionId, Integer score, String feedback) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found with id: " + submissionId));

        if (score < 0 || score > submission.getAssignment().getMaxScore()) {
            throw new IllegalArgumentException("Score must be between 0 and " + submission.getAssignment().getMaxScore());
        }

        submission.setScore(score);
        submission.setFeedback(feedback);

        Submission updatedSubmission = submissionRepository.save(submission);
        log.info("Graded submission {} with score {}", submissionId, score);
        return convertToSubmissionDTO(updatedSubmission);
    }

    @Transactional(readOnly = true)
    public List<SubmissionDTO> getSubmissionsByAssignment(Long assignmentId) {
        return submissionRepository.findByAssignmentIdWithStudent(assignmentId).stream()
                .map(this::convertToSubmissionDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SubmissionDTO> getSubmissionsByStudent(Long studentId) {
        return submissionRepository.findByStudentIdWithAssignment(studentId).stream()
                .map(this::convertToSubmissionDTO)
                .collect(Collectors.toList());
    }

    private AssignmentDTO convertToDTO(Assignment assignment) {
        AssignmentDTO dto = new AssignmentDTO();
        dto.setId(assignment.getId());
        dto.setTitle(assignment.getTitle());
        dto.setDescription(assignment.getDescription());
        dto.setDueDate(assignment.getDueDate());
        dto.setMaxScore(assignment.getMaxScore());
        dto.setLessonId(assignment.getLesson().getId());
        dto.setLessonTitle(assignment.getLesson().getTitle());
        return dto;
    }

    private SubmissionDTO convertToSubmissionDTO(Submission submission) {
        SubmissionDTO dto = new SubmissionDTO();
        dto.setId(submission.getId());
        dto.setContent(submission.getContent());
        dto.setSubmittedAt(submission.getSubmittedAt());
        dto.setScore(submission.getScore());
        dto.setFeedback(submission.getFeedback());
        dto.setAssignmentId(submission.getAssignment().getId());
        dto.setAssignmentTitle(submission.getAssignment().getTitle());
        dto.setStudentId(submission.getStudent().getId());
        dto.setStudentName(submission.getStudent().getName());
        return dto;
    }
}