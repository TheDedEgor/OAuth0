document.addEventListener('DOMContentLoaded', async () => {
    const loginContainer = document.getElementById('login-container');
    const errorContainer = document.getElementById('error-container');
    const closeAppBtn = document.getElementById('close-app-btn');
    const errorMessage = document.getElementById('error-message');

    const tg = window.Telegram.WebApp;
    const user = tg.initDataUnsafe.user;
    const uuid = tg.initDataUnsafe.start_param;

    if (!uuid) {
        // --- ЕСЛИ UUID НЕТ, ПОКАЗЫВАЕМ ОШИБКУ ---
        errorContainer.classList.add('active');

        closeAppBtn.addEventListener('click', () => {
            if (tg) {
                tg.close();
            } else {
                console.error("Telegram WebApp API не найден. Невозможно закрыть приложение.");
            }
        });

        return;
    }

    const confirmButton = document.getElementById('confirm-button');
    const cardLoader = document.getElementById('card-loader');
    const cardStatus = document.getElementById('card-status');

    const cardTitle = document.getElementById('card-title');
    const cardDesc = document.getElementById('card-description');
    const cardLogo = document.getElementById('card-logo');

    try {
        const response = await axios.get(`api/init?uuid=${uuid}`);
        const data = response.data;
        // --- ЕСЛИ UUID ЕСТЬ, ПОКАЗЫВАЕМ ОСНОВНУЮ КАРТОЧКУ ---
        loginContainer.classList.add('active');

        cardTitle.innerText = data.name;
        cardDesc.innerText = data.description;
        if (data.logoUrl) {
            cardLogo.src = data.logoUrl;
        }
        // Авторизуемся
        await auth(data.authUrl);
    } catch (err) {
        console.error(err);
        let msg = 'Во время авторизации произошла ошибка! Обратитесь к администратору!';
        if (axios.isAxiosError(err)) {
            msg = err.response?.data?.message || msg;
        }
        errorMessage.innerText = msg;
        errorContainer.classList.add('active');
    }

    async function auth(authUrl) {
        showLoadingState();

        try {
            await axios.post(authUrl, {
                id: user.id,
                uuid
            });

            showResultState('success', 'Авторизация успешна!', 'Можно закрыть данное окно.');
        } catch (err) {
            console.error(err);
            showResultState('error', 'Ошибка авторизации', 'Не удалось подтвердить вход. Попробуйте еще раз.');
        }
    }

    function showLoadingState() {
        confirmButton.style.display = 'none';
        cardLoader.style.display = 'flex';
        cardStatus.style.display = 'none';
    }

    function showResultState(type, title, message) {
        cardLoader.style.display = 'none';

        const iconSvg = type === 'success'
            ? '<svg class="status-icon success" viewBox="0 0 24 24"><path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"/></svg>'
            : '<svg class="status-icon error" viewBox="0 0 24 24"><path d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z"/></svg>';

        cardStatus.innerHTML = `
                    ${iconSvg}
                    <div class="status-title">${title}</div>
                    <div class="status-message">${message}</div>
                `;
        cardStatus.style.display = 'flex';
    }
});