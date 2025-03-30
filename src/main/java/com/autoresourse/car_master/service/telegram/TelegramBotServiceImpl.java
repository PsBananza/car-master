package com.autoresourse.car_master.service.telegram;

import com.autoresourse.car_master.client.WebAppClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
@Slf4j
@RequiredArgsConstructor
public class TelegramBotServiceImpl implements TelegramBotService {

    private final WebAppClient webAppClient;

    @Override
    public void processTextMessage(Update update) {
        Long chatId = update.getMessage().getChatId();
        Long userId = update.getMessage().getFrom().getId();
        String text = update.getMessage().getText();

        if ("/start".equals(text)) {
            webAppClient.sendWebAppButton(chatId, userId);
        } else {
            webAppClient.sendMessage(chatId, "❓ Неизвестная команда.");
        }
    }

    @Override
    public void processWebAppData(Update update) {
        Long chatId = update.getMessage().getChatId();
        String data = update.getMessage().getWebAppData().getData();

        try {
            JSONObject json = new JSONObject(data);
            String action = json.getString("action");

            if ("login".equals(action)) {
                webAppClient.sendMessage(chatId, "🔑 Вы выбрали вход в систему.");
            } else if ("register".equals(action)) {
                webAppClient.sendMessage(chatId, "📝 Вы выбрали регистрацию.");
            }
        } catch (JSONException e) {
            webAppClient.sendMessage(chatId, "❌ Ошибка обработки данных.");
        }
    }
}

