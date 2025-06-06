package com.autoresourse.car_master.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
public class OrganizationCardResponse {
    private UUID id;
    private String company;
    private String phone;
    private String district;
    private String description;
    private String city;
    private String address;
    private String categories;
    private Boolean isActive;
    private List<FileResponseDTO> files;

    @Getter
    @Setter
    public static class FileResponseDTO {
        private String originalFileName;
        private String contentType;
        private UUID fileId;
        private long size;
    }
}