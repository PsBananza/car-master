let uploadedFiles = [];
let isSubmitting = false;

function updateImagePreview() {
    const fileInput = document.getElementById("fileInput");
    const imagePreview = document.getElementById("imagePreview");
    const imageCount = document.getElementById("imageCount");
    const submitButton = document.getElementById("submitButton");

    if (fileInput.files.length + uploadedFiles.length > 5) {
        alert("Вы можете загрузить не более 5 фотографий.");
        return;
    }

    for (let i = 0; i < fileInput.files.length; i++) {
        uploadedFiles.push(fileInput.files[i]);
    }

    imagePreview.innerHTML = '';
    uploadedFiles.forEach(file => {
        const img = document.createElement("img");
        img.src = URL.createObjectURL(file);
        imagePreview.appendChild(img);
    });

    imageCount.textContent = `Загружено файлов: ${uploadedFiles.length}`;
    submitButton.disabled = uploadedFiles.length === 0;
}

function submitPhotos() {
    if (isSubmitting) return;
    isSubmitting = true;

    const loader = document.getElementById("loader");
    const loadingText = document.getElementById("loadingText");
    const submitButton = document.getElementById("submitButton");

    submitButton.classList.add("loading");
    loader.style.display = "block";
    loadingText.style.display = "block";
    submitButton.disabled = true;

    if (uploadedFiles.length === 0) {
        alert("Вы не выбрали файлы!");
        hideLoader();
        isSubmitting = false;
        return;
    }

    const formData = new FormData();

    uploadedFiles.forEach(file => {
        formData.append("file[]", file);
    });

    const company = localStorage.getItem("company");
    const description = localStorage.getItem("description");
    const address = localStorage.getItem("address");
    const phone = localStorage.getItem("phone");
    const selectedCategories = JSON.parse(localStorage.getItem("selectedCategories"));
    const district = localStorage.getItem("district");
    const city = localStorage.getItem("city");
    const telegramId = localStorage.getItem("telegramId");

    if (!district || !city) {
        alert("Пожалуйста, выберите район и город.");
        hideLoader();
        isSubmitting = false;
        return;
    }

    if (!telegramId) {
        alert("Не удалось получить ID пользователя.");
        hideLoader();
        isSubmitting = false;
        return;
    }

    formData.append("company", company);
    formData.append("description", description);
    formData.append("district", district);
    formData.append("city", city);
    formData.append("address", address);
    formData.append("phone", phone);
    formData.append("categories", JSON.stringify(selectedCategories));
    formData.append("telegramId", telegramId);

    fetch('/webhook/save-registration', {
        method: 'POST',
        body: formData
    })
        .then(response => response.json())
        .then(data => {
            hideLoader();
            document.querySelector(".container").style.display = "none";
            document.getElementById("successContainer").style.display = "block";
        })
        .catch(error => {
            console.error("Ошибка:", error);
            alert("Произошла ошибка при отправке данных.");
            hideLoader();
        })
        .finally(() => {
            isSubmitting = false;
        });
}

function hideLoader() {
    const loader = document.getElementById("loader");
    const loadingText = document.getElementById("loadingText");
    const submitButton = document.getElementById("submitButton");

    loader.style.display = "none";
    loadingText.style.display = "none";
    submitButton.classList.remove("loading");
    submitButton.disabled = uploadedFiles.length === 0;
}

function goToWebApp() {
    window.location.href = '/webapp.html';
}