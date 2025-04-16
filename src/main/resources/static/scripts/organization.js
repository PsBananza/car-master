let currentEditingOrg = null;
let newImages = [];
let imagesToDelete = [];
let allCategories = {};
let selectedCategories = {};

// Список областей и городов
const districtsCities = {
    "Ростовская область": [
        "Ростов-на-Дону", "Аксай", "Азов", "Батайск", "Волгодонск", "Гуково", "Зверево",
        "Каменск-Шахтинский", "Красносулинск", "Миллерово", "Морозовск", "Новочеркасск",
        "Новошахтинск", "Пролетарск", "Ремонтное", "Сальск", "Семикаракорск", "Шахты",
        "Таганрог", "Тимашевск", "Чертков", "Цимлянск", "Шолоховский"
    ],
    "Краснодарский край": [
        "Краснодар"
    ]
};

let cardToDeleteId = null;

function openDeleteModal(cardId) {
    cardToDeleteId = cardId;
    document.getElementById('delete-modal').style.display = 'flex';
}

async function confirmDelete() {
    if (!cardToDeleteId) return;

    try {
        const urlParams = new URLSearchParams(window.location.search);
        const telegramId = urlParams.get('telegramId') || localStorage.getItem('telegramId');

        const response = await fetch(`/card/remove?id=${cardToDeleteId}&telegramId=${telegramId}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            alert('Карточка успешно удалена');
            closeModal('delete-modal');
            loadOrganizationCards(); // Перезагружаем список карточек
        } else {
            throw new Error('Ошибка при удалении');
        }
    } catch (error) {
        console.error('Ошибка:', error);
        alert('Не удалось удалить карточку: ' + error.message);
    } finally {
        cardToDeleteId = null;
    }
}

function goBack() {
    window.history.back();
}

function validatePhone(phone) {
    const cleaned = phone.replace(/\D/g, '');
    return cleaned.length === 11 && cleaned.startsWith('7');
}

function formatPhoneNumber(phone) {
    if (!phone) return '';
    const cleaned = phone.replace(/\D/g, '');
    const match = cleaned.match(/^(\d{1})(\d{3})(\d{3})(\d{2})(\d{2})$/);
    if (match) {
        return `+${match[1]} ${match[2]} ${match[3]} ${match[4]}${match[5]}`;
    }
    return phone;
}

function validateForm() {
    let isValid = true;

    // Проверка названия компании
    const company = document.getElementById('edit-company').value.trim();
    if (!company) {
        document.getElementById('edit-company-error').textContent = 'Введите название компании';
        isValid = false;
    } else {
        document.getElementById('edit-company-error').textContent = '';
    }

    // Проверка телефона
    const phone = document.getElementById('edit-phone').value.trim();
    if (!phone) {
        document.getElementById('edit-phone-error').textContent = 'Введите номер телефона';
        isValid = false;
    } else if (!validatePhone(phone)) {
        document.getElementById('edit-phone-error').textContent = 'Телефон должен быть в формате +7 XXX XXX XX XX';
        isValid = false;
    } else {
        document.getElementById('edit-phone-error').textContent = '';
    }

    // Проверка области
    const district = document.getElementById('edit-district').value;
    if (!district) {
        document.getElementById('edit-district-error').textContent = 'Выберите область/край';
        isValid = false;
    } else {
        document.getElementById('edit-district-error').textContent = '';
    }

    // Проверка города
    const city = document.getElementById('edit-city').value;
    if (!city) {
        document.getElementById('edit-city-error').textContent = 'Выберите город';
        isValid = false;
    } else {
        document.getElementById('edit-city-error').textContent = '';
    }

    // Проверка адреса
    const address = document.getElementById('edit-address').value.trim();
    if (!address) {
        document.getElementById('edit-address-error').textContent = 'Введите адрес';
        isValid = false;
    } else {
        document.getElementById('edit-address-error').textContent = '';
    }

    // Проверка описания
    const description = document.getElementById('edit-description').value.trim();
    if (!description) {
        document.getElementById('edit-description-error').textContent = 'Введите описание';
        isValid = false;
    } else if (description.length > 300) {
        document.getElementById('edit-description-error').textContent = 'Описание не должно превышать 300 символов';
        isValid = false;
    } else {
        document.getElementById('edit-description-error').textContent = '';
    }

    return isValid;
}

function updateDescriptionCounter() {
    const description = document.getElementById('edit-description').value;
    document.getElementById('description-counter').textContent = description.length;
}

function formatCategories(categories) {
    if (!categories || !Array.isArray(categories) || categories.length === 0) {
        return '';
    }

    let html = '<div class="categories-container">';
    html += '<div class="categories-title">Категории:</div>';

    categories.forEach(category => {
        const hasSubcategories = category.subCategories && category.subCategories.length > 0;
        const showLimited = hasSubcategories && category.subCategories.length > 10;
        const visibleCount = showLimited ? 10 : category.subCategories?.length || 0;

        html += `
                <div class="category-block" data-category-id="${category.categoryId}">
                    <div class="category-header" onclick="toggleSubcategories(this)">
                        <span class="toggle-icon">${hasSubcategories ? '▸' : '•'}</span>
                        <span class="category-name">${category.name || 'Без названия'}</span>
                    </div>
                    ${hasSubcategories ? `
                    <div class="subcategories-container">
                        <ul class="subcategories-list">
                            ${category.subCategories.slice(0, visibleCount).map(subCat => `
                                <li class="subcategory-item">${subCat.name || 'Без названия'}</li>
                            `).join('')}
                        </ul>
                        ${showLimited ? `
                        <button class="show-more" onclick="showAllSubcategories(event, this)">
                            Показать все (${category.subCategories.length})
                        </button>
                        ` : ''}
                    </div>
                    ` : ''}
                </div>
            `;
    });

    html += '</div>';
    return html;
}

function toggleSubcategories(headerElement) {
    const icon = headerElement.querySelector('.toggle-icon');
    const container = headerElement.nextElementSibling;

    if (container && container.classList.contains('subcategories-container')) {
        container.classList.toggle('expanded');
        icon.textContent = container.classList.contains('expanded') ? '▾' : '▸';
    }
}

function showAllSubcategories(event, buttonElement) {
    event.stopPropagation();
    const container = buttonElement.parentElement;
    const list = container.querySelector('.subcategories-list');
    const categoryBlock = container.closest('.category-block');
    const categoryId = categoryBlock.dataset.categoryId;

    const orgItem = categoryBlock.closest('.organization-item');
    const orgData = orgItem._orgData;

    if (orgData?.categories) {
        const category = orgData.categories.find(cat => cat.categoryId === categoryId);
        if (category?.subCategories) {
            buttonElement.remove();

            const alreadyShown = list.querySelectorAll('.subcategory-item').length;
            const remainingSubcats = category.subCategories.slice(alreadyShown);

            const fragment = document.createDocumentFragment();
            remainingSubcats.forEach(subCat => {
                const item = document.createElement('li');
                item.className = 'subcategory-item';
                item.textContent = subCat.name || 'Без названия';
                fragment.appendChild(item);
            });

            list.appendChild(fragment);
            container.classList.add('expanded');
            const icon = categoryBlock.querySelector('.toggle-icon');
            if (icon) icon.textContent = '▾';
        }
    }
}

async function loadCategories() {
    try {
        const response = await fetch('/categories/subcategories');
        if (!response.ok) throw new Error('Ошибка загрузки категорий');
        const data = await response.json();
        allCategories = data;

        const categorySelect = document.getElementById('category-select');
        categorySelect.innerHTML = '<option value="">Выберите категорию</option>';

        Object.keys(data).forEach(categoryName => {
            const option = document.createElement('option');
            option.value = categoryName;
            option.textContent = categoryName;
            categorySelect.appendChild(option);
        });
    } catch (error) {
        console.error('Ошибка загрузки категорий:', error);
    }
}

function updateSubcategorySelect(categoryName) {
    const subcategoryControls = document.getElementById('subcategory-controls');
    const subcategorySelect = document.getElementById('subcategory-select');
    subcategorySelect.innerHTML = '';

    if (categoryName && allCategories[categoryName] && allCategories[categoryName].length > 0) {
        subcategoryControls.style.display = 'block';

        allCategories[categoryName].forEach(subcategory => {
            const option = document.createElement('option');
            option.value = subcategory;
            option.textContent = subcategory;
            subcategorySelect.appendChild(option);
        });
    } else {
        subcategoryControls.style.display = 'none';
    }
}

function selectAllSubcategories() {
    const subcategorySelect = document.getElementById('subcategory-select');
    const options = subcategorySelect.options;

    for (let i = 0; i < options.length; i++) {
        options[i].selected = true;
    }
}

function deselectAllSubcategories() {
    const subcategorySelect = document.getElementById('subcategory-select');
    const options = subcategorySelect.options;

    for (let i = 0; i < options.length; i++) {
        options[i].selected = false;
    }
}

function addSelectedCategory() {
    const categorySelect = document.getElementById('category-select');
    const subcategorySelect = document.getElementById('subcategory-select');
    const categoryName = categorySelect.value;

    if (!categoryName) return;

    const selectedSubcategories = Array.from(subcategorySelect.selectedOptions)
        .map(option => option.value)
        .filter(Boolean);

    if (!selectedCategories[categoryName]) {
        selectedCategories[categoryName] = [];
    }

    selectedSubcategories.forEach(subcategory => {
        if (!selectedCategories[categoryName].includes(subcategory)) {
            selectedCategories[categoryName].push(subcategory);
        }
    });

    if (selectedSubcategories.length === 0 && subcategorySelect.style.display === 'none') {
        selectedCategories[categoryName] = [];
    }

    renderSelectedCategories();
}

function renderSelectedCategories() {
    const container = document.getElementById('selected-categories');
    container.innerHTML = '';

    Object.keys(selectedCategories).forEach(categoryName => {
        const categoryDiv = document.createElement('div');
        categoryDiv.className = 'selected-category';

        let subcatsText = '';
        if (selectedCategories[categoryName].length > 0) {
            const subcats = selectedCategories[categoryName];
            const displayCount = Math.min(3, subcats.length);
            subcatsText = `: ${subcats.slice(0, displayCount).join(', ')}`;
            if (subcats.length > displayCount) {
                subcatsText += ` +${subcats.length - displayCount} more`;
            }
        }

        categoryDiv.innerHTML = `
                ${categoryName}${subcatsText}
                <button onclick="removeCategory('${categoryName}')">×</button>
            `;

        container.appendChild(categoryDiv);
    });
}

function removeCategory(categoryName) {
    delete selectedCategories[categoryName];
    renderSelectedCategories();
}

async function openEditModal(orgData) {
    currentEditingOrg = orgData;
    newImages = [];
    imagesToDelete = [];
    selectedCategories = {};

    if (orgData.categories) {
        orgData.categories.forEach(cat => {
            selectedCategories[cat.name] = cat.subCategories?.map(sc => sc.name) || [];
        });
    }

    document.getElementById('edit-org-id').value = orgData.id;
    document.getElementById('edit-company').value = orgData.company || '';
    document.getElementById('edit-phone').value = formatPhoneNumber(orgData.phone || '+7');
    document.getElementById('edit-district').value = orgData.district || '';
    document.getElementById('edit-address').value = orgData.address || '';
    document.getElementById('edit-description').value = orgData.description || '';
    updateDescriptionCounter();

    // Заполняем города в зависимости от выбранной области
    document.getElementById('edit-district').value = orgData.district || '';
    if (orgData.district) {
        await updateCities(orgData.district);
    }
    const citySelect = document.getElementById('edit-city');

    if (orgData.district) {
        citySelect.innerHTML = '<option value="">Выберите город</option>';
        const cities = districtsCities[orgData.district] || [];
        cities.forEach(city => {
            const option = document.createElement('option');
            option.value = city;
            option.textContent = city;
            if (orgData.city === city) {
                option.selected = true;
            }
            citySelect.appendChild(option);
        });
        citySelect.disabled = false;
    }

    const statusDisplay = document.getElementById('edit-status-display');
    statusDisplay.textContent = orgData.isActive ? 'Активно' : 'Неактивно';
    statusDisplay.className = `organization-status ${orgData.isActive ? 'status-active' : 'status-moderation'}`;

    const imagesContainer = document.getElementById('edit-images-container');
    imagesContainer.innerHTML = '';

    if (orgData.files && orgData.files.length > 0) {
        const filesToShow = orgData.files.slice(0, 5);
        for (const file of filesToShow) {
            try {
                const imageResponse = await fetch(`/files/${file.fileId}`);
                if (imageResponse.ok) {
                    const blob = await imageResponse.blob();
                    const dataUrl = await blobToDataURL(blob);

                    const wrapper = document.createElement('div');
                    wrapper.className = 'image-wrapper';
                    wrapper.dataset.fileId = file.fileId;
                    wrapper.innerHTML = `
                        <img src="${dataUrl}" class="organization-image">
                        <button class="remove-image-btn" onclick="removeImage('${file.fileId}')">×</button>
                    `;
                    imagesContainer.appendChild(wrapper);
                }
            } catch (error) {
                console.error('Ошибка загрузки изображения:', error);
            }
        }
    }

    await loadCategories();
    renderSelectedCategories();
    document.getElementById('edit-modal').style.display = 'flex';
}

async function updateCities(district) {
    const citySelect = document.getElementById('edit-city');
    citySelect.innerHTML = '<option value="">Загрузка городов...</option>';
    citySelect.disabled = true;

    try {
        const response = await fetch(`/cities?district=${encodeURIComponent(district)}`);
        if (!response.ok) throw new Error('Ошибка загрузки городов');

        const data = await response.json();
        const cities = data.cities || [];

        citySelect.innerHTML = '<option value="">Выберите город</option>';
        cities.forEach(city => {
            const option = document.createElement('option');
            option.value = city;
            option.textContent = city;
            citySelect.appendChild(option);
        });

        citySelect.disabled = false;

        if (currentEditingOrg && currentEditingOrg.district === district) {
            citySelect.value = currentEditingOrg.city || '';
        }
    } catch (error) {
        console.error('Ошибка загрузки городов:', error);
        citySelect.innerHTML = '<option value="">Ошибка загрузки городов</option>';
    }
}

function removeImage(fileId) {
    if (confirm('Удалить это изображение?')) {
        const wrapper = document.querySelector(`.image-wrapper[data-file-id="${fileId}"]`);
        if (wrapper) {
            wrapper.remove();
            if (!imagesToDelete.includes(fileId)) {
                imagesToDelete.push(fileId);
            }
        }
    }
}

function setupFileUpload() {
    const uploadArea = document.getElementById('upload-area');
    const fileInput = document.getElementById('file-input');
    const imagesContainer = document.getElementById('edit-images-container');

    uploadArea.addEventListener('click', () => fileInput.click());

    uploadArea.addEventListener('dragover', (e) => {
        e.preventDefault();
        uploadArea.style.borderColor = '#007bff';
    });

    uploadArea.addEventListener('dragleave', () => {
        uploadArea.style.borderColor = '#ddd';
    });

    uploadArea.addEventListener('drop', (e) => {
        e.preventDefault();
        uploadArea.style.borderColor = '#ddd';
        if (e.dataTransfer.files.length > 0) {
            handleFiles(e.dataTransfer.files);
        }
    });

    fileInput.addEventListener('change', () => {
        if (fileInput.files.length > 0) {
            handleFiles(fileInput.files);
            fileInput.value = '';
        }
    });

    function handleFiles(files) {
        const currentImageCount = document.querySelectorAll('#edit-images-container .image-wrapper').length;
        const availableSlots = 5 - currentImageCount;

        if (availableSlots <= 0) {
            alert('Максимум 5 фотографий на карточку');
            return;
        }

        const filesToAdd = Array.from(files).slice(0, availableSlots);

        filesToAdd.forEach(file => {
            if (!file.type.match('image.*')) return;

            const reader = new FileReader();
            reader.onload = (e) => {
                const wrapper = document.createElement('div');
                wrapper.className = 'image-wrapper';
                wrapper.innerHTML = `
                    <img src="${e.target.result}" class="organization-image">
                    <button class="remove-image-btn" onclick="this.parentElement.remove()">×</button>
                `;
                imagesContainer.appendChild(wrapper);

                newImages.push({
                    file: file,
                    preview: e.target.result
                });
            };
            reader.readAsDataURL(file);
        });
    }
}

async function saveOrganization() {
    if (!validateForm()) {
        return;
    }

    const formData = new FormData();
    const textFields = {
        'id': document.getElementById('edit-org-id').value,
        'company': document.getElementById('edit-company').value.trim(),
        'phone': document.getElementById('edit-phone').value.trim(),
        'district': document.getElementById('edit-district').value,
        'city': document.getElementById('edit-city').value,
        'address': document.getElementById('edit-address').value.trim(),
        'description': document.getElementById('edit-description').value.trim()
    };

    Object.entries(textFields).forEach(([key, value]) => {
        formData.append(key, new Blob([value], { type: 'text/plain' }));
    });

    const categories = Object.keys(selectedCategories).map(categoryName => ({
        name: categoryName,
        subCategories: selectedCategories[categoryName].map(name => ({ name }))
    }));
    formData.append('categories', new Blob([JSON.stringify(categories)], {
        type: 'application/json'
    }));

    if (imagesToDelete && imagesToDelete.length > 0) {
        formData.append('filesToDelete', new Blob(
            [JSON.stringify(imagesToDelete)],
            { type: 'application/json' }
        ));
    }

    if (newImages && newImages.length > 0) {
        newImages.forEach(img => {
            if (img.file) {
                formData.append('newFiles', img.file);
            }
        });
    }

    try {
        const response = await fetch('/card/update', {
            method: 'POST',
            body: formData
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(errorText || 'Ошибка сохранения (статус: ' + response.status + ')');
        }

        const result = await response.json();

        if (result.success) {
            alert('Изменения успешно сохранены');
            closeModal('edit-modal');
            loadOrganizationCards();
        } else {
            throw new Error(result.message || 'Неизвестная ошибка сервера');
        }

    } catch (error) {
        console.error('Ошибка при сохранении:', error);
        alert('Ошибка при сохранении: ' + error.message);
    }
}

async function loadOrganizationCards() {
    const list = document.getElementById('organization-list');
    list.innerHTML = '<div class="loading-spinner"></div>';

    try {
        const urlParams = new URLSearchParams(window.location.search);
        const telegramId = urlParams.get('telegramId') || localStorage.getItem('telegramId');

        if (!telegramId) throw new Error('Telegram ID не указан');

        const response = await fetch('/card/info', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ telegramId: telegramId })
        });

        if (!response.ok) throw new Error('Ошибка сервера');

        const organizations = await response.json();

        for (const org of organizations) {
            if (org.files && org.files.length > 0) {
                for (const file of org.files) {
                    const imageResponse = await fetch(`/files/${file.fileId}`);
                    if (imageResponse.ok) {
                        const blob = await imageResponse.blob();
                        file.dataUrl = await blobToDataURL(blob);
                    }
                }
            }
        }

        renderOrganizations(organizations);
    } catch (error) {
        console.error('Ошибка загрузки:', error);
        list.innerHTML = '<li>Ошибка загрузки данных. Пожалуйста, попробуйте позже.</li>';
    }
}

function blobToDataURL(blob) {
    return new Promise((resolve) => {
        const reader = new FileReader();
        reader.onload = () => resolve(reader.result);
        reader.readAsDataURL(blob);
    });
}

function renderOrganizations(organizations) {
    const list = document.getElementById('organization-list');
    list.innerHTML = '';

    if (!Array.isArray(organizations) || organizations.length === 0) {
        list.innerHTML = '<li>Информация об организациях не найдена</li>';
        return;
    }

    organizations.forEach(org => {
        const li = document.createElement('li');
        li.className = 'organization-item';
        li._orgData = org;

        const statusText = org.isActive ? 'Активно' : 'Неактивно';
        const statusClass = org.isActive ? 'status-active' : 'status-moderation';

        const headerHtml = `
                <div class="organization-header">
                    <div class="organization-status ${statusClass}">${statusText}</div>
                    <div class="card-actions">
                        <button class="edit-button" onclick="openEditModal(${JSON.stringify(org).replace(/"/g, '&quot;')})">
                            ✏️ Редактировать
                        </button>
                        <button class="delete-button" onclick="openDeleteModal('${org.id}')">
                            🗑️ Удалить
                        </button>
                    </div>
                </div>
            `;

        const imagesToShow = org.files || [];
        let imagesHtml = '';

        if (imagesToShow.length > 0) {
            imagesHtml = '<div class="images-container">';
            imagesToShow.forEach((file) => {
                if (file.dataUrl) {
                    imagesHtml += `
                            <div class="image-wrapper">
                                <img src="${file.dataUrl}"
                                     alt="Фото организации" class="organization-image"
                                     onclick="openModal('${file.dataUrl}')">
                            </div>
                        `;
                }
            });
            imagesHtml += '</div>';
        }

        const phoneHtml = org.phone ? `☎ ${org.phone}` : 'Телефон не указан';

        const addressParts = [];
        if (org.district) addressParts.push(org.district);
        if (org.city) addressParts.push(org.city);
        if (org.address) addressParts.push(org.address);
        const addressHtml = addressParts.length > 0 ? `📍 ${addressParts.join(', ')}` : '';

        li.innerHTML = `
                ${headerHtml}
                ${imagesHtml}
                <div class="organization-info">
                    <div class="organization-company">${org.company || 'Компания не указана'}</div>
                    ${addressHtml ? `<div class="organization-address">${addressHtml}</div>` : ''}
                    <div class="organization-phone">${phoneHtml}</div>
                    ${org.description ? `<div class="organization-description">${org.description}</div>` : ''}
                    ${org.categories ? formatCategories(org.categories) : ''}
                </div>
            `;

        list.appendChild(li);
    });
}

function openModal(dataUrl) {
    const modalImg = document.getElementById('modal-image');
    modalImg.src = dataUrl;
    document.getElementById('image-modal').style.display = 'flex';
}

function closeModal(modalId) {
    document.getElementById(modalId).style.display = 'none';
    if (modalId === 'delete-modal') {
        cardToDeleteId = null;
    }
}
document.addEventListener('DOMContentLoaded', () => {
    setupFileUpload();

    document.getElementById('edit-form').addEventListener('submit', (e) => {
        e.preventDefault();
        saveOrganization();
    });

    document.getElementById('delete-modal').addEventListener('click', function(e) {
        if (e.target === this) {
            closeModal('delete-modal');
        }
    });

    document.getElementById('deselect-all-subcategories').addEventListener('click', deselectAllSubcategories);

    document.getElementById('select-all-subcategories').addEventListener('click', selectAllSubcategories);

    document.getElementById('category-select').addEventListener('change', function() {
        updateSubcategorySelect(this.value);
        document.getElementById('add-category-btn').disabled = !this.value;
    });

    document.getElementById('add-category-btn').addEventListener('click', addSelectedCategory);

    document.getElementById('deselect-all-subcategories').addEventListener('click', deselectAllSubcategories);

    loadOrganizationCards();
});

document.querySelectorAll('.modal').forEach(modal => {
    modal.addEventListener('click', function(e) {
        if (e.target === this) {
            this.style.display = 'none';
        }
    });
});