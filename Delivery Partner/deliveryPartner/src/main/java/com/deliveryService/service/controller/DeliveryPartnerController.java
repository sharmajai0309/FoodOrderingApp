package com.deliveryService.service.controller;

import com.deliveryService.service.service.DeliveryPartnerService;
import com.deliveryService.service.api_output.ApiResponse;
import com.deliveryService.service.dto.request.DeliveryPartnerRegistrationRequest;
import com.deliveryService.service.dto.response.DeliveryPartnerResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/DeliveryPartners")
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


}
