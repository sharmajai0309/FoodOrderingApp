package com.deliveryService.service.controller;

import com.deliveryService.service.api_output.ApiResponse;
import com.deliveryService.service.dto.request.AssignOrderRequest;
import com.deliveryService.service.dto.response.DeliveryAssignmentResponseDTO;
import com.deliveryService.service.service.DeliveryAssignmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/delivery")
@RequiredArgsConstructor
public class DeliveryAssignmentController {
    private final DeliveryAssignmentService assignmentService;

    /**
     * Get all open/pending assignments for drivers to view and accept.
     * GET /api/delivery/open
     */
    @GetMapping("/open")
    public ResponseEntity<ApiResponse<List<DeliveryAssignmentResponseDTO>>> getOpenAssignments() {
        return ResponseEntity.ok(ApiResponse.success(
                assignmentService.getOpenAssignments(), "Fetched available assignments"));
    }

    /**
     * Assign an order to an available driver.
     * Called by the main food ordering service.
     * POST /api/delivery/assign
     */
    @PostMapping("/assign")
    public ResponseEntity<ApiResponse<DeliveryAssignmentResponseDTO>> assignOrder(
            @Valid @RequestBody AssignOrderRequest request) {
        DeliveryAssignmentResponseDTO dto = assignmentService.assignOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(dto, "Order assigned to driver: " + dto.getDriverName()));
    }

    /**
     * Driver accepts the assignment.
     * POST /api/delivery/{id}/accept?driverId=1
     */
    @PostMapping("/{id}/accept")
    public ResponseEntity<ApiResponse<DeliveryAssignmentResponseDTO>> accept(
            @PathVariable Long id,
            @RequestParam Long driverId) {
        return ResponseEntity.ok(ApiResponse.success(
                assignmentService.acceptAssignment(id, driverId), "Assignment accepted"));
    }

    /**
     * Driver picks up the order from restaurant.
     * POST /api/delivery/{id}/pickup?driverId=1
     */
    @PostMapping("/{id}/pickup")
    public ResponseEntity<ApiResponse<DeliveryAssignmentResponseDTO>> pickup(
            @PathVariable Long id,
            @RequestParam Long driverId) {
        return ResponseEntity.ok(ApiResponse.success(
                assignmentService.markPickedUp(id, driverId), "Order picked up from restaurant"));
    }

    /**
     * Driver marks order as delivered to customer.
     * POST /api/delivery/{id}/deliver?driverId=1
     * Auto-credits ₹30 earnings.
     */
    @PostMapping("/{id}/deliver")
    public ResponseEntity<ApiResponse<DeliveryAssignmentResponseDTO>> deliver(
            @PathVariable Long id,
            @RequestParam Long driverId) {
        return ResponseEntity.ok(ApiResponse.success(
                assignmentService.markDelivered(id, driverId),
                "Delivery completed! Earnings credited."));
    }

    /**
     * Cancel an assignment.
     * POST /api/delivery/{id}/cancel?reason=...
     */
    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<DeliveryAssignmentResponseDTO>> cancel(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "Cancelled") String reason) {
        return ResponseEntity.ok(ApiResponse.success(
                assignmentService.cancelAssignment(id, reason), "Assignment cancelled"));
    }

    /**
     * Get delivery status by order ID — main service calls this to track.
     * GET /api/delivery/order/{orderId}
     */
    @GetMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse<DeliveryAssignmentResponseDTO>> getByOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(ApiResponse.success(
                assignmentService.getByOrderId(orderId), "Assignment fetched"));
    }

    /**
     * Driver's full delivery history.
     * GET /api/delivery/driver/{driverId}/history
     */
    @GetMapping("/driver/{driverId}/history")
    public ResponseEntity<ApiResponse<List<DeliveryAssignmentResponseDTO>>> driverHistory(
            @PathVariable Long driverId) {
        List<DeliveryAssignmentResponseDTO> history = assignmentService.getDriverHistory(driverId);
        return ResponseEntity.ok(ApiResponse.success(history, "History fetched: " + history.size() + " deliveries"));
    }

    /**
     * Driver's current active (in-progress) assignment.
     * GET /api/delivery/driver/{driverId}/active
     */
    @GetMapping("/driver/{driverId}/active")
    public ResponseEntity<ApiResponse<DeliveryAssignmentResponseDTO>> driverActiveAssignment(
            @PathVariable Long driverId) {
        DeliveryAssignmentResponseDTO dto = assignmentService.getActiveAssignment(driverId);
        return ResponseEntity.ok(ApiResponse.success(dto, dto != null ? "Active assignment found" : "No active assignment"));
    }
}
