package com.memsource.business.project;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public ProjectService(final MemsourceApiService memsourceApiService) {
        this.memsourceApiService = memsourceApiService;
    }

    /**
     * Get projects.
     *
     * @return list of projects
     */
    @Transactional
    public List<Project> getProjects() {
        log.debug("Getting project list");

        return memsourceApiService.listProjects();
    }
}
