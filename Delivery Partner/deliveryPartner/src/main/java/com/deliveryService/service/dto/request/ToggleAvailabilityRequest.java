package com.deliveryService.service.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ToggleAvailabilityRequest {
    // Optional — driver can send their current location when going online
    private Double latitude;
    private Double longitude;
}
