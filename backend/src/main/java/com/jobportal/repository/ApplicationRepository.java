package com.jobportal.repository;

import com.jobportal.entity.Application;
import com.jobportal.entity.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    // Get all applications by a specific user
    List<Application> findByUserId(Long userId);

    // Get all applications for a specific job (for admin)
    List<Application> findByJobId(Long jobId);

    // Check if user already applied to a job
    boolean existsByUserIdAndJobId(Long userId, Long jobId);

    // Get specific application by user and job
    Optional<Application> findByUserIdAndJobId(Long userId, Long jobId);

    // Filter applications by status
    List<Application> findByStatus(ApplicationStatus status);
}