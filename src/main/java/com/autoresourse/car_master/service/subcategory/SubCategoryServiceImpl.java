package com.autoresourse.car_master.service.subcategory;

import com.autoresourse.car_master.dto.SubCategoryResponse;
import com.autoresourse.car_master.entity.SubCategory;
import com.autoresourse.car_master.mapper.SubCategoryMapper;
import com.autoresourse.car_master.repository.SubCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubCategoryServiceImpl implements SubCategoryService {

    private final SubCategoryRepository subCategoryRepository;

    @Override
    public List<SubCategoryResponse> getSubCategoriesById(UUID id) {
        List<SubCategory> categoryList = subCategoryRepository.findAllByCategoryId(id);
        return SubCategoryMapper.INSTANCE.toSubCategoryResponse(categoryList);
    }

    @Override
    public Optional<SubCategory> findByNameAndCategory(String subCategoryName, UUID categoryId) {
        return subCategoryRepository.findByNameAndCategory(subCategoryName, categoryId);
    }

    @Override
    public Boolean isSubCategoryExist(UUID categoryId) {
        return subCategoryRepository.existsByCategoryId(categoryId);
    }
}
