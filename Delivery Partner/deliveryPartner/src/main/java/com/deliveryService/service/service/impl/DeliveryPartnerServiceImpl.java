package com.deliveryService.service.service.impl;

import com.deliveryService.service.entity.DeliveryPartner;
import com.deliveryService.service.entity.DriverVerification;
import com.deliveryService.service.entity.Enums.PartnerStatus;
import com.deliveryService.service.entity.Enums.VerificationStatus;
import com.deliveryService.service.exception.customexception.InvalidRequestException;
import com.deliveryService.service.exception.customexception.PartnerNotFoundException;
import com.deliveryService.service.mapper.DeliveryPartnerMapper;
import com.deliveryService.service.repository.DeliveryPartnerRepository;
import com.deliveryService.service.repository.DriverEarningsRepository;
import com.deliveryService.service.dto.request.DeliveryPartnerRegistrationRequest;
import com.deliveryService.service.dto.response.DeliveryPartnerResponseDTO;
import com.deliveryService.service.dto.response.EarningsSummaryDTO;
import com.deliveryService.service.repository.DriverVerificationRepository;
import com.deliveryService.service.service.DeliveryPartnerService;
import com.deliveryService.service.utils.CommonUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.deliveryService.service.constants.ApplicationConstants.DOCUMENT_VERIFICATION_DONE;

@AllArgsConstructor
@Service
public class DeliveryPartnerServiceImpl implements DeliveryPartnerService {

    private final DeliveryPartnerMapper mapper;
    private final DeliveryPartnerRepository deliveryPartnerRepository;
    private final DriverVerificationRepository driverVerificationRepository;
    private final DriverEarningsRepository earningsRepository;


    /**
     * @param request
     * @return
     */
    @Override
    public DeliveryPartnerResponseDTO registerDriver(final DeliveryPartnerRegistrationRequest request) {

        if (CommonUtils.isNullOrEmptyObject(request)) {
            throw new InvalidRequestException("Request cannot be null or empty");
        }

        // Check if phone number already exists
        deliveryPartnerRepository.findByPhone(request.getPhone())
                .ifPresent(p -> {
                    throw new InvalidRequestException("Phone number already registered: " + request.getPhone());
                });

        DeliveryPartner deliveryEntity = mapper.toEntity(request);
        DeliveryPartner savedDeliveryPartner = deliveryPartnerRepository.save(deliveryEntity);
        return mapper.toDTO(savedDeliveryPartner);

    }

    /**
     * @param id
     * @param request
     * @return
     */
    @Override
    public DeliveryPartnerResponseDTO updateRegisterDetails(
            Long id,
            DeliveryPartnerRegistrationRequest request) {

        if (id == null) {
            throw new InvalidRequestException("Id cannot be null");
        }

        if (CommonUtils.isNullOrEmptyObject(request)) {
            throw new InvalidRequestException("Request cannot be null or empty");
        }

        DeliveryPartner partner = deliveryPartnerRepository.findById(id)
                .orElseThrow(() -> new PartnerNotFoundException(id));


        partner.setName(request.getName());
        partner.setPhone(request.getPhone());
        partner.setVehicleType(request.getVehicleType());


        DeliveryPartner updatedPartner =
                deliveryPartnerRepository.save(partner);


        return mapper.toDTO(updatedPartner);
    }


    /**
     * approve driver
     *
     * @param driverID  driverID
     * @param adminName adminName (future: resolved from Keycloak principal)
     */
    @Override
    @Transactional
    public void approveDriver(final Long driverID, final String adminName) {

        DeliveryPartner deliveryPartner = deliveryPartnerRepository.findById(driverID)
                .orElseThrow(() -> new PartnerNotFoundException(driverID));

        if (deliveryPartner.getStatus() != PartnerStatus.DOCUMENT_VERIFICATION_PENDING)
            throw new InvalidRequestException("Partner is not ready for verification or has not uploaded documents");

        deliveryPartner.setStatus(PartnerStatus.ACTIVE);
        deliveryPartnerRepository.save(deliveryPartner);

        DriverVerification driverVerification = driverVerificationRepository.findByPartner_Id(driverID)
                .orElseThrow(() -> new PartnerNotFoundException(driverID));

        driverVerification.setVerificationStatus(VerificationStatus.APPROVED);
        driverVerification.setVerifiedBy(adminName);
        driverVerification.setRemarks(DOCUMENT_VERIFICATION_DONE);
        // FIX: persist the DriverVerification changes
        driverVerificationRepository.save(driverVerification);
    }


