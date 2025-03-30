package com.autoresourse.car_master.service.card;

import com.autoresourse.car_master.dto.OrganizationCardRequest;
import com.autoresourse.car_master.dto.OrganizationCardResponseDTO;
import com.autoresourse.car_master.dto.RegistrationOrganizationRequest;

import java.util.List;

public interface OrganizationCardService {
    void processCreateOrganizationCard(RegistrationOrganizationRequest request);
    List<OrganizationCardResponseDTO> getOrganizationCardWithFiles(OrganizationCardRequest request);
}
