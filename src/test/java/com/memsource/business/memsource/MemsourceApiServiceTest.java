package com.memsource.business.memsource;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withUnauthorizedRequest;

import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.hamcrest.core.StringEndsWith;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.client.MockRestServiceServer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


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

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldLogin() {
        this.server.expect(requestTo(new StringEndsWith("/auth/login")))
            .andRespond(withSuccess("{\"token\": \"token\"}", MediaType.APPLICATION_JSON));

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
    void shouldListProjects() throws JsonProcessingException {
        var token = new AuthToken("token");
        var project = new Project();
        var projectList = new ProjectList();

        project.setUid("uid");
        project.setName("name");
        project.setStatus("NEW");
        project.setName("name");
        project.setSourceLang("en");
        project.setTargetLangs(Stream.of("fr").collect(Collectors.toSet()));

        projectList.setContent(Collections.singletonList(project));

        server.expect(requestTo(new StringEndsWith("/projects")))
            .andExpect(header(HttpHeaders.AUTHORIZATION, token.getHeaderValue()))
            .andRespond(withSuccess(objectMapper.writeValueAsString(projectList), MediaType.APPLICATION_JSON));

        var projects = tested.listProjects(token);

        assertEquals(1, projects.size());
        assertEquals(project.getUid(), projects.get(0).getUid());
        assertEquals(project.getName(), projects.get(0).getName());
        assertEquals(project.getSourceLang(), projects.get(0).getSourceLang());
        assertEquals(project.getTargetLangs(), projects.get(0).getTargetLangs());
        assertEquals(project.getStatus(), projects.get(0).getStatus());
    }

    @Test
    void shouldThrowExceptionWhenTokenIsNull() {
        assertThrows(IllegalArgumentException.class, () -> tested.listProjects(null));
        assertThrows(IllegalArgumentException.class, () -> tested.listProjects(new AuthToken()));
    }

    @Test
    void shouldThrowUnauthorizedExceptionWhenListProjects() {
        server.expect(requestTo(new StringEndsWith("/projects")))
            .andRespond(withUnauthorizedRequest());

        assertThrows(UnauthorizedException.class, () -> tested.listProjects(new AuthToken("token")));
    }

    @Test
    void shouldThrowApiExceptionWhenListProjects() {
        server.expect(requestTo(new StringEndsWith("/projects")))
            .andRespond(withBadRequest());

        assertThrows(ApiException.class, () -> tested.listProjects(new AuthToken("token")));
    }
}
