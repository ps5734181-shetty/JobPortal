package com.jobportal.service;

import com.jobportal.dto.ApplicationRequest;
import com.jobportal.dto.ApplicationResponse;
import com.jobportal.entity.Application;
import com.jobportal.entity.ApplicationStatus;
import com.jobportal.entity.Job;
import com.jobportal.entity.User;
import com.jobportal.repository.ApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final AuthService authService;
    private final JobService jobService;

    @Transactional
    public ApplicationResponse applyToJob(String userEmail, ApplicationRequest request) {
        User user = authService.getUserByEmail(userEmail);
        Job job = jobService.findJobEntityById(request.getJobId());

        if (applicationRepository.existsByUserIdAndJobId(user.getId(), job.getId())) {
            throw new ResponseStatusException(CONFLICT, "You have already applied for this job");
        }

        Application application = Application.builder()
                .user(user)
                .job(job)
                .build();

        return mapToResponse(applicationRepository.save(application));
    }

    public List<ApplicationResponse> getMyApplications(String userEmail) {
        User user = authService.getUserByEmail(userEmail);
        return applicationRepository.findByUserId(user.getId()).stream()
                .sorted(Comparator.comparing(Application::getAppliedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .map(this::mapToResponse)
                .toList();
    }

    public List<ApplicationResponse> getApplicationsForJob(Long jobId) {
        jobService.findJobEntityById(jobId);
        return applicationRepository.findByJobId(jobId).stream()
                .sorted(Comparator.comparing(Application::getAppliedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .map(this::mapToResponse)
                .toList();
    }

    public List<ApplicationResponse> getApplicationsByStatus(ApplicationStatus status) {
        return applicationRepository.findByStatus(status).stream()
                .sorted(Comparator.comparing(Application::getAppliedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public ApplicationResponse updateApplicationStatus(Long applicationId, ApplicationStatus status) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResponseStatusException(
                        NOT_FOUND, "Application not found with id: " + applicationId));

        application.setStatus(status);
        return mapToResponse(applicationRepository.save(application));
    }

    public ApplicationResponse getUserApplicationForJob(String userEmail, Long jobId) {
        User user = authService.getUserByEmail(userEmail);
        Application application = applicationRepository.findByUserIdAndJobId(user.getId(), jobId)
                .orElseThrow(() -> new ResponseStatusException(
                        NOT_FOUND, "Application not found for the given user and job"));

        return mapToResponse(application);
    }

    private ApplicationResponse mapToResponse(Application application) {
        return ApplicationResponse.builder()
                .id(application.getId())
                .jobId(application.getJob().getId())
                .jobTitle(application.getJob().getTitle())
                .company(application.getJob().getCompany())
                .applicantName(application.getUser().getFullName())
                .applicantEmail(application.getUser().getEmail())
                .status(application.getStatus())
                .appliedAt(application.getAppliedAt())
                .build();
    }
}
