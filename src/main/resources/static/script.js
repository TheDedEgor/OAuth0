document.addEventListener('DOMContentLoaded', async () => {
    const loginContainer = document.getElementById('login-container');
    const errorContainer = document.getElementById('error-container');
    const closeAppBtn = document.getElementById('close-app-btn');
    const errorMessage = document.getElementById('error-message');

    const tg = window.Telegram.WebApp;
    tg.ready();
    const uuid = tg.initDataUnsafe.start_param;

    // Элементы UI
    const actionButtons = document.getElementById('action-buttons');
    const btnYes = document.getElementById('btn-yes');
    const btnNo = document.getElementById('btn-no');

    const cardLoader = document.getElementById('card-loader');
    const cardStatus = document.getElementById('card-status');
    const cardTitle = document.getElementById('card-title');
    const cardDesc = document.getElementById('card-description');
    const cardLogo = document.getElementById('card-logo');

    // Общие заголовки для запросов
    const headers = {
        'X-Telegram-Init': tg.initData
    };

    // Проверка UUID
    if (!uuid) {
        showErrorState('Не удалось запустить приложение. Отсутствует обязательный параметр для аутентификации. Пожалуйста, попробуйте войти снова.');
        return;
    }

    // Закрытие приложения (для карточки ошибки)
    closeAppBtn.addEventListener('click', () => {
        tg.close();
    });

    // Настраиваем поведение кнопки "Да"
    btnYes.addEventListener('click', () => confirmAction(true));

    // Настраиваем поведение кнопки "Нет"
    btnNo.addEventListener('click', () => confirmAction(false));

    try {
        // Получение данных о сервисе
        const response = await axios.get(`api/webapp/link`, {headers});
        const data = response.data;

        // Показываем карточку
        loginContainer.classList.add('active');

        cardTitle.innerText = data.name;
        cardDesc.innerText = data.description;
        if (data.logoUrl) {
            cardLogo.src = data.logoUrl;
        }

        const sessionStatus = data.sessionStatus;

        // --- ЛОГИКА ВЕТВЛЕНИЯ ---

        if (sessionStatus === 'AWAITING_CONFIRMATION') {
            // ВЕТКА 1: Нужен ручной ввод
            console.log('Статус: Ожидание подтверждения. Показываем кнопки.');

            // Показываем кнопки для взаимодействия
            resetInitialState();
        } else {
            // ВЕТКА 2: Подтверждение не нужно
            console.log('Статус: Подтверждение не требуется. Автоматическая авторизация.');

            // Прячем кнопки и сразу запускаем процесс
            showLoadingState();

            try {
                await performAuthorization();
            } catch (err) {
                console.error(err);
                handleRequestError(err);
            }
        }

    } catch (err) {
        console.error(err);
        let msg = 'Во время инициализации произошла ошибка! Обратитесь к администратору!';
        if (axios.isAxiosError(err)) {
            msg = err.response?.data?.message || msg;
        }
        showErrorState(msg);
    }

    // --- ФУНКЦИИ API ---

    // Функция подтверждения (отдельный запрос)
    async function confirmRequest(isConfirmed) {
        console.log('Отправляем запрос подтверждения...');
        // Путь к API может отличаться, я использую логичное название
        const response = await axios.post('api/webapp/confirm', null, {
            headers,
            params: {
                isConfirmed
            }
        });
        return response.data;
    }

    // Функция авторизации (основной вход)
    async function performAuthorization() {
        console.log('Отправляем запрос авторизации...');
        await axios.post('api/webapp/auth', null, {headers});
        showResultState('success', 'Аутентификация успешна!', 'Можно закрыть данное окно.');
    }

    // --- UI ФУНКЦИИ ---

    async function confirmAction(isConfirmed) {
        // Блокируем кнопки, чтобы не нажали много раз
        btnYes.disabled = true;
        btnNo.disabled = true;

        try {
            // Подтверждение действия
            const status = await confirmRequest(isConfirmed);
            if (status === 'CONFIRMED') {
                // Если подтверждение ок, сразу аутентифицируемся
                showLoadingState();
                await performAuthorization();
            } else {
                tg.close();
            }
        } catch (err) {
            console.error(err);
            // При любой ошибке разблокируем кнопки, чтобы можно было попробовать снова
            btnYes.disabled = false;
            btnNo.disabled = false;
            handleRequestError(err);
        }
    }

    function handleRequestError(err) {
        let msg = 'Ошибка запроса.';
        if (axios.isAxiosError(err)) {
            // Пытаемся взять сообщение об ошибке от сервера
            msg = err.response?.data?.message || 'Не удалось выполнить операцию. Попробуйте еще раз.';
        }
        showResultState('error', 'Ошибка', msg);
    }

    function resetInitialState() {
        cardLoader.style.display = 'none';
        cardStatus.style.display = 'none';

        actionButtons.style.display = 'flex';
        btnYes.disabled = false;
        btnNo.disabled = false;
    }

    function showLoadingState() {
        actionButtons.style.display = 'none';
        cardLoader.style.display = 'flex';
        cardStatus.style.display = 'none';
    }

    function showResultState(type, title, message) {
        cardLoader.style.display = 'none';
        actionButtons.style.display = 'none';

        const iconSvg = type === 'success'
            ? '<svg class="status-icon success" viewBox="0 0 24 24"><path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"/></svg>'
            : '<svg class="status-icon error" viewBox="0 0 24 24"><path d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z"/></svg>';

        // Генерируем HTML кнопки только для успеха
        let closeButtonHtml = '';
        if (type === 'success') {
            closeButtonHtml = `<button class="status-close-btn" id="final-close-btn">Закрыть</button>`;
        }

        cardStatus.innerHTML = `
            ${iconSvg}
            <div class="status-title">${title}</div>
            <div class="status-message">${message}</div>
            ${closeButtonHtml}
        `;
        cardStatus.style.display = 'flex';

        // Если кнопка была добавлена, вешаем на неё обработчик
        if (type === 'success') {
            const finalCloseBtn = document.getElementById('final-close-btn');
            if (finalCloseBtn) {
                finalCloseBtn.addEventListener('click', () => {
                    tg.close();
                });
            }
        }
    }

    function showErrorState(msg) {
        errorMessage.innerText = msg;
        errorContainer.classList.add('active');
    }
});