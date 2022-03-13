package com.rooftop.academy.service;

import static com.rooftop.academy.utils.TestUtils.CHARS;
import static com.rooftop.academy.utils.TestUtils.TO_ANALYZE;
import static com.rooftop.academy.utils.TestUtils.getAnalizedTextList;
import static com.rooftop.academy.utils.TestUtils.getAnalyzedTextFromAlgorithm;
import static com.rooftop.academy.utils.TestUtils.getAnalyzedTextFromDB;
import static com.rooftop.academy.utils.TestUtils.getAnalyzedTextFromDB_deleted;
import static com.rooftop.academy.utils.TestUtils.getAnalyzedTextWithMaxLengthChars;
import static com.rooftop.academy.utils.TestUtils.getShortAnalyzedText;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.rooftop.academy.exception.ResourceNotFoundException;
import com.rooftop.academy.model.AnalyzedText;
import com.rooftop.academy.model.Word;
import com.rooftop.academy.repository.TextRepository;
import com.rooftop.academy.utils.TestUtils;

class TextServiceTest {
    @Mock private TextRepository textRepository;
    private TextService underTest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        underTest = new TextService(textRepository);
    }

    @Test
    void testEncodeRequest() {
        // given
        String text = "abc";
        Integer chars = 3;
        String expectedHash = "F712B066791EDDE174D7F2E56A875543";

        // when
        underTest.getAnalysis(text, chars);

        // then
        verify(textRepository, times(1)).save(
                AnalyzedText.builder()
                .hash(expectedHash)
                .result(List.of(Word.builder().word("abc").occurrences(1).build()))
                .chars(3)
                .deleted(false)
                .build()
        );
    }

    @Test
    void testGetAnalysis_whenHash_IsFound() {
        // given
        Integer chars = CHARS;
        AnalyzedText expected = getAnalyzedTextFromDB(1);

        // when
        Mockito.when(textRepository.findByHash(any())).thenReturn(Optional.of(getAnalyzedTextFromDB(1)));

        AnalyzedText response = underTest.getAnalysis(TO_ANALYZE, chars);

        // then
        assertEquals(expected.getId(), response.getId());
        assertEquals(expected.getHash(), response.getHash());
        assertEquals(expected.getChars(), response.getChars());
        assertTrue(expected.getResult().stream().allMatch(expectedWord ->
                response.getResult().stream().anyMatch(expectedWord::equals)));

        verify(textRepository, times(1)).findByHash(TestUtils.HASH);
        verify(textRepository, times(0)).undeleteRegistry(any());
        verify(textRepository, times(0)).save(any());
    }

    @Test
    void testGetAnalysis_whenHash_isNotFound() {
        // given
        Integer chars = CHARS;
        AnalyzedText expected = getAnalyzedTextFromDB(1);

        // when
        Mockito.when(textRepository.findByHash(any())).thenReturn(Optional.empty());
        Mockito.when(textRepository.save(any())).thenReturn(getAnalyzedTextFromDB(1));

        AnalyzedText response = underTest.getAnalysis(TO_ANALYZE, chars);

        // then
        assertEquals(expected.getId(), response.getId());
        assertEquals(expected.getHash(), response.getHash());
        assertEquals(expected.getChars(), response.getChars());
        assertTrue(expected.getResult().stream().allMatch(expectedWord ->
                response.getResult().stream().anyMatch(expectedWord::equals)));

        verify(textRepository, times(1)).findByHash(TestUtils.HASH);
        verify(textRepository, times(0)).undeleteRegistry(any());
        verify(textRepository, times(1)).save(getAnalyzedTextFromAlgorithm());
    }

    @Test
    void testGetAnalysis_whenHash_isFound_andRegistryDeletedFromDB() {
        // given
        Integer chars = CHARS;
        AnalyzedText expected = getAnalyzedTextFromDB_deleted();

        // when
        Mockito.when(textRepository.findByHash(any())).thenReturn(Optional.of(getAnalyzedTextFromDB_deleted()));
        Mockito.when(textRepository.save(any())).thenReturn(getAnalyzedTextFromDB_deleted());

        AnalyzedText response = underTest.getAnalysis(TO_ANALYZE, chars);

        // then
        assertEquals(expected.getId(), response.getId());
        assertEquals(expected.getHash(), response.getHash());
        assertEquals(expected.getChars(), response.getChars());
        assertTrue(expected.getResult().stream().allMatch(expectedWord ->
                response.getResult().stream().anyMatch(expectedWord::equals)));

        verify(textRepository, times(1)).findByHash(TestUtils.HASH);
        verify(textRepository, times(0)).save(any());
        verify(textRepository, times(1)).undeleteRegistry(expected.getId());
    }

    @Test
    void testGetAnalysis_whenTextIsNull_shouldThrowException() {
        // given
        Integer chars = CHARS;

        // when
        Mockito.when(textRepository.findByHash(any())).thenReturn(Optional.empty());
        Mockito.when(textRepository.save(any())).thenReturn(getShortAnalyzedText());

        try {
            underTest.getAnalysis(null, chars);
            fail("An exception should be thrown");
        } catch (Exception e) {
            assertEquals(RuntimeException.class, e.getClass());
            assertEquals(e.getMessage(), "Required text must not be null");
        }

        verify(textRepository, times(0)).findByHash(any());
        verify(textRepository, times(0)).undeleteRegistry(any());
        verify(textRepository, times(0)).save(any());
    }

    @Test
    void testGetAnalysis_whenTextIsEmpty() {
        // given
        Integer chars = CHARS;
        AnalyzedText expected = getShortAnalyzedText();

        // when
        Mockito.when(textRepository.findByHash(any())).thenReturn(Optional.of(getShortAnalyzedText()));

        AnalyzedText response = underTest.getAnalysis("", chars);

        // then
        assertEquals(expected.getId(), response.getId());
        assertEquals(expected.getHash(), response.getHash());
        assertEquals(expected.getChars(), response.getChars());
        assertTrue(expected.getResult().stream().allMatch(expectedWord ->
                response.getResult().stream().anyMatch(expectedWord::equals)));

        verify(textRepository, times(1)).findByHash(any());
        verify(textRepository, times(0)).undeleteRegistry(any());
        verify(textRepository, times(0)).save(any());
    }

    @Test
    void testGetAnalysis_whenCharsAreNull() {
        // given
        Integer requestedChars = null;

        // when
        Mockito.when(textRepository.findByHash(any())).thenReturn(Optional.empty());

        underTest.getAnalysis(TO_ANALYZE, requestedChars);

        // then
        verify(textRepository, times(1)).save(getAnalyzedTextFromAlgorithm());
    }

    @Test
    void testGetAnalysis_whenCharsAreGraterThanTextLength() {
        // given
        Integer requestedChars = 30;

        // when
        Mockito.when(textRepository.findByHash(any())).thenReturn(Optional.empty());
        Mockito.when(textRepository.save(any())).thenReturn(getAnalyzedTextWithMaxLengthChars());

        underTest.getAnalysis(TO_ANALYZE, requestedChars);

        // then
        verify(textRepository, times(1)).save(getAnalyzedTextWithMaxLengthChars());
    }

    @Test
    void testGetById_HappyPath() {
        // given
        Integer requestedId = 1;
        AnalyzedText expected = getAnalyzedTextFromDB(1);

        // when
        Mockito.when(textRepository.findById(any())).thenReturn(Optional.of(getAnalyzedTextFromDB(1)));

        AnalyzedText response = underTest.getById(requestedId);

        // then
        assertEquals(expected.getId(), response.getId());
        assertEquals(expected.getHash(), response.getHash());
        assertEquals(expected.getChars(), response.getChars());
        assertTrue(expected.getResult().stream().allMatch(expectedWord ->
                response.getResult().stream().anyMatch(expectedWord::equals)));

        verify(textRepository, times(1)).findById(requestedId);
    }

    @Test
    void testGetById_WhenIntegerIsNull_ExceptionShouldBeThrown() {
        // given
        Integer requestedId = null;

        // when
        try {
            underTest.getById(requestedId);
            fail("Exception should be thrown");
        } catch (Exception e) {

            // then
            assertEquals(ResourceNotFoundException.class, e.getClass());
        }
    }

    @Test
    void testGetById_WhenIdIsNotFound_ExceptionShouldBeThrown() {
        // given
        Integer requestedId = 1;

        // when
        Mockito.when(textRepository.findById(any())).thenReturn(Optional.empty());

        try {
            underTest.getById(requestedId);
            fail("Exception should be thrown");
        } catch (Exception e) {
            assertEquals(ResourceNotFoundException.class, e.getClass());
            assertEquals("Text not found", e.getMessage());
        }

        verify(textRepository, times(1)).findById(requestedId);
    }

    @Test
    void testGetAll_withCustomRPP() {
        // given
        Integer page = 1;
        Integer rpp = 15;
        Page<AnalyzedText> paginatedText = new PageImpl<AnalyzedText>(getAnalizedTextList(rpp), Pageable.ofSize(rpp),rpp);

        // when
        when(textRepository.findAllByCharsPaginated(any(), any())).thenReturn(paginatedText);
        Page<AnalyzedText> response = underTest.getAll(2, page, rpp);

        // then
        verify(textRepository, times(1)).findAllByCharsPaginated(any(), any());
        assertEquals(rpp, (int) response.get().count());
    }

    @Test
    void testGetAll_withRPP_lessThanMinimum() {
        // given
        Integer rpp = TextService.RPP_MIN - 1;
        PageRequest expected = PageRequest.of(1, TextService.RPP_MIN);
        // when
        underTest.getAll(2, 1, rpp);

        // then
        verify(textRepository, times(1)).findAllByCharsPaginated(any(), eq(expected));
    }

    @Test
    void testGetAll_withRPP_moreThanMaximum() {
        // given
        Integer rpp = TextService.RPP_MAX + 1;
        PageRequest expected = PageRequest.of(1, TextService.RPP_MAX);
        // when
        underTest.getAll(2, 1, rpp);

        // then
        verify(textRepository, times(1)).findAllByCharsPaginated(any(), eq(expected));
    }

    @Test
    void testDeleteById_HappyPath() {
        // given
        Integer requestedId = 1;

        // when
        when(textRepository.existsById(any())).thenReturn(true);

        underTest.deleteById(requestedId);

        // then
        verify(textRepository, times(1)).existsById(requestedId);
        verify(textRepository, times(1)).logicalDelete(requestedId);

    }

    @Test
    void testDeleteById_whenIdDoesntExist() {
        // given
        Integer requestedId = 1;

        // when
        when(textRepository.existsById(any())).thenReturn(false);

        try {
            underTest.deleteById(requestedId);
            fail("Exception expected");
        } catch (Exception e) {
            assertEquals(ResourceNotFoundException.class, e.getClass());
            assertEquals("Text not found", e.getMessage());
        }

        verify(textRepository, times(1)).existsById(requestedId);
        verify(textRepository, times(0)).logicalDelete(any());
    }


}