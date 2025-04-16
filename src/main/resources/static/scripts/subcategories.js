function loadSubCategories() {
    const catId = localStorage.getItem('categoryId');

    fetch(`/subcategories?id=${catId}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Ошибка сети');
            }
            return response.json();
        })
        .then(data => {
            console.log('Полученные данные: ', data);
            const list = document.getElementById('subcategory-list');
            list.innerHTML = '';

            if (Array.isArray(data) && data.length > 0) {
                data.forEach(subCategory => {
                    const li = document.createElement('li');
                    li.className = 'subcategory-item';

                    const nameDiv = document.createElement('div');
                    nameDiv.className = 'subcategory-name';
                    nameDiv.textContent = subCategory.name;

                    li.appendChild(nameDiv);

                    li.onclick = () => {
                        localStorage.setItem("subcategoryName", subCategory.name);
                        window.location.href = 'organization_list.html';
                    };

                    list.appendChild(li);
                });
            } else {
                list.innerHTML = '<li class="error-text">Нет подкатегорий для отображения</li>';
            }
        })
        .catch(error => {
            console.error('Ошибка загрузки подкатегорий:', error);
            document.getElementById('subcategory-list').innerHTML =
                '<li class="error-text">Ошибка загрузки данных</li>';
        });
}

document.getElementById('show-all').onclick = () => {
    localStorage.setItem("subcategoryName", "Показать все");
    window.location.href = 'organization_list.html';
};

window.onload = loadSubCategories;