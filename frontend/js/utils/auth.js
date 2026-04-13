function saveToken(token) {
    localStorage.setItem("token", token);
}

function getToken() {
    return localStorage.getItem("token");
}

function saveUserInfo(data) {
    localStorage.setItem("token", data.token);
    localStorage.setItem("role", data.role);
    localStorage.setItem("fullName", data.fullName);
    localStorage.setItem("email", data.email);
}

function getUserRole() {
    return localStorage.getItem("role");
}

function getFullName() {
    return localStorage.getItem("fullName");
}

function clearToken() {
    localStorage.removeItem("token");
    localStorage.removeItem("role");
    localStorage.removeItem("fullName");
    localStorage.removeItem("email");
}

function isLoggedIn() {
    return !!getToken();
}

function isAdmin() {
    return getUserRole() === "ROLE_ADMIN";
}

function logout() {
    clearToken();
    window.location.href = "login.html";
}
