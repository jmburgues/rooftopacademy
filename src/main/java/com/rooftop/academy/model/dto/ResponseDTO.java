package com.rooftop.academy.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class ResponseDTO {
    private int id;
    private String url;
}
