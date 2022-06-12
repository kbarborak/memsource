package com.memsource.business.configuration;

import java.util.Optional;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;


/**
 * Configuration management service.
 */
@Service
@Slf4j
public class ConfigurationService {

    private final ConfigurationRepository configurationRepository;

    private final Validator validator;

    public ConfigurationService(final ConfigurationRepository configurationRepository, final Validator validator) {
        this.configurationRepository = configurationRepository;
        this.validator = validator;
    }

    /**
     * Get configuration for Memsource REST API.
     *
     * @param username username
     */
    @Transactional(readOnly = true)
    public Optional<Configuration> getConfiguration(final String username) {
        log.debug("Getting configuration for user {}", username);

        if (!StringUtils.hasText(username)) {
            return Optional.empty();
        }

        return configurationRepository.findById(username);
    }

    /**
     * Save configuration for Memsource REST API.
     *
     * @param configuration configuration object
     */
    @Transactional
    public void saveConfiguration(final Configuration configuration) {
        Assert.notNull(configuration, "Configuration could not be null");

        log.debug("Saving configuration for user {}", configuration.getUsername());

        final Set<ConstraintViolation<Configuration>> violations = validator.validate(configuration);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        configurationRepository.save(configuration);
    }
}
