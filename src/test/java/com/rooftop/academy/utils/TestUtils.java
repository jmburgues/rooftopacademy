package com.rooftop.academy.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.rooftop.academy.model.AnalyzedText;
import com.rooftop.academy.model.Word;
import com.rooftop.academy.model.dto.GetResponseDTO;

public class TestUtils {
    public static final String TO_ANALYZE = "solo se que no se nada";
    public static final String HASH = "670F77DFCBD00C12033DD8E2884CDF9A";
    public static final int CHARS = 2;

    public static AnalyzedText getAnalyzedTextFromDB(int id) {
        return AnalyzedText.builder()
                .id(id)
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

    public static AnalyzedText getAnalyzedTextWithMaxLengthChars() {
        return AnalyzedText.builder()
                .hash("D66A30A59679070581F2084F591FA6AC")
                .chars(TO_ANALYZE.length())
                .result(List.of(Word.builder().word(TO_ANALYZE).occurrences(1).build()))
                .deleted(false)
                .build();
    }

    public static AnalyzedText getShortAnalyzedText() {
        return AnalyzedText.builder()
                .id(1)
                .hash("MockedHash")
                .chars(2)
                .result(List.of(Word.builder().id(1).word("s").occurrences(1).build()))
                .deleted(false)
                .build();
    }

    public static AnalyzedText getAnalyzedTextFromDB_deleted() {
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

    public static AnalyzedText getAnalyzedTextFromAlgorithm() {
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

    public static Map<String, Integer> getConvertedResult() {
        Map<String, Integer> map = new LinkedHashMap<>();
            map.put("so", 1);
            map.put("ol", 1);
            map.put("lo", 1);
            map.put("o ", 2);
            map.put(" s", 2);
            map.put("se", 2);
            map.put("e ", 3);
            map.put(" q", 1);
            map.put("qu", 1);
            map.put("ue", 1);
            map.put(" n", 2);
            map.put("no", 1);
            map.put("na", 1);
            map.put("ad", 1);
            map.put("da", 1);
        return map;
    }

    public static List<GetResponseDTO> getResponseDtoList(Integer size) {
        List<GetResponseDTO> list = new ArrayList<>();
        for(int i=0; i<size; i++) {
            list.add(GetResponseDTO.builder()
                    .id(i+1)
                    .chars(2)
                    .hash(HASH)
                    .result(getConvertedResult())
                    .build());
        }
        return list;
    }

    public static List<AnalyzedText> getAnalizedTextList(int listSize) {
        List<AnalyzedText> list = new java.util.ArrayList<>(List.of());
        for (int i = 0; i < listSize; i++) {
            list.add(getAnalyzedTextFromDB(i+1));
        }
        return list;
    }
}
