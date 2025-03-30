package com.autoresourse.car_master.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class RegistrationOrganizationRequest {
    private String company;
    private String description;
    private String phone;
    private String telegramId;
    private String district;
    private String city;
    private String address;
    private String categories;
    private MultipartFile[] files;
}
