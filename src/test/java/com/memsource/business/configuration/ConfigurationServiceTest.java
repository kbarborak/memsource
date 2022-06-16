package com.memsource.business.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;

import javax.validation.ConstraintViolationException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;


/**
 * Test class for {@link ConfigurationService}.
 */
@SpringBootTest
public class ConfigurationServiceTest {

    @Autowired
    private ConfigurationService tested;

    @MockBean
    private ConfigurationRepository configurationRepository;

    @Test
    void shouldReturnConfiguration() {
        var configuration = new Configuration();

        configuration.setUsername("username");
        configuration.setPassword("password");
        configuration.setToken("token");

        when(configurationRepository.findAll()).thenReturn(Collections.singletonList(configuration));

        var result = tested.getConfiguration();

        verify(configurationRepository, times(1)).findAll();

        assertTrue(result.isPresent());
        assertEquals(configuration.getUsername(), result.get().getUsername());
        assertEquals(configuration.getPassword(), result.get().getPassword());
        assertEquals(configuration.getToken(), result.get().getToken());
    }

    @Test
    void shouldReturnEmptyConfigurationWhenNoData() {
        var configuration = new Configuration();

        configuration.setUsername("username");
        configuration.setPassword("password");

        when(configurationRepository.findAll()).thenReturn(Collections.emptyList());

        var result = tested.getConfiguration();

        verify(configurationRepository, times(1)).findAll();

        assertFalse(result.isPresent());
    }

    @Test
    void shouldSaveNewConfiguration() {
        var username = "username";
        var password = "password1234";
        var configuration = new Configuration();

        configuration.setUsername(username);
        configuration.setPassword(password);

        tested.saveNewConfiguration(configuration);

        verify(configurationRepository, times(1)).deleteAll();
        verify(configurationRepository, times(1)).save(configuration);
    }

    @Test
    void shouldNotSaveNewConfigurationWhenNullArgument() {
        assertThrows(IllegalArgumentException.class, () -> tested.saveNewConfiguration(null));

        verify(configurationRepository, times(1)).deleteAll();
        verify(configurationRepository, times(0)).save(isA(Configuration.class));
    }

    @Test
    void shouldNotSaveNewConfigurationValidationError() {
        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class, () -> tested.saveNewConfiguration(new Configuration()));

        verify(configurationRepository, times(1)).deleteAll();
        verify(configurationRepository, times(0)).save(isA(Configuration.class));

        assertEquals(2, exception.getConstraintViolations().size());
    }

    @Test
    void shouldNotSaveNewConfigurationShortPassword() {
        var username = "username";
        var password = "password";
        var configuration = new Configuration();

        configuration.setUsername(username);
        configuration.setPassword(password);

        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class, () -> tested.saveNewConfiguration(configuration));

        verify(configurationRepository, times(1)).deleteAll();
        verify(configurationRepository, times(0)).save(configuration);

        assertEquals(1, exception.getConstraintViolations().size());
    }

    @Test
    void shouldSaveConfiguration() {
        var username = "username";
        var password = "password1234";
        var configuration = new Configuration();

        configuration.setUsername(username);
        configuration.setPassword(password);

        tested.saveConfiguration(configuration);

        verify(configurationRepository, times(1)).save(configuration);
    }

    @Test
    void shouldNotSaveConfigurationWhenNullArgument() {
        assertThrows(IllegalArgumentException.class, () -> tested.saveConfiguration(null));

        verify(configurationRepository, times(0)).save(isA(Configuration.class));
    }

    @Test
    void shouldNotSaveConfigurationValidationError() {
        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class, () -> tested.saveConfiguration(new Configuration()));

        verify(configurationRepository, times(0)).save(isA(Configuration.class));

        assertEquals(2, exception.getConstraintViolations().size());
    }

    @Test
    void shouldNotSaveConfigurationShortPassword() {
        var username = "username";
        var password = "password";
        var configuration = new Configuration();

        configuration.setUsername(username);
        configuration.setPassword(password);

        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class, () -> tested.saveConfiguration(configuration));

        verify(configurationRepository, times(0)).save(configuration);

        assertEquals(1, exception.getConstraintViolations().size());
    }
}
