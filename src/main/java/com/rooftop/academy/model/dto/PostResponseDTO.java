package com.rooftop.academy.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class PostResponseDTO {
    private int id;
    private String url;
}
