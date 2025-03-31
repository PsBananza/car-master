package com.autoresourse.car_master.controller;

import com.autoresourse.car_master.dto.OrganizationCardRequest;
import com.autoresourse.car_master.dto.OrganizationCardResponse;
import com.autoresourse.car_master.dto.OrganizationsCardsRequest;
import com.autoresourse.car_master.dto.OrganizationsCardsResponse;
import com.autoresourse.car_master.service.card.OrganizationCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/cards/info")
    public List<OrganizationCardResponse> getOrganizationsCards(@RequestBody OrganizationsCardsRequest request) {
        return cardService.getOrganizationsCardsWithFiles(request);
    }

    @PostMapping("/card/info")
    public List<OrganizationsCardsResponse> getOrganizationCard(@RequestBody OrganizationCardRequest request) {
        return cardService.getOrganizationCardWithFiles(request);
    }

    @PostMapping("/card/save")
    public ResponseEntity.BodyBuilder saveOrganizationCard(@RequestBody OrganizationCardRequest request) {
        return ResponseEntity.ok();
    }
}