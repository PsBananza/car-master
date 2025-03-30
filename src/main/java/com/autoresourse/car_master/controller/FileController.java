package com.autoresourse.car_master.controller;

import com.autoresourse.car_master.dto.FileDto;
import com.autoresourse.car_master.dto.OrganizationCardRequest;
import com.autoresourse.car_master.dto.OrganizationCardResponseDTO;
import com.autoresourse.car_master.service.card.OrganizationCardService;
import com.autoresourse.car_master.service.file.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @GetMapping("/files/{fileId}")
    public ResponseEntity<byte[]> getFile(@PathVariable UUID fileId) {
        FileDto fileData = fileService.getFileData(fileId);
        String contentType = fileData.getContentType();

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .cacheControl(CacheControl.maxAge(10, TimeUnit.MINUTES))
                .body(fileData.getFileData());
    }
}