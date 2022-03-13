package com.rooftop.academy.utils;

import java.util.LinkedHashMap;
import java.util.Map;

import com.rooftop.academy.model.AnalyzedText;
import com.rooftop.academy.model.dto.GetResponseDTO;

public class Converter {
    public static GetResponseDTO convert(AnalyzedText analyzedText) {
        Map<String, Integer> convertedResult = new LinkedHashMap<>();
        analyzedText.getResult().forEach(word -> convertedResult.put(word.getWord(), word.getOccurrences()));

        return GetResponseDTO.builder()
                .id(analyzedText.getId())
                .hash(analyzedText.getHash())
                .chars(analyzedText.getChars())
                .result(convertedResult)
                .build();
    }
}
