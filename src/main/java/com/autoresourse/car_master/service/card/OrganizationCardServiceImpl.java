package com.autoresourse.car_master.service.card;

import com.autoresourse.car_master.dto.OrganizationCardRequest;
import com.autoresourse.car_master.dto.OrganizationCardResponseDTO;
import com.autoresourse.car_master.dto.RegistrationOrganizationRequest;
import com.autoresourse.car_master.entity.Category;
import com.autoresourse.car_master.entity.OrganizationCards;
import com.autoresourse.car_master.entity.SubCategory;
import com.autoresourse.car_master.entity.Users;
import com.autoresourse.car_master.mapper.OrganizationCardMapper;
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

@Service
@RequiredArgsConstructor
public class OrganizationCardServiceImpl implements OrganizationCardService {
    private final OrganizationCardRepository organizationCardRepository;
    private final OrganizationCardMapper organizationCardMapper;
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
    public List<OrganizationCardResponseDTO> getOrganizationCardWithFiles(OrganizationCardRequest request) {
        List<OrganizationCards> organizationCards;
        if (request.getSubcategory() != null && !request.getSubcategory().equals("Показать все")) {
            organizationCards = organizationCardRepository.findBySubCategoriesName(request.getSubcategory(), request.getCity());
        } else {
            organizationCards = organizationCardRepository.findByCategoryId(UUID.fromString(request.getCategory()), request.getCity());

        }
        return organizationCardMapper.toDtoList(organizationCards);
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
}