    /**
     * reject driver
     *
     * @param driverID  driverID
     * @param adminName adminName (future: resolved from Keycloak principal)
     * @param reason    reason for rejection
     */
    @Override
    @Transactional
    public void rejectDriver(final Long driverID, final String adminName, final String reason) {

        DeliveryPartner deliveryPartner = deliveryPartnerRepository.findById(driverID)
                .orElseThrow(() -> new PartnerNotFoundException(driverID));

        if (deliveryPartner.getStatus() != PartnerStatus.DOCUMENT_VERIFICATION_PENDING)
            throw new InvalidRequestException("Partner is not ready for verification or has not uploaded documents");

        deliveryPartner.setStatus(PartnerStatus.DOCUMENT_VERIFICATION_REJECTED);
        deliveryPartnerRepository.save(deliveryPartner);

        DriverVerification driverVerification = driverVerificationRepository.findByPartner_Id(driverID)
                .orElseThrow(() -> new PartnerNotFoundException(driverID));

        driverVerification.setVerificationStatus(VerificationStatus.REJECTED);
        driverVerification.setVerifiedBy(adminName);
        driverVerification.setRemarks(reason);

        driverVerificationRepository.save(driverVerification);
    }

// for admin
    @Override
    public List<DeliveryPartner> getPendingDriverVerification(){
        return deliveryPartnerRepository.findByStatus(PartnerStatus.DOCUMENT_VERIFICATION_PENDING);
    }

    @Override
    public DeliveryPartnerResponseDTO getPartnerById(Long id) {
        DeliveryPartner partner = deliveryPartnerRepository.findById(id)
                .orElseThrow(() -> new PartnerNotFoundException(id));
        return mapper.toDTO(partner);
    }

    @Override
    public List<DeliveryPartnerResponseDTO> getAllPartners() {
        return deliveryPartnerRepository.findAll().stream().map(mapper::toDTO).toList();
    }

    @Override
    @Transactional
    public void deletePartner(Long id) {
        if (!deliveryPartnerRepository.existsById(id)) {
            throw new PartnerNotFoundException(id);
        }
        deliveryPartnerRepository.deleteById(id);
    }

    @Override
    public EarningsSummaryDTO getEarningsSummary(Long id) {
        DeliveryPartner partner = deliveryPartnerRepository.findById(id)
                .orElseThrow(() -> new PartnerNotFoundException(id));

        java.time.Instant weekAgo = java.time.Instant.now().minus(7, java.time.temporal.ChronoUnit.DAYS);
        java.time.Instant monthAgo = java.time.Instant.now().minus(30, java.time.temporal.ChronoUnit.DAYS);

        EarningsSummaryDTO dto = new EarningsSummaryDTO();
        dto.setDriverId(id);
        dto.setDriverName(partner.getName());
        dto.setTotalEarnings(earningsRepository.sumTotalByPartnerId(id));
        dto.setThisWeekEarnings(earningsRepository.sumByPartnerIdAndEarnedAtAfter(id, weekAgo));
        dto.setThisMonthEarnings(earningsRepository.sumByPartnerIdAndEarnedAtAfter(id, monthAgo));
        dto.setTotalDeliveries(earningsRepository.countByPartner_Id(id));
        dto.setThisMonthDeliveries(earningsRepository.countByPartner_IdAndEarnedAtAfter(id, monthAgo));
        return dto;
    }






}
