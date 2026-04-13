document.getElementById("loginForm").addEventListener("submit", async (e) => {
    e.preventDefault();

    const payload = {
        email: document.getElementById("email").value.trim(),
        password: document.getElementById("password").value.trim()
    };

    const message = document.getElementById("message");
    const button = e.target.querySelector("button[type='submit']");

    message.innerText = "";
    message.style.color = "";
    button.disabled = true;
    button.innerText = "Logging in...";

    try {
        const data = await loginUser(payload);

        saveUserInfo(data);
        window.location.href = "dashboard.html";
    } catch (err) {
        message.style.color = "red";
        message.innerText = err.error || "Invalid email or password.";

        button.disabled = false;
        button.innerText = "Login";
    }
});
