package com.autoresourse.car_master.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Getter
@Setter
@Builder
public class CategoryResponse {
    private UUID id;
    private String name;
    private Boolean isSub;
}
