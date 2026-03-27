package com.deliveryService.service.service;

import com.deliveryService.service.entity.DeliveryPartner;
import com.deliveryService.service.exception.customexception.InvalidRequestException;
import com.deliveryService.service.mapper.DeliveryPartnerMapper;
import com.deliveryService.service.repository.DeliveryPartnerRepository;
import com.deliveryService.service.dto.request.DeliveryPartnerRegistrationRequest;
import com.deliveryService.service.dto.response.DeliveryPartnerResponseDTO;
import com.deliveryService.service.utils.CommonUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class DeliveryPartnerServiceImpl implements DeliveryPartnerService {

    private final DeliveryPartnerMapper mapper;
    private final DeliveryPartnerRepository deliveryPartnerRepository;

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
}
