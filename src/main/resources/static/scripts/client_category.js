function loadCategories() {
    fetch('/categories')
        .then(response => {
            if (!response.ok) {
                throw new Error('Ошибка сети');
            }
            return response.json();
        })
        .then(data => {
            const list = document.getElementById('category-list');
            list.innerHTML = '';

            if (data.length === 0) {
                list.innerHTML = '<li class="error-text">Нет доступных категорий</li>';
                return;
            }

            data.forEach(category => {
                const li = document.createElement('li');
                li.className = 'category-item';

                const nameDiv = document.createElement('div');
                nameDiv.className = 'category-name';
                nameDiv.textContent = category.name;

                li.appendChild(nameDiv);

                li.onclick = () => {
                    localStorage.setItem("categoryId", category.id);
                    if (category.isSub) {
                        window.location.href = 'subcategories.html';
                    } else {
                        window.location.href = 'organization_list.html';
                    }
                };

                list.appendChild(li);
            });
        })
        .catch(error => {
            console.error('Ошибка загрузки категорий:', error);
            document.getElementById('category-list').innerHTML =
                '<li class="error-text">Ошибка загрузки данных</li>';
        });
}

window.onload = loadCategories;