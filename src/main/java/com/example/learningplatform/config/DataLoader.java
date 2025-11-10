package com.example.learningplatform.config;

import com.example.learningplatform.entity.*;
import com.example.learningplatform.entity.Module;
import com.example.learningplatform.entity.enums.UserRole;
import com.example.learningplatform.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final CourseRepository courseRepository;
    private final TagRepository tagRepository;
    private final ModuleRepository moduleRepository;
    private final LessonRepository lessonRepository;
    private final AssignmentRepository assignmentRepository;
    private final EnrollmentRepository enrollmentRepository;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            loadDemoData();
        }
    }

    private void loadDemoData() {
        log.info("Loading demo data...");

        // Создание пользователей
        User admin = createUser("Admin User", "admin@example.com", UserRole.ADMIN, "+1111111111");
        User teacher1 = createUser("Professor Smith", "smith@example.com", UserRole.TEACHER, "+2222222222");
        User teacher2 = createUser("Dr. Johnson", "johnson@example.com", UserRole.TEACHER, "+3333333333");
        User student1 = createUser("Alice Brown", "alice@example.com", UserRole.STUDENT, "+4444444444");
        User student2 = createUser("Bob Wilson", "bob@example.com", UserRole.STUDENT, "+5555555555");

        // Создание категорий
        Category programming = createCategory("Programming", "Software development courses");
        Category design = createCategory("Design", "UI/UX and graphic design courses");
        Category business = createCategory("Business", "Business and management courses");

        // Создание тегов
        Tag javaTag = createTag("Java");
        Tag springTag = createTag("Spring Boot");
        Tag pythonTag = createTag("Python");
        Tag webTag = createTag("Web Development");
        Tag beginnerTag = createTag("Beginner");
        Tag advancedTag = createTag("Advanced");

        // Создание курсов
        Course javaCourse = createCourse("Java Fundamentals",
                "Learn Java programming from scratch", 60, LocalDate.now(),
                LocalDate.now().plusMonths(6), BigDecimal.valueOf(149.99), programming, teacher1,
                List.of(javaTag, beginnerTag), true);

        Course springCourse = createCourse("Spring Boot Masterclass",
                "Build modern web applications with Spring Boot", 80, LocalDate.now().plusDays(7),
                LocalDate.now().plusMonths(8), BigDecimal.valueOf(199.99), programming, teacher2,
                List.of(javaTag, springTag, webTag, advancedTag), true);

        Course pythonCourse = createCourse("Python for Data Science",
                "Data analysis and machine learning with Python", 70, LocalDate.now().plusDays(14),
                LocalDate.now().plusMonths(7), BigDecimal.valueOf(179.99), programming, teacher1,
                List.of(pythonTag, beginnerTag), false);

        // Создание модулей и уроков
        createCourseStructure(javaCourse);

        // Записи на курсы
        createEnrollment(student1, javaCourse, 25);
        createEnrollment(student2, javaCourse, 50);
        createEnrollment(student1, springCourse, 10);

        log.info("Demo data loaded successfully!");
    }

    private User createUser(String name, String email, UserRole role, String phone) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setRole(role);
        user.setPhone(phone);
        return userRepository.save(user);
    }

    private Category createCategory(String name, String description) {
        Category category = new Category();
        category.setName(name);
        category.setDescription(description);
        return categoryRepository.save(category);
    }

    private Tag createTag(String name) {
        return tagRepository.findByName(name)
                .orElseGet(() -> {
                    Tag tag = new Tag();
                    tag.setName(name);
                    return tagRepository.save(tag);
                });
    }

    private Course createCourse(String title, String description, int duration,
                                LocalDate startDate, LocalDate endDate, BigDecimal price,
                                Category category, User teacher, List<Tag> tags, boolean published) {
        Course course = new Course();
        course.setTitle(title);
        course.setDescription(description);
        course.setDuration(duration);
        course.setStartDate(startDate);
        course.setEndDate(endDate);
        course.setPrice(price);
        course.setCategory(category);
        course.setTeacher(teacher);
        course.setIsPublished(published);
        course.setTags(new HashSet<>(tags));
        return courseRepository.save(course);
    }


    private void createEnrollment(User student, Course course, int progress) {
        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setProgress(progress);
        enrollmentRepository.save(enrollment);
    }

    private void createCourseStructure(Course course) {
        // Модуль 1: Введение в Java
        Module module1 = new Module();
        module1.setTitle("Введение в Java");
        module1.setDescription("Основы языка Java и настройка окружения");
        module1.setOrderIndex(1);
        module1.setCourse(course);
        module1 = moduleRepository.save(module1);

        // Уроки для модуля 1
        Lesson lesson1 = createLesson("Что такое Java?",
                "Java - это популярный объектно-ориентированный язык программирования...",
                "https://example.com/video1", 1, module1);

        Lesson lesson2 = createLesson("Настройка среды разработки",
                "Установка JDK, IntelliJ IDEA и настройка первого проекта...",
                "https://example.com/video2", 2, module1);

        // Модуль 2: Объектно-ориентированное программирование
        Module module2 = new Module();
        module2.setTitle("Объектно-ориентированное программирование");
        module2.setDescription("Основы ООП: классы, объекты, наследование, полиморфизм");
        module2.setOrderIndex(2);
        module2.setCourse(course);
        module2 = moduleRepository.save(module2);

        // Уроки для модуля 2
        Lesson lesson3 = createLesson("Классы и объекты",
                "Создание классов, конструкторы, методы и работа с объектами...",
                "https://example.com/video3", 1, module2);

        Lesson lesson4 = createLesson("Наследование и полиморфизм",
                "Принципы наследования, переопределение методов и полиморфное поведение...",
                "https://example.com/video4", 2, module2);

        // Задания
        createAssignment("Первая Java программа",
                "Напишите программу 'Hello World' и продемонстрируйте ее работу",
                LocalDateTime.now().plusDays(7), 100, lesson1);

        createAssignment("Создание класса Student",
                "Создайте класс Student с полями name, age и методами get/set",
                LocalDateTime.now().plusDays(14), 100, lesson3);
    }

    private Lesson createLesson(String title, String content, String videoUrl, int order, Module module) {
        Lesson lesson = new Lesson();
        lesson.setTitle(title);
        lesson.setContent(content);
        lesson.setVideoUrl(videoUrl);
        lesson.setOrderIndex(order);
        lesson.setModule(module);
        return lessonRepository.save(lesson);
    }

    private void createAssignment(String title, String description, LocalDateTime dueDate, int maxScore, Lesson lesson) {
        Assignment assignment = new Assignment();
        assignment.setTitle(title);
        assignment.setDescription(description);
        assignment.setDueDate(dueDate);
        assignment.setMaxScore(maxScore);
        assignment.setLesson(lesson);
        assignmentRepository.save(assignment);
    }
}