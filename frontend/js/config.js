const CONFIG = {
    BASE_URL: "http://localhost:8081"
};

async function apiRequest(path, options = {}) {
    const requestConfig = {
        headers: {
            "Content-Type": "application/json",
            ...(options.headers || {})
        },
        ...options
    };

    const response = await fetch(`${CONFIG.BASE_URL}${path}`, requestConfig);

    if (!response.ok) {
        const error = await response.json().catch(() => ({
            error: `HTTP error ${response.status}`
        }));
        error.status = response.status;
        throw error;
    }

    if (response.status === 204) return null;

    return response.json();
}
