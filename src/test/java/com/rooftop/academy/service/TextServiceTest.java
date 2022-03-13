package com.rooftop.academy.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.rooftop.academy.exception.ResourceNotFoundException;
import com.rooftop.academy.model.AnalyzedText;
import com.rooftop.academy.model.Word;
import com.rooftop.academy.repository.TextRepository;

class TextServiceTest {
    private final String HASH = "670F77DFCBD00C12033DD8E2884CDF9A";
    private final String TO_ANALYZE = "solo se que no se nada";
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
        Integer chars = 2;
        AnalyzedText expected = getAnalyzedTextFromDB();

        // when
        Mockito.when(textRepository.findByHash(any())).thenReturn(Optional.of(getAnalyzedTextFromDB()));

        AnalyzedText response = underTest.getAnalysis(TO_ANALYZE, chars);

        // then
        assertEquals(expected.getId(), response.getId());
        assertEquals(expected.getHash(), response.getHash());
        assertEquals(expected.getChars(), response.getChars());
        assertTrue(expected.getResult().stream().allMatch(expectedWord ->
                response.getResult().stream().anyMatch(expectedWord::equals)));

        verify(textRepository, times(1)).findByHash(HASH);
        verify(textRepository, times(0)).undeleteRegistry(any());
        verify(textRepository, times(0)).save(any());
    }

    @Test
    void testGetAnalysis_whenHash_isNotFound() {
        // given
        Integer chars = 2;
        AnalyzedText expected = getAnalyzedTextFromDB();

        // when
        Mockito.when(textRepository.findByHash(any())).thenReturn(Optional.empty());
        Mockito.when(textRepository.save(any())).thenReturn(getAnalyzedTextFromDB());

        AnalyzedText response = underTest.getAnalysis(TO_ANALYZE, chars);

        // then
        assertEquals(expected.getId(), response.getId());
        assertEquals(expected.getHash(), response.getHash());
        assertEquals(expected.getChars(), response.getChars());
        assertTrue(expected.getResult().stream().allMatch(expectedWord ->
                response.getResult().stream().anyMatch(expectedWord::equals)));

        verify(textRepository, times(1)).findByHash(HASH);
        verify(textRepository, times(0)).undeleteRegistry(any());
        verify(textRepository, times(1)).save(getAnalyzedTextFromAlgorithm());
    }

    @Test
    void testGetAnalysis_whenHash_isFound_andRegistryDeletedFromDB() {
        // given
        Integer chars = 2;
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

        verify(textRepository, times(1)).findByHash(HASH);
        verify(textRepository, times(0)).save(any());
        verify(textRepository, times(1)).undeleteRegistry(expected.getId());
    }

    @Test
    void testGetAnalysis_whenTextIsNull_shouldThrowException() {
        // given
        Integer chars = 2;
        AnalyzedText expected = getShortAnalyzedText();

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
        Integer chars = 2;
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
        AnalyzedText expected = getAnalyzedTextFromDB();

        // when
        Mockito.when(textRepository.findById(any())).thenReturn(Optional.of(getAnalyzedTextFromDB()));

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
        Integer rpp = 15;

        // when
        when(textRepository.findAllWithChars(any(), any(), any())).thenReturn(getCustomTextPer(rpp));
        List<AnalyzedText> response = underTest.getAll(2, 1, rpp);

        // then
        verify(textRepository, times(1)).findAllWithChars(any(), any(), eq(rpp));
        assertEquals(rpp, response.size());
    }

    @Test
    void testGetAll_withRPP_lessThanMinimum() {
        // given
        Integer rpp = TextService.RPP_MIN - 1;

        // when
        underTest.getAll(2, 1, rpp);

        // then
        verify(textRepository, times(1)).findAllWithChars(any(), any(), eq(TextService.RPP_MIN));
    }

    @Test
    void testGetAll_withRPP_moreThanMaximum() {
        // given
        Integer rpp = TextService.RPP_MAX + 1;

        // when
        underTest.getAll(2, 1, rpp);

        // then
        verify(textRepository, times(1)).findAllWithChars(any(), any(), eq(TextService.RPP_MAX));
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

    private List<AnalyzedText> getCustomTextPer(int listSize) {
        List<AnalyzedText> list = new java.util.ArrayList<>(List.of());
        for (int i = 0; i < listSize; i++) {
            list.add(getAnalyzedTextFromDB());
        }
        return list;
    }

    private AnalyzedText getAnalyzedTextFromDB() {
        return AnalyzedText.builder()
                .id(1)
                .hash(HASH)
                .chars(2)
                .result(Arrays.asList(
                        Word.builder().id(1).word("so").occurrences(1).build(),
                        Word.builder().id(2).word("ol").occurrences(1).build(),
                        Word.builder().id(3).word("lo").occurrences(1).build(),
                        Word.builder().id(4).word("o ").occurrences(2).build(),
                        Word.builder().id(5).word(" s").occurrences(2).build(),
                        Word.builder().id(6).word("se").occurrences(2).build(),
                        Word.builder().id(7).word("e ").occurrences(3).build(),
                        Word.builder().id(8).word(" q").occurrences(1).build(),
                        Word.builder().id(9).word("qu").occurrences(1).build(),
                        Word.builder().id(10).word("ue").occurrences(1).build(),
                        Word.builder().id(11).word(" n").occurrences(2).build(),
                        Word.builder().id(12).word("no").occurrences(1).build(),
                        Word.builder().id(13).word("na").occurrences(1).build(),
                        Word.builder().id(14).word("ad").occurrences(1).build(),
                        Word.builder().id(15).word("da").occurrences(1).build())
                )
                .deleted(false)
                .build();
    }

    private AnalyzedText getAnalyzedTextWithMaxLengthChars() {
        return AnalyzedText.builder()
                .hash("D66A30A59679070581F2084F591FA6AC")
                .chars(TO_ANALYZE.length())
                .result(List.of(Word.builder().word(TO_ANALYZE).occurrences(1).build()))
                .deleted(false)
                .build();
    }

    private AnalyzedText getShortAnalyzedText() {
        return AnalyzedText.builder()
                .id(1)
                .hash("MockedHash")
                .chars(2)
                .result(List.of(Word.builder().id(1).word("s").occurrences(1).build()))
                .deleted(false)
                .build();
    }

    private AnalyzedText getAnalyzedTextFromDB_deleted() {
        return AnalyzedText.builder()
                .id(1)
                .hash(HASH)
                .chars(2)
                .result(Arrays.asList(
                        Word.builder().id(1).word("so").occurrences(1).build(),
                        Word.builder().id(2).word("ol").occurrences(1).build(),
                        Word.builder().id(3).word("lo").occurrences(1).build(),
                        Word.builder().id(4).word("o ").occurrences(2).build(),
                        Word.builder().id(5).word(" s").occurrences(2).build(),
                        Word.builder().id(6).word("se").occurrences(2).build(),
                        Word.builder().id(7).word("e ").occurrences(3).build(),
                        Word.builder().id(8).word(" q").occurrences(1).build(),
                        Word.builder().id(9).word("qu").occurrences(1).build(),
                        Word.builder().id(10).word("ue").occurrences(1).build(),
                        Word.builder().id(11).word(" n").occurrences(2).build(),
                        Word.builder().id(12).word("no").occurrences(1).build(),
                        Word.builder().id(13).word("na").occurrences(1).build(),
                        Word.builder().id(14).word("ad").occurrences(1).build(),
                        Word.builder().id(15).word("da").occurrences(1).build())
                )
                .deleted(true)
                .build();
    }

    private AnalyzedText getAnalyzedTextFromAlgorithm() {
        return AnalyzedText.builder()
                .hash(HASH)
                .chars(2)
                .result(Arrays.asList(
                        Word.builder().word("so").occurrences(1).build(),
                        Word.builder().word("ol").occurrences(1).build(),
                        Word.builder().word("lo").occurrences(1).build(),
                        Word.builder().word("o ").occurrences(2).build(),
                        Word.builder().word(" s").occurrences(2).build(),
                        Word.builder().word("se").occurrences(2).build(),
                        Word.builder().word("e ").occurrences(3).build(),
                        Word.builder().word(" q").occurrences(1).build(),
                        Word.builder().word("qu").occurrences(1).build(),
                        Word.builder().word("ue").occurrences(1).build(),
                        Word.builder().word("e ").occurrences(3).build(),
                        Word.builder().word(" n").occurrences(2).build(),
                        Word.builder().word("no").occurrences(1).build(),
                        Word.builder().word("o ").occurrences(2).build(),
                        Word.builder().word(" s").occurrences(2).build(),
                        Word.builder().word("se").occurrences(2).build(),
                        Word.builder().word("e ").occurrences(3).build(),
                        Word.builder().word(" n").occurrences(2).build(),
                        Word.builder().word("na").occurrences(1).build(),
                        Word.builder().word("ad").occurrences(1).build(),
                        Word.builder().word("da").occurrences(1).build())
                )
                .deleted(false)
                .build();
    }

}