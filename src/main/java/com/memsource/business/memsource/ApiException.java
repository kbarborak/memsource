package com.memsource.business.memsource;

/**
 * Memsource general API call exception.
 */
public class ApiException extends RuntimeException {

    public ApiException(String message, Throwable cause) {
        super(message, cause);
    }
}

