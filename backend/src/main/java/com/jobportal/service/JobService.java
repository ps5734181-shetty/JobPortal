package com.jobportal.service;

import com.jobportal.dto.JobRequest;
import com.jobportal.dto.JobResponse;
import com.jobportal.entity.Job;
import com.jobportal.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;

    // ✅ Used internally by ApplicationService — returns the raw Job entity
    // (not a DTO), needed to build Application objects.
    @Transactional(readOnly = true)
    public Job findJobEntityById(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        NOT_FOUND, "Job not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<JobResponse> searchJobs(String title, String location,
                                        String company, String jobType) {
        List<Job> jobs = jobRepository.findAll();
        return jobs.stream()
                .filter(j -> title    == null || j.getTitle().toLowerCase().contains(title.toLowerCase()))
                .filter(j -> location == null || j.getLocation().toLowerCase().contains(location.toLowerCase()))
                .filter(j -> company  == null || j.getCompany().toLowerCase().contains(company.toLowerCase()))
                .filter(j -> jobType  == null || j.getJobType().equalsIgnoreCase(jobType))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public JobResponse getJobById(Long id) {
        return mapToResponse(findJobEntityById(id));
    }

    @Transactional
    public JobResponse createJob(JobRequest request) {
        Job job = Job.builder()
                .title(request.getTitle())
                .company(request.getCompany())
                .location(request.getLocation())
                .description(request.getDescription())
                .salary(request.getSalary())
                .jobType(request.getJobType())
                .build();
        return mapToResponse(jobRepository.save(job));
    }

    @Transactional
    public JobResponse updateJob(Long id, JobRequest request) {
        Job job = findJobEntityById(id);
        job.setTitle(request.getTitle());
        job.setCompany(request.getCompany());
        job.setLocation(request.getLocation());
        job.setDescription(request.getDescription());
        job.setSalary(request.getSalary());
        job.setJobType(request.getJobType());
        return mapToResponse(jobRepository.save(job));
    }

    @Transactional
    public void deleteJob(Long id) {
        if (!jobRepository.existsById(id)) {
            throw new ResponseStatusException(NOT_FOUND, "Job not found with id: " + id);
        }
        jobRepository.deleteById(id);
    }

    // ✅ @Transactional on the calling method keeps the session open,
    // so job.getApplications().size() on the lazy collection is safe.
    private JobResponse mapToResponse(Job job) {
        return JobResponse.builder()
                .id(job.getId())
                .title(job.getTitle())
                .company(job.getCompany())
                .location(job.getLocation())
                .description(job.getDescription())
                .salary(job.getSalary())
                .jobType(job.getJobType())
                .postedAt(job.getPostedAt())
                .totalApplications(job.getApplications() == null ? 0 : job.getApplications().size())
                .build();
    }
}