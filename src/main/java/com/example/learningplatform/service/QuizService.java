package com.example.learningplatform.service;

import com.example.learningplatform.dto.*;
import com.example.learningplatform.entity.*;
import com.example.learningplatform.entity.Module;
import com.example.learningplatform.exception.ResourceNotFoundException;
import com.example.learningplatform.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class QuizService {

    private final QuizRepository quizRepository;
    private final QuizSubmissionRepository quizSubmissionRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final ModuleRepository moduleRepository;
    private final QuestionRepository questionRepository;
    private final AnswerOptionRepository answerOptionRepository;

    public QuizDTO createQuiz(CreateQuizRequest request) {
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + request.getCourseId()));

        Quiz quiz = new Quiz();
        quiz.setTitle(request.getTitle());
        quiz.setDescription(request.getDescription());
        quiz.setTimeLimit(request.getTimeLimit());
        quiz.setCourse(course);

        if (request.getModuleId() != null) {
            Module module = moduleRepository.findById(request.getModuleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Module not found with id: " + request.getModuleId()));
            quiz.setModule(module);
        }

        Quiz savedQuiz = quizRepository.save(quiz);

        // Создаем вопросы и варианты ответов
        for (CreateQuestionRequest questionRequest : request.getQuestions()) {
            Question question = new Question();
            question.setQuiz(savedQuiz);
            question.setText(questionRequest.getText());
            question.setType(questionRequest.getType());
            question.setPoints(questionRequest.getPoints());

            Question savedQuestion = questionRepository.save(question);

            for (CreateAnswerOptionRequest optionRequest : questionRequest.getOptions()) {
                AnswerOption option = new AnswerOption();
                option.setQuestion(savedQuestion);
                option.setText(optionRequest.getText());
                option.setIsCorrect(optionRequest.getIsCorrect());
                answerOptionRepository.save(option);
            }
        }

        log.info("Created quiz with id: {} and {} questions", savedQuiz.getId(), request.getQuestions().size());
        return convertToSimpleDTO(savedQuiz);
    }

    @Transactional(readOnly = true)
    public QuizDetailDTO getQuizWithQuestions(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id: " + quizId));

        // Загружаем вопросы отдельно
        List<Question> questions = questionRepository.findByQuizId(quizId);

        // Для каждого вопроса загружаем варианты ответов
        List<QuestionDetailDTO> questionDTOs = new ArrayList<>();
        for (Question question : questions) {
            List<AnswerOption> options = answerOptionRepository.findByQuestionId(question.getId());
            questionDTOs.add(convertToQuestionDetailDTO(question, options));
        }

        return convertToDetailDTO(quiz, questionDTOs);
    }

    public QuizSubmissionDTO submitQuizWithDetails(Long quizId, Long studentId, Map<Long, List<Long>> answers) {
        // Проверяем существование теста и студента
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id: " + quizId));

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        // Проверяем, не проходил ли студент уже этот тест
        if (quizSubmissionRepository.findByStudentIdAndQuizId(studentId, quizId).isPresent()) {
            throw new IllegalArgumentException("Student has already taken this quiz");
        }

        // Загружаем вопросы и варианты ответов для подсчета баллов
        List<Question> questions = questionRepository.findByQuizId(quizId);
        Map<Long, Question> questionMap = new HashMap<>();
        Map<Long, List<AnswerOption>> questionOptionsMap = new HashMap<>();

        for (Question question : questions) {
            questionMap.put(question.getId(), question);
            List<AnswerOption> options = answerOptionRepository.findByQuestionId(question.getId());
            questionOptionsMap.put(question.getId(), options);
        }

        // Вычисляем результат
        int score = calculateScore(questionMap, questionOptionsMap, answers);

        QuizSubmission submission = new QuizSubmission();
        submission.setQuiz(quiz);
        submission.setStudent(student);
        submission.setScore(score);

        QuizSubmission savedSubmission = quizSubmissionRepository.save(submission);
        log.info("Student {} submitted quiz {} with score {}", studentId, quizId, score);

        return convertToSubmissionDTO(savedSubmission);
    }

    private int calculateScore(Map<Long, Question> questionMap,
                               Map<Long, List<AnswerOption>> questionOptionsMap,
                               Map<Long, List<Long>> answers) {
        int score = 0;

        for (Map.Entry<Long, List<Long>> entry : answers.entrySet()) {
            Long questionId = entry.getKey();
            List<Long> selectedOptionIds = entry.getValue();

            Question question = questionMap.get(questionId);
            List<AnswerOption> options = questionOptionsMap.get(questionId);

            if (question != null && options != null) {
                boolean isCorrect = isAnswerCorrect(question, options, selectedOptionIds);
                if (isCorrect) {
                    score += question.getPoints();
                }
            }
        }

        return score;
    }

    private boolean isAnswerCorrect(Question question, List<AnswerOption> options, List<Long> selectedOptionIds) {
        List<Long> correctOptionIds = options.stream()
                .filter(AnswerOption::getIsCorrect)
                .map(AnswerOption::getId)
                .collect(Collectors.toList());

        return selectedOptionIds.containsAll(correctOptionIds) &&
                correctOptionIds.containsAll(selectedOptionIds);
    }

    @Transactional(readOnly = true)
    public List<QuizDTO> getQuizzesByCourse(Long courseId) {
        return quizRepository.findByCourseId(courseId).stream()
                .map(this::convertToSimpleDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<QuizSubmissionDTO> getQuizSubmissionsByStudent(Long studentId) {
        return quizSubmissionRepository.findByStudentId(studentId).stream()
                .map(this::convertToSubmissionDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<QuizSubmissionDTO> getQuizSubmissionsByQuiz(Long quizId) {
        return quizSubmissionRepository.findByQuizId(quizId).stream()
                .map(this::convertToSubmissionDTO)
                .collect(Collectors.toList());
    }

    public void deleteQuiz(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id: " + quizId));

        // Проверяем, есть ли уже отправленные решения
        if (!quizSubmissionRepository.findByQuizId(quizId).isEmpty()) {
            throw new IllegalStateException("Cannot delete quiz that has submissions");
        }

        // Удаляем варианты ответов, вопросы и затем тест
        List<Question> questions = questionRepository.findByQuizId(quizId);
        for (Question question : questions) {
            answerOptionRepository.deleteByQuestionId(question.getId());
        }
        questionRepository.deleteByQuizId(quizId);
        quizRepository.delete(quiz);

        log.info("Deleted quiz with id: {}", quizId);
    }

    // Простые конвертеры без ленивой загрузки
    private QuizDTO convertToSimpleDTO(Quiz quiz) {
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

    private QuizDetailDTO convertToDetailDTO(Quiz quiz, List<QuestionDetailDTO> questions) {
        QuizDetailDTO dto = new QuizDetailDTO();
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

        dto.setQuestions(questions);
        return dto;
    }

    private QuestionDetailDTO convertToQuestionDetailDTO(Question question, List<AnswerOption> options) {
        QuestionDetailDTO dto = new QuestionDetailDTO();
        dto.setId(question.getId());
        dto.setText(question.getText());
        dto.setType(question.getType());
        dto.setPoints(question.getPoints());
        dto.setOptions(options.stream()
                .map(this::convertToAnswerOptionDTO)
                .collect(Collectors.toList()));
        return dto;
    }

    private AnswerOptionDTO convertToAnswerOptionDTO(AnswerOption option) {
        AnswerOptionDTO dto = new AnswerOptionDTO();
        dto.setId(option.getId());
        dto.setText(option.getText());
        dto.setIsCorrect(option.getIsCorrect());
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

    @Transactional(readOnly = true)
    public QuizDTO getQuizByModule(Long moduleId) {
        return quizRepository.findByModuleId(moduleId)
                .map(this::convertToSimpleDTO)
                .orElse(null);
    }

    public QuizSubmissionDTO submitQuiz(Long quizId, Long studentId, Map<Long, Long> answers) {
        // Конвертируем Map<Long, Long> в Map<Long, List<Long>> для совместимости
        Map<Long, List<Long>> convertedAnswers = new HashMap<>();
        for (Map.Entry<Long, Long> entry : answers.entrySet()) {
            convertedAnswers.put(entry.getKey(), List.of(entry.getValue()));
        }

        return submitQuizWithDetails(quizId, studentId, convertedAnswers);
    }
}