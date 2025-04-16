window.addEventListener('load', () => {
    const telegramId = new URLSearchParams(window.location.search).get('telegramId');
    if (telegramId) {
        localStorage.setItem("telegramId", telegramId);
        console.log("telegramId сохранен в localStorage:", telegramId);
    } else {
        console.log("telegramId не найден в URL");
    }
});

function showOrganizations() {
    window.location.href = "client_category.html";
}