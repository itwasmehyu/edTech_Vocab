package com.example.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class TranslationService {

    private final WebClient webClient;

    public TranslationService() {
        // Khởi tạo WebClient trỏ tới API dịch miễn phí của Google
        this.webClient = WebClient.builder()
                .baseUrl("https://translate.googleapis.com")
                .build();
    }

    /**
     * Hàm dịch một đoạn văn bản từ Anh (en) sang Việt (vi)
     */
    public String translateEnToVi(String text) {
        if (text == null || text.isBlank()) return "";
        try {
            String encodedText = URLEncoder.encode(text, StandardCharsets.UTF_8);

            // Gọi API dịch ngầm
            List<?> response = this.webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/translate_a/single")
                            .queryParam("client", "gtx")
                            .queryParam("sl", "en")
                            .queryParam("tl", "vi")
                            .queryParam("dt", "t")
                            .queryParam("q", encodedText)
                            .build())
                    .retrieve()
                    .bodyToMono(List.class)
                    .block();

            if (response != null && !response.isEmpty()) {
                // Phân tách cấu trúc mảng phức tạp mà Google trả về để lấy chuỗi đã dịch
                List<?> firstMatch = (List<?>) response.get(0);
                List<?> firstResult = (List<?>) firstMatch.get(0);
                return firstResult.get(0).toString();
            }
        } catch (Exception e) {
            // Nếu lỗi dịch (mạng lỗi...), giữ nguyên tiếng Anh hoặc trả về chuỗi trống để không làm sập app
            return "[Translation Error] " + text;
        }
        return text;
    }
}