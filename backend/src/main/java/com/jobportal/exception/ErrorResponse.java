package com.jobportal.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // hides null fields from JSON response
public class ErrorResponse {

    private LocalDateTime timestamp;
    private int status;
    private String error;
    private Map<String, String> fieldErrors; // only populated for validation errors
}