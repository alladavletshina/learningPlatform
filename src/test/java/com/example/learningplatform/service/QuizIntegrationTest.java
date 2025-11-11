package com.example.learningplatform.service;

import com.example.learningplatform.dto.*;
import com.example.learningplatform.entity.*;
import com.example.learningplatform.entity.enums.QuestionType;
import com.example.learningplatform.entity.enums.UserRole;
import com.example.learningplatform.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class QuizIntegrationTest {

    @Autowired
    private QuizService quizService;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerOptionRepository answerOptionRepository;

    @Autowired
    private QuizSubmissionRepository quizSubmissionRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private ModuleRepository moduleRepository;

    @Autowired
    private CourseReviewRepository courseReviewRepository;

    private Course testCourse;
    private User testStudent;

    @BeforeEach
    void setUp() {
        // Очистка данных в правильном порядке (обратном зависимости)
        quizSubmissionRepository.deleteAll();
        answerOptionRepository.deleteAll();
        questionRepository.deleteAll();
        quizRepository.deleteAll();

        // Очистка связанных данных курса
        submissionRepository.deleteAll();
        assignmentRepository.deleteAll();
        lessonRepository.deleteAll();
        moduleRepository.deleteAll();
        courseReviewRepository.deleteAll();
        enrollmentRepository.deleteAll();

        // Теперь можно удалить курсы и пользователей
        courseRepository.deleteAll();
        userRepository.deleteAll();

        // Создаем преподавателя
        User teacher = new User();
        teacher.setName("Test Teacher");
        teacher.setEmail("teacher@test.com");
        teacher.setRole(UserRole.TEACHER);
        userRepository.save(teacher);

        // Создаем курс
        testCourse = new Course();
        testCourse.setTitle("Test Course");
        testCourse.setDescription("Test Description");
        testCourse.setTeacher(teacher);
        testCourse.setIsPublished(true);
        testCourse = courseRepository.save(testCourse);

        // Создаем студента
        testStudent = new User();
        testStudent.setName("Test Student");
        testStudent.setEmail("student@test.com");
        testStudent.setRole(UserRole.STUDENT);
        testStudent = userRepository.save(testStudent);
    }

    @Test
    void createQuiz_ShouldCreateQuizSuccessfully() {
        // Given
        CreateQuizRequest request = createSampleQuizRequest();

        // When
        QuizDTO result = quizService.createQuiz(request);

        // Then
        assertNotNull(result.getId());
        assertEquals("Java Basics Quiz", result.getTitle());
        assertEquals(testCourse.getId(), result.getCourseId());

        // Проверяем что тест сохранен в БД
        assertTrue(quizRepository.existsById(result.getId()));

        // Проверяем что вопросы созданы
        List<Question> questions = questionRepository.findByQuizId(result.getId());
        assertEquals(2, questions.size());

        // Проверяем что варианты ответов созданы
        int totalOptions = 0;
        for (Question question : questions) {
            List<AnswerOption> options = answerOptionRepository.findByQuestionId(question.getId());
            totalOptions += options.size();
        }
        assertEquals(5, totalOptions); // 2 + 3 варианта
    }

    @Test
    void getQuizWithQuestions_ShouldReturnQuizWithQuestions() {
        // Given
        CreateQuizRequest request = createSampleQuizRequest();
        QuizDTO quiz = quizService.createQuiz(request);

        // When
        QuizDetailDTO quizDetail = quizService.getQuizWithQuestions(quiz.getId());

        // Then
        assertNotNull(quizDetail);
        assertEquals(quiz.getId(), quizDetail.getId());
        assertEquals(2, quizDetail.getQuestions().size());

        // Проверяем структуру вопросов
        QuestionDetailDTO question1 = quizDetail.getQuestions().getFirst();
        assertEquals("What is Java?", question1.getText());
        assertEquals(QuestionType.SINGLE_CHOICE, question1.getType());
        assertEquals(10, question1.getPoints());
        assertEquals(2, question1.getOptions().size());

        QuestionDetailDTO question2 = quizDetail.getQuestions().get(1);
        assertEquals("Which of these are Java features?", question2.getText());
        assertEquals(QuestionType.MULTIPLE_CHOICE, question2.getType());
        assertEquals(20, question2.getPoints());
        assertEquals(3, question2.getOptions().size());
    }

    @Test
    void submitQuiz_ShouldCalculateScoreCorrectly() {
        // Given
        CreateQuizRequest request = createSampleQuizRequest();
        QuizDTO quiz = quizService.createQuiz(request);

        // Получаем детали для получения ID вопросов
        QuizDetailDTO quizDetail = quizService.getQuizWithQuestions(quiz.getId());
        QuestionDetailDTO question1 = quizDetail.getQuestions().getFirst();

        // Находим правильный ответ
        Long correctOptionId = question1.getOptions().stream()
                .filter(AnswerOptionDTO::getIsCorrect)
                .findFirst()
                .map(AnswerOptionDTO::getId)
                .orElseThrow();

        // When - отправляем правильный ответ
        Map<Long, List<Long>> answers = new HashMap<>();
        answers.put(question1.getId(), List.of(correctOptionId));

        QuizSubmissionDTO submission = quizService.submitQuizWithDetails(quiz.getId(), testStudent.getId(), answers);

        // Then
        assertNotNull(submission.getId());
        assertEquals(10, submission.getScore()); // 10 баллов за первый вопрос
        assertEquals(testStudent.getId(), submission.getStudentId());
        assertEquals(quiz.getId(), submission.getQuizId());

        // Проверяем что submission сохранен в БД
        assertTrue(quizSubmissionRepository.existsById(submission.getId()));
    }

    @Test
    void submitQuiz_DuplicateSubmission_ShouldThrowException() {
        // Given
        CreateQuizRequest request = createSampleQuizRequest();
        QuizDTO quiz = quizService.createQuiz(request);

        QuizDetailDTO quizDetail = quizService.getQuizWithQuestions(quiz.getId());
        QuestionDetailDTO question = quizDetail.getQuestions().getFirst();

        Long correctOptionId = question.getOptions().stream()
                .filter(AnswerOptionDTO::getIsCorrect)
                .findFirst()
                .map(AnswerOptionDTO::getId)
                .orElseThrow();

        Map<Long, List<Long>> answers = new HashMap<>();
        answers.put(question.getId(), List.of(correctOptionId));

        // Первая отправка
        quizService.submitQuizWithDetails(quiz.getId(), testStudent.getId(), answers);

        // When & Then - вторая отправка должна вызвать исключение
        assertThrows(IllegalArgumentException.class, () -> {
            quizService.submitQuizWithDetails(quiz.getId(), testStudent.getId(), answers);
        });
    }

    @Test
    void getQuizSubmissions_ShouldReturnSubmissions() {
        // Given
        CreateQuizRequest request = createSampleQuizRequest();
        QuizDTO quiz = quizService.createQuiz(request);

        QuizDetailDTO quizDetail = quizService.getQuizWithQuestions(quiz.getId());
        QuestionDetailDTO question = quizDetail.getQuestions().getFirst();

        Long correctOptionId = question.getOptions().stream()
                .filter(AnswerOptionDTO::getIsCorrect)
                .findFirst()
                .map(AnswerOptionDTO::getId)
                .orElseThrow();

        Map<Long, List<Long>> answers = new HashMap<>();
        answers.put(question.getId(), List.of(correctOptionId));

        quizService.submitQuizWithDetails(quiz.getId(), testStudent.getId(), answers);

        // When
        List<QuizSubmissionDTO> submissionsByStudent = quizService.getQuizSubmissionsByStudent(testStudent.getId());
        List<QuizSubmissionDTO> submissionsByQuiz = quizService.getQuizSubmissionsByQuiz(quiz.getId());

        // Then
        assertEquals(1, submissionsByStudent.size());
        assertEquals(1, submissionsByQuiz.size());
        assertEquals(testStudent.getId(), submissionsByStudent.getFirst().getStudentId());
        assertEquals(quiz.getId(), submissionsByQuiz.getFirst().getQuizId());
    }

    @Test
    void deleteQuiz_WithoutSubmissions_ShouldDeleteSuccessfully() {
        // Given
        CreateQuizRequest request = createSampleQuizRequest();
        QuizDTO quiz = quizService.createQuiz(request);

        // Проверяем что данные созданы
        assertEquals(2, questionRepository.findByQuizId(quiz.getId()).size());

        // When
        quizService.deleteQuiz(quiz.getId());

        // Then
        assertFalse(quizRepository.existsById(quiz.getId()));
        assertEquals(0, questionRepository.findByQuizId(quiz.getId()).size());
    }

    @Test
    void deleteQuiz_WithSubmissions_ShouldThrowException() {
        // Given
        CreateQuizRequest request = createSampleQuizRequest();
        QuizDTO quiz = quizService.createQuiz(request);

        QuizDetailDTO quizDetail = quizService.getQuizWithQuestions(quiz.getId());
        QuestionDetailDTO question = quizDetail.getQuestions().getFirst();

        Long correctOptionId = question.getOptions().stream()
                .filter(AnswerOptionDTO::getIsCorrect)
                .findFirst()
                .map(AnswerOptionDTO::getId)
                .orElseThrow();

        Map<Long, List<Long>> answers = new HashMap<>();
        answers.put(question.getId(), List.of(correctOptionId));

        quizService.submitQuizWithDetails(quiz.getId(), testStudent.getId(), answers);

        // When & Then
        assertThrows(IllegalStateException.class, () -> {
            quizService.deleteQuiz(quiz.getId());
        });
    }

    @Test
    void getQuizzesByCourse_ShouldReturnCourseQuizzes() {
        // Given
        CreateQuizRequest request = createSampleQuizRequest();
        quizService.createQuiz(request);

        // When
        List<QuizDTO> quizzes = quizService.getQuizzesByCourse(testCourse.getId());

        // Then
        assertFalse(quizzes.isEmpty());
        assertEquals("Java Basics Quiz", quizzes.getFirst().getTitle());
        assertEquals(testCourse.getId(), quizzes.getFirst().getCourseId());
    }

    private CreateQuizRequest createSampleQuizRequest() {
        CreateQuizRequest request = new CreateQuizRequest();
        request.setTitle("Java Basics Quiz");
        request.setDescription("Test your Java knowledge");
        request.setCourseId(testCourse.getId());
        request.setTimeLimit(1800L);

        // Создаем вопросы
        CreateQuestionRequest question1 = new CreateQuestionRequest();
        question1.setText("What is Java?");
        question1.setType(QuestionType.SINGLE_CHOICE);
        question1.setPoints(10);

        CreateAnswerOptionRequest option1 = new CreateAnswerOptionRequest();
        option1.setText("A programming language");
        option1.setIsCorrect(true);

        CreateAnswerOptionRequest option2 = new CreateAnswerOptionRequest();
        option2.setText("A coffee brand");
        option2.setIsCorrect(false);

        question1.setOptions(Arrays.asList(option1, option2));

        CreateQuestionRequest question2 = new CreateQuestionRequest();
        question2.setText("Which of these are Java features?");
        question2.setType(QuestionType.MULTIPLE_CHOICE);
        question2.setPoints(20);

        CreateAnswerOptionRequest option3 = new CreateAnswerOptionRequest();
        option3.setText("Object-oriented");
        option3.setIsCorrect(true);

        CreateAnswerOptionRequest option4 = new CreateAnswerOptionRequest();
        option4.setText("Platform independent");
        option4.setIsCorrect(true);

        CreateAnswerOptionRequest option5 = new CreateAnswerOptionRequest();
        option5.setText("Compiled to machine code");
        option5.setIsCorrect(false);

        question2.setOptions(Arrays.asList(option3, option4, option5));

        request.setQuestions(Arrays.asList(question1, question2));
        return request;
    }
}