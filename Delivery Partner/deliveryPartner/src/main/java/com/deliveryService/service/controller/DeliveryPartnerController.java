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

@RestController
@RequestMapping("/api/deliveryPartners")
@RequiredArgsConstructor
public class DeliveryPartnerController {

    private final DeliveryPartnerService deliveryPartnerService;

    /**
     * register
     *
     * @param request request
     * @return {@link ResponseEntity}
     * @see ResponseEntity
     * @see ApiResponse
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<DeliveryPartnerResponseDTO>> register(
            @Valid @RequestBody DeliveryPartnerRegistrationRequest request) {
        DeliveryPartnerResponseDTO responseDTO =
                deliveryPartnerService.registerDriver(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(responseDTO, 
                        request.getName() + ", your registration is successful. Please upload documents for activation."));
    }


    /**
     * update register details
     *
     * @param id id
     * @param request request
     * @return {@link ResponseEntity}
     * @see ResponseEntity
     * @see ApiResponse
     */
    @PutMapping("/{id}/register-details")
    public ResponseEntity<ApiResponse<DeliveryPartnerResponseDTO>> updateRegisterDetails(
            @PathVariable Long id,
            @Valid @RequestBody DeliveryPartnerRegistrationRequest request) {

        DeliveryPartnerResponseDTO response = deliveryPartnerService.updateRegisterDetails(id, request);

        return ResponseEntity.ok(
                ApiResponse.success(response, "Delivery partner details updated successfully")
        );
    }

    //implementation in admin panel or regional admin panel
//    get registration detail of deliveryPartner
//    get details by name
//    get details by vehicleType type
//    get details by status

}
