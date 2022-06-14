package com.memsource.business.project;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.memsource.business.memsource.Project;


/**
 * Project controller.
 */
@RestController
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(final ProjectService projectService) {
        this.projectService = projectService;
    }

    /**
     * Get projects.
     *
     * @return List of projects
     */
    @GetMapping("/projects")
    public List<Project> getProjects() {
        return projectService.getProjects();
    }
}
