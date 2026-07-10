package com.deliveryService.service.mapper;

import com.deliveryService.service.dto.request.DeliveryPartnerRegistrationRequest;
import com.deliveryService.service.dto.response.DeliveryPartnerResponseDTO;
import com.deliveryService.service.entity.DeliveryPartner;
import com.deliveryService.service.entity.Enums.PartnerStatus;
import org.springframework.stereotype.Component;

@Component
public class DeliveryPartnerMapper {

    /**
     * Maps DeliveryPartnerRegistrationRequest → DeliveryPartner entity
     */
    public DeliveryPartner toEntity(DeliveryPartnerRegistrationRequest request) {
        DeliveryPartner partner = new DeliveryPartner();
        partner.setName(request.getName());
        partner.setPhone(request.getPhone());
        partner.setVehicleType(request.getVehicleType());
        partner.setStatus(PartnerStatus.PENDING);
        return partner;
    }

    /**
     * Maps DeliveryPartner entity → DeliveryPartnerResponseDTO
     */
    public DeliveryPartnerResponseDTO toDTO(DeliveryPartner partner) {
        DeliveryPartnerResponseDTO dto = new DeliveryPartnerResponseDTO();
        dto.setId(partner.getId());
        dto.setName(partner.getName());
        dto.setPhone(partner.getPhone());
        dto.setVehicleType(partner.getVehicleType());
        dto.setStatus(partner.getStatus());
        dto.setCreatedAt(partner.getCreatedAt());
        dto.setUpdatedAt(partner.getUpdatedAt());
        return dto;
    }
}
