package com.deliveryService.service.dto.response;

import com.deliveryService.service.entity.Enums.PartnerStatus;
import com.deliveryService.service.entity.Enums.VehicleType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import java.time.Instant;

@Setter
@Getter
public class DeliveryPartnerResponseDTO {

    private Long id;

    private String name;

    private String phone;

    private VehicleType vehicleType;

    private PartnerStatus status;

    private Instant createdAt;

    private Instant updatedAt;
}
