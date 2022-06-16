package com.memsource.business.memsource;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withUnauthorizedRequest;

import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.hamcrest.core.StringEndsWith;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.client.MockRestServiceServer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.memsource.business.AssignmentException;
import com.memsource.business.configuration.Configuration;
import com.memsource.business.configuration.ConfigurationService;


/**
 * Test class for {@link MemsourceApiService}.
 */
@ExtendWith(SpringExtension.class)
@RestClientTest(MemsourceApiService.class)
public class MemsourceApiServiceTest {

    @Autowired
    private MemsourceApiService tested;

    @Autowired
    private MockRestServiceServer server;

    @MockBean
    private ConfigurationService configurationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldLogin() {
        expectLoginCall();

        var token = tested.login(new Credentials("user", "pass"));

        assertEquals("token", token.getToken());
    }

    @Test
    void shouldThrowExceptionWhenCredentialsNull() {
        assertThrows(IllegalArgumentException.class, () -> tested.login(null));
    }

    @Test
    void shouldThrowUnauthorizedExceptionWhenLogin() {
        this.server.expect(requestTo(new StringEndsWith("/auth/login")))
            .andRespond(withUnauthorizedRequest());

        assertThrows(UnauthorizedException.class, () -> tested.login(new Credentials("user", "pass")));
    }

    @Test
    void shouldThrowApiExceptionWhenLogin() {
        this.server.expect(requestTo(new StringEndsWith("/auth/login")))
            .andRespond(withBadRequest());

        assertThrows(ApiException.class, () -> tested.login(new Credentials("user", "pass")));
    }

    @Test
    void shouldListProjectsWithLogin() throws JsonProcessingException {
        var configuration = new Configuration();
        var project = new Project();
        var projectList = new ProjectList();
        var token = new AuthToken("token");

        configuration.setUsername("user");
        configuration.setPassword("pass");

        project.setUid("uid");
        project.setName("name");
        project.setStatus("NEW");
        project.setName("name");
        project.setSourceLang("en");
        project.setTargetLangs(Stream.of("fr").collect(Collectors.toSet()));

        projectList.setContent(Collections.singletonList(project));

        expectLoginCall();
        expectListProjectsCall(projectList, token);

        when(configurationService.getConfiguration()).thenReturn(Optional.of(configuration));

        var projects = tested.listProjects();

        verify(configurationService, times(1)).getConfiguration();

        var savedConfiguration = new Configuration();

        savedConfiguration.setUsername(configuration.getUsername());
        savedConfiguration.setPassword(configuration.getPassword());
        savedConfiguration.setToken(token.getToken());

        verify(configurationService, times(1)).saveConfiguration(savedConfiguration);

        assertEquals(1, projects.size());
        assertEquals(project.getUid(), projects.get(0).getUid());
        assertEquals(project.getName(), projects.get(0).getName());
        assertEquals(project.getSourceLang(), projects.get(0).getSourceLang());
        assertEquals(project.getTargetLangs(), projects.get(0).getTargetLangs());
        assertEquals(project.getStatus(), projects.get(0).getStatus());
    }

    @Test
    void shouldListProjectsWithStoredToken() throws JsonProcessingException {
        var configuration = new Configuration();
        var project = new Project();
        var projectList = new ProjectList();
        var token = new AuthToken("token");

        configuration.setUsername("user");
        configuration.setPassword("pass");
        configuration.setToken(token.getToken());

        project.setUid("uid");
        project.setName("name");
        project.setStatus("NEW");
        project.setName("name");
        project.setSourceLang("en");
        project.setTargetLangs(Stream.of("fr").collect(Collectors.toSet()));

        projectList.setContent(Collections.singletonList(project));

        expectListProjectsCall(projectList, token);

        when(configurationService.getConfiguration()).thenReturn(Optional.of(configuration));

        var projects = tested.listProjects();

        verify(configurationService, times(1)).getConfiguration();

        assertEquals(1, projects.size());
        assertEquals(project.getUid(), projects.get(0).getUid());
        assertEquals(project.getName(), projects.get(0).getName());
        assertEquals(project.getSourceLang(), projects.get(0).getSourceLang());
        assertEquals(project.getTargetLangs(), projects.get(0).getTargetLangs());
        assertEquals(project.getStatus(), projects.get(0).getStatus());
    }

    @Test
    void shouldNotListProjectsWithoutConfiguration() {
        when(configurationService.getConfiguration()).thenReturn(Optional.empty());

        assertThrows(AssignmentException.class, () -> tested.listProjects());
    }

    @Test
    void shouldThrowUnauthorizedExceptionWhenListProjects() {
        var configuration = new Configuration();
        var token = new AuthToken("token");

        configuration.setUsername("user");
        configuration.setPassword("pass");
        configuration.setToken(token.getToken());

        when(configurationService.getConfiguration()).thenReturn(Optional.of(configuration));

        server.expect(requestTo(new StringEndsWith("/projects")))
            .andRespond(withUnauthorizedRequest());

        assertThrows(UnauthorizedException.class, () -> tested.listProjects());
    }

    @Test
    void shouldThrowApiExceptionWhenListProjects() {
        var configuration = new Configuration();
        var token = new AuthToken("token");

        configuration.setUsername("user");
        configuration.setPassword("pass");
        configuration.setToken(token.getToken());

        when(configurationService.getConfiguration()).thenReturn(Optional.of(configuration));

        server.expect(requestTo(new StringEndsWith("/projects")))
            .andRespond(withBadRequest());

        assertThrows(ApiException.class, () -> tested.listProjects());
    }

    private void expectLoginCall() {
        server.expect(requestTo(new StringEndsWith("/auth/login")))
            .andRespond(withSuccess("{\"token\": \"token\"}", MediaType.APPLICATION_JSON));
    }

    private void expectListProjectsCall(final ProjectList projectList, final AuthToken token) throws JsonProcessingException {
        server.expect(requestTo(new StringEndsWith("/projects")))
            .andExpect(header(HttpHeaders.AUTHORIZATION, token.getHeaderValue()))
            .andRespond(withSuccess(objectMapper.writeValueAsString(projectList), MediaType.APPLICATION_JSON));
    }
}
