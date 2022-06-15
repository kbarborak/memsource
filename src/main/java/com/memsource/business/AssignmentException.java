package com.memsource.business;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


/**
 * Memsource assignment app exception.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AssignmentException extends RuntimeException {

    public AssignmentException(final String message) {
        super(message);
    }
}

