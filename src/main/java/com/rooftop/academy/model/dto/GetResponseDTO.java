package com.rooftop.academy.model.dto;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@AllArgsConstructor
@Builder
@Data
@EqualsAndHashCode
public class GetResponseDTO {
    private Integer id;
    private String hash;
    private Integer chars;
    private Map<String, Integer> result;
}
