package com.example.learningplatform.service;

import com.example.learningplatform.dto.CourseReviewDTO;
import com.example.learningplatform.dto.CreateCourseReviewRequest;
import com.example.learningplatform.entity.Course;
import com.example.learningplatform.entity.CourseReview;
import com.example.learningplatform.entity.User;
import com.example.learningplatform.exception.ResourceNotFoundException;
import com.example.learningplatform.repository.CourseRepository;
import com.example.learningplatform.repository.CourseReviewRepository;
import com.example.learningplatform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CourseReviewService {

    private final CourseReviewRepository courseReviewRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    public CourseReviewDTO createReview(Long studentId, CreateCourseReviewRequest request) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + request.getCourseId()));

        if (courseReviewRepository.existsByStudentIdAndCourseId(studentId, request.getCourseId())) {
            throw new IllegalArgumentException("Student has already reviewed this course");
        }

        CourseReview review = new CourseReview();
        review.setStudent(student);
        review.setCourse(course);
        review.setRating(request.getRating());
        review.setComment(request.getComment());

        CourseReview savedReview = courseReviewRepository.save(review);
        log.info("Student {} created review for course {} with rating {}", studentId, request.getCourseId(), request.getRating());

        return convertToDTO(savedReview);
    }

    public CourseReviewDTO updateReview(Long reviewId, CreateCourseReviewRequest request) {
        CourseReview review = courseReviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));

        review.setRating(request.getRating());
        review.setComment(request.getComment());

        CourseReview updatedReview = courseReviewRepository.save(review);
        log.info("Updated review with id: {}", reviewId);

        return convertToDTO(updatedReview);
    }

    public void deleteReview(Long reviewId) {
        if (!courseReviewRepository.existsById(reviewId)) {
            throw new ResourceNotFoundException("Review not found with id: " + reviewId);
        }
        courseReviewRepository.deleteById(reviewId);
        log.info("Deleted review with id: {}", reviewId);
    }

    @Transactional(readOnly = true)
    public List<CourseReviewDTO> getReviewsByCourse(Long courseId) {
        return courseReviewRepository.findByCourseIdWithStudent(courseId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CourseReviewDTO> getReviewsByStudent(Long studentId) {
        return courseReviewRepository.findByStudentIdWithCourse(studentId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Double getAverageRatingByCourse(Long courseId) {
        return courseReviewRepository.findAverageRatingByCourseId(courseId);
    }

    @Transactional(readOnly = true)
    public CourseReviewDTO getReviewById(Long reviewId) {
        CourseReview review = courseReviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));
        return convertToDTO(review);
    }

    private CourseReviewDTO convertToDTO(CourseReview review) {
        CourseReviewDTO dto = new CourseReviewDTO();
        dto.setId(review.getId());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setCreatedAt(review.getCreatedAt());
        dto.setCourseId(review.getCourse().getId());
        dto.setCourseTitle(review.getCourse().getTitle());
        dto.setStudentId(review.getStudent().getId());
        dto.setStudentName(review.getStudent().getName());
        return dto;
    }
}