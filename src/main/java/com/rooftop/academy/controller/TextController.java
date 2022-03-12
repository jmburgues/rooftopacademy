package com.rooftop.academy.controller;

import java.util.List;

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
import com.rooftop.academy.model.dto.ResponseDTO;
import com.rooftop.academy.service.TextService;

import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "*" , methods = {RequestMethod.POST, RequestMethod.DELETE, RequestMethod.GET})
@RestController
@RequestMapping(TextController.PATH)
@RequiredArgsConstructor
public class TextController {
    public static final String PATH = "/text";
    private final TextService textService;

    @PostMapping
    public ResponseEntity<ResponseDTO> analyzeText(@RequestBody String request, @RequestParam(value = "chars", required = false) Integer chars) {
        AnalyzedText result = textService.getAnalysis(request, chars);

        return ResponseEntity.ok(ResponseDTO.builder()
                .id(result.getId())
                .url(PATH + "/" + result.getId())
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnalyzedText> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(textService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<AnalyzedText>> getAllTexts(
            @RequestParam(value= "chars", required = false) Integer chars,
            @RequestParam(value="page", defaultValue = "1") Integer page,
            @RequestParam(value="rpp", defaultValue = "10") Integer rpp) {

        List<AnalyzedText> result = textService.getAll(chars, page, rpp);

        return ResponseEntity.status(result.isEmpty() ? HttpStatus.NO_CONTENT : HttpStatus.OK)
                .body(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteText(@PathVariable Integer id) {
        textService.deleteById(id);
        return ResponseEntity.accepted().body("");
    }
}
