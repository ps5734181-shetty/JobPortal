package com.jobportal.controller;

import com.jobportal.dto.ApplicationRequest;
import com.jobportal.dto.ApplicationResponse;
import com.jobportal.entity.ApplicationStatus;
import com.jobportal.service.ApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    // USER: Apply to a job
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping
    public ResponseEntity<ApplicationResponse> applyToJob(
            Authentication authentication,
            @Valid @RequestBody ApplicationRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(applicationService.applyToJob(authentication.getName(), request));
    }

    // USER: Get my own applications
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/my")
    public ResponseEntity<List<ApplicationResponse>> getMyApplications(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                applicationService.getMyApplications(authentication.getName()));
    }

    // USER: Get my specific application for a job
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/job/{jobId}/mine")
    public ResponseEntity<ApplicationResponse> getMyApplicationForJob(
            Authentication authentication,
            @PathVariable Long jobId
    ) {
        return ResponseEntity.ok(
                applicationService.getUserApplicationForJob(authentication.getName(), jobId));
    }

    // ADMIN: Get all applications for a specific job
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/job/{jobId}")
    public ResponseEntity<List<ApplicationResponse>> getApplicationsForJob(
            @PathVariable Long jobId
    ) {
        return ResponseEntity.ok(applicationService.getApplicationsForJob(jobId));
    }

    // ADMIN: Get all applications filtered by status
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/status")
    public ResponseEntity<List<ApplicationResponse>> getApplicationsByStatus(
            @RequestParam ApplicationStatus status
    ) {
        return ResponseEntity.ok(applicationService.getApplicationsByStatus(status));
    }

    // ADMIN: Update application status (PENDING → REVIEWED → ACCEPTED / REJECTED)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PatchMapping("/{applicationId}/status")
    public ResponseEntity<ApplicationResponse> updateApplicationStatus(
            @PathVariable Long applicationId,
            @RequestParam ApplicationStatus status
    ) {
        return ResponseEntity.ok(
                applicationService.updateApplicationStatus(applicationId, status));
    }
}