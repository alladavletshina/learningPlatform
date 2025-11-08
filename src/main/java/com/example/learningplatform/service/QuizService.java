package com.example.learningplatform.service;

import com.example.learningplatform.dto.QuizDTO;
import com.example.learningplatform.dto.QuizSubmissionDTO;
import com.example.learningplatform.entity.*;
import com.example.learningplatform.exception.ResourceNotFoundException;
import com.example.learningplatform.repository.QuizRepository;
import com.example.learningplatform.repository.QuizSubmissionRepository;
import com.example.learningplatform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class QuizService {

    private final QuizRepository quizRepository;
    private final QuizSubmissionRepository quizSubmissionRepository;
    private final UserRepository userRepository;

    public QuizDTO createQuiz(Quiz quiz) {
        Quiz savedQuiz = quizRepository.save(quiz);
        log.info("Created quiz with id: {}", savedQuiz.getId());
        return convertToDTO(savedQuiz);
    }

    public QuizSubmissionDTO submitQuiz(Long quizId, Long studentId, Map<Long, Long> answers) {
        Quiz quiz = quizRepository.findByIdWithQuestions(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id: " + quizId));

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        // Проверяем, не проходил ли студент уже этот тест
        quizSubmissionRepository.findByStudentIdAndQuizId(studentId, quizId)
                .ifPresent(submission -> {
                    throw new IllegalArgumentException("Student has already taken this quiz");
                });

        // Вычисляем результат
        int score = calculateScore(quiz, answers);
        int maxScore = quiz.getQuestions().stream().mapToInt(Question::getPoints).sum();

        QuizSubmission submission = new QuizSubmission();
        submission.setQuiz(quiz);
        submission.setStudent(student);
        submission.setScore(score);

        QuizSubmission savedSubmission = quizSubmissionRepository.save(submission);
        log.info("Student {} submitted quiz {} with score {}/{}", studentId, quizId, score, maxScore);

        return convertToSubmissionDTO(savedSubmission);
    }

    private int calculateScore(Quiz quiz, Map<Long, Long> answers) {
        int score = 0;

        for (Question question : quiz.getQuestions()) {
            Long selectedOptionId = answers.get(question.getId());
            if (selectedOptionId != null) {
                boolean isCorrect = question.getOptions().stream()
                        .filter(AnswerOption::getIsCorrect)
                        .anyMatch(option -> option.getId().equals(selectedOptionId));

                if (isCorrect) {
                    score += question.getPoints();
                }
            }
        }

        return score;
    }

    @Transactional(readOnly = true)
    public List<QuizSubmissionDTO> getQuizSubmissionsByStudent(Long studentId) {
        return quizSubmissionRepository.findByStudentIdWithQuiz(studentId).stream()
                .map(this::convertToSubmissionDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<QuizSubmissionDTO> getQuizSubmissionsByQuiz(Long quizId) {
        return quizSubmissionRepository.findByQuizIdWithStudent(quizId).stream()
                .map(this::convertToSubmissionDTO)
                .collect(Collectors.toList());
    }

    private QuizDTO convertToDTO(Quiz quiz) {
        QuizDTO dto = new QuizDTO();
        dto.setId(quiz.getId());
        dto.setTitle(quiz.getTitle());
        dto.setDescription(quiz.getDescription());
        dto.setTimeLimit(quiz.getTimeLimit());

        if (quiz.getModule() != null) {
            dto.setModuleId(quiz.getModule().getId());
            dto.setModuleTitle(quiz.getModule().getTitle());
        }

        if (quiz.getCourse() != null) {
            dto.setCourseId(quiz.getCourse().getId());
            dto.setCourseTitle(quiz.getCourse().getTitle());
        }

        return dto;
    }

    private QuizSubmissionDTO convertToSubmissionDTO(QuizSubmission submission) {
        QuizSubmissionDTO dto = new QuizSubmissionDTO();
        dto.setId(submission.getId());
        dto.setScore(submission.getScore());
        dto.setTakenAt(submission.getTakenAt());
        dto.setQuizId(submission.getQuiz().getId());
        dto.setQuizTitle(submission.getQuiz().getTitle());
        dto.setStudentId(submission.getStudent().getId());
        dto.setStudentName(submission.getStudent().getName());
        return dto;
    }
}