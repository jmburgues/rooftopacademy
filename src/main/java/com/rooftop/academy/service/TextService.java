package com.rooftop.academy.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.rooftop.academy.exception.ResourceNotFoundException;
import com.rooftop.academy.model.AnalyzedText;
import com.rooftop.academy.repository.TextRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TextService {
    private static final String MINIMUM_chars = "2";
    private static final Integer RPP_MAX = 100;
    private static final Integer RPP_MIN = 10;

    private final TextRepository textRepository;

    public AnalyzedText analyzeText(String text, Integer chars) {
        chars = validateChars(text, chars);

        String hash = encodeRequest(text, chars);
        Optional<AnalyzedText> analyzedText = textRepository.findByHash(hash);
        Integer finalChars = chars;
        return analyzedText.orElseGet(() -> analyze(text, finalChars));
    }

    private AnalyzedText analyze(String text, Integer chars) {
        //TODO: Create algorithm.
        return AnalyzedText.builder().build();
    }

    //TODO: Verify this exception thrown.
    public AnalyzedText getById(Integer id) {
        return textRepository.findById(id)
                .orElseThrow( () -> new ResourceNotFoundException("Text not found"));
    }

    public List<AnalyzedText> getAll(Integer chars, Integer page, Integer rpp) {
        chars = validateChars(chars);
        rpp = validateRpp(rpp);
        page = (page <= 1) ? 0 : page -1;

        return textRepository.findAllWithChars(chars, page, rpp);
    }

    private String encodeRequest(String text, Integer chars) {
        try {
            byte[] bytesMessage = (chars.toString() + "-" + text).getBytes(StandardCharsets.UTF_8);
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] md5digest = md.digest(bytesMessage);
            return Arrays.toString(md5digest);
        } catch (NoSuchAlgorithmException e) {
            log.error("Error=encode, " + e.getMessage());
            throw new RuntimeException();
        }
    }

    private Integer validateRpp(Integer rpp) {
        if(rpp > RPP_MAX)
            return RPP_MAX;
        if(rpp < RPP_MIN)
            return RPP_MIN;
        return rpp;
    }

    private Integer validateChars(String requestedText, Integer requestedNumber) {
        Integer minCharNumber = Integer.valueOf(MINIMUM_chars);

        if(Objects.isNull(requestedNumber) || requestedNumber < minCharNumber)
            return minCharNumber;
        if(requestedNumber > requestedText.length())
            return requestedText.length();
        return requestedNumber;
    }

    private Integer validateChars(Integer requestedNumber) {
        int minCharNumber = Integer.parseInt(MINIMUM_chars);
        return (Objects.isNull(requestedNumber) || requestedNumber < minCharNumber) ?
                minCharNumber :
                requestedNumber;
    }

    public void deleteById(Integer id) {
        if(!textRepository.existsById(id))
            throw new ResourceNotFoundException("Text not found");
        textRepository.deleteById(id);
    }
}
