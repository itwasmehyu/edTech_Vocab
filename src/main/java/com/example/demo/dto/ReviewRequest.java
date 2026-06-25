package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewRequest {
    private Long userId;
    private Long vocabId;
    private Integer quality; // Điểm số từ 0 đến 5 (0: quên sạch, 5: nhớ hoàn toàn)
}