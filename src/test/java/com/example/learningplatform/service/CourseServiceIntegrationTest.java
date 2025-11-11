package com.example.learningplatform.service;

import com.example.learningplatform.dto.CreateCourseRequest;
import com.example.learningplatform.entity.Category;
import com.example.learningplatform.entity.User;
import com.example.learningplatform.entity.enums.UserRole;
import com.example.learningplatform.repository.CategoryRepository;
import com.example.learningplatform.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CourseServiceIntegrationTest {

    @Autowired
    private CourseService courseService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private User teacher;
    private Category category;

    @BeforeEach
    void setUp() {

        teacher = new User();
        teacher.setName("Professor X");
        teacher.setEmail("professor@example.com");
        teacher.setRole(UserRole.TEACHER);
        teacher = userRepository.save(teacher);

        category = new Category();
        category.setName("Programming Extreem");
        category.setDescription("Programming courses category");
        category = categoryRepository.save(category);
    }

    @Test
    void createCourse_ShouldCreateCourseSuccessfully() {
        // Given
        CreateCourseRequest request = new CreateCourseRequest();
        request.setTitle("Java Programming");
        request.setDescription("Learn Java from scratch");
        request.setDuration(40);
        request.setStartDate(LocalDate.now().plusDays(1));
        request.setEndDate(LocalDate.now().plusMonths(3));
        request.setPrice(BigDecimal.valueOf(99.99));
        request.setCategoryId(category.getId());
        request.setTeacherId(teacher.getId());
        request.setTags(List.of("Java", "Programming", "Beginner"));

        // When
        var result = courseService.createCourse(request);

        // Then
        assertNotNull(result.getId());
        assertEquals("Java Programming", result.getTitle());
        assertEquals(40, result.getDuration());
        assertEquals(BigDecimal.valueOf(99.99), result.getPrice());
        assertEquals(category.getId(), result.getCategoryId());
        assertEquals(teacher.getId(), result.getTeacherId());
        assertFalse(result.getIsPublished());
        assertEquals(3, result.getTags().size());
    }

    @Test
    void publishCourse_ShouldUpdatePublishStatus() {
        // Given
        CreateCourseRequest request = new CreateCourseRequest();
        request.setTitle("Test Course");
        request.setDescription("Test Description");
        request.setCategoryId(category.getId());
        request.setTeacherId(teacher.getId());

        var course = courseService.createCourse(request);
        assertFalse(course.getIsPublished());

        // When
        courseService.publishCourse(course.getId());

        // Then
        var publishedCourse = courseService.getCourseById(course.getId());
        assertTrue(publishedCourse.getIsPublished());
    }

    @Test
    void searchCourses_ShouldReturnMatchingCourses() {
        // Given - создаем курсы с уникальными названиями
        String uniquePrefix = "TestSearch_" + System.currentTimeMillis();

        CreateCourseRequest request1 = new CreateCourseRequest();
        request1.setTitle(uniquePrefix + " Java Advanced");
        request1.setDescription("Advanced Java programming");
        request1.setCategoryId(category.getId());
        request1.setTeacherId(teacher.getId());
        courseService.createCourse(request1);

        CreateCourseRequest request2 = new CreateCourseRequest();
        request2.setTitle(uniquePrefix + " Python Basics");
        request2.setDescription("Learn Python programming");
        request2.setCategoryId(category.getId());
        request2.setTeacherId(teacher.getId());
        courseService.createCourse(request2);

        // When - ищем по уникальному префиксу
        var foundCourses = courseService.searchCourses(uniquePrefix + " Java");

        // Then
        assertEquals(1, foundCourses.size());
        assertEquals(uniquePrefix + " Java Advanced", foundCourses.get(0).getTitle());
    }
}