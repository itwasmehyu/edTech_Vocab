package com.example.demo.controller;

import com.example.demo.dto.WordsApiResponse;
import com.example.demo.service.WordsApiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/test")
public class TestApiController {

    private final WordsApiService wordsApiService;

    // Inject service gọi API ngoài vào đây
    public TestApiController(WordsApiService wordsApiService) {
        this.wordsApiService = wordsApiService;
    }

    /**
     * Endpoint test tra cứu từ cụ thể
     * URL: http://localhost:8080/api/v1/test/search?word=developer
     */
    @GetMapping("/search")
    public ResponseEntity<WordsApiResponse> testSearch(@RequestParam("word") String word) {
        WordsApiResponse response = wordsApiService.fetchWordDetails(word);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint test bốc từ ngẫu nhiên
     * URL: http://localhost:8080/api/v1/test/random
     */
    @GetMapping("/random")
    public ResponseEntity<WordsApiResponse> testRandom() {
        WordsApiResponse response = wordsApiService.fetchRandomWord();
        return ResponseEntity.ok(response);
    }
}