package com.example.learningplatform.service;

import com.example.learningplatform.entity.User;
import com.example.learningplatform.entity.enums.UserRole;
import com.example.learningplatform.exception.ResourceNotFoundException;
import com.example.learningplatform.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setName("John Doe");
        testUser.setEmail("john.doe@example.com");
        testUser.setRole(UserRole.STUDENT);
        testUser.setPhone("+1234567890");
    }

    @Test
    void createUser_ShouldCreateUserSuccessfully() {
        // When
        var result = userService.createUser(testUser);

        // Then
        assertNotNull(result.getId());
        assertEquals("John Doe", result.getName());
        assertEquals("john.doe@example.com", result.getEmail());
        assertEquals(UserRole.STUDENT, result.getRole());
    }

    @Test
    void createUser_WithDuplicateEmail_ShouldThrowException() {
        // Given
        userService.createUser(testUser);

        // When & Then
        User duplicateUser = new User();
        duplicateUser.setName("Jane Doe");
        duplicateUser.setEmail("john.doe@example.com");
        duplicateUser.setRole(UserRole.STUDENT);

        assertThrows(IllegalArgumentException.class, () -> userService.createUser(duplicateUser));
    }

    @Test
    void getUserById_WhenUserExists_ShouldReturnUser() {
        // Given
        var createdUser = userService.createUser(testUser);

        // When
        var result = userService.getUserById(createdUser.getId());

        // Then
        assertNotNull(result);
        assertEquals(createdUser.getId(), result.getId());
        assertEquals("John Doe", result.getName());
    }

    @Test
    void getUserById_WhenUserNotExists_ShouldThrowException() {
        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(999L));
    }

    @Test
    void updateUser_ShouldUpdateUserSuccessfully() {
        // Given
        var createdUser = userService.createUser(testUser);

        User updateDetails = new User();
        updateDetails.setName("John Updated");
        updateDetails.setEmail("updated@example.com");
        updateDetails.setRole(UserRole.TEACHER);

        // When
        var result = userService.updateUser(createdUser.getId(), updateDetails);

        // Then
        assertEquals("John Updated", result.getName());
        assertEquals("updated@example.com", result.getEmail());
        assertEquals(UserRole.TEACHER, result.getRole());
    }

    @Test
    void deleteUser_ShouldDeleteUserSuccessfully() {
        // Given
        var createdUser = userService.createUser(testUser);

        // When
        userService.deleteUser(createdUser.getId());

        // Then
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(createdUser.getId()));
    }
}