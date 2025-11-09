package com.example.learningplatform.controller;

import com.example.learningplatform.dto.CourseReviewDTO;
import com.example.learningplatform.dto.CreateCourseReviewRequest;
import com.example.learningplatform.service.CourseReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class CourseReviewController {

    private final CourseReviewService courseReviewService;

    @PostMapping("/student/{studentId}")
    public ResponseEntity<CourseReviewDTO> createReview(
            @PathVariable Long studentId,
            @Valid @RequestBody CreateCourseReviewRequest request) {
        CourseReviewDTO review = courseReviewService.createReview(studentId, request);
        return ResponseEntity.ok(review);
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<CourseReviewDTO> updateReview(
            @PathVariable Long reviewId,
            @Valid @RequestBody CreateCourseReviewRequest request) {
        CourseReviewDTO updatedReview = courseReviewService.updateReview(reviewId, request);
        return ResponseEntity.ok(updatedReview);
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<CourseReviewDTO>> getReviewsByCourse(@PathVariable Long courseId) {
        List<CourseReviewDTO> reviews = courseReviewService.getReviewsByCourse(courseId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<CourseReviewDTO>> getReviewsByStudent(@PathVariable Long studentId) {
        List<CourseReviewDTO> reviews = courseReviewService.getReviewsByStudent(studentId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/course/{courseId}/average-rating")
    public ResponseEntity<Double> getAverageRating(@PathVariable Long courseId) {
        Double averageRating = courseReviewService.getAverageRatingByCourse(courseId);
        return ResponseEntity.ok(averageRating != null ? averageRating : 0.0);
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<CourseReviewDTO> getReviewById(@PathVariable Long reviewId) {
        CourseReviewDTO review = courseReviewService.getReviewById(reviewId);
        return ResponseEntity.ok(review);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId) {
        courseReviewService.deleteReview(reviewId);
        return ResponseEntity.noContent().build();
    }
}