package com.memsource.business.project;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.memsource.business.configuration.Configuration;
import com.memsource.business.memsource.MemsourceApiService;
import com.memsource.business.memsource.Project;


/**
 * Test class for {@link ProjectService}.
 */
@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {

    @InjectMocks
    private ProjectService tested;

    @Mock
    private MemsourceApiService memsourceApiService;

    @Test
    void shouldReturnProjects() {
        var configuration = new Configuration();

        configuration.setUsername("user");
        configuration.setPassword("pass");

        when(memsourceApiService.listProjects()).thenReturn(Collections.singletonList(new Project()));

        var projects = tested.getProjects();

        verify(memsourceApiService, times(1)).listProjects();

        assertEquals(1, projects.size());
    }
}
