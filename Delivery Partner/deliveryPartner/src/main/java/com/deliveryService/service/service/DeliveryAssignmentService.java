package com.deliveryService.service.service;

import com.deliveryService.service.dto.request.AssignOrderRequest;
import com.deliveryService.service.dto.response.DeliveryAssignmentResponseDTO;

import java.util.List;

public interface DeliveryAssignmentService {

    // Called by main food service — auto-assigns best available driver
    DeliveryAssignmentResponseDTO assignOrder(AssignOrderRequest request);

    // Driver actions
    DeliveryAssignmentResponseDTO acceptAssignment(Long assignmentId, Long driverId);
    DeliveryAssignmentResponseDTO markPickedUp(Long assignmentId, Long driverId);
    DeliveryAssignmentResponseDTO markDelivered(Long assignmentId, Long driverId);
    DeliveryAssignmentResponseDTO cancelAssignment(Long assignmentId, String reason);
    void cancelAssignmentByOrder(Long orderId, String reason);

    // Queries
    DeliveryAssignmentResponseDTO getByOrderId(Long orderId);
    List<DeliveryAssignmentResponseDTO> getDriverHistory(Long driverId);
    List<DeliveryAssignmentResponseDTO> getOpenAssignments();
    DeliveryAssignmentResponseDTO getActiveAssignment(Long driverId);
}
