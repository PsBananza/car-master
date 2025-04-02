package com.autoresourse.car_master.service.card;

import com.autoresourse.car_master.dto.*;
import com.autoresourse.car_master.entity.Category;
import com.autoresourse.car_master.entity.OrganizationCards;
import com.autoresourse.car_master.entity.SubCategory;
import com.autoresourse.car_master.entity.Users;
import com.autoresourse.car_master.mapper.OrganizationCardMapper;
import com.autoresourse.car_master.mapper.OrganizationsCardsMapper;
import com.autoresourse.car_master.repository.OrganizationCardRepository;
import com.autoresourse.car_master.service.category.CategoryService;
import com.autoresourse.car_master.service.file.FileService;
import com.autoresourse.car_master.service.subcategory.SubCategoryService;
import com.autoresourse.car_master.service.user.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrganizationCardServiceImpl implements OrganizationCardService {
    private final OrganizationCardRepository organizationCardRepository;
    private final OrganizationCardMapper organizationCardMapper;
    private final OrganizationsCardsMapper organizationsCardsMapper;
    private final UserService userService;
    private final FileService fileService;
    private final CategoryService categoryService;
    private final SubCategoryService subCategoryService;

    @Override
    @Transactional
    public void processCreateOrganizationCard(RegistrationOrganizationRequest request) {
        Users user = userService.findUserOrCreate(Long.valueOf(request.getTelegramId()));

        OrganizationCards card = OrganizationCards.builder()
                .user(user)
                .company(request.getCompany())
                .description(request.getDescription())
                .phone(request.getPhone())
                .district(request.getDistrict())
                .city(request.getCity())
                .address(request.getAddress())
                .isActive(Boolean.FALSE)
                .categories(new HashSet<>())
                .subCategories(new HashSet<>())
                .build();

        Map<String, List<String>> categoriesMap = parseCategories(request.getCategories());

        categoriesMap.forEach((categoryName, subCategoryNames) -> {
            Category category = categoryService.findByName(categoryName)
                    .orElseThrow(() -> new RuntimeException("Category not found: " + categoryName));

            category.getOrganizations().add(card);
            card.getCategories().add(category);

            for (String subCategoryName : subCategoryNames) {
                SubCategory subCategory = subCategoryService.findByNameAndCategory(subCategoryName, category.getId())
                        .orElseThrow(() -> new RuntimeException("SubCategory not found: " + subCategoryName));

                subCategory.getOrganizations().add(card);
                card.getSubCategories().add(subCategory);
            }
        });

        organizationCardRepository.save(card);

        Arrays.stream(request.getFiles()).forEach(x -> fileService.uploadFile(x, card));
    }

    @Override
    public List<OrganizationCardResponse> getOrganizationsCardsWithFiles(OrganizationsCardsRequest request) {
        List<OrganizationCards> organizationCards;
        if (request.getSubcategory() != null && !request.getSubcategory().equals("Показать все")) {
            organizationCards = organizationCardRepository.findBySubCategoriesName(request.getSubcategory(), request.getCity());
        } else {
            organizationCards = organizationCardRepository.findByCategoryId(UUID.fromString(request.getCategory()), request.getCity());

        }
        return organizationCardMapper.toDtoList(organizationCards);
    }

    @Override
    public List<OrganizationsCardsResponse> getOrganizationCardWithFiles(OrganizationCardRequest request) {
        List<OrganizationCards> organizationCards = organizationCardRepository.findByTelegramId(request.getTelegramId());
        return organizationCards.stream().map(organizationsCardsMapper::toResponse).toList();
    }

    private Map<String, List<String>> parseCategories(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(json, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Ошибка при обработке JSON с категориями", e);
        }
    }

    @Override
    @Transactional
    public OrganizationsCardsResponse updateCard(UpdateCardDto updateDto) {
        OrganizationCards card = organizationCardRepository.findById(updateDto.getId())
                .orElseThrow(() -> new RuntimeException("Карточка не найдена с ID: " + updateDto.getId()));

        card.setCompany(updateDto.getCompany());
        card.setPhone(updateDto.getPhone());
        card.setDistrict(updateDto.getDistrict());
        card.setCity(updateDto.getCity());
        card.setAddress(updateDto.getAddress());
        card.setDescription(updateDto.getDescription());

        if (updateDto.getFilesToDelete() != null && !updateDto.getFilesToDelete().isEmpty()) {
            updateDto.getFilesToDelete().forEach(fileId -> {
                try {
                    fileService.deleteFile(UUID.fromString(fileId));
                    card.getFiles().removeIf(file -> file.getId().equals(UUID.fromString(fileId)));
                } catch (Exception e) {
                    throw new RuntimeException("Ошибка при удалении файла: " + fileId, e);
                }
            });
        }

        if (updateDto.getNewFiles() != null && !updateDto.getNewFiles().isEmpty()) {
            updateDto.getNewFiles()
                    .forEach(file -> fileService.uploadFile(file, card));
        }

        if (updateDto.getCategoriesJson() != null && !updateDto.getCategoriesJson().isEmpty()) {
            updateCategories(card, updateDto.getCategoriesJson());
        }

        OrganizationCards updatedCard = organizationCardRepository.save(card);
        return organizationsCardsMapper.toResponse(updatedCard);
    }

    @Override
    public void deleteCard(String id, String telegramId) {
        OrganizationCards organizationCard = organizationCardRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new RuntimeException("Карточка не найдена"));
        if (!organizationCard.getUser().getId().equals(UUID.fromString(telegramId))) {
            throw new RuntimeException("Нет прав на удаление карточки");
        }
        organizationCardRepository.delete(organizationCard);
    }

    private void updateCategories(OrganizationCards card, String categoriesJson) {
        card.getCategories().forEach(category -> category.getOrganizations().remove(card));
        card.getSubCategories().forEach(subCategory -> subCategory.getOrganizations().remove(card));
        card.getCategories().clear();
        card.getSubCategories().clear();

        Map<String, List<String>> categoriesMap = parseUpdateCategories(categoriesJson);

        categoriesMap.forEach((categoryName, subCategoryNames) -> {
            Category category = categoryService.findByName(categoryName)
                    .orElseThrow(() -> new RuntimeException("Категория не найдена: " + categoryName));

            if (!subCategoryNames.isEmpty()) {
                category.getOrganizations().add(card);
                card.getCategories().add(category);

                subCategoryNames.forEach(subCategoryName -> {
                    SubCategory subCategory = subCategoryService.findByNameAndCategory(subCategoryName, category.getId())
                            .orElseThrow(() -> new RuntimeException("Подкатегория не найдена: " + subCategoryName));

                    subCategory.getOrganizations().add(card);
                    card.getSubCategories().add(subCategory);
                });
            }
        });
    }

    private Map<String, List<String>> parseUpdateCategories(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            List<Map<String, Object>> categories = objectMapper.readValue(json, new TypeReference<>() {
            });

            Map<String, List<String>> result = new HashMap<>();

            for (Map<String, Object> category : categories) {
                String categoryName = (String) category.get("name");
                List<Map<String, String>> subCategories = (List<Map<String, String>>) category.get("subCategories");

                List<String> subCategoryNames = subCategories.stream()
                        .map(subCat -> subCat.get("name"))
                        .toList();

                result.put(categoryName, subCategoryNames);
            }

            return result;
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при обработке JSON с категориями: " + json, e);
        }
    }
}
