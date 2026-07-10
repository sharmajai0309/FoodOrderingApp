package com.deliveryService.service.repository;

import com.deliveryService.service.entity.DriverAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DriverAvailabilityRepository extends JpaRepository<DriverAvailability, Long> {

    Optional<DriverAvailability> findByPartner_Id(Long partnerId);

}