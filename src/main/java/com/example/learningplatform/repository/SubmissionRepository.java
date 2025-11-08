package com.example.learningplatform.repository;

import com.example.learningplatform.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    List<Submission> findByAssignmentId(Long assignmentId);
    List<Submission> findByStudentId(Long studentId);
    Optional<Submission> findByStudentIdAndAssignmentId(Long studentId, Long assignmentId);

    @Query("SELECT s FROM Submission s LEFT JOIN FETCH s.assignment WHERE s.student.id = :studentId")
    List<Submission> findByStudentIdWithAssignment(@Param("studentId") Long studentId);

    @Query("SELECT s FROM Submission s LEFT JOIN FETCH s.student WHERE s.assignment.id = :assignmentId")
    List<Submission> findByAssignmentIdWithStudent(@Param("assignmentId") Long assignmentId);
}