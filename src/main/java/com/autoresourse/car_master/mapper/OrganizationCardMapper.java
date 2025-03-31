package com.autoresourse.car_master.mapper;

import com.autoresourse.car_master.dto.OrganizationCardResponse;
import com.autoresourse.car_master.entity.Category;
import com.autoresourse.car_master.entity.Files;
import com.autoresourse.car_master.entity.OrganizationCards;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.Named;
import java.util.Base64;

@Mapper(componentModel = "spring")
public interface OrganizationCardMapper {

    @Mapping(target = "categories", expression = "java(mapCategories(entity.getCategories()))")
    @Mapping(target = "files", source = "files")
    OrganizationCardResponse toDto(OrganizationCards entity);

    List<OrganizationCardResponse> toDtoList(List<OrganizationCards> entities);

//    @Mapping(target = "byte64", source = "fileData", qualifiedByName = "encodeBase64")
    @Mapping(target = "fileId", source = "id")
    OrganizationCardResponse.FileResponseDTO toDto(Files file);

    default List<OrganizationCardResponse.FileResponseDTO> mapFiles(List<Files> files) {
        return files.stream().map(this::toDto).collect(Collectors.toList());
    }

    default String mapCategories(Set<Category> categories) {
        return categories.stream()
                .map(Category::getName)
                .collect(Collectors.joining(", "));
    }

    @Named("encodeBase64")
    static String encodeBase64(byte[] fileData) {
        return fileData != null ? Base64.getEncoder().encodeToString(fileData) : null;
    }
}

