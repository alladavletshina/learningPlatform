package com.example.learningplatform.controller;

import com.example.learningplatform.dto.ProfileDTO;
import com.example.learningplatform.dto.UpdateProfileRequest;
import com.example.learningplatform.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @PostMapping("/user/{userId}")
    public ResponseEntity<ProfileDTO> createProfile(
            @PathVariable Long userId,
            @RequestBody UpdateProfileRequest request) {
        ProfileDTO profile = profileService.createProfile(userId, request);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/user/{userId}")
    public ResponseEntity<ProfileDTO> updateProfile(
            @PathVariable Long userId,
            @RequestBody UpdateProfileRequest request) {
        ProfileDTO profile = profileService.updateProfile(userId, request);
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ProfileDTO> getProfileByUserId(@PathVariable Long userId) {
        ProfileDTO profile = profileService.getProfileByUserId(userId);
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/{profileId}")
    public ResponseEntity<ProfileDTO> getProfileById(@PathVariable Long profileId) {
        ProfileDTO profile = profileService.getProfileById(profileId);
        return ResponseEntity.ok(profile);
    }

    @DeleteMapping("/user/{userId}")
    public ResponseEntity<Void> deleteProfile(@PathVariable Long userId) {
        profileService.deleteProfile(userId);
        return ResponseEntity.noContent().build();
    }
}