package com.jobportal.repository;

import com.jobportal.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    // Search jobs by title (case-insensitive)
    List<Job> findByTitleContainingIgnoreCase(String title);

    // Filter jobs by location
    List<Job> findByLocationContainingIgnoreCase(String location);

    // Filter jobs by job type (Full-Time, Part-Time, Remote)
    List<Job> findByJobTypeIgnoreCase(String jobType);

    // Filter by company name
    List<Job> findByCompanyContainingIgnoreCase(String company);
}