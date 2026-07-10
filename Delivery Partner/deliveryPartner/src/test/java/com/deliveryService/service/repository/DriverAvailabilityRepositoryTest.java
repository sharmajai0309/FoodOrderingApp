package com.deliveryService.service.repository;

import com.deliveryService.service.entity.DriverAvailability;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class DriverAvailabilityRepositoryTest {

    @Autowired
    private DriverAvailabilityRepository repository;

    @Test
    @DisplayName("Find by partner id - success if exists")
    void testFindByPartner_Id() {
        Optional<DriverAvailability> result = repository.findByPartner_Id(1L);

        if (result.isPresent()) {
            assertThat(result.get().getPartner()).isNotNull();
        }
    }
}
