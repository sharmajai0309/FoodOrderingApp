package com.deliveryService.service.service.impl;

import com.deliveryService.service.api_output.ApiResponse;
import com.deliveryService.service.dto.request.DriverDocumentRequest;
import com.deliveryService.service.entity.DeliveryPartner;
import com.deliveryService.service.entity.DriverDocument;
import com.deliveryService.service.entity.DriverVerification;
import com.deliveryService.service.entity.Enums.PartnerStatus;
import com.deliveryService.service.entity.Enums.VerificationStatus;
import com.deliveryService.service.exception.customexception.PartnerNotFoundException;
import com.deliveryService.service.repository.DeliveryPartnerRepository;
import com.deliveryService.service.repository.DriverDocumentRepository;
import com.deliveryService.service.repository.DriverVerificationRepository;
import com.deliveryService.service.service.DriverDocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.deliveryService.service.constants.ApplicationConstants.DOCUMENT_VERIFICATION_IN_PROCESS;

@Service
@RequiredArgsConstructor
public class DriverDocumentServiceImpl implements DriverDocumentService {

        private final DriverDocumentRepository driverDocumentRepository;
        private final DeliveryPartnerRepository deliveryPartnerRepository;
        private final DriverVerificationRepository driverVerificationRepository;

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

                // Check if document type already exists
                if (driverDocumentRepository.existsByPartner_IdAndDocumentTypeIgnoreCase(driverId,
                                request.getDocumentType())) {
                        throw new com.deliveryService.service.exception.customexception.DocumentAlreadyUploadedException(
                                        request.getDocumentType());
                }

                // save driver document
                DriverDocument driverDocument = new DriverDocument();
                driverDocument.setPartner(deliveryPartner);
                driverDocument.setDocumentType(request.getDocumentType());
                driverDocument.setDocumentUrl(request.getDocumentUrl());

                driverDocumentRepository.save(driverDocument);

                // Update delivery partner onboarding status
                deliveryPartner.setStatus(
                                PartnerStatus.DOCUMENT_VERIFICATION_PENDING);

                // Persist updated partner status
                deliveryPartnerRepository.save(deliveryPartner);

                // Update DriverVerification status (Check for existing record to avoid unique
                // constraint error)
                DriverVerification driverVerification = driverVerificationRepository.findByPartner_Id(driverId)
                                .orElse(new DriverVerification());

                driverVerification.setPartner(deliveryPartner);
                driverVerification.setVerificationStatus(VerificationStatus.PENDING);
                driverVerification.setRemarks(DOCUMENT_VERIFICATION_IN_PROCESS);
                driverVerification.setVerifiedBy("SERVER CHECK"); // later we use the name of admin using jwt token
                DriverVerification saved = driverVerificationRepository.save(driverVerification);

                return ApiResponse.success(
                                request.getDocumentUrl(),
                                "Document uploaded successfully and status updated : current Verification Status : "
                                                + saved.getVerificationStatus());
        }

}
