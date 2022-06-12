package com.memsource.business.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;


/**
 * Test class for {@link ConfigurationRepository}.
 */
@DataJpaTest
public class ConfigurationRepositoryTest {

    @Autowired
    private ConfigurationRepository configurationRepository;

    @Test
    void shouldSaveAndReturnData() {
        var configuration = new Configuration();

        configuration.setUsername("karel");
        configuration.setPassword("pass");

        configurationRepository.save(configuration);

        var dbResult = configurationRepository.findById(configuration.getUsername());

        assertTrue(dbResult.isPresent());
        assertEquals(configuration.getUsername(), dbResult.get().getUsername());
        assertEquals(configuration.getPassword(), dbResult.get().getPassword());
    }
}
