package com.deliveryService.service.repository.mongo;

import com.deliveryService.service.entity.mongo.DriverActivityLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DriverActivityLogRepository extends MongoRepository<DriverActivityLog, String> {

    List<DriverActivityLog> findByPartnerIdOrderByCreatedAtDesc(Long partnerId);

    List<DriverActivityLog> findByPartnerIdAndActivityTypeOrderByCreatedAtDesc(Long partnerId, String activityType);
}
