package com.autoresourse.car_master.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
public class CategoryDto {
    private UUID categoryId;
    private String name;
    private List<SubCategoryDto> subCategories;

    @Getter
    @Setter
    @Builder
    public static class SubCategoryDto {
        private UUID subCategoryId;
        private String name;
    }
}
