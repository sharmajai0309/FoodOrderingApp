package com.deliveryService.service.mapper;

import com.deliveryService.service.entity.DeliveryPartner;
import com.deliveryService.service.dto.request.DeliveryPartnerRegistrationRequest;
import com.deliveryService.service.dto.response.DeliveryPartnerResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DeliveryPartnerMapper {

    //  Request → Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "documents", ignore = true)
    @Mapping(target = "verification", ignore = true)
    @Mapping(target = "availability", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    DeliveryPartner toEntity(DeliveryPartnerRegistrationRequest request);


    //  Entity → ResponseDTO
    DeliveryPartnerResponseDTO toDTO(DeliveryPartner entity);
}
