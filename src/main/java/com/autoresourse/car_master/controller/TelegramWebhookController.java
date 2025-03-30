package com.autoresourse.car_master.controller;

import com.autoresourse.car_master.dto.RegistrationOrganizationRequest;
import com.autoresourse.car_master.service.card.OrganizationCardService;
import com.autoresourse.car_master.service.telegram.TelegramBotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;


@RestController
@RequestMapping("/webhook")
@RequiredArgsConstructor
public class TelegramWebhookController {

    private final TelegramBotService telegramBotService;
    private final OrganizationCardService organizationCardService;

    @PostMapping
    public ResponseEntity<String> receiveUpdate(@RequestBody Update update) {
        if (update.hasMessage()) {
            if (update.getMessage().getWebAppData() != null) {
                telegramBotService.processWebAppData(update);
            } else if (update.getMessage().hasText()) {
                telegramBotService.processTextMessage(update);
            }
        }
        return ResponseEntity.ok("OK");
    }

    @PostMapping("/save-registration")
    public ResponseEntity<String> saveRegistration(@RequestParam Map<String, String> formData,
                                                   @RequestParam("file[]") MultipartFile[] files) {

        RegistrationOrganizationRequest request = new RegistrationOrganizationRequest();

        request.setCompany(formData.get("company"));
        request.setDescription(formData.get("description"));
        request.setPhone(formData.get("phone"));
        request.setTelegramId(formData.get("telegramId"));
        request.setDistrict(formData.get("district"));
        request.setCity(formData.get("city"));
        request.setAddress(formData.get("address"));
        request.setCategories(formData.get("categories"));

        request.setFiles(files);

        organizationCardService.processCreateOrganizationCard(request);

        return ResponseEntity.ok("{\"status\": \"success\"}");
    }

}

