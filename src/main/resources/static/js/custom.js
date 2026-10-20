function togglePassword(fieldId, button) {
    const input = document.getElementById(fieldId);
    const icon = button.querySelector("i");

    if (input.type === "password") {
        input.type = "text";
        icon.classList.remove("bi-eye");
        icon.classList.add("bi-eye-slash");
    } else {
        input.type = "password";
        icon.classList.remove("bi-eye-slash");
        icon.classList.add("bi-eye");
    }
}

document.addEventListener("DOMContentLoaded", function () {
    const alert = document.getElementById("floatingAlert");

    if (alert) {
        setTimeout(() => {
            const bsAlert = bootstrap.Alert.getOrCreateInstance(alert);
            bsAlert.close();
        }, 5000);
    }
});

// PRODUCT SEARCH
document.getElementById("tableSearch")
    .addEventListener("keyup", function () {

        let search = this.value.toLowerCase();

        document
            .querySelectorAll("#table tbody tr")
            .forEach(function (row) {

                let text = row.innerText.toLowerCase();

                row.style.display =
                    text.includes(search)
                        ? ""
                        : "none";

            });

    });

function getLocation(id) {
    const latId = id === 'New' ? 'latNew' : 'lat' + id;
    const lngId = id === 'New' ? 'lngNew' : 'lng' + id;
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(pos => {
            document.getElementById(latId).value = pos.coords.latitude.toFixed(6);
            document.getElementById(lngId).value = pos.coords.longitude.toFixed(6);
            initMap(id, pos.coords.latitude, pos.coords.longitude);
        }, () => alert("Could not get location. Please allow GPS permission."));
    } else { alert("Geolocation not supported"); }
}
function initMap(id, lat, lng) {
    const mapId = id === 'New' ? 'mapNew' : 'map' + id;
    if (document.getElementById(mapId)._leaflet_id) return; // prevent re-init
    const map = L.map(mapId).setView([lat, lng], 15);
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png').addTo(map);
    L.marker([lat, lng]).addTo(map);
}

/*!-- ================= Alert Dismiss ================= -->*/
document.addEventListener("DOMContentLoaded", function () {
    const alertError = document.getElementById("floatingAlertError");
    if (alertError) {
        setTimeout(() => {
            const brAlert = bootstrap.Alert.getOrCreateInstance(alertError);
            brAlert.close();
        }, 5000);
    }

    const alertSuccess = document.getElementById("floatingAlertSuccess");
    if (alertSuccess) {
        setTimeout(() => {
            const bsAlert = bootstrap.Alert.getOrCreateInstance(alertSuccess);
            bsAlert.close();
        }, 5000);
    }
});

//session time out
let sessionTimeout = 60 * 60 * 1000; // 1 hour in ms
let warningTime = 5 * 60 * 1000; // show modal 5 min before
let logoutTimer;
let warningTimer;

function startTimers() {
    clearTimeout(logoutTimer);
    clearTimeout(warningTimer);

    // Show modal 5 min before logout
    warningTimer = setTimeout(showSessionModal, sessionTimeout - warningTime);

    // Force logout after 1 hour
    logoutTimer = setTimeout(forceLogout, sessionTimeout);
}

function showSessionModal() {
    const modal = new bootstrap.Modal(document.getElementById('sessionModal'));
    modal.show();

    // Start 60s countdown inside modal
    let countdown = 60;
    const countdownEl = document.getElementById('countdown');
    const interval = setInterval(() => {
        countdownEl.innerText = countdown;
        countdown--;
        if (countdown < 0) {
            clearInterval(interval);
            forceLogout();
        }
    }, 1000);

    // If user clicks continue
    document.getElementById('continueBtn').onclick = function () {
        clearInterval(interval);
        modal.hide();
        extendSession();
    }
}

function extendSession() {
    // Ping server to refresh session
    fetch('/session/keepalive', { method: 'POST' })
        .then(() => {
            startTimers(); // restart 1 hour timer
        });
}

function forceLogout() {
    window.location.href = '/logout';
}

// Reset timers on any user activity
['click', 'mousemove', 'keypress', 'scroll'].forEach(event => {
    document.addEventListener(event, startTimers);
});

// Start on page load
window.onload = startTimers;
//end session