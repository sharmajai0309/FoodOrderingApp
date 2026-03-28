package com.deliveryService.service.service.impl;

import com.deliveryService.service.api_output.ApiResponse;
import com.deliveryService.service.dto.request.DriverDocumentRequest;
import com.deliveryService.service.entity.DeliveryPartner;
import com.deliveryService.service.entity.DriverDocument;
import com.deliveryService.service.entity.Enums.PartnerStatus;
import com.deliveryService.service.exception.customexception.PartnerNotFoundException;
import com.deliveryService.service.repository.DeliveryPartnerRepository;
import com.deliveryService.service.repository.DriverDocumentRepository;
import com.deliveryService.service.service.DriverDocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.deliveryService.service.ApplicationConstants.DOCUMENT_UPLOADED_SUCCESSFULLY;

@Service
@RequiredArgsConstructor
public class DriverDocumentServiceImpl implements DriverDocumentService {


    private final DriverDocumentRepository driverDocumentRepository;
    private final DeliveryPartnerRepository deliveryPartnerRepository;
    /**
     * @param driverId
     * @param request
     */
    @Override
    @Transactional
    public ApiResponse<String> uploadDocument(Long driverId, DriverDocumentRequest request) {

        // Find delivery partner
        DeliveryPartner deliveryPartner = deliveryPartnerRepository.findById(driverId)
                .orElseThrow(() -> new PartnerNotFoundException(driverId));

        // save driver document
        DriverDocument driverDocument = new DriverDocument();
        driverDocument.setPartner(deliveryPartner);
        driverDocument.setDocumentType(request.getDocumentType());
        driverDocument.setDocumentUrl(request.getDocumentUrl());

        driverDocumentRepository.save(driverDocument);

        // Update delivery partner onboarding status
        deliveryPartner.setStatus(
                PartnerStatus.DOCUMENT_VERIFICATION_PENDING
        );

        // Persist updated partner status
        deliveryPartnerRepository.save(deliveryPartner);


        return ApiResponse.success(
                request.getDocumentUrl(),
                "Document uploaded successfully and status updated"
        );
    }
}
