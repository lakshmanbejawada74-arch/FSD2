/**
 * CampusCare Client-Side Form Validation and Dynamic Behavior
 */

document.addEventListener("DOMContentLoaded", function () {
    // 1. Dynamic Hostel / Day Scholar toggle in Student Registration
    const studentTypeSelect = document.getElementById("studentType");
    const hostelDetailsContainer = document.getElementById("hostelDetailsContainer");
    const hostelNameInput = document.getElementById("hostelName");
    const roomNumberInput = document.getElementById("roomNumber");

    function updateHostelFields() {
        if (!studentTypeSelect || !hostelDetailsContainer) return;
        if (studentTypeSelect.value === "HOSTEL") {
            hostelDetailsContainer.style.display = "block";
            if (hostelNameInput) hostelNameInput.required = true;
            if (roomNumberInput) roomNumberInput.required = true;
        } else {
            hostelDetailsContainer.style.display = "none";
            if (hostelNameInput) {
                hostelNameInput.required = false;
                hostelNameInput.value = "";
            }
            if (roomNumberInput) {
                roomNumberInput.required = false;
                roomNumberInput.value = "";
            }
        }
    }

    if (studentTypeSelect) {
        studentTypeSelect.addEventListener("change", updateHostelFields);
        updateHostelFields(); // Initial call
    }

    // 2. Student Registration Form Validation
    const registerForm = document.getElementById("studentRegisterForm");
    if (registerForm) {
        registerForm.addEventListener("submit", function (e) {
            const password = document.getElementById("password") ? document.getElementById("password").value : "";
            const confirmPassword = document.getElementById("confirmPassword") ? document.getElementById("confirmPassword").value : "";
            const studentId = document.getElementById("studentId") ? document.getElementById("studentId").value.trim() : "";
            const mobile = document.getElementById("mobile") ? document.getElementById("mobile").value.trim() : "";
            const fullName = document.getElementById("fullName") ? document.getElementById("fullName").value.trim() : "";

            // Check full name
            if (fullName.length < 2 || fullName.length > 100) {
                alert("Full Name must be between 2 and 100 characters.");
                e.preventDefault();
                return false;
            }

            // Check ID format
            const idRegex = /^[A-Za-z0-9_-]{3,30}$/;
            if (!idRegex.test(studentId)) {
                alert("Student ID must be 3-30 alphanumeric characters.");
                e.preventDefault();
                return false;
            }

            // Check mobile format
            const mobileRegex = /^\+?[0-9]{10,15}$/;
            if (!mobileRegex.test(mobile)) {
                alert("Mobile number must be 10 to 15 digits (optional leading +).");
                e.preventDefault();
                return false;
            }

            // Check password complexity: min 8, 1 uppercase, 1 lowercase, 1 digit, 1 special char
            const pwdRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&^#()[\]{}|;:,.<>~_\-+=\\/])[A-Za-z\d@$!%*?&^#()[\]{}|;:,.<>~_\-+=\\/]{8,}$/;
            if (!pwdRegex.test(password)) {
                alert("Password must contain at least 8 characters, with at least one uppercase letter, one lowercase letter, one number, and one special character.");
                e.preventDefault();
                return false;
            }

            if (password !== confirmPassword) {
                alert("Passwords do not match. Please re-enter.");
                e.preventDefault();
                return false;
            }

            // Hostel validation
            if (studentTypeSelect && studentTypeSelect.value === "HOSTEL") {
                if (!hostelNameInput.value.trim() || !roomNumberInput.value.trim()) {
                    alert("Hostel Name and Room Number are required for Hostel students.");
                    e.preventDefault();
                    return false;
                }
            }
        });
    }

    // 3. Complaint Submission Form Validation
    const complaintForm = document.getElementById("complaintForm");
    if (complaintForm) {
        complaintForm.addEventListener("submit", function (e) {
            const category = document.getElementById("categoryId");
            const title = document.getElementById("title");
            const desc = document.getElementById("description");
            const priority = document.getElementById("priority");

            if (!category || !category.value) {
                alert("Please select a complaint category.");
                e.preventDefault();
                return false;
            }

            if (!title || title.value.trim().length < 5 || title.value.trim().length > 100) {
                alert("Complaint title must be between 5 and 100 characters.");
                e.preventDefault();
                return false;
            }

            if (!desc || desc.value.trim().length < 10 || desc.value.trim().length > 1000) {
                alert("Complaint description must be between 10 and 1000 characters.");
                e.preventDefault();
                return false;
            }

            if (!priority || !priority.value) {
                alert("Please select a priority level.");
                e.preventDefault();
                return false;
            }
        });
    }

    // 4. Resolution Remarks Validation
    const resolveForm = document.getElementById("resolveForm");
    if (resolveForm) {
        resolveForm.addEventListener("submit", function (e) {
            const statusSelect = document.getElementById("statusSelect");
            const remarksInput = document.getElementById("resolutionRemarks");
            if (statusSelect && statusSelect.value === "RESOLVED") {
                if (!remarksInput || !remarksInput.value.trim()) {
                    alert("Resolution remarks are required before marking this complaint as RESOLVED.");
                    e.preventDefault();
                    return false;
                }
                if (remarksInput.value.trim().length > 500) {
                    alert("Resolution remarks must not exceed 500 characters.");
                    e.preventDefault();
                    return false;
                }
            }
        });
    }

    // 5. Generic Confirmation for critical actions
    const confirmButtons = document.querySelectorAll("[data-confirm]");
    confirmButtons.forEach(function (btn) {
        btn.addEventListener("click", function (e) {
            const msg = btn.getAttribute("data-confirm") || "Are you sure you want to proceed?";
            if (!confirm(msg)) {
                e.preventDefault();
                return false;
            }
        });
    });
});
