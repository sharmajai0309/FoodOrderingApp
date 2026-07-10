package com.deliveryService.service.controller;


import com.deliveryService.service.api_output.ApiResponse;
import com.deliveryService.service.entity.DeliveryPartner;
import com.deliveryService.service.service.DeliveryPartnerService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/driver")
@RequiredArgsConstructor
@Validated
public class AdminController {

    private final DeliveryPartnerService deliveryPartnerService;

    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<List<DeliveryPartner>>> getPendingDrivers() {
        List<DeliveryPartner> pending = deliveryPartnerService.getPendingDriverVerification();
        return ResponseEntity.ok(ApiResponse.success(pending, "Pending drivers fetched successfully"));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<?>> approveDriver(@PathVariable Long id) {
        // TODO: Replace "ADMIN" with authenticated principal name (Keycloak)
        deliveryPartnerService.approveDriver(id, "ADMIN");

        return ResponseEntity.ok(
                ApiResponse.success("Driver approved successfully")
        );
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<?>> rejectDriver(
            @PathVariable Long id,
            @RequestParam @NotBlank(message = "Rejection reason cannot be blank") String reason) {
        // TODO: Replace "ADMIN" with authenticated principal name (Keycloak)
        deliveryPartnerService.rejectDriver(id, "ADMIN", reason);

        return ResponseEntity.ok(
                ApiResponse.success("Driver rejected successfully")
        );
    }







}
