package com.example.learningplatform.dto;

import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String bio;
    private String avatarUrl;
    private String website;
    private String socialLinks;
}