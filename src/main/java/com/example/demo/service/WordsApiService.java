package com.example.demo.service;

import com.example.demo.dto.WordsApiResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class WordsApiService {

    private final WebClient wordsApiClient;

    // Spring sẽ tự động inject Bean wordsApiClient đã cấu hình ở Bước 1 vào đây
    public WordsApiService(WebClient wordsApiClient) {
        this.wordsApiClient = wordsApiClient;
    }

    /**
     * Hàm tra cứu thông tin của một từ cụ thể (Tương ứng endpoint /words/{word})
     */
    public WordsApiResponse fetchWordDetails(String word) {
        return this.wordsApiClient.get()
                .uri("/words/{word}", word)
                .retrieve()
                .bodyToMono(WordsApiResponse.class)
                .block(); // .block() để chuyển từ Reactive sang xử lý đồng bộ (Synchronous) cho đơn giản
    }

    /**
     * Hàm lấy một từ ngẫu nhiên (Tương ứng endpoint /words?random=true)
     */
    public WordsApiResponse fetchRandomWord() {
        return this.wordsApiClient.get()
                .uri(uriBuilder -> uriBuilder.path("/words").queryParam("random", "true").build())
                .retrieve()
                .bodyToMono(WordsApiResponse.class)
                .block();
    }
}