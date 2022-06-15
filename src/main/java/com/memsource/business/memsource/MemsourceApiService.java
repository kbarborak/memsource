package com.memsource.business.memsource;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;


/**
 * Memsource API service.
 */
@Service
@Slf4j
public class MemsourceApiService {

    private final RestTemplate restTemplate;

    @Value("${memsource.api-url}")
    private String memsourceApiUrl;

    public MemsourceApiService(final RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    /**
     * Login to Memsource API and obtain auth token.
     *
     * @param credentials credentials
     * @return auth token
     */
    public AuthToken login(final Credentials credentials) {
        log.debug("Logging into Memsource API");

        Assert.notNull(credentials, "Credentials could not be null");

        final ResponseEntity<AuthToken> loginResponse = exchange("/auth/login", HttpMethod.POST, new HttpEntity<>(credentials, null), AuthToken.class);

        return new AuthToken(loginResponse.getBody().getToken());
    }

    /**
     * List projects from Memsource API.
     *
     * @param authToken
     * @return
     */
    public List<Project> listProjects(final AuthToken authToken) {
        log.debug("Listing projects from Memsource API");

        Assert.notNull(authToken, "AuthToken could not be null");
        Assert.hasText(authToken.getToken(), "Token could not be empty");

        final HttpHeaders headers = new HttpHeaders();

        headers.add(HttpHeaders.AUTHORIZATION, authToken.getHeaderValue());

        final ResponseEntity<ProjectList> projectResponse = exchange("/projects", HttpMethod.GET, new HttpEntity<>(null, headers), ProjectList.class);

        return projectResponse.getBody().getContent();
    }

    /**
     * Send request to MemSource api with basic error handling.
     *
     * @param path url path
     * @param method http methd
     * @param entity entity
     * @param responseType response type
     * @return response entity
     * @param <T> type of response object
     */
    private <T> ResponseEntity<T> exchange(final String path, final HttpMethod method, final HttpEntity entity, final Class<T> responseType) {
        try {
            return restTemplate.exchange(memsourceApiUrl + path, method, entity, responseType);
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new UnauthorizedException("Memsource API call resulted in unauthorized access. Please check your configuration.", ex);
            }

            throw new ApiException(String.format("Memsource API call resulted in error %s.", ex.getMessage()), ex);
        }
    }
}
