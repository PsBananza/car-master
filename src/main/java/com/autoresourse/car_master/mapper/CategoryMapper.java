package com.autoresourse.car_master.mapper;

import com.autoresourse.car_master.dto.CategoryResponse;
import com.autoresourse.car_master.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CategoryMapper {
    CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);

    CategoryResponse toResponse(Category category);
}
