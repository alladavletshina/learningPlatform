# Learning Platform - Учебная платформа

## Описание проекта

Учебная платформа для онлайн-курсов по ORM и Hibernate, разработанная на базе Spring Boot 3.2.0 с использованием Hibernate/JPA и PostgreSQL. Система предоставляет полный функционал для управления учебными курсами, пользователями, заданиями и тестированием.

## Контакты
Для вопросов и предложений обращайтесь к Давлетшиной Алле atdavletshina@gmail.com.


## Технологический стек

- **Java 21**
- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **Hibernate ORM 6.3+**
- **PostgreSQL** (продакшен) + **H2** (тестирование)
- **Maven**
- **Liquibase** (миграции базы данных)
- **Testcontainers 1.19.3** (интеграционное тестирование)
- **Docker & Docker Compose**
- **Lombok**
- **Bean Validation**

## Функциональность

### Основные модули:

- **Управление пользователями** (Студенты, Преподаватели, Администраторы)
- **Управление курсами** с категориями и тегами
- **Модульная структура** курсов (Курс → Модули → Уроки → Задания)
- **Система заданий** и проверки решений
- **Тестирование** с системой викторин (Quiz)
- **Запись на курсы** и отслеживание прогресса
- **Отзывы и рейтинги** курсов
- **Профили пользователей**

## Архитектура проекта
- src/main/java/com/example/learningplatform/
- ├── config/ # Конфигурационные классы
- ├── controller/ # REST контроллеры
- ├── dto/ # Data Transfer Objects
- ├── entity/ # JPA сущности
- ├── exception/ # Обработка исключений
- ├── repository/ # Spring Data репозитории
- └── service/ # Бизнес-логика


## Модель данных

### Основные сущности:

| Сущность | Описание |
|----------|-----------|
| **User** | Пользователи системы (STUDENT, TEACHER, ADMIN) |
| **Profile** | Профили пользователей (OneToOne) |
| **Course** | Учебные курсы с тегами (ManyToMany) |
| **Category** | Категории курсов |
| **Module** | Модули курса с порядком |
| **Lesson** | Уроки модуля |
| **Assignment** | Задания с дедлайнами |
| **Submission** | Решения заданий |
| **Quiz** | Тесты/викторины |
| **Question** | Вопросы тестов (SINGLE_CHOICE, MULTIPLE_CHOICE, TEXT) |
| **AnswerOption** | Варианты ответов |
| **QuizSubmission** | Результаты тестов |
| **Enrollment** | Записи на курсы (ACTIVE, COMPLETED, DROPPED) |
| **CourseReview** | Отзывы о курсах (1-5 звёзд) |
| **Tag** | Теги курсов (ManyToMany) |

## API Endpoints

### Пользователи (/api/users)
- POST /api/users
- GET /api/users
- GET /api/users/{id}
- GET /api/users/role/{role}
- PUT /api/users/{id}
- DELETE /api/users/{id}

### Курсы (/api/courses)
- POST /api/courses
- GET /api/courses
- GET /api/courses/published
- GET /api/courses/{id}
- GET /api/courses/{id}/detailed
- GET /api/courses/category/{categoryId}
- GET /api/courses/teacher/{teacherId}
- GET /api/courses/search?query=
- PUT /api/courses/{id}
- POST /api/courses/{id}/publish
- POST /api/courses/{id}/unpublish
- DELETE /api/courses/{id}

### Записи на курсы (/api/enrollments)
- POST /api/enrollments?studentId={}&courseId={}
- DELETE /api/enrollments?studentId={}&courseId={}
- GET /api/enrollments/student/{studentId}
- GET /api/enrollments/course/{courseId}
- PUT /api/enrollments/{enrollmentId}/status
- PUT /api/enrollments/{enrollmentId}/progress

### Задания и решения (/api/assignments)
- POST /api/assignments/lesson/{lessonId}
- POST /api/assignments/{assignmentId}/submit?studentId={}&content={}
- PUT /api/assignments/submissions/{submissionId}/grade?score={}&feedback={}
- GET /api/assignments/{assignmentId}/submissions
- GET /api/assignments/student/{studentId}/submissions

