package com.ctzn.ytsservice.interfaces.rest.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class ExceptionHandlerAdvice {

    @ExceptionHandler(ResourceException.class)
    public ResponseEntity handleException(ResourceException e) {
        log.warn(e.getMessage());
        return ResponseEntity.status(e.getHttpStatus()).body(e.getMessage());
    }

}
