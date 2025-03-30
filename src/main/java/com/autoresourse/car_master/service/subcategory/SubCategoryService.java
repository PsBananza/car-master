package com.autoresourse.car_master.service.subcategory;

import com.autoresourse.car_master.dto.SubCategoryResponse;
import com.autoresourse.car_master.entity.SubCategory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SubCategoryService {

    List<SubCategoryResponse> getSubCategoriesById(UUID id);

    Optional<SubCategory> findByNameAndCategory(String subCategoryName, UUID category);

    Boolean isSubCategoryExist(UUID categoryId);

}
