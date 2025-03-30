package com.autoresourse.car_master.mapper;

import com.autoresourse.car_master.dto.FileDto;
import com.autoresourse.car_master.entity.Files;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FileMapper {

    FileDto toDto(Files entity);
}

