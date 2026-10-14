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