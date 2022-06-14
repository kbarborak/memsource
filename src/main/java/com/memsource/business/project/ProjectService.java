package com.memsource.business.project;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.memsource.business.AssignmentException;
import com.memsource.business.configuration.Configuration;
import com.memsource.business.configuration.ConfigurationService;
import com.memsource.business.memsource.AuthToken;
import com.memsource.business.memsource.Credentials;
import com.memsource.business.memsource.MemsourceApiService;
import com.memsource.business.memsource.Project;
import lombok.extern.slf4j.Slf4j;


/**
 * Project service.
 */
@Service
@Slf4j
public class ProjectService {

    private final MemsourceApiService memsourceApiService;

    private final ConfigurationService configurationService;

    public ProjectService(final MemsourceApiService memsourceApiService, final ConfigurationService configurationService) {
        this.memsourceApiService = memsourceApiService;
        this.configurationService = configurationService;
    }

    /**
     * Get projects.
     *
     * @return list of projects
     */
    @Transactional(readOnly = true)
    public List<Project> getProjects() {
        log.debug("Getting project list");

        final Optional<Configuration> configuration = configurationService.getConfiguration();

        if (!configuration.isPresent()) {
            throw new AssignmentException("Configuration is empty. Please call configurations endpoint first.");
        }

        final AuthToken token = memsourceApiService.login(new Credentials(configuration.get()));

        return memsourceApiService.listProjects(token);
    }
}
