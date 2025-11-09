package com.example.learningplatform.dto;

import lombok.Data;

@Data
public class ProfileDTO {
    private Long id;
    private String bio;
    private String avatarUrl;
    private String website;
    private String socialLinks;
    private Long userId;
    private String userName;
    private String userEmail;
}