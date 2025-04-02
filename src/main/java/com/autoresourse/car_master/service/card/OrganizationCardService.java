package com.autoresourse.car_master.service.card;

import com.autoresourse.car_master.dto.*;

import java.util.List;

public interface OrganizationCardService {
    void processCreateOrganizationCard(RegistrationOrganizationRequest request);
    List<OrganizationCardResponse> getOrganizationsCardsWithFiles(OrganizationsCardsRequest request);

    List<OrganizationsCardsResponse> getOrganizationCardWithFiles(OrganizationCardRequest request);

    OrganizationsCardsResponse updateCard(UpdateCardDto updateDto);

    void deleteCard(String id, String telegramId);
}
