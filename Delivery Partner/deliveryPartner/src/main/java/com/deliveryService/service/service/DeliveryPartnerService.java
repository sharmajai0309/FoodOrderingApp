package com.deliveryService.service.service;


import com.deliveryService.service.dto.request.DeliveryPartnerRegistrationRequest;
import com.deliveryService.service.dto.response.DeliveryPartnerResponseDTO;
import jakarta.validation.Valid;

public interface DeliveryPartnerService {


    public DeliveryPartnerResponseDTO registerDriver(
            DeliveryPartnerRegistrationRequest request);

    public DeliveryPartnerResponseDTO updateRegisterDetails(final Long id, @Valid DeliveryPartnerRegistrationRequest request);
}
