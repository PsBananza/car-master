package com.autoresourse.car_master.service.category;

import com.autoresourse.car_master.dto.CategoryResponse;
import com.autoresourse.car_master.entity.Category;
import com.autoresourse.car_master.entity.SubCategory;
import com.autoresourse.car_master.mapper.CategoryMapper;
import com.autoresourse.car_master.repository.CategoryRepository;
import com.autoresourse.car_master.service.subcategory.SubCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final SubCategoryService subCategoryService;

    @Override
    public List<CategoryResponse> getAllCategories() {
        List<Category> categoryList = categoryRepository.findAll();
        return categoryList.stream().map(category -> {
            CategoryResponse categoryResponse = CategoryMapper.INSTANCE.toResponse(category);
            Boolean exist = subCategoryService.isSubCategoryExist(categoryResponse.getId());
            categoryResponse.setIsSub(exist);
            return categoryResponse;
        }).toList();
    }

    @Override
    public Map<String, Object> getAllCategoriesWithSubCategories() {
        Map<String, Object> categories = new LinkedHashMap<>();

        categoryRepository.findAllOrderByNameASC().forEach(category -> {
            if (category.getSubCategories() != null && !category.getSubCategories().isEmpty()) {
                categories.put(category.getName(), category.getSubCategories().stream()
                        .map(SubCategory::getName)
                        .toArray(String[]::new));
            } else {
                categories.put(category.getName(), new String[]{});
            }
        });

        return categories;
    }

    @Override
    public Optional<Category> findByName(String categoryName) {
        return categoryRepository.findByName(categoryName);
    }
}
