package com.rooftop.academy.controller;

import static com.rooftop.academy.utils.TestUtils.CHARS;
import static com.rooftop.academy.utils.TestUtils.HASH;
import static com.rooftop.academy.utils.TestUtils.TO_ANALYZE;
import static com.rooftop.academy.utils.TestUtils.getAnalizedTextList;
import static com.rooftop.academy.utils.TestUtils.getConvertedResult;
import static com.rooftop.academy.utils.TestUtils.getResponseDtoList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.rooftop.academy.model.AnalyzedText;
import com.rooftop.academy.model.dto.GetResponseDTO;
import com.rooftop.academy.model.dto.PostRequestDTO;
import com.rooftop.academy.model.dto.PostResponseDTO;
import com.rooftop.academy.service.TextService;
import com.rooftop.academy.utils.TestUtils;

class TextControllerTest {
    @Mock private TextService textService;
    private TextController underTest;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        underTest = new TextController(textService);
    }

    @Test
    public void testAnalyzeText_HappyPath() {
        // given
        int chars = CHARS;
        PostRequestDTO request = PostRequestDTO.builder().text(TO_ANALYZE).chars(chars).build();
        AnalyzedText mockedServiceResponse = TestUtils.getAnalyzedTextFromDB(1);
        PostResponseDTO expected = PostResponseDTO.builder()
                .id(mockedServiceResponse.getId())
                .url(TextController.PATH + "/" + mockedServiceResponse.getId())
                .build();

        // when
        when(textService.getAnalysis(any(), any())).thenReturn(mockedServiceResponse);

        ResponseEntity<PostResponseDTO> response = underTest.analyzeText(request);

        // then
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(expected.getId(), response.getBody().getId());
        assertEquals(expected.getUrl(), response.getBody().getUrl());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        verify(textService, times(1)).getAnalysis(TO_ANALYZE, chars);
    }

    @Test
    public void testAnalyzeText_whenServiceThrowsException() {
        // given
        int chars = CHARS;
        PostRequestDTO request = PostRequestDTO.builder().text(TO_ANALYZE).chars(chars).build();
        AnalyzedText mockedServiceResponse = TestUtils.getAnalyzedTextFromDB(1);
        PostResponseDTO expected = PostResponseDTO.builder()
                .id(mockedServiceResponse.getId())
                .url(TextController.PATH + "/" + mockedServiceResponse.getId())
                .build();

        // when
        when(textService.getAnalysis(any(), any())).thenThrow(new RuntimeException("Internal error"));

        try {
            underTest.analyzeText(request);
            fail("Exception should be thrown");
        } catch (Exception e) {
            assertEquals(RuntimeException.class, e.getClass());
        }

        verify(textService, times(1)).getAnalysis(TO_ANALYZE, chars);
    }

    @Test
    public void testGetById_HappyPath() {
        // given
        int expectedId = 1;
        AnalyzedText mockedServiceResponse = TestUtils.getAnalyzedTextFromDB(1);
        GetResponseDTO expected = GetResponseDTO.builder()
                .id(expectedId)
                .hash(HASH)
                .chars(CHARS)
                .result(getConvertedResult())
                .build();

        // when
        when(textService.getById(any())).thenReturn(mockedServiceResponse);
        ResponseEntity<GetResponseDTO> response = underTest.getById(expectedId);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expected, response.getBody());

        verify(textService, times(1)).getById(expectedId);
    }

    @Test
    public void testGetById_whenServiceThrowsException() {
        // given
        int expectedId = 1;

        // when
        when(textService.getById(any())).thenThrow(new RuntimeException("Internal error"));

        try {
            underTest.getById(expectedId);
            fail("Exception should be thrown");
        } catch (Exception e) {
            assertEquals(RuntimeException.class, e.getClass());
        }

        verify(textService, times(1)).getById(expectedId);
    }

    @Test
    public void testGetAll_HappyPath() {
        // given
        Integer chars = 2;
        Integer page = 1;
        Integer rpp = 10;
        List<GetResponseDTO> expected = getResponseDtoList(rpp);
        Page<AnalyzedText> paginatedText = new PageImpl<AnalyzedText>(getAnalizedTextList(rpp),Pageable.ofSize(rpp),rpp);
        // when
        when(textService.getAll(any(),any(), any())).thenReturn(paginatedText);

        ResponseEntity<List<GetResponseDTO>> response = underTest.getAllTexts(chars, PageRequest.of(page,rpp));

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(expected.size(), response.getBody().size());
        assertTrue(expected.stream().allMatch( expectedDTO ->
            response.getBody().stream().anyMatch(expectedDTO::equals)));

        verify(textService, times(1)).getAll(chars, page, rpp);
    }

    @Test
    public void testGetAll_DefaultValues() {
        // given
        Integer defaultPage = 1;
        Integer defaultRpp = 10;

        // when
        underTest.getAllTexts(null, null);

        // then
        verify(textService, times(1)).getAll(any(), defaultPage, defaultRpp);
    }


}