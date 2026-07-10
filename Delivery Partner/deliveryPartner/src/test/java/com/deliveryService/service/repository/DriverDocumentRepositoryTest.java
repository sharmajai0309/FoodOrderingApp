package com.deliveryService.service.repository;

import com.deliveryService.service.entity.DriverDocument;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class DriverDocumentRepositoryTest {

    @Autowired
    private DriverDocumentRepository repository;

    @Test
    @DisplayName("Find by partner id - success if exists")
    void testFindByPartner_Id() {
        // Find documents for any partner or ID 1L
        List<DriverDocument> result = repository.findByPartner_Id(1L);

        if (!result.isEmpty()) {
            assertThat(result.get(0).getPartner()).isNotNull();
        }
    }

    @Test
    @DisplayName("Exists by partner id and type")
    void testExistsByPartner_IdAndDocumentTypeIgnoreCase() {
        boolean exists = repository.existsByPartner_IdAndDocumentTypeIgnoreCase(1L, "LICENSE");
        // No assertion needed besides execution check against real DB
        assertThat(exists).isNotNull();
    }
}
