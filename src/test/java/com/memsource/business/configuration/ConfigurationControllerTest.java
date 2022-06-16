package com.memsource.business.configuration;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


/**
 * Test class for {@link ConfigurationController}.
 */
@WebMvcTest(ConfigurationController.class)
public class ConfigurationControllerTest {

    @MockBean
    private ConfigurationService configurationService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnOk() throws Exception {
        var configuration = new Configuration();
        var configurationPayload = new JSONObject();

        configuration.setUsername("username");
        configuration.setPassword("passwordpassword");

        configurationPayload
            .put("username", configuration.getUsername())
            .put("password", configuration.getPassword());

        mockMvc.perform(post("/configurations").contentType(MediaType.APPLICATION_JSON).content(configurationPayload.toString()))
            .andExpect(status().isOk());

        verify(configurationService, times(1)).saveNewConfiguration(configuration);
    }

    @Test
    void shouldReturnBadRequestNoUsername() throws Exception {
        var configurationPayload = new JSONObject();

        configurationPayload
            .put("username", " ")
            .put("password", "passwordpassword");

        mockMvc.perform(post("/configurations").contentType(MediaType.APPLICATION_JSON).content(configurationPayload.toString()))
            .andExpect(status().isBadRequest());

        verify(configurationService, times(0)).saveNewConfiguration(isA(Configuration.class));
    }

    @Test
    void shouldReturnBadRequestShortPassword() throws Exception {
        var configurationPayload = new JSONObject();

        configurationPayload
            .put("username", "username")
            .put("password", "passwordpas");

        mockMvc.perform(post("/configurations").contentType(MediaType.APPLICATION_JSON).content(configurationPayload.toString()))
            .andExpect(status().isBadRequest());

        verify(configurationService, times(0)).saveNewConfiguration(isA(Configuration.class));
    }

    @Test
    void shouldReturnBadRequestNoPassword() throws Exception {
        var configurationPayload = new JSONObject();

        configurationPayload
            .put("username", "username")
            .put("password", "");

        mockMvc.perform(post("/configurations").contentType(MediaType.APPLICATION_JSON).content(configurationPayload.toString()))
            .andExpect(status().isBadRequest());

        verify(configurationService, times(0)).saveNewConfiguration(isA(Configuration.class));
    }
}
