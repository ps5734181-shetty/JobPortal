window.onload = async () => {
    if (!isLoggedIn()) {
        window.location.href = "login.html";
        return;
    }

    const nameEl = document.getElementById("userName");
    if (nameEl) nameEl.innerText = getFullName() || "User";

    const adminPanel = document.getElementById("adminPanel");
    if (adminPanel) {
        adminPanel.style.display = isAdmin() ? "block" : "none";
    }

    await loadJobs();
};

async function loadJobs() {
    const message = document.getElementById("dashboardMessage");
    const container = document.getElementById("jobsContainer");
    container.innerHTML = "<p style='color: var(--muted)'>Loading jobs...</p>";
    message.innerText = "";

    try {
        const jobs = await getAllJobs();
        container.innerHTML = "";

        if (!Array.isArray(jobs) || jobs.length === 0) {
            message.innerText = "No jobs available right now.";
            return;
        }

        jobs.forEach((job) => renderJobCard(job, container));
    } catch (err) {
        container.innerHTML = "";
        message.style.color = "red";
        message.innerText = "Unable to load jobs. Is the backend running?";
    }
}

function renderJobCard(job, container) {
    const div = document.createElement("div");
    div.className = "job-card";
    div.innerHTML = `
        <div class="job-card-header">
            <h3>${job.title}</h3>
            <span class="job-type-badge">${job.jobType}</span>
        </div>
        <p class="job-company">🏢 ${job.company}</p>
        <p class="job-location">📍 ${job.location}</p>
        <p class="job-salary">💰 ${job.salary}</p>
        <p class="job-desc">${job.description}</p>
        <div class="job-card-footer">
            <small class="job-posted">Posted: ${new Date(job.postedAt).toLocaleDateString()}</small>
            ${isAdmin()
                ? `<div class="job-actions">
                        <button class="button secondary btn-sm" onclick="viewApplicants(${job.id})">Applicants (${job.totalApplications})</button>
                        <button class="button ghost btn-sm" onclick="openEditJob(${job.id})">Edit</button>
                        <button class="button danger btn-sm" onclick="confirmDeleteJob(${job.id})">Delete</button>
                   </div>`
                : `<button class="button primary btn-sm" onclick="applyNow(${job.id}, this)">Apply</button>`
            }
        </div>
    `;
    container.appendChild(div);
}

async function applyNow(jobId, btn) {
    btn.disabled = true;
    btn.innerText = "Applying...";

    try {
        await applyToJob(jobId);
        btn.innerText = "Applied ✓";
        btn.classList.remove("primary");
        btn.classList.add("secondary");
    } catch (err) {
        btn.disabled = false;
        btn.innerText = "Apply";

        if (err.status === 409) {
            alert("You have already applied for this job.");
        } else {
            alert(err.error || "Application failed. Please try again.");
        }
    }
}

async function viewApplicants(jobId) {
    try {
        const applications = await getApplicationsForJob(jobId);

        if (!applications || applications.length === 0) {
            alert("No applicants for this job yet.");
            return;
        }

        const list = applications.map((a) =>
            `• ${a.applicantName} (${a.applicantEmail}) — ${a.status}`
        ).join("\n");

        alert(`Applicants:\n\n${list}`);
    } catch (err) {
        alert("Could not load applicants.");
    }
}

async function confirmDeleteJob(jobId) {
    if (!confirm("Are you sure you want to delete this job?")) return;

    try {
        await deleteJob(jobId);
        await loadJobs();
    } catch (err) {
        alert(err.error || "Failed to delete job.");
    }
}

function openEditJob(jobId) {
    const form = document.getElementById("adminPanel");
    if (form) {
        form.scrollIntoView({ behavior: "smooth" });
        document.getElementById("editJobId").value = jobId || "";
    }
}

const jobForm = document.getElementById("jobForm");
if (jobForm) {
    jobForm.addEventListener("submit", async (e) => {
        e.preventDefault();

        const jobId = document.getElementById("editJobId").value;
        const payload = {
            title: document.getElementById("jobTitle").value.trim(),
            company: document.getElementById("jobCompany").value.trim(),
            location: document.getElementById("jobLocation").value.trim(),
            description: document.getElementById("jobDescription").value.trim(),
            salary: document.getElementById("jobSalary").value.trim(),
            jobType: document.getElementById("jobType").value
        };

        const msg = document.getElementById("jobFormMessage");
        msg.innerText = "";

        try {
            if (jobId) {
                await updateJob(jobId, payload);
                msg.style.color = "green";
                msg.innerText = "Job updated successfully!";
            } else {
                await createJob(payload);
                msg.style.color = "green";
                msg.innerText = "Job posted successfully!";
            }

            jobForm.reset();
            document.getElementById("editJobId").value = "";
            await loadJobs();
        } catch (err) {
            msg.style.color = "red";
            if (err.fieldErrors) {
                msg.innerText = Object.values(err.fieldErrors).join(", ");
            } else {
                msg.innerText = err.error || "Failed to save job.";
            }
        }
    });
}
