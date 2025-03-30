package com.autoresourse.car_master.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebAppClient {

    private final RestTemplate restTemplate;

    @Value("${telegrambots.bot.token}")
    private String tokenApi;

    @Value("${webApp.url}")
    private String webAppUrl;

    private final String TELEGRAM_URL = "https://api.telegram.org/bot";
    private final String TELEGRAM_METHOD = "/sendMessage";

    public void sendMessage(Long chatId, String text) {
        Map<String, Object> request = Map.of("chat_id", chatId, "text", text);
        restTemplate.postForObject(TELEGRAM_URL + tokenApi + TELEGRAM_METHOD, request, String.class);
    }

    public void sendWebAppButton(Long chatId, Long userId) {

        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "CustomUserAgent/1.0");
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> request = getStringObjectMap(chatId, userId);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(request, headers);

        restTemplate.postForObject(TELEGRAM_URL + tokenApi + TELEGRAM_METHOD, requestEntity, String.class);
    }

    private Map<String, Object> getStringObjectMap(Long chatId, Long userId) {
        String urlWithTelegramId = webAppUrl + "/main.html?telegramId=" + userId;

        Map<String, Object> webApp = Map.of("url", urlWithTelegramId);
        Map<String, Object> keyboardButton = Map.of("text", "Открыть Web App", "web_app", webApp);
        Map<String, Object> keyboard = Map.of("keyboard", List.of(List.of(keyboardButton)), "resize_keyboard", true);

        return Map.of(
                "chat_id", chatId,
                "text", "👋 Привет! Нажмите кнопку, чтобы открыть Web App.",
                "reply_markup", keyboard
        );
    }
}
