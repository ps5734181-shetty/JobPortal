package com.jobportal.dto;

import com.jobportal.entity.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationResponse {

    private Long id;
    private Long jobId;
    private String jobTitle;
    private String company;
    private String applicantName;    // for admin view
    private String applicantEmail;   // for admin view
    private ApplicationStatus status;
    private LocalDateTime appliedAt;
}