package com.autoresourse.car_master.controller;

import com.autoresourse.car_master.dto.CategoryResponse;
import com.autoresourse.car_master.dto.SubCategoryResponse;
import com.autoresourse.car_master.service.category.CategoryService;
import com.autoresourse.car_master.service.subcategory.SubCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final SubCategoryService subCategoryService;

    @GetMapping("/categories/subcategories")
    public Map<String, Object> getCategoriesWithSubCategories() {

        return categoryService.getAllCategoriesWithSubCategories();
    }

    @GetMapping("/categories")
    public List<CategoryResponse> getCategories() {

        return categoryService.getAllCategories();
    }

    @GetMapping("/subcategories")
    public List<SubCategoryResponse> getSubCategories(@RequestParam String id) {

        return subCategoryService.getSubCategoriesById(UUID.fromString(id));
    }
}
