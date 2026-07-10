package com.deliveryService.service.service;

import com.deliveryService.service.dto.request.ToggleAvailabilityRequest;
import com.deliveryService.service.entity.DriverAvailability;

import java.util.List;

public interface DriverAvailabilityService {

    DriverAvailability toggleAvailability(Long driverId, ToggleAvailabilityRequest request);

    DriverAvailability getStatus(Long driverId);

    List<DriverAvailability> getAvailableDrivers();
}
