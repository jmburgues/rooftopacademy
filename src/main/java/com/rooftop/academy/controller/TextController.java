package com.rooftop.academy.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rooftop.academy.model.AnalyzedText;
import com.rooftop.academy.model.dto.GetResponseDTO;
import com.rooftop.academy.model.dto.PostRequestDTO;
import com.rooftop.academy.model.dto.PostResponseDTO;
import com.rooftop.academy.service.TextService;
import com.rooftop.academy.utils.Converter;

import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "*", methods = {RequestMethod.POST, RequestMethod.DELETE, RequestMethod.GET})
@RestController
@RequestMapping(TextController.PATH)
@RequiredArgsConstructor
public class TextController {
    public static final String PATH = "/text";
    private final TextService textService;

    @PostMapping
    public ResponseEntity<PostResponseDTO> analyzeText(@RequestBody PostRequestDTO request) {
        AnalyzedText result = textService.getAnalysis(request.getText(), request.getChars());

        return ResponseEntity.ok(PostResponseDTO.builder()
                .id(result.getId())
                .url(PATH + "/" + result.getId())
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetResponseDTO> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(Converter.convert(textService.getById(id)));
    }

    @GetMapping
    public ResponseEntity<List<GetResponseDTO>> getAllTexts(
            @RequestParam(value = "chars", required = false) Integer chars, Pageable pageable) {

        List<GetResponseDTO> result = textService.getAll(chars,pageable.getPageNumber(), pageable.getPageSize()).get()
                .map(Converter::convert)
                .collect(Collectors.toList());

        return ResponseEntity.status(result.isEmpty() ? HttpStatus.NO_CONTENT : HttpStatus.OK)
                .body(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteText(@PathVariable Integer id) {
        textService.deleteById(id);
        return ResponseEntity.accepted().body("");
    }
}
