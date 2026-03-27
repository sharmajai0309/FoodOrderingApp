package com.deliveryService.service.repository;

import com.deliveryService.service.entity.DriverAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DriverAvailabilityRepository extends JpaRepository<DriverAvailability, Long> {
}