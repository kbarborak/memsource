package com.memsource.business.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

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
        var username = "username";
        var password = "password";
        var configuration = new Configuration();

        configuration.setUsername(username);
        configuration.setPassword(password);

        when(configurationRepository.findById(username)).thenReturn(Optional.of(configuration));

        var result = tested.getConfiguration(username);

        verify(configurationRepository, times(1)).findById(username);

        assertTrue(result.isPresent());
        assertEquals(username, result.get().getUsername());
        assertEquals(password, result.get().getPassword());
    }

    @Test
    void shouldReturnEmptyConfigurationWhenEmptyUsername() {
        var result = tested.getConfiguration(" ");

        verify(configurationRepository, times(0)).findById(" ");

        assertFalse(result.isPresent());
    }

    @Test
    void shouldReturnEmptyConfigurationWhenNullUsername() {
        var result = tested.getConfiguration(null);

        verify(configurationRepository, times(0)).findById(null);

        assertFalse(result.isPresent());
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
    void shouldSaveConfigurationShortPassword() {
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
