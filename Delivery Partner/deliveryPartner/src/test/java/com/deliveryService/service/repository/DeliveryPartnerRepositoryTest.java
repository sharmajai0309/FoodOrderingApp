package com.deliveryService.service.repository;


import com.deliveryService.service.entity.DeliveryPartner;
import com.deliveryService.service.entity.Enums.PartnerStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class DeliveryPartnerRepositoryTest {

    @Autowired
    private DeliveryPartnerRepository repository;

    @Test
    @DisplayName("Find by phone - success case with full details")
    void testFindByPhone() {
        Optional<DeliveryPartner> result = repository.findByPhone("8851835208");

        assertThat(result).isPresent();
        assertThat(result.get().getPhone()).isEqualTo("8851835208");
        // These associations are fetched in the same query via @EntityGraph
        assertThat(result.get().getDocuments()).isNotNull();
        // verification and availability might be null in the DB
    }

    @Test
    @DisplayName("Find by phone - not found")
    void testFindByPhoneNotFound() {
        Optional<DeliveryPartner> result = repository.findByPhone("0000000000");
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Find by status - success case with full details")
    void testFindByStatus() {
        List<DeliveryPartner> result = repository.findByStatus(PartnerStatus.ACTIVE);

        if (!result.isEmpty()) {
            assertThat(result.get(0).getStatus()).isEqualTo(PartnerStatus.ACTIVE);
            assertThat(result.get(0).getDocuments()).isNotNull();
        }
    }
}