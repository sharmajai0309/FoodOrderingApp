package com.deliveryService.service.repository;

import com.deliveryService.service.entity.DeliveryPartner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeliveryPartnerRepository extends JpaRepository<DeliveryPartner, Long> {

//    Register driver
//    Find driver
//    Check phone duplicates
    Optional<DeliveryPartner> findByPhone(String phone);

}