package com.memsource.business.memsource;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.memsource.business.configuration.Configuration;


/**
 * Test class for {@link Credentials}.
 */
public class CredentialsTest {

    @Test
    void shouldSetValuesFromConfiguration() {
        var configuration = new Configuration();

        configuration.setUsername("user");
        configuration.setPassword("pass");

        var credentials = new Credentials(configuration);

        assertEquals(configuration.getUsername(), credentials.getUserName());
        assertEquals(configuration.getPassword(), credentials.getPassword());
    }
}
