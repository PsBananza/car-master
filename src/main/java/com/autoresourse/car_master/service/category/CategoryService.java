package com.autoresourse.car_master.service.category;

import com.autoresourse.car_master.dto.CategoryResponse;
import com.autoresourse.car_master.entity.Category;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CategoryService {

    List<CategoryResponse> getAllCategories();

    Map<String, Object> getAllCategoriesWithSubCategories();

    Optional<Category> findByName(String categoryName);
}
