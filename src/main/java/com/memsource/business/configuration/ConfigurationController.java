package com.memsource.business.configuration;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


/**
 * Configuration management for Memsource REST API controller.
 */
@RestController
public class ConfigurationController {

    private final ConfigurationService configurationService;

    public ConfigurationController(final ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    /**
     * Save configuration for logging to Memsource. There is no complete CRUD support for configuration.
     * POST endpoint is also capable of replacing existing configuration.
     *
     * @param configuration configuration object
     */
    @PostMapping("/configurations")
    public void saveConfiguration(@Valid @RequestBody final Configuration configuration) {
        configurationService.saveNewConfiguration(configuration);
    }
}