### Тесты (/api/quizzes)
- POST /api/quizzes
- GET /api/quizzes/course/{courseId}
- GET /api/quizzes/module/{moduleId}
- GET /api/quizzes/{quizId}
- POST /api/quizzes/{quizId}/submit/single?studentId={}
- POST /api/quizzes/{quizId}/submit/multiple?studentId={}
- GET /api/quizzes/student/{studentId}/submissions
- GET /api/quizzes/{quizId}/submissions
- DELETE /api/quizzes/{quizId}

### Структура курса (/api/courses/{courseId}/structure)
- POST /api/courses/{courseId}/structure/modules
- POST /api/courses/{courseId}/structure/modules/{moduleId}/lessons
- GET /api/courses/{courseId}/structure/full
- PUT /api/courses/{courseId}/structure/modules/{moduleId}/order?order={}
- PUT /api/courses/{courseId}/structure/lessons/{lessonId}/order?order={}
- DELETE /api/courses/{courseId}/structure/modules/{moduleId}
- DELETE /api/courses/{courseId}/structure/lessons/{lessonId}
- PUT /api/courses/{courseId}/structure/modules/{moduleId}
- PUT /api/courses/{courseId}/structure/lessons/{lessonId}
- GET /api/courses/{courseId}/structure/modules/{moduleId}
- GET /api/courses/{courseId}/structure/lessons/{lessonId}

### Отзывы (/api/reviews)
- POST /api/reviews/student/{studentId}
- PUT /api/reviews/{reviewId}
- GET /api/reviews/course/{courseId}
- GET /api/reviews/student/{studentId}
- GET /api/reviews/course/{courseId}/average-rating
- GET /api/reviews/{reviewId}
- DELETE /api/reviews/{reviewId}

### Профили (/api/profiles)
- POST /api/profiles/user/{userId}
- PUT /api/profiles/user/{userId}
- GET /api/profiles/user/{userId}
- GET /api/profiles/{profileId}
- DELETE /api/profiles/user/{userId}

# Learning Platform

## Тестирование

### Запуск тестов

mvn test

## Интеграционные тесты

Проект включает комплексные тесты с Testcontainers:

**UserServiceIntegrationTest** - CRUD операции пользователей

**CourseServiceIntegrationTest** - Управление курсами

**CreateCourseRequestValidationTest** - Валидация DTO

**ApplicationTest** - Тестирование контекста Spring

## Примеры использования API

**1. Создание курса**

POST /api/courses
Content-Type: application/json

{
  "title": "Advanced Java Programming",
  "description": "Deep dive into Java advanced features",
  "duration": 80,
  "startDate": "2024-02-01",
  "endDate": "2024-06-01",
  "price": 199.99,
  "categoryId": 1,
  "teacherId": 2,
  "tags": ["Java", "Advanced", "Concurrency"]
}

**2. Запись студента на курс**

POST /api/enrollments?studentId=3&courseId=1

**3. Отправка решения задания**

POST /api/assignments/1/submit?studentId=3
Content-Type: application/x-www-form-urlencoded

## Быстрый старт

### Предварительные требования

- **Java 21** или выше
- **Maven 3.6+**
- **PostgreSQL 15+**
- **Docker** (опционально)

### Способ 1: Локальный запуск

1. **Настройка базы данных:**

Используется база postgres по умолчанию

2. **Запуск приложения:**

chmod +x start.sh
./start.sh

**Или вручную:**

mvn clean install
mvn spring-boot:run

### Способ 2: Запуск с Docker

chmod +x start-with-docker.sh
./start-with-docker.sh      

# База данных
spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
spring.datasource.username=platform_user
spring.datasource.password=password

# JPA и Hibernate
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Liquibase
spring.liquibase.change-log=classpath:db/changelog/db.changelog-master.yaml

## Миграции базы данных
Проект использует Liquibase для управления миграциями БД:

mvn liquibase:diff
- Особенности реализации
- ORM и Hibernate
- Ленивая загрузка (Lazy Loading) для оптимизации

Каскадные операции для связанных сущностей

Сложные запросы с @Query и JOIN FETCH

Валидация JSR-380 с кастомными сообщениями

## Безопасность и валидация
Bean Validation аннотации (@NotBlank, @Size, @Email)

Глобальная обработка исключений с @RestControllerAdvice

Уникальные ограничения на уровне БД

