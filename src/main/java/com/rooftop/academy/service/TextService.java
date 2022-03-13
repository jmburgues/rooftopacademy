package com.rooftop.academy.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.rooftop.academy.exception.ResourceNotFoundException;
import com.rooftop.academy.model.AnalyzedText;
import com.rooftop.academy.model.Word;
import com.rooftop.academy.repository.TextRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TextService {
    public static final String MINIMUM_CHARS = "2";
    public static final Integer RPP_MAX = 100;
    public static final Integer RPP_MIN = 10;

    private final TextRepository textRepository;

    public AnalyzedText getAnalysis(String text, Integer chars) {
        Integer validatedChars = validateChars(text, chars);
        String hash = encodeRequest(text, validatedChars);
        Optional<AnalyzedText> analyzedText = textRepository.findByHash(hash);
        if (analyzedText.isPresent() && analyzedText.get().getDeleted()) {
            textRepository.undeleteRegistry(analyzedText.get().getId());
        }
        return analyzedText.orElseGet(() -> analyze(text, validatedChars));
    }

    public AnalyzedText getById(Integer id) {
        return textRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Text not found"));
    }

    public Page<AnalyzedText> getAll(Integer chars, Integer page, Integer rpp) {
        chars = validateChars(chars);
        rpp = validateRpp(rpp);

        return textRepository.findAllByCharsPaginated(chars, PageRequest.of(page,rpp));
    }

    public void deleteById(Integer id) {
        if (!textRepository.existsById(id))
            throw new ResourceNotFoundException("Text not found");
        textRepository.logicalDelete(id);
    }

    private AnalyzedText analyze(String text, Integer chars) {

        String hash = encodeRequest(text, chars);
        LinkedList<Word> result = new LinkedList<>();

        for (int i = 0; i < (text.length() - chars + 1); i++) {
            String substring = text.substring(i, i + chars);
            int matches = StringUtils.countOccurrencesOf(text, substring);
            result.add(Word.builder().word(substring).occurrences(matches).build());
        }

        return textRepository.save(AnalyzedText.builder()
                .hash(hash)
                .chars(chars)
                .result(result)
                .deleted(false)
                .build());
    }

    private String encodeRequest(String text, Integer chars) {
        try {
            byte[] bytesMessage = (chars.toString() + "-" + text).getBytes(StandardCharsets.UTF_8);
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] md5digest = md.digest(bytesMessage);
            return convertMd5ToString(md5digest);
        } catch (NoSuchAlgorithmException e) {
            log.error("Error=encode, " + e.getMessage());
            throw new RuntimeException();
        }
    }

    private String convertMd5ToString(byte[] md5digest) {
        StringBuilder sb = new StringBuilder();
        for (byte b : md5digest) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    private Integer validateRpp(Integer rpp) {
        if (Objects.isNull(rpp))
            return RPP_MIN;
        if (rpp > RPP_MAX)
            return RPP_MAX;
        if (rpp < RPP_MIN)
            return RPP_MIN;
        return rpp;
    }

    private Integer validateChars(String requestedText, Integer requestedNumber) {
        Integer minCharNumber = Integer.valueOf(MINIMUM_CHARS);

        if(Objects.isNull(requestedText))
            throw new RuntimeException("Required text must not be null");
        // If text is empty or 1 char, analysis will be saved just one time
        if(requestedText.length() < minCharNumber)
            return minCharNumber;
        if (Objects.isNull(requestedNumber) || requestedNumber < minCharNumber)
            return minCharNumber;
        if (requestedNumber > requestedText.length())
            return requestedText.length();
        return requestedNumber;
    }

    private Integer validateChars(Integer requestedNumber) {
        int minCharNumber = Integer.parseInt(MINIMUM_CHARS);
        return (Objects.isNull(requestedNumber) || requestedNumber < minCharNumber) ?
                minCharNumber :
                requestedNumber;
    }
}
