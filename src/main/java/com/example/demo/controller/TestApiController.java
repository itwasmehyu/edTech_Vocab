package com.example.demo.controller;

import com.example.demo.service.VocabService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/test")
public class TestApiController {

    private final VocabService vocabService;

    // Inject service gọi API ngoài vào đây
    public TestApiController(VocabService vocabService) {
        this.vocabService = vocabService;
    }

    /**
     * Endpoint test tra cứu từ cụ thể
     * URL: http://localhost:8080/api/v1/test/search?word=developer
     */
    @GetMapping("/search")
    public ResponseEntity<com.example.demo.entity.Vocabulary> testSearch(@RequestParam("word") String word) {
        // Gọi qua VocabService để kích hoạt luồng: Check DB -> Gọi API -> Dịch -> Lưu DB
        com.example.demo.entity.Vocabulary vocabulary = vocabService.getOrFetchVocabulary(word);
        return ResponseEntity.ok(vocabulary);
    }

    /**
     * Endpoint test bốc từ ngẫu nhiên
     * URL: http://localhost:8080/api/v1/test/random
     */
//    @GetMapping("/random")
//    public ResponseEntity<WordsApiResponse> testRandom() {
//        WordsApiResponse response = wordsApiService.fetchRandomWord();
//        return ResponseEntity.ok(response);
//    }

    /**
     * Hàm bốc một từ ngẫu nhiên từ WordsAPI, dịch nghĩa và lưu vào DB
     */
    @GetMapping("/random")
    public ResponseEntity<com.example.demo.entity.Vocabulary> testRandom() {
        // Gọi qua VocabService để bốc từ ngẫu nhiên, tự động dịch và lưu DB
        com.example.demo.entity.Vocabulary response = vocabService.getOrFetchRandomVocabulary();
        return ResponseEntity.ok(response);
    }

}