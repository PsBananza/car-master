package com.autoresourse.car_master.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class OrganizationCardRequest {
    private String city;
    private String category;
    private String subcategory;
}