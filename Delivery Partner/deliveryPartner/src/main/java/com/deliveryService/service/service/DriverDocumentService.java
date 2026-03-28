package com.deliveryService.service.service;


import com.deliveryService.service.api_output.ApiResponse;
import com.deliveryService.service.dto.request.DriverDocumentRequest;

public interface DriverDocumentService {

    public ApiResponse<String> uploadDocument(
            Long driverId,
            DriverDocumentRequest request);

}
