package com.autoresourse.car_master.controller;

import com.autoresourse.car_master.dto.OrganizationCardRequest;
import com.autoresourse.car_master.dto.OrganizationCardResponseDTO;
import com.autoresourse.car_master.service.card.OrganizationCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class OrganizationCardController {

    private final OrganizationCardService cardService;

    @PostMapping("/card/info")
    public List<OrganizationCardResponseDTO> getCitiesByDistrict(@RequestBody OrganizationCardRequest request) {
        return cardService.getOrganizationCardWithFiles(request);
    }
}