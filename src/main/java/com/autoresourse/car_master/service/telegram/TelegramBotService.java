package com.autoresourse.car_master.service.telegram;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface TelegramBotService {
    void processWebAppData(Update update);

    void processTextMessage(Update update);
}
