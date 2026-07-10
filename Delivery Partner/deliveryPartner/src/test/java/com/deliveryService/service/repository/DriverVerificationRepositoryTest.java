package com.deliveryService.service.repository;

import com.deliveryService.service.entity.DriverVerification;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class DriverVerificationRepositoryTest {

    @Autowired
    private DriverVerificationRepository repository;

    @Test
    @DisplayName("Find by partner id - success if exists")
    void testFindByPartner_Id() {
        Optional<DriverVerification> result = repository.findByPartner_Id(1L);

        if (result.isPresent()) {
            assertThat(result.get().getPartner()).isNotNull();
        }
    }
}
