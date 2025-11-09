package com.example.learningplatform.validation;

import com.example.learningplatform.dto.CreateCourseRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CreateCourseRequestValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenAllFieldsValid_thenNoViolations() {
        CreateCourseRequest request = new CreateCourseRequest();
        request.setTitle("Valid Title");
        request.setDescription("Valid Description");
        request.setDuration(40);
        request.setStartDate(LocalDate.now().plusDays(1));
        request.setEndDate(LocalDate.now().plusMonths(3));
        request.setPrice(BigDecimal.valueOf(99.99));
        request.setCategoryId(1L);
        request.setTeacherId(1L);
        request.setTags(List.of("Java", "Spring"));

        Set<ConstraintViolation<CreateCourseRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void whenTitleBlank_thenValidationFails() {
        CreateCourseRequest request = new CreateCourseRequest();
        request.setTitle("");
        request.setCategoryId(1L);
        request.setTeacherId(1L);

        Set<ConstraintViolation<CreateCourseRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());

        // Проверяем, что есть хотя бы одна ошибка валидации для title
        boolean hasTitleError = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("title"));
        assertTrue(hasTitleError);
    }

    @Test
    void whenTitleNull_thenValidationFails() {
        CreateCourseRequest request = new CreateCourseRequest();
        request.setTitle(null);
        request.setCategoryId(1L);
        request.setTeacherId(1L);

        Set<ConstraintViolation<CreateCourseRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());

        boolean hasTitleError = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("title"));
        assertTrue(hasTitleError);
    }

    @Test
    void whenTitleTooShort_thenValidationFails() {
        CreateCourseRequest request = new CreateCourseRequest();
        request.setTitle("Ab");
        request.setCategoryId(1L);
        request.setTeacherId(1L);

        Set<ConstraintViolation<CreateCourseRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());

        boolean hasSizeError = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("title")
                        && v.getMessage().contains("between 3 and 100 characters"));
        assertTrue(hasSizeError);
    }

    @Test
    void whenTitleTooLong_thenValidationFails() {
        CreateCourseRequest request = new CreateCourseRequest();
        request.setTitle("A".repeat(101));
        request.setCategoryId(1L);
        request.setTeacherId(1L);

        Set<ConstraintViolation<CreateCourseRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());

        boolean hasSizeError = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("title")
                        && v.getMessage().contains("between 3 and 100 characters"));
        assertTrue(hasSizeError);
    }

    @Test
    void whenPriceNegative_thenValidationFails() {
        CreateCourseRequest request = new CreateCourseRequest();
        request.setTitle("Valid Title");
        request.setCategoryId(1L);
        request.setTeacherId(1L);
        request.setPrice(BigDecimal.valueOf(-10));

        Set<ConstraintViolation<CreateCourseRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertEquals("Price must be greater than 0", violations.iterator().next().getMessage());
    }

    @Test
    void whenCategoryIdNull_thenValidationFails() {
        CreateCourseRequest request = new CreateCourseRequest();
        request.setTitle("Valid Title");
        request.setCategoryId(null);
        request.setTeacherId(1L);

        Set<ConstraintViolation<CreateCourseRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());

        boolean hasCategoryError = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("categoryId"));
        assertTrue(hasCategoryError);
    }

    @Test
    void whenTeacherIdNull_thenValidationFails() {
        CreateCourseRequest request = new CreateCourseRequest();
        request.setTitle("Valid Title");
        request.setCategoryId(1L);
        request.setTeacherId(null);

        Set<ConstraintViolation<CreateCourseRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());

        boolean hasTeacherError = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("teacherId"));
        assertTrue(hasTeacherError);
    }
}