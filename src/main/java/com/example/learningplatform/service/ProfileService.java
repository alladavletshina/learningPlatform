package com.example.learningplatform.service;

import com.example.learningplatform.dto.ProfileDTO;
import com.example.learningplatform.dto.UpdateProfileRequest;
import com.example.learningplatform.entity.Profile;
import com.example.learningplatform.entity.User;
import com.example.learningplatform.exception.ResourceNotFoundException;
import com.example.learningplatform.repository.ProfileRepository;
import com.example.learningplatform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;

    public ProfileDTO createProfile(Long userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        if (profileRepository.findByUserId(userId).isPresent()) {
            throw new IllegalArgumentException("Profile already exists for user: " + userId);
        }

        Profile profile = new Profile();
        profile.setUser(user);
        profile.setBio(request.getBio());
        profile.setAvatarUrl(request.getAvatarUrl());
        profile.setWebsite(request.getWebsite());
        profile.setSocialLinks(request.getSocialLinks());

        Profile savedProfile = profileRepository.save(profile);
        log.info("Created profile for user: {}", userId);

        return convertToDTO(savedProfile);
    }

    public ProfileDTO updateProfile(Long userId, UpdateProfileRequest request) {
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for user: " + userId));

        profile.setBio(request.getBio());
        profile.setAvatarUrl(request.getAvatarUrl());
        profile.setWebsite(request.getWebsite());
        profile.setSocialLinks(request.getSocialLinks());

        Profile updatedProfile = profileRepository.save(profile);
        log.info("Updated profile for user: {}", userId);

        return convertToDTO(updatedProfile);
    }

    @Transactional(readOnly = true)
    public ProfileDTO getProfileByUserId(Long userId) {
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for user: " + userId));
        return convertToDTO(profile);
    }

    @Transactional(readOnly = true)
    public ProfileDTO getProfileById(Long profileId) {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found with id: " + profileId));
        return convertToDTO(profile);
    }

    public void deleteProfile(Long userId) {
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for user: " + userId));
        profileRepository.delete(profile);
        log.info("Deleted profile for user: {}", userId);
    }

    private ProfileDTO convertToDTO(Profile profile) {
        ProfileDTO dto = new ProfileDTO();
        dto.setId(profile.getId());
        dto.setBio(profile.getBio());
        dto.setAvatarUrl(profile.getAvatarUrl());
        dto.setWebsite(profile.getWebsite());
        dto.setSocialLinks(profile.getSocialLinks());
        dto.setUserId(profile.getUser().getId());
        dto.setUserName(profile.getUser().getName());
        dto.setUserEmail(profile.getUser().getEmail());
        return dto;
    }
}