/**
 * CampusCare Dashboard Interactive Features
 */

document.addEventListener("DOMContentLoaded", function () {
    // Client-side quick filter for tables
    const quickSearchInput = document.getElementById("quickTableSearch");
    if (quickSearchInput) {
        quickSearchInput.addEventListener("keyup", function () {
            const filter = quickSearchInput.value.toLowerCase();
            const table = document.querySelector(".table-custom tbody");
            if (!table) return;
            const rows = table.getElementsByTagName("tr");
            for (let i = 0; i < rows.length; i++) {
                const text = rows[i].textContent.toLowerCase();
                rows[i].style.display = text.indexOf(filter) > -1 ? "" : "none";
            }
        });
    }

    // Auto-dismiss alerts after 6 seconds
    const alerts = document.querySelectorAll(".alert-dismissible");
    alerts.forEach(function (alertElem) {
        setTimeout(function () {
            try {
                const bsAlert = bootstrap.Alert.getOrCreateInstance(alertElem);
                if (bsAlert) bsAlert.close();
            } catch (e) {
                alertElem.style.display = "none";
            }
        }, 6000);
    });
});
