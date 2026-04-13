package com.jobportal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobResponse {

    private Long id;
    private String title;
    private String company;
    private String location;
    private String description;
    private String salary;
    private String jobType;
    private LocalDateTime postedAt;
    private int totalApplications;   // count of applicants, useful for admin dashboard
}