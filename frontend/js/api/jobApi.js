async function getAllJobs() {
    return apiRequest("/api/jobs", {
        method: "GET"
    });
}

async function createJob(payload) {
    return apiRequest("/api/jobs", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",        // ✅ FIXED: was missing
            "Authorization": `Bearer ${getToken()}`
        },
        body: JSON.stringify(payload)
    });
}

async function updateJob(id, payload) {
    return apiRequest(`/api/jobs/${id}`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json",        // ✅ FIXED: was missing
            "Authorization": `Bearer ${getToken()}`
        },
        body: JSON.stringify(payload)
    });
}

async function deleteJob(id) {
    return apiRequest(`/api/jobs/${id}`, {
        method: "DELETE",
        headers: {
            "Authorization": `Bearer ${getToken()}`
        }
    });
}

async function applyToJob(jobId) {
    return apiRequest("/api/applications", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",        // ✅ FIXED: was missing
            "Authorization": `Bearer ${getToken()}`
        },
        body: JSON.stringify({ jobId: jobId })
    });
}

async function getMyApplications() {
    return apiRequest("/api/applications/my", {
        method: "GET",
        headers: {
            "Authorization": `Bearer ${getToken()}`
        }
    });
}

async function getApplicationsForJob(jobId) {
    return apiRequest(`/api/applications/job/${jobId}`, {
        method: "GET",
        headers: {
            "Authorization": `Bearer ${getToken()}`
        }
    });
}

async function updateApplicationStatus(applicationId, status) {
    return apiRequest(`/api/applications/${applicationId}/status?status=${status}`, {
        method: "PATCH",
        headers: {
            "Authorization": `Bearer ${getToken()}`
        }
    });
}