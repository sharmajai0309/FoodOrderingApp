package com.deliveryService.service.service;


import com.deliveryService.service.dto.request.DeliveryPartnerRegistrationRequest;
import com.deliveryService.service.dto.response.DeliveryPartnerResponseDTO;
import com.deliveryService.service.entity.DeliveryPartner;
import jakarta.validation.Valid;

import java.util.List;

public interface DeliveryPartnerService {


    public DeliveryPartnerResponseDTO registerDriver(
            DeliveryPartnerRegistrationRequest request);

    public DeliveryPartnerResponseDTO updateRegisterDetails(final Long id, @Valid DeliveryPartnerRegistrationRequest request);

    void approveDriver(Long driverID, String adminName);

    //    for admin
    void rejectDriver(Long driverID, String adminName, String reason);

    List<DeliveryPartner> getPendingDriverVerification();

    DeliveryPartnerResponseDTO getPartnerById(Long id);
    List<DeliveryPartnerResponseDTO> getAllPartners();
    void deletePartner(Long id);

    com.deliveryService.service.dto.response.EarningsSummaryDTO getEarningsSummary(Long id);
}
