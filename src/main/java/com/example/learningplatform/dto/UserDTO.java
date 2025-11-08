package com.example.learningplatform.dto;

import com.example.learningplatform.entity.enums.UserRole;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private UserRole role;
    private String phone;
    private LocalDateTime createdAt;

    // Конструкторы
    public UserDTO() {}

    public UserDTO(Long id, String name, String email, UserRole role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
    }
}