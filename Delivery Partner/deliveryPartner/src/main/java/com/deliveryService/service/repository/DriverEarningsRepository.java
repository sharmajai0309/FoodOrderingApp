package com.deliveryService.service.repository;

import com.deliveryService.service.entity.DriverEarnings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Repository
public interface DriverEarningsRepository extends JpaRepository<DriverEarnings, Long> {

    List<DriverEarnings> findByPartner_IdOrderByEarnedAtDesc(Long partnerId);

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM DriverEarnings e WHERE e.partner.id = :partnerId")
    BigDecimal sumTotalByPartnerId(@Param("partnerId") Long partnerId);

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM DriverEarnings e WHERE e.partner.id = :partnerId AND e.earnedAt >= :from")
    BigDecimal sumByPartnerIdAndEarnedAtAfter(@Param("partnerId") Long partnerId, @Param("from") Instant from);

    long countByPartner_Id(Long partnerId);

    long countByPartner_IdAndEarnedAtAfter(Long partnerId, Instant from);
}
