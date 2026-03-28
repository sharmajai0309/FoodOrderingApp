package com.deliveryService.service.controller;


import com.deliveryService.service.api_output.ApiResponse;
import com.deliveryService.service.dto.request.DriverDocumentRequest;
import com.deliveryService.service.service.DriverDocumentService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/driverDocument")
public class DriverDocumentController {

    private final DriverDocumentService driverDocumentService;

    /**
     * upload document
     *
     * @param driverId driverId
     * @param request request
     * @return {@link ResponseEntity}
     * @see ResponseEntity
     * @see ApiResponse
     */
    @PostMapping("/{driverId}/documents")
    public ResponseEntity<ApiResponse<?>> uploadDocument(
            @PathVariable Long driverId,
            @Valid @RequestBody DriverDocumentRequest request) {
        ApiResponse<String> apiResponse = driverDocumentService.uploadDocument(driverId, request);
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);

    }
}
