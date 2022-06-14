package com.memsource.business.project;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.memsource.business.AssignmentException;
import com.memsource.business.configuration.Configuration;
import com.memsource.business.configuration.ConfigurationService;
import com.memsource.business.memsource.AuthToken;
import com.memsource.business.memsource.Credentials;
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
    private ConfigurationService configurationService;

    @Mock
    private MemsourceApiService memsourceApiService;

    @Test
    void shouldReturnProjects() {
        var configuration = new Configuration();

        configuration.setUsername("user");
        configuration.setPassword("pass");

        when(configurationService.getConfiguration()).thenReturn(Optional.of(configuration));
        when(memsourceApiService.login(isA(Credentials.class))).thenReturn(new AuthToken("token"));
        when(memsourceApiService.listProjects(new AuthToken("token"))).thenReturn(Collections.singletonList(new Project()));

        var projects = tested.getProjects();

        verify(configurationService, times(1)).getConfiguration();
        verify(memsourceApiService, times(1)).login(new Credentials(configuration));
        verify(memsourceApiService, times(1)).listProjects(new AuthToken("token"));

        assertEquals(1, projects.size());


    }

    @Test
    void shouldThrowExceptionCauseConfigurationIsEmpty() {
        when(configurationService.getConfiguration()).thenReturn(Optional.empty());

        assertThrows(AssignmentException.class, () -> tested.getProjects());
    }
}
