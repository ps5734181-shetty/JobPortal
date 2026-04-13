document.getElementById("registerForm").addEventListener("submit", async (e) => {
    e.preventDefault();

    const payload = {
        fullName: document.getElementById("fullName").value.trim(),
        email: document.getElementById("email").value.trim(),
        password: document.getElementById("password").value.trim(),
        role: document.getElementById("role").value
    };

    const message = document.getElementById("message");
    const button = e.target.querySelector("button[type='submit']");

    message.innerText = "";
    message.style.color = "";
    button.disabled = true;
    button.innerText = "Registering...";

    try {
        const data = await registerUser(payload);

        saveUserInfo(data);

        message.style.color = "green";
        message.innerText = "Registered successfully! Redirecting...";
        setTimeout(() => {
            window.location.href = "dashboard.html";
        }, 800);
    } catch (err) {
        message.style.color = "red";

        if (err.fieldErrors) {
            const errors = Object.values(err.fieldErrors).join(", ");
            message.innerText = errors;
        } else {
            message.innerText = err.error || "Registration failed. Try again.";
        }

        button.disabled = false;
        button.innerText = "Register";
    }
});
