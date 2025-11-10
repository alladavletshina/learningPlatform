package com.example.learningplatform.service;

import com.example.learningplatform.dto.CreateLessonRequest;
import com.example.learningplatform.dto.CreateModuleRequest;
import com.example.learningplatform.dto.LessonDTO;
import com.example.learningplatform.dto.ModuleDTO;
import com.example.learningplatform.dto.CourseDTO;
import com.example.learningplatform.entity.Category;
import com.example.learningplatform.entity.Course;
import com.example.learningplatform.entity.User;
import com.example.learningplatform.entity.enums.UserRole;
import com.example.learningplatform.repository.CategoryRepository;
import com.example.learningplatform.repository.CourseRepository;
import com.example.learningplatform.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CourseStructureIntegrationTest {

    @Autowired
    private CourseService courseService;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Course testCourse;
    private User teacher;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        // Создаем категорию
        testCategory = new Category();
        testCategory.setName("Test Category");
        testCategory.setDescription("Test Category Description");
        testCategory = categoryRepository.save(testCategory);

        // Создаем преподавателя
        teacher = new User();
        teacher.setName("Test Teacher");
        teacher.setEmail("teacher@test.com");
        teacher.setRole(UserRole.TEACHER);
        teacher = userRepository.save(teacher);

        // Создаем курс с категорией
        testCourse = new Course();
        testCourse.setTitle("Test Course");
        testCourse.setDescription("Test Description");
        testCourse.setTeacher(teacher);
        testCourse.setCategory(testCategory);
        testCourse.setIsPublished(true);
        testCourse = courseRepository.save(testCourse);
    }

    @Test
    void addModuleToCourse_ShouldCreateModuleSuccessfully() {
        // Given
        CreateModuleRequest request = new CreateModuleRequest();
        request.setTitle("New Module");
        request.setDescription("Module Description");
        request.setCourseId(testCourse.getId());
        request.setOrderIndex(1);

        // When
        ModuleDTO result = courseService.addModuleToCourse(request);

        // Then
        assertNotNull(result.getId());
        assertEquals("New Module", result.getTitle());
        assertEquals(testCourse.getId(), result.getCourseId());
        assertEquals(1, result.getOrderIndex());
    }

    @Test
    void addLessonToModule_ShouldCreateLessonSuccessfully() {
        // Given
        CreateModuleRequest moduleRequest = new CreateModuleRequest();
        moduleRequest.setTitle("Test Module");
        moduleRequest.setCourseId(testCourse.getId());
        moduleRequest.setOrderIndex(1);
        ModuleDTO module = courseService.addModuleToCourse(moduleRequest);

        CreateLessonRequest lessonRequest = new CreateLessonRequest();
        lessonRequest.setTitle("New Lesson");
        lessonRequest.setContent("Lesson Content");
        lessonRequest.setModuleId(module.getId());
        lessonRequest.setOrderIndex(1);

        // When
        LessonDTO result = courseService.addLessonToModule(lessonRequest);

        // Then
        assertNotNull(result.getId());
        assertEquals("New Lesson", result.getTitle());
        assertEquals(module.getId(), result.getModuleId());
        assertEquals(1, result.getOrderIndex());
    }

    @Test
    void getCourseFullStructure_ShouldReturnCourseWithModules() {
        // Given
        CreateModuleRequest moduleRequest = new CreateModuleRequest();
        moduleRequest.setTitle("Test Module");
        moduleRequest.setCourseId(testCourse.getId());
        moduleRequest.setOrderIndex(1);
        courseService.addModuleToCourse(moduleRequest);

        // When
        CourseDTO result = courseService.getCourseFullStructure(testCourse.getId());

        // Then - простые проверки
        assertNotNull(result);
        assertEquals(testCourse.getId(), result.getId());
        assertNotNull(result.getModules());
        assertTrue(result.getModules().size() > 0); // Просто проверяем что есть модули
    }

    @Test
    void addModuleWithDuplicateOrder_ShouldThrowException() {
        // Given
        CreateModuleRequest request1 = new CreateModuleRequest();
        request1.setTitle("Module 1");
        request1.setCourseId(testCourse.getId());
        request1.setOrderIndex(1);
        courseService.addModuleToCourse(request1);

        CreateModuleRequest request2 = new CreateModuleRequest();
        request2.setTitle("Module 2");
        request2.setCourseId(testCourse.getId());
        request2.setOrderIndex(1); // Дублирующий orderIndex

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            courseService.addModuleToCourse(request2);
        });
    }

    @Test
    void updateModuleOrder_ShouldUpdateSuccessfully() {
        // Given
        CreateModuleRequest request = new CreateModuleRequest();
        request.setTitle("Test Module");
        request.setCourseId(testCourse.getId());
        request.setOrderIndex(1);
        ModuleDTO module = courseService.addModuleToCourse(request);

        // When
        courseService.updateModuleOrder(module.getId(), 2);

        // Then
        ModuleDTO updatedModule = courseService.getModuleWithLessons(module.getId());
        assertEquals(2, updatedModule.getOrderIndex());
    }

    @Test
    void getModuleWithLessons_ShouldReturnModule() {
        // Given
        CreateModuleRequest moduleRequest = new CreateModuleRequest();
        moduleRequest.setTitle("Test Module");
        moduleRequest.setCourseId(testCourse.getId());
        moduleRequest.setOrderIndex(1);
        ModuleDTO module = courseService.addModuleToCourse(moduleRequest);

        // When
        ModuleDTO result = courseService.getModuleWithLessons(module.getId());

        // Then - простые проверки
        assertNotNull(result);
        assertEquals(module.getId(), result.getId());
        assertNotNull(result.getLessons()); // Просто проверяем что lessons не null
    }

    @Test
    void getModuleWithoutLessons_ShouldReturnModule() {
        // Given
        CreateModuleRequest moduleRequest = new CreateModuleRequest();
        moduleRequest.setTitle("Test Module");
        moduleRequest.setCourseId(testCourse.getId());
        moduleRequest.setOrderIndex(1);
        ModuleDTO module = courseService.addModuleToCourse(moduleRequest);

        // When - не добавляем уроки
        ModuleDTO result = courseService.getModuleWithLessons(module.getId());

        // Then
        assertNotNull(result);
        assertEquals(module.getId(), result.getId());
        assertNotNull(result.getLessons());
    }

    @Test
    void updateLesson_ShouldUpdateSuccessfully() {
        // Given
        CreateModuleRequest moduleRequest = new CreateModuleRequest();
        moduleRequest.setTitle("Test Module");
        moduleRequest.setCourseId(testCourse.getId());
        moduleRequest.setOrderIndex(1);
        ModuleDTO module = courseService.addModuleToCourse(moduleRequest);

        CreateLessonRequest lessonRequest = new CreateLessonRequest();
        lessonRequest.setTitle("Original Lesson");
        lessonRequest.setContent("Original Content");
        lessonRequest.setModuleId(module.getId());
        lessonRequest.setOrderIndex(1);
        LessonDTO lesson = courseService.addLessonToModule(lessonRequest);

        // When
        CreateLessonRequest updateRequest = new CreateLessonRequest();
        updateRequest.setTitle("Updated Lesson");
        updateRequest.setContent("Updated Content");
        updateRequest.setModuleId(module.getId());
        updateRequest.setOrderIndex(2);

        LessonDTO updatedLesson = courseService.updateLesson(lesson.getId(), updateRequest);

        // Then
        assertEquals("Updated Lesson", updatedLesson.getTitle());
        assertEquals("Updated Content", updatedLesson.getContent());
        assertEquals(2, updatedLesson.getOrderIndex());
    }

    @Test
    void addModuleWithDifferentOrder_ShouldWorkSuccessfully() {
        // Given
        CreateModuleRequest request1 = new CreateModuleRequest();
        request1.setTitle("Module 1");
        request1.setCourseId(testCourse.getId());
        request1.setOrderIndex(1);
        courseService.addModuleToCourse(request1);

        CreateModuleRequest request2 = new CreateModuleRequest();
        request2.setTitle("Module 2");
        request2.setCourseId(testCourse.getId());
        request2.setOrderIndex(2); // Разный orderIndex

        // When & Then - не должно быть исключения
        assertDoesNotThrow(() -> {
            courseService.addModuleToCourse(request2);
        });
    }

    @Test
    void getCourseModules_ShouldReturnModulesList() {
        // Given
        CreateModuleRequest request = new CreateModuleRequest();
        request.setTitle("Test Module");
        request.setCourseId(testCourse.getId());
        request.setOrderIndex(1);
        courseService.addModuleToCourse(request);

        // When
        List<ModuleDTO> modules = courseService.getCourseModules(testCourse.getId());

        // Then
        assertNotNull(modules);
        assertTrue(modules.size() > 0);
    }
}