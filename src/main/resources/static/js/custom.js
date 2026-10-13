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

    // POS PRODUCT SEARCH
document.addEventListener('DOMContentLoaded', function () {
    const searchInput = document.getElementById('tableSearch');
    const products = document.querySelectorAll('.product-item');
    const noResults = document.getElementById('noProductsFound');

    searchInput.addEventListener('input', function () {
        const query = this.value.trim().toLowerCase();
        let visibleCount = 0;

        products.forEach(function (product) {
            const name = (product.dataset.name || '').toLowerCase();
            const sku = (product.dataset.sku || '').toLowerCase();

            const matches = name.includes(query) || sku.includes(query);

            product.style.display = matches ? '' : 'none';

            if (matches) {
                visibleCount++;
            }
        });

        noResults.style.display = visibleCount === 0 ? 'block' : 'none';
    });
});