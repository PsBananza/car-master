package com.autoresourse.car_master.service.file;

import com.autoresourse.car_master.dto.FileDto;
import com.autoresourse.car_master.entity.OrganizationCards;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface FileService {

    void uploadFile(MultipartFile files, OrganizationCards card);

    FileDto getFileData(UUID fileId);

    void deleteFile(UUID uuid);
}
