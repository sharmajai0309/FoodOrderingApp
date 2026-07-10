package com.deliveryService.service.controller;

import com.deliveryService.service.service.DeliveryPartnerService;
import com.deliveryService.service.api_output.ApiResponse;
import com.deliveryService.service.dto.request.DeliveryPartnerRegistrationRequest;
import com.deliveryService.service.dto.response.DeliveryPartnerResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/deliveryPartners")
@RequiredArgsConstructor
public class DeliveryPartnerController {

    private final DeliveryPartnerService deliveryPartnerService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<DeliveryPartnerResponseDTO>> register(
            @Valid @RequestBody DeliveryPartnerRegistrationRequest request) {
        DeliveryPartnerResponseDTO responseDTO =
                deliveryPartnerService.registerDriver(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(responseDTO,
                        request.getName() + ", your registration is successful. Please upload documents for activation."));
    }

    @PutMapping("/{id}/register-details")
    public ResponseEntity<ApiResponse<DeliveryPartnerResponseDTO>> updateRegisterDetails(
            @PathVariable Long id,
            @Valid @RequestBody DeliveryPartnerRegistrationRequest request) {

        DeliveryPartnerResponseDTO response = deliveryPartnerService.updateRegisterDetails(id, request);

        return ResponseEntity.ok(
                ApiResponse.success(response, "Delivery partner details updated successfully")
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DeliveryPartnerResponseDTO>> getProfile(@PathVariable Long id) {
        DeliveryPartnerResponseDTO response = deliveryPartnerService.getPartnerById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Profile fetched successfully"));
    }

    @GetMapping("/{id}/earnings")
    public ResponseEntity<ApiResponse<com.deliveryService.service.dto.response.EarningsSummaryDTO>> getEarnings(
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(
                deliveryPartnerService.getEarningsSummary(id), "Earnings fetched successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DeliveryPartnerResponseDTO>>> getAllPartners() {
        return ResponseEntity.ok(ApiResponse.success(
                deliveryPartnerService.getAllPartners(), "Fetched all delivery partners"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePartner(@PathVariable Long id) {
        deliveryPartnerService.deletePartner(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Delivery partner deleted successfully"));
    }

}
