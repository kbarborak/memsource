package com.memsource.business.project;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.memsource.business.memsource.Project;


/**
 * Test class for {@link ProjectController}.
 */
@WebMvcTest(ProjectController.class)
public class ProjectControllerTest {

    @MockBean
    private ProjectService projectService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnOk() throws Exception {
        var project = new Project();

        project.setUid("uid");
        project.setName("name");
        project.setStatus("NEW");
        project.setSourceLang("en");
        project.setTargetLangs(Stream.of("fr").collect(Collectors.toSet()));

        when(projectService.getProjects()).thenReturn(Collections.singletonList(project));

        mockMvc.perform(get("/projects").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].uid").value(project.getUid()))
            .andExpect(jsonPath("$[0].name").value(project.getName()))
            .andExpect(jsonPath("$[0].status").value(project.getStatus()))
            .andExpect(jsonPath("$[0].sourceLang").value(project.getSourceLang()))
            .andExpect(jsonPath("$[0].targetLangs").value(project.getTargetLangs().stream().collect(Collectors.toList()).get(0)));

        verify(projectService, times(1)).getProjects();
    }
}
