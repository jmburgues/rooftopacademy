package com.rooftop.academy.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler({
            ResourceNotFoundException.class
    })

    public ResponseEntity<Object> resourceNotFoundHandler(ResourceNotFoundException exception) {
        ApiError apiError = ApiError.builder().error(true).code(404).message(exception.getMessage()).build();
        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({
            Exception.class
    })

    public ResponseEntity<Object> defaultExceptionHandler(Exception exception) {
        ApiError apiError = ApiError.builder().error(true).code(422).message("An error occurred when processing the text").build();
        return new ResponseEntity<>(apiError, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
