package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "user_streaks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserStreak {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @OneToOne
    @MapsId // Dùng chính userId làm Khóa chính và liên kết trực tiếp với bảng User (Quan hệ 1-1)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "current_streak")
    private Integer currentStreak = 0;

    @Column(name = "longest_streak")
    private Integer longestStreak = 0;

    @Column(name = "last_learned_date")
    private LocalDate lastLearnedDate;

    public void setWithId(Long id) {
        this.userId = id;
    }
}