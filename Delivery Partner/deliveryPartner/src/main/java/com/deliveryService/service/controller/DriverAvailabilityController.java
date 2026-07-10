package com.deliveryService.service.controller;

import com.deliveryService.service.api_output.ApiResponse;
import com.deliveryService.service.dto.request.ToggleAvailabilityRequest;
import com.deliveryService.service.entity.DriverAvailability;
import com.deliveryService.service.service.DriverAvailabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/drivers")
@RequiredArgsConstructor
public class DriverAvailabilityController {

    private final DriverAvailabilityService availabilityService;

    /**
     * Toggle driver online/offline.
     * POST /api/drivers/{id}/availability/toggle
     */
    @PostMapping("/{id}/availability/toggle")
    public ResponseEntity<ApiResponse<DriverAvailability>> toggle(
            @PathVariable Long id,
            @RequestBody(required = false) ToggleAvailabilityRequest request) {

        DriverAvailability result = availabilityService.toggleAvailability(id,
                request != null ? request : new ToggleAvailabilityRequest());

        boolean online = result.isAvailable();
        String msg = online ? "You are now ONLINE and ready to receive orders" : "You are now OFFLINE";
        return ResponseEntity.ok(ApiResponse.success(result, msg));
    }

    /**
     * Get current availability status.
     * GET /api/drivers/{id}/availability
     */
    @GetMapping("/{id}/availability")
    public ResponseEntity<ApiResponse<DriverAvailability>> getStatus(@PathVariable Long id) {
        DriverAvailability status = availabilityService.getStatus(id);
        return ResponseEntity.ok(ApiResponse.success(status, "Availability fetched successfully"));
    }

    /**
     * List all currently online drivers (for internal/admin use).
     * GET /api/drivers/available
     */
    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<DriverAvailability>>> getAvailableDrivers() {
        List<DriverAvailability> drivers = availabilityService.getAvailableDrivers();
        return ResponseEntity.ok(ApiResponse.success(drivers, "Available drivers fetched: " + drivers.size()));
    }


}
