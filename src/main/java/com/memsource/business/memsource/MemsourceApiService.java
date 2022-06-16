package com.memsource.business.memsource;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.memsource.business.AssignmentException;
import com.memsource.business.configuration.Configuration;
import com.memsource.business.configuration.ConfigurationService;
import lombok.extern.slf4j.Slf4j;


/**
 * Memsource API service managing rest calls and auth token reusing.
 */
@Service
@Slf4j
public class MemsourceApiService {

    private final ConfigurationService configurationService;

    private final RestTemplate restTemplate;

    private String memsourceApiUrl;

    public MemsourceApiService(final ConfigurationService configurationService, final RestTemplateBuilder restTemplateBuilder,
        @Value("${memsource.api-url}") final String memsourceApiUrl) {

        this.configurationService = configurationService;
        this.restTemplate = restTemplateBuilder.build();
        this.memsourceApiUrl = memsourceApiUrl;
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
     * @return
     */
    public List<Project> listProjects() {
        log.debug("Listing projects from Memsource API");

        final AuthToken authToken = getExistingOrNewAuthToken();
        final ResponseEntity<ProjectList> projectResponse = exchange("/projects", HttpMethod.GET, new HttpEntity<>(null, getAuthHeader(authToken)),
            ProjectList.class);

        return projectResponse.getBody().getContent();
    }

    /**
     * Get existing token from configuration or new from Memsource API.
     *
     * @return auth token
     */
    private AuthToken getExistingOrNewAuthToken() {
        final Optional<Configuration> configuration = configurationService.getConfiguration();

        if (!configuration.isPresent()) {
            throw new AssignmentException("Configuration is empty. Please call configurations endpoint first.");
        }

        if (StringUtils.hasText(configuration.get().getToken())) {
            return getExistingAuthToken(configuration.get());
        }

        return getNewAuthToken(configuration.get());
    }

    /**
     * Get existing auth token.
     *
     * @param configuration configuration object
     * @return existing auth token
     */
    private AuthToken getExistingAuthToken(final Configuration configuration) {
        log.debug("Using existing token from configuration");

        return new AuthToken(configuration.getToken());
    }

    /**
     * Get new token from Memsource API.
     *
     * @param configuration configuration object
     * @return new auth token
     */
    private AuthToken getNewAuthToken(final Configuration configuration) {
        log.debug("Obtaining token for first use");

        final AuthToken newAuthToken = login(new Credentials(configuration));

        saveTokenToConfiguration(configuration, newAuthToken);

        return newAuthToken;
    }

    /**
     * Save token to configuration for reusing purposes.
     *
     * @param configuration configuration object
     * @param newAuthToken new auth token
     */
    private void saveTokenToConfiguration(final Configuration configuration, final AuthToken newAuthToken) {
        configuration.setToken(newAuthToken.getToken());
        configurationService.saveConfiguration(configuration);
    }

    /**
     * Get auth header for API requests.
     *
     * @param authToken auth token
     * @return auth header
     */
    private HttpHeaders getAuthHeader(final AuthToken authToken) {
        final HttpHeaders headers = new HttpHeaders();

        headers.add(HttpHeaders.AUTHORIZATION, authToken.getHeaderValue());

        return headers;
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
