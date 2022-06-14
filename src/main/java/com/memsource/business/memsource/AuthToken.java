package com.memsource.business.memsource;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Memsource auth token.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthToken {

    private String token;

    /**
     * Get header value with token for authorization.
     *
     * @return token for use in authorization header
     */
    public String getHeaderValue() {
        return "ApiToken " + getToken();
    }
}
