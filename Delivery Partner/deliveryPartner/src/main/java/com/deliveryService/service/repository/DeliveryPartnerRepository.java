package com.deliveryService.service.repository;

import com.deliveryService.service.entity.DeliveryPartner;
import com.deliveryService.service.entity.Enums.PartnerStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryPartnerRepository extends JpaRepository<DeliveryPartner, Long> {

//    Register driver
//    Find driver
//    Check phone duplicates

    @EntityGraph(attributePaths = {"documents", "verification", "availability"})
    @Query("""
       SELECT dp FROM DeliveryPartner dp
       WHERE dp.phone = :phone
       """)
    Optional<DeliveryPartner> findByPhone(@Param("phone") String phone);
    

    @EntityGraph(attributePaths = {"documents", "verification", "availability"})
    @Query("SELECT dp FROM DeliveryPartner dp WHERE dp.status = :status")
    List<DeliveryPartner> findByStatus(@Param("status") PartnerStatus status);


    @Query("""
            SELECT dp FROM DeliveryPartner dp
            LEFT JOIN FETCH dp.documents
            WHERE dp.status = :status
            """)
    List<DeliveryPartner> findByStatusWithDocuments(@Param("status") PartnerStatus status);

}

