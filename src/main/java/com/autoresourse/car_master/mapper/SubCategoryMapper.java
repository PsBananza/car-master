package com.autoresourse.car_master.mapper;

import com.autoresourse.car_master.dto.SubCategoryResponse;
import com.autoresourse.car_master.entity.SubCategory;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface SubCategoryMapper {
    SubCategoryMapper INSTANCE = Mappers.getMapper(SubCategoryMapper.class);

    List<SubCategoryResponse> toSubCategoryResponse(List<SubCategory> category);
}
