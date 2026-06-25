package com.example.demo.controller;

import com.example.demo.entity.Vocabulary;
import com.example.demo.service.WidgetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/widget")
public class WidgetController {

    private final WidgetService widgetService;

    public WidgetController(WidgetService widgetService) {
        this.widgetService = widgetService;
    }

    /**
     * API lấy từ vựng hôm nay cho Widget dựa trên userId truyền lên
     * URL test: http://localhost:8080/api/v1/widget/daily?userId=1
     */
    @GetMapping("/daily")
    public ResponseEntity<?> getDailyWord(@RequestParam("userId") Long userId) {
        try {
            Vocabulary vocabulary = widgetService.getDailyVocabularyForUser(userId);
            return ResponseEntity.ok(vocabulary);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}