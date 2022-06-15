package com.memsource.business.memsource;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


/**
 * Memsource general API call exception.
 */
public class ApiException extends RuntimeException {

    public ApiException(String message, Throwable cause) {
        super(message, cause);
    }
}

