package com.rooftop.academy.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class PostRequestDTO {
    private String text;
    private int chars;
}
