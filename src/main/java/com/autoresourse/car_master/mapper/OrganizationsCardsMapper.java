package com.autoresourse.car_master.mapper;

import com.autoresourse.car_master.dto.CategoryDto;
import com.autoresourse.car_master.dto.OrganizationCardResponse;
import com.autoresourse.car_master.dto.OrganizationsCardsResponse;
import com.autoresourse.car_master.entity.Category;
import com.autoresourse.car_master.entity.Files;
import com.autoresourse.car_master.entity.OrganizationCards;
import com.autoresourse.car_master.entity.SubCategory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Base64;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface OrganizationsCardsMapper {

    @Mapping(target = "categories", source = "categories", qualifiedByName = "mapCategories")
    @Mapping(target = "files", source = "files", qualifiedByName = "mapFiles")
    OrganizationsCardsResponse toResponse(OrganizationCards organizationCards);

    @Named("mapCategories")
    default List<CategoryDto> mapCategories(Set<Category> categories) {
        if (categories == null) {
            return null;
        }
        return categories.stream()
                .map(this::toCategoryDto)
                .collect(java.util.stream.Collectors.toList());
    }

    @Named("mapFiles")
    default List<OrganizationsCardsResponse.FileResponseDTO> mapFiles(List<Files> files) {
        if (files == null) {
            return null;
        }
        return files.stream()
                .map(this::toFileResponseDTO)
                .collect(java.util.stream.Collectors.toList());
    }

    default CategoryDto toCategoryDto(Category category) {
        if (category == null) {
            return null;
        }
        return CategoryDto.builder()
                .categoryId(category.getId())
                .name(category.getName())
                .subCategories(toSubCategoryDtoList(category.getSubCategories()))
                .build();
    }

    default List<CategoryDto.SubCategoryDto> toSubCategoryDtoList(List<SubCategory> subCategories) {
        if (subCategories == null) {
            return null;
        }
        return subCategories.stream()
                .map(this::toSubCategoryDto)
                .collect(java.util.stream.Collectors.toList());
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

