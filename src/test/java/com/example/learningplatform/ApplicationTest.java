package com.example.learningplatform;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
class LearningPlatformApplicationTest {

    @Test
    void contextLoads() {
        // Проверяем, что контекст Spring загружается корректно
        assertTrue(true, "Context should load successfully");
    }

    @Test
    void mainMethodStartsApplication() {
        // Проверяем, что метод main запускает приложение без ошибок
        LearningPlatformApplication.main(new String[]{});
        assertTrue(true, "Application should start successfully");
    }
}