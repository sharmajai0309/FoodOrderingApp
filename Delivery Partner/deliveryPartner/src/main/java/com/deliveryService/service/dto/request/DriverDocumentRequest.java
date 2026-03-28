package com.deliveryService.service.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DriverDocumentRequest {

    @NotBlank
    private String documentType;

    @NotBlank
    private String documentUrl;
}
