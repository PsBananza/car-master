package com.autoresourse.car_master.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class UpdateCardDto {
    private UUID id;
    private String company;
    private String phone;
    private String district;
    private String city;
    private String address;
    private String description;

    // Список ID файлов для удаления (в виде строк)
    private List<String> filesToDelete;

    // Новые загружаемые файлы
    private List<MultipartFile> newFiles;

    // Категории в формате JSON строки
    private String categoriesJson;

    // Внутренний DTO для категорий
    @Data
    @Builder
    public static class CategoryDto {
        private String name;
        private List<SubCategoryDto> subCategories;
    }

    @Data
    @Builder
    public static class SubCategoryDto {
        private String name;
    }
}
