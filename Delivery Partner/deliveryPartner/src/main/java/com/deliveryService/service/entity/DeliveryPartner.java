package com.deliveryService.service.entity;

import com.deliveryService.service.entity.base.BaseEntity;
import com.deliveryService.service.entity.Enums.PartnerStatus;
import com.deliveryService.service.entity.Enums.VehicleType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "delivery_partner")
@Getter
@Setter
public class DeliveryPartner extends BaseEntity {

    @NotBlank(message = "Name is required")
    private String name;

    @Column(unique = true, nullable = false)
    @NotBlank
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid phone number")
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VehicleType vehicleType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PartnerStatus status = PartnerStatus.PENDING;

    @PrePersist
    @PreUpdate
    private void normalizePhone() {
        if (this.phone != null) {
            this.phone = this.phone.replaceAll("[^0-9]", "");
        }
    }

    @OneToMany(mappedBy = "partner")
    private List<DriverDocument> documents;

    @OneToOne(mappedBy = "partner")
    private DriverVerification verification;

    @OneToOne(mappedBy = "partner")
    private DriverAvailability availability;
}
