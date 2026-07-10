package com.deliveryService.service.repository;

import com.deliveryService.service.entity.DriverDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DriverDocumentRepository extends JpaRepository<DriverDocument, Long> {

//    Admin reviews driver documents

    List<DriverDocument> findByPartner_Id(Long id);

    boolean existsByPartner_IdAndDocumentTypeIgnoreCase(Long id, String documentType);




}