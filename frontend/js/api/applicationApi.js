async function applyToJob(jobId) {
    return apiRequest("/api/applications", {
        method: "POST",
        headers: {
            Authorization: `Bearer ${getToken()}`
        },
        body: JSON.stringify({ jobId: jobId })
    });
}

async function getMyApplications() {
    return apiRequest("/api/applications/my", {
        method: "GET",
        headers: {
            Authorization: `Bearer ${getToken()}`
        }
    });
}

async function getApplicationsForJob(jobId) {
    return apiRequest(`/api/applications/job/${jobId}`, {
        method: "GET",
        headers: {
            Authorization: `Bearer ${getToken()}`
        }
    });
}

async function updateApplicationStatus(applicationId, status) {
    return apiRequest(`/api/applications/${applicationId}/status?status=${status}`, {
        method: "PATCH",
        headers: {
            Authorization: `Bearer ${getToken()}`
        }
    });
}
