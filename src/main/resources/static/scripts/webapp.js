window.onload = function() {
    const urlParams = new URLSearchParams(window.location.search);
    const telegramId = urlParams.get('telegramId');

    if (telegramId) {
        localStorage.setItem("telegramId", telegramId);
        console.log("telegramId сохранен в localStorage:", telegramId);
    } else {
        console.log("telegramId не найден в URL");
    }
};

function sendData(action) {
    if (action === 'login') {
        const telegramId = localStorage.getItem("telegramId");
        if (!telegramId) {
            alert("Не удалось найти telegramId в localStorage.");
            return;
        }
        console.log("ID пользователя для логина:", telegramId);
    }
}