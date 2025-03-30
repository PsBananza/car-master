package com.autoresourse.car_master.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class FileDto {
    private byte[] fileData;
    private String contentType;
}
