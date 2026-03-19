package com.sht4873.reservation.core.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class VisitException extends RuntimeException {
    private final HttpStatus status;

    public VisitException(String message) {
        this(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public VisitException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
