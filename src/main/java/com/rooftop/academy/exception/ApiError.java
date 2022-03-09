package com.rooftop.academy.exception;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiError {
    Boolean error;
    String message;
    int code;
}
