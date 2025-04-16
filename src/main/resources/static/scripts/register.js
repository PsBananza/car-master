// Обновление счетчика символов
document.getElementById('description').addEventListener('input', function() {
    document.getElementById('char-count').textContent = this.value.length;
});

// Закрытие клавиатуры при тапе вне поля ввода
document.addEventListener('click', function(e) {
    if (!e.target.closest('input') && !e.target.closest('textarea') && !e.target.closest('select')) {
        document.activeElement.blur();
    }
});
document.getElementById('phone').addEventListener('input', function(e) {
    // Удаляем все нецифровые символы
    let phone = this.value.replace(/\D/g, '');

    // Ограничиваем длину номера (10 цифр)
    if (phone.length > 10) {
        phone = phone.substring(0, 10);
        return;
    }

    // Форматируем номер с пробелами
    let formatted = '';
    if (phone.length > 0) {
        formatted = phone.substring(0, 3);
        if (phone.length > 3) {
            formatted += ' ' + phone.substring(3, 6);
        }
        if (phone.length > 6) {
            formatted += ' ' + phone.substring(6, 8);
        }
        if (phone.length > 8) {
            formatted += ' ' + phone.substring(8, 10);
        }
    }

    // Обновляем значение поля
    this.value = formatted;
});

// Обработчики для всех полей ввода
const inputs = document.querySelectorAll('input, textarea, select');
inputs.forEach(input => {
    input.addEventListener('blur', function() {
        // Плавная прокрутка к элементу после закрытия клавиатуры
        setTimeout(() => {
            this.scrollIntoView({behavior: 'smooth', block: 'center'});
        }, 100);
    });
});

function updateCities() {
    const district = document.getElementById('district').value;
    if (!district) return;

    fetch(`/cities?district=${encodeURIComponent(district)}`)
        .then(response => response.json())
        .then(data => {
            const citySelect = document.getElementById('city');
            citySelect.innerHTML = '<option value="">Выберите город</option>';

            if (data.cities && data.cities.length > 0) {
                data.cities.forEach(city => {
                    const option = document.createElement('option');
                    option.value = city;
                    option.textContent = city;
                    citySelect.appendChild(option);
                });
            }
        })
        .catch(error => console.error('Ошибка загрузки городов:', error));
}

document.getElementById('submit-btn').addEventListener('click', function() {
    // Скрываем клавиатуру перед валидацией
    document.activeElement.blur();

    setTimeout(() => {
        continueRegistration();
    }, 300); // Даем время для закрытия клавиатуры
});

function continueRegistration() {
    const company = document.getElementById("company").value.trim();
    const description = document.getElementById("description").value.trim();
    const district = document.getElementById("district").value;
    const city = document.getElementById("city").value;
    const address = document.getElementById("address").value.trim();
    const phone = document.getElementById("phone").value.replace(/\D/g, '');

    if (phone.length !== 10) {
        alert("Пожалуйста, введите полный номер телефона (10 цифр после +7)");
        document.getElementById("phone").scrollIntoView({behavior: 'smooth', block: 'center'});
        return;
    }

    if (!company || !description || !district || !city || !address || !phone) {
        alert("Пожалуйста, заполните все обязательные поля!");
        // Прокрутка к первому незаполненному полю
        if (!company) document.getElementById("company").scrollIntoView({behavior: 'smooth', block: 'center'});
        else if (!description) document.getElementById("description").scrollIntoView({behavior: 'smooth', block: 'center'});
        else if (!district) document.getElementById("district").scrollIntoView({behavior: 'smooth', block: 'center'});
        else if (!city) document.getElementById("city").scrollIntoView({behavior: 'smooth', block: 'center'});
        else if (!address) document.getElementById("address").scrollIntoView({behavior: 'smooth', block: 'center'});
        else if (!phone) document.getElementById("phone").scrollIntoView({behavior: 'smooth', block: 'center'});
        return;
    }

    if (description.length > 300) {
        alert("Описание не должно превышать 300 символов!");
        document.getElementById("description").scrollIntoView({behavior: 'smooth', block: 'center'});
        return;
    }

    // Сохраняем данные и переходим дальше
    localStorage.setItem("company", company);
    localStorage.setItem("description", description);
    localStorage.setItem("district", district);
    localStorage.setItem("city", city);
    localStorage.setItem("address", address);
    localStorage.setItem("phone", "+7" + phone);

    window.location.href = "categories.html";
}