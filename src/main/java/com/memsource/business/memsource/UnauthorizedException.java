package com.memsource.business.memsource;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


/**
 * Memsource unauthorized API call exception.
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UnauthorizedException extends ApiException {

    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}

