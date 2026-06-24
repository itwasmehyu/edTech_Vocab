package com.example.demo.dto;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class WordsApiResponse {
    private String word;
    private List<Result> results;
    private Pronunciation pronunciation;
    private Double frequency;

    @Getter
    @Setter
    @ToString
    public static class Result {
        private String definition;
        private String partOfSpeech;
        private List<String> synonyms;
        private List<String> antonyms;
        private List<String> examples;
    }

    @Getter
    @Setter
    @ToString
    public static class Pronunciation {
        private String all;
    }
}