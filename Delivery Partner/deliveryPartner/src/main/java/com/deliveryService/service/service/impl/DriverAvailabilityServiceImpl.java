package com.deliveryService.service.service.impl;

import com.deliveryService.service.dto.request.ToggleAvailabilityRequest;
import com.deliveryService.service.entity.DeliveryPartner;
import com.deliveryService.service.entity.DriverAvailability;
import com.deliveryService.service.entity.Enums.PartnerStatus;
import com.deliveryService.service.exception.customexception.PartnerNotFoundException;
import com.deliveryService.service.exception.customexception.PartnerNotAvailableException;
import com.deliveryService.service.repository.DeliveryPartnerRepository;
import com.deliveryService.service.repository.DriverAvailabilityRepository;
import com.deliveryService.service.service.DriverAvailabilityService;
import com.deliveryService.service.service.EventLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DriverAvailabilityServiceImpl implements DriverAvailabilityService {

    private final DriverAvailabilityRepository availabilityRepository;
    private final DeliveryPartnerRepository partnerRepository;
    private final EventLogService eventLogService;

    /**
     * Toggle driver online/offline.
     * Only ACTIVE partners can go online.
     */
    @Override
    @Transactional
    public DriverAvailability toggleAvailability(Long driverId, ToggleAvailabilityRequest request) {

        DeliveryPartner partner = partnerRepository.findById(driverId)
                .orElseThrow(() -> new PartnerNotFoundException(driverId));

        if (partner.getStatus() != PartnerStatus.ACTIVE) {
            throw new PartnerNotAvailableException(driverId);
        }

        DriverAvailability availability = availabilityRepository.findByPartner_Id(driverId)
                .orElse(new DriverAvailability());

        availability.setPartner(partner);
        // Flip the status
        availability.setAvailable(!availability.isAvailable());

        // Update location if provided
        if (request != null && request.getLatitude() != null) {
            availability.setLatitude(request.getLatitude());
            availability.setLongitude(request.getLongitude());
        }

        DriverAvailability saved = availabilityRepository.save(availability);

        // Log to MongoDB
        String activity = saved.isAvailable() ? "WENT_ONLINE" : "WENT_OFFLINE";
        eventLogService.logDriverActivity(
                partner.getId(), partner.getName(),
                activity, null, null,
                partner.getName() + " is now " + (saved.isAvailable() ? "ONLINE" : "OFFLINE"));

        return saved;
    }

    /**
     * Get current availability status of a driver.
     */
    @Override
    public DriverAvailability getStatus(Long driverId) {
        if (!partnerRepository.existsById(driverId)) {
            throw new PartnerNotFoundException(driverId);
        }
        return availabilityRepository.findByPartner_Id(driverId)
                .orElseThrow(() -> new PartnerNotAvailableException(driverId));
    }

    /**
     * List all currently available (online) drivers — used by assignment engine.
     */
    @Override
    public List<DriverAvailability> getAvailableDrivers() {
        return availabilityRepository.findAll().stream()
                .filter(DriverAvailability::isAvailable)
                .toList();
    }
}
