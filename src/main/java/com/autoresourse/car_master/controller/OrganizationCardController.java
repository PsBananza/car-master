package com.autoresourse.car_master.controller;

import com.autoresourse.car_master.dto.*;
import com.autoresourse.car_master.service.card.OrganizationCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class OrganizationCardController {

    private final OrganizationCardService cardService;

    @PostMapping("/cards/info")
    public List<OrganizationCardResponse> getOrganizationsCards(@RequestBody OrganizationsCardsRequest request) {
        return cardService.getOrganizationsCardsWithFiles(request);
    }

    @PostMapping("/card/info")
    public List<OrganizationsCardsResponse> getOrganizationCard(@RequestBody OrganizationCardRequest request) {
        return cardService.getOrganizationCardWithFiles(request);
    }

    @PostMapping(value = "/card/update",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateCard(
            @RequestPart("id") String id,
            @RequestPart("company") String company,
            @RequestPart(value = "phone", required = false) String phone,
            @RequestPart(value = "district", required = false) String district,
            @RequestPart(value = "city", required = false) String city,
            @RequestPart(value = "address", required = false) String address,
            @RequestPart(value = "description", required = false) String description,
            @RequestPart(value = "filesToDelete", required = false) List<String> filesToDelete,
            @RequestPart(value = "newFiles", required = false) List<MultipartFile> newFiles,
            @RequestPart(value = "categories", required = false) String categoriesJson) {

        try {
            UpdateCardDto updateDto = UpdateCardDto.builder()
                    .id(UUID.fromString(id))
                    .company(company)
                    .phone(phone)
                    .district(district)
                    .city(city)
                    .address(address)
                    .description(description)
                    .filesToDelete(filesToDelete)
                    .newFiles(newFiles)
                    .categoriesJson(categoriesJson)
                    .build();

            OrganizationsCardsResponse updatedCard = cardService.updateCard(updateDto);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "card", updatedCard
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    @DeleteMapping("/card/remove")
    public ResponseEntity<String> deleteCard(@RequestParam("id") String id, @RequestParam("telegramId") String telegramId) {
        cardService.deleteCard(id, telegramId);
        return ResponseEntity.ok("{\"status\": \"success\"}");
    }
}