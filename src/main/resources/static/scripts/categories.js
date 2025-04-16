function populateCategories(categories) {
    const container = document.getElementById("categoriesContainer");
    container.innerHTML = "";

    Object.keys(categories).forEach(category => {
        const li = document.createElement("li");
        li.className = "category-item";

        const categoryHeader = document.createElement("div");
        categoryHeader.className = "category-header";
        categoryHeader.innerHTML = `
                <input type="checkbox" class="category-checkbox" onchange="toggleSubcategories('${category}')">
                ${category}
            `;

        const subContainer = document.createElement("div");
        subContainer.id = `sub-${category}`;
        subContainer.className = "subcategories";

        if (categories[category].length > 0) {
            const selectAllDiv = document.createElement("div");
            selectAllDiv.className = "select-all";
            selectAllDiv.innerHTML = `
                    <input type="checkbox" class="subcategory-checkbox"
                           id="select-all-${category}"
                           onchange="toggleAllSubcategories('${category}')">
                    <label for="select-all-${category}">Выбрать все</label>
                `;
            subContainer.appendChild(selectAllDiv);

            categories[category].forEach(subcategory => {
                const subItem = document.createElement("div");
                subItem.className = "subcategory-item";
                subItem.innerHTML = `
                        <input type="checkbox" class="subcategory-checkbox"
                               name="subcategories" value="${subcategory}"
                               data-category="${category}"
                               onchange="checkSelectAll('${category}')">
                        <label>${subcategory}</label>
                    `;
                subContainer.appendChild(subItem);
            });
        } else {
            subContainer.style.display = "none";
        }

        li.appendChild(categoryHeader);
        li.appendChild(subContainer);
        container.appendChild(li);
    });
}

function toggleSubcategories(category) {
    const subContainer = document.getElementById(`sub-${category}`);
    const categoryCheckbox = document.querySelector(`.category-header input[onchange="toggleSubcategories('${category}')"]`);

    if (subContainer) {
        subContainer.style.display = categoryCheckbox.checked ? "block" : "none";
    }
}

function toggleAllSubcategories(category) {
    const selectAllCheckbox = document.getElementById(`select-all-${category}`);
    const subcategoryCheckboxes = document.querySelectorAll(`input[name='subcategories'][data-category='${category}']`);
    const categoryCheckbox = document.querySelector(`.category-header input[onchange="toggleSubcategories('${category}')"]`);

    subcategoryCheckboxes.forEach(checkbox => {
        checkbox.checked = selectAllCheckbox.checked;
    });

    categoryCheckbox.checked = selectAllCheckbox.checked;
}

function checkSelectAll(category) {
    const subcategoryCheckboxes = document.querySelectorAll(`input[name='subcategories'][data-category='${category}']`);
    const selectAllCheckbox = document.getElementById(`select-all-${category}`);
    const categoryCheckbox = document.querySelector(`.category-header input[onchange="toggleSubcategories('${category}')"]`);

    selectAllCheckbox.checked = Array.from(subcategoryCheckboxes).every(checkbox => checkbox.checked);

    const isAnySubcategoryChecked = Array.from(subcategoryCheckboxes).some(checkbox => checkbox.checked);
    categoryCheckbox.checked = isAnySubcategoryChecked;
}

function continueRegistration() {
    const selectedCategories = {};

    document.querySelectorAll(".category-item").forEach(categoryItem => {
        const category = categoryItem.querySelector(".category-header").textContent.trim();
        const subContainer = categoryItem.querySelector(".subcategories");
        const categoryCheckbox = categoryItem.querySelector(".category-header input[type='checkbox']");

        if (subContainer) {
            const checkedSubcategories = Array.from(subContainer.querySelectorAll("input[name='subcategories']:checked"))
                .map(checkbox => checkbox.value);

            if (checkedSubcategories.length > 0) {
                selectedCategories[category] = checkedSubcategories;
            } else if (categoryCheckbox.checked) {
                selectedCategories[category] = [];
            }
        } else {
            if (categoryCheckbox.checked) {
                selectedCategories[category] = [];
            }
        }
    });

    if (Object.keys(selectedCategories).length === 0) {
        alert("Выберите хотя бы одну категорию или подкатегорию!");
        return;
    }

    localStorage.setItem("selectedCategories", JSON.stringify(selectedCategories));
    window.location.href = "upload_photos.html";
}

window.onload = function() {
    fetch("/categories/subcategories")
        .then(response => response.json())
        .then(data => populateCategories(data));
};