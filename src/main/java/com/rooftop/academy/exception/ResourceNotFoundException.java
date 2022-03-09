package com.rooftop.academy.exception;

import lombok.Data;

@Data
public class ResourceNotFoundException extends RuntimeException{
    public ResourceNotFoundException(String reason) {
        super(reason);
    }
}
