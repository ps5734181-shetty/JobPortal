async function registerUser(payload) {
    return apiRequest("/api/auth/register", {
        method: "POST",
        body: JSON.stringify(payload)
    });
}

async function loginUser(payload) {
    return apiRequest("/api/auth/login", {
        method: "POST",
        body: JSON.stringify(payload)
    });
}
