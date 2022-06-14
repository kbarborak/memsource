package com.memsource.business.configuration;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

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
     * Get latest saved configuration.
     */
    @Transactional(readOnly = true)
    public Optional<Configuration> getConfiguration() {
        log.debug("Getting latest configuration");

        final List<Configuration> configurations = configurationRepository.findAll();

        if (configurations.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(configurations.get(0));
    }

    /**
     * Save configuration. There is always only the latest configuration record.
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

        configurationRepository.deleteAll();
        configurationRepository.save(configuration);
    }
}
