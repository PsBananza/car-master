package com.autoresourse.car_master.mapper;

import com.autoresourse.car_master.dto.CategoryDto;
import com.autoresourse.car_master.dto.OrganizationsCardsResponse;
import com.autoresourse.car_master.entity.Category;
import com.autoresourse.car_master.entity.Files;
import com.autoresourse.car_master.entity.OrganizationCards;
import com.autoresourse.car_master.entity.SubCategory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface OrganizationsCardsMapper {

    @Mapping(target = "categories", source = "organizationCards", qualifiedByName = "mapCardCategories")
    @Mapping(target = "files", source = "files", qualifiedByName = "mapFiles")
    OrganizationsCardsResponse toResponse(OrganizationCards organizationCards);

    @Named("mapCardCategories")
    default List<CategoryDto> mapCardCategories(OrganizationCards organizationCards) {
        if (organizationCards.getCategories() == null) {
            return Collections.emptyList();
        }

        return organizationCards.getCategories().stream()
                .map(category -> toCategoryDto(category, organizationCards.getSubCategories()))
                .toList();
    }

    default CategoryDto toCategoryDto(Category category, Set<SubCategory> cardSubCategories) {
        if (category == null) {
            return null;
        }

        // Фильтруем подкатегории, оставляя только те, которые есть в карточке
        List<SubCategory> filteredSubCategories = category.getSubCategories().stream()
                .filter(subCat -> cardSubCategories.stream()
                        .anyMatch(cardSubCat -> cardSubCat.getId().equals(subCat.getId())))
                .toList();

        return CategoryDto.builder()
                .categoryId(category.getId())
                .name(category.getName())
                .subCategories(toSubCategoryDtoList(filteredSubCategories))
                .build();
    }

    @Named("mapFiles")
    default List<OrganizationsCardsResponse.FileResponseDTO> mapFiles(List<Files> files) {
        if (files == null) {
            return Collections.emptyList();
        }
        return files.stream()
                .map(this::toFileResponseDTO)
                .toList();
    }

    default List<CategoryDto.SubCategoryDto> toSubCategoryDtoList(List<SubCategory> subCategories) {
        if (subCategories == null) {
            return Collections.emptyList();
        }
        return subCategories.stream()
                .map(this::toSubCategoryDto)
                .toList();
    }

    default CategoryDto.SubCategoryDto toSubCategoryDto(SubCategory subCategory) {
        if (subCategory == null) {
            return null;
        }
        return CategoryDto.SubCategoryDto.builder()
                .subCategoryId(subCategory.getId())
                .name(subCategory.getName())
                .build();
    }

    default OrganizationsCardsResponse.FileResponseDTO toFileResponseDTO(Files file) {
        if (file == null) {
            return null;
        }
        return OrganizationsCardsResponse.FileResponseDTO.builder()
                .originalFileName(file.getOriginalFileName())
                .contentType(file.getContentType())
                .fileId(file.getId())
                .size(file.getSize())
                .build();
    }

}

