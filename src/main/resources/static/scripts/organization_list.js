let currentDistrict = '';
let currentCity = '';
const districts = ["Ростовская область", "Краснодарский край"];
const imageCache = new Map();

function goBack() {
    localStorage.removeItem('categoryId');
    localStorage.removeItem('subcategoryName');
    window.location.href = 'client_category.html';
}

async function initLocation() {
    // Инициализация выбора области
    const districtSelect = document.getElementById('district-select');
    districts.forEach(district => {
        const option = document.createElement('option');
        option.value = district;
        option.textContent = district;
        districtSelect.appendChild(option);
    });

    // Устанавливаем первую область по умолчанию
    currentDistrict = districts[0];
    await loadCities();

    // Устанавливаем первый город по умолчанию
    const citySelect = document.getElementById('city-select');
    if (citySelect.options.length > 0) {
        currentCity = citySelect.options[0].value;
        updateLocation();
        loadOrganizations(); // Загружаем организации после инициализации
    }
}

async function loadCities() {
    const districtSelect = document.getElementById('district-select');
    currentDistrict = districtSelect.value;

    try {
        const response = await fetch(`/cities?district=${encodeURIComponent(currentDistrict)}`);
        const data = await response.json();

        const citySelect = document.getElementById('city-select');
        citySelect.innerHTML = '';

        data.cities.forEach(city => {
            const option = document.createElement('option');
            option.value = city;
            option.textContent = city;
            citySelect.appendChild(option);
        });

        // Обновляем текущий город
        if (data.cities.length > 0) {
            currentCity = data.cities[0];
            updateLocationAndLoadOrganizations();
        }
    } catch (error) {
        console.error('Ошибка загрузки городов:', error);
        document.getElementById('organization-list').textContent = 'Ошибка загрузки списка городов';
    }
}

function updateLocation() {
    const citySelect = document.getElementById('city-select');
    currentCity = citySelect.value;

    document.getElementById('current-location').textContent =
        `${currentDistrict}, ${currentCity}`;
}

function updateLocationAndLoadOrganizations() {
    updateLocation();
    loadOrganizations(); // Перезагружаем организации при смене города
}

async function loadOrganizations() {
    const list = document.getElementById('organization-list');
    list.innerHTML = '<li>Загрузка организаций...</li>';

    try {
        const response = await fetch('/cards/info', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                city: currentCity,
                category: localStorage.getItem('categoryId'),
                subcategory: localStorage.getItem('subcategoryName')
            })
        });
        const data = await response.json();
        renderOrganizations(data);
    } catch (error) {
        console.error('Ошибка загрузки:', error);
        document.getElementById('organization-list').innerHTML =
            '<li>Ошибка загрузки данных. Пожалуйста, попробуйте позже.</li>';
    }
}

function renderOrganizations(data) {
    const list = document.getElementById('organization-list');
    list.innerHTML = '';

    if (!Array.isArray(data) || data.length === 0) {
        list.innerHTML = '<li>Нет организаций для отображения в выбранном городе</li>';
        return;
    }

    data.forEach((org) => {
        const li = document.createElement('li');
        li.className = 'organization-item';

        // Ограничиваем количество изображений до 5
        const filesToShow = org.files?.slice(0, 5) || [];

        // Создаем контейнер для изображений
        let imagesHtml = '';
        if (filesToShow.length > 0) {
            imagesHtml = '<div class="images-container">';
            filesToShow.forEach((file) => {
                imagesHtml += `
                        <div class="image-wrapper">
                            <div class="image-loading">Загрузка...</div>
                            <img data-file-id="${file.fileId}"
                                 alt="${file.originalFileName || 'Фото организации'}"
                                 class="organization-image"
                                 onload="this.style.display='block'; this.previousElementSibling.style.display='none'"
                                 style="display: none;">
                        </div>
                    `;
            });
            imagesHtml += '</div>';
        }

        // Форматируем телефон для ссылки tel:
        let phoneHtml = 'Телефон не указан';
        if (org.phone) {
            phoneHtml = `☎ <a href="tel:${org.phone.replace(/[^\d+]/g, '')}" target="_blank" class="phone-link">${org.phone}</a>`;
        }


        // Форматируем адрес для ссылки на Яндекс.Карты
        let addressHtml = 'Адрес не указан';
        if (org.district && org.city && org.address) {
            const query = encodeURIComponent(`${org.district}, ${org.city}, ${org.address}`);
            addressHtml = `
                    <span class="location-icon">📍</span>
                    <a href="https://yandex.ru/maps/?text=${query}"
                       target="_blank"
                       class="address-link">
                        ${org.address}
                    </a>
                `;
        } else if (org.address) {
            addressHtml = `<span class="location-icon">📍</span> ${org.address}`;
        }

        li.innerHTML = `
                ${imagesHtml}
                <div class="organization-info">
                    <div class="organization-company">${org.company || 'Компания не указана'}</div>
                    <div class="organization-address">${addressHtml}</div>
                    <div class="organization-phone">${phoneHtml}</div>
                    ${org.description ? `<div class="organization-description">${org.description}</div>` : ''}
                </div>
            `;
        list.appendChild(li);

        // Загружаем изображения для этой организации
        filesToShow.forEach(file => {
            loadImage(file.fileId);
        });
    });
}

async function loadImage(fileId) {
    // Проверяем кэш
    if (imageCache.has(fileId)) {
        const imgElement = document.querySelector(`img[data-file-id="${fileId}"]`);
        if (imgElement) {
            imgElement.src = imageCache.get(fileId);
            imgElement.onclick = function() {
                openModal(this.src);
            };
        }
        return;
    }

    try {
        const response = await fetch(`/files/${fileId}`);
        if (!response.ok) throw new Error('Ошибка загрузки изображения');

        const blob = await response.blob();
        const objectUrl = URL.createObjectURL(blob);

        // Сохраняем в кэш
        imageCache.set(fileId, objectUrl);

        // Находим все элементы с этим fileId (на случай дублирования)
        const imgElements = document.querySelectorAll(`img[data-file-id="${fileId}"]`);
        imgElements.forEach(img => {
            img.src = objectUrl;
            img.onclick = function() {
                openModal(this.src);
            };
        });
    } catch (error) {
        console.error('Ошибка загрузки изображения:', error);
        const imgElement = document.querySelector(`img[data-file-id="${fileId}"]`);
        if (imgElement) {
            imgElement.previousElementSibling.textContent = 'Ошибка загрузки';
        }
    }
}

function openModal(imageUrl) {
    const modal = document.getElementById('modal');
    const modalImg = document.getElementById('modal-image');
    modalImg.src = imageUrl;
    modal.style.display = 'flex';
}

function closeModal() {
    document.getElementById('modal').style.display = 'none';
}

document.getElementById('modal').addEventListener('click', (event) => {
    if (event.target === event.currentTarget) {
        closeModal();
    }
});

window.onload = initLocation;