package com.example.demo.controller;

import com.example.demo.dto.ReviewRequest;
import com.example.demo.entity.UserVocabProgress;
import com.example.demo.service.SrsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/srs")
public class SrsController {

    private final SrsService srsService;

    public SrsController(SrsService srsService) {
        this.srsService = srsService;
    }

    /**
     * API chấm điểm ôn tập từ vựng
     * URL: http://localhost:8080/api/v1/srs/review
     */
    @PostMapping("/review")
    public ResponseEntity<?> submitReview(@RequestBody ReviewRequest request) {
        try {
            UserVocabProgress updatedProgress = srsService.processReview(request);
            return ResponseEntity.ok(updatedProgress);
        } catch (RuntimeException e) {
            // Trả về lỗi chi tiết dạng Text để dễ debug trên Postman
            return ResponseEntity.badRequest().body("Lỗi nghiệp vụ: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi hệ thống: " + e.getMessage());
        }
    }
}