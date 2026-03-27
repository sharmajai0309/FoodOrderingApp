package com.deliveryService.service.service;


import com.deliveryService.service.dto.request.DeliveryPartnerRegistrationRequest;
import com.deliveryService.service.dto.response.DeliveryPartnerResponseDTO;

public interface DeliveryPartnerService {


    public DeliveryPartnerResponseDTO registerDriver(
            DeliveryPartnerRegistrationRequest request);

}
