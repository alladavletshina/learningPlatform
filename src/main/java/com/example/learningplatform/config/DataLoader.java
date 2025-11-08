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

    private void createCourseStructure(Course course) {
        // Модуль 1
        Module module1 = new Module();
        module1.setTitle("Introduction to Java");
        module1.setDescription("Java basics and setup");
        module1.setOrderIndex(1);
        module1.setCourse(course);
        module1 = moduleRepository.save(module1);

        Lesson lesson1 = new Lesson();
        lesson1.setTitle("What is Java?");
        lesson1.setContent("Java is a popular programming language...");
        lesson1.setVideoUrl("https://example.com/video1");
        lesson1.setOrderIndex(1);
        lesson1.setModule(module1);
        lessonRepository.save(lesson1);

        Lesson lesson2 = new Lesson();
        lesson2.setTitle("Setting up Development Environment");
        lesson2.setContent("Install JDK and IDE...");
        lesson2.setVideoUrl("https://example.com/video2");
        lesson2.setOrderIndex(2);
        lesson2.setModule(module1);
        lessonRepository.save(lesson2);

        // Модуль 2
        Module module2 = new Module();
        module2.setTitle("Object-Oriented Programming");
        module2.setDescription("OOP concepts in Java");
        module2.setOrderIndex(2);
        module2.setCourse(course);
        module2 = moduleRepository.save(module2);

        Lesson lesson3 = new Lesson();
        lesson3.setTitle("Classes and Objects");
        lesson3.setContent("Understanding classes and objects...");
        lesson3.setVideoUrl("https://example.com/video3");
        lesson3.setOrderIndex(1);
        lesson3.setModule(module2);
        lessonRepository.save(lesson3);

        // Задание
        Assignment assignment = new Assignment();
        assignment.setTitle("First Java Program");
        assignment.setDescription("Write a simple Hello World program");
        assignment.setDueDate(LocalDateTime.now().plusDays(7));
        assignment.setMaxScore(100);
        assignment.setLesson(lesson3);
        assignmentRepository.save(assignment);
    }

    private void createEnrollment(User student, Course course, int progress) {
        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setProgress(progress);
        enrollmentRepository.save(enrollment);
    }
}