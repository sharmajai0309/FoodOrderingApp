package com.deliveryService.service.repository;

import com.deliveryService.service.entity.DriverVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DriverVerificationRepository extends JpaRepository<DriverVerification, Long> {

    Optional<DriverVerification> findByPartner_Id(Long partnerId);

}