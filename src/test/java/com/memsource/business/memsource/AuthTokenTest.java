package com.memsource.business.memsource;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;


/**
 * Test class for {@link AuthToken}.
 */
public class AuthTokenTest {

    @Test
    void shouldReturnTokenForHeader() {
        var token = new AuthToken("token");

        assertEquals("ApiToken token", token.getHeaderValue());
    }
}
