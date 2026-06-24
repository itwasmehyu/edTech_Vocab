package com.example.demo.repository;

import com.example.demo.entity.UserVocabProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserVocabProgressRepository extends JpaRepository<UserVocabProgress, Long> {

    // Tìm các từ đã đến hạn ôn tập của User (next_review_date <= thời gian hiện tại)
    // Sắp xếp theo thứ tự ưu tiên từ hạn cũ nhất trở đi
    @Query("SELECT p FROM UserVocabProgress p WHERE p.user.id = :userId " +
            "AND p.nextReviewDate <= :now AND p.status = 'LEARNING' " +
            "ORDER BY p.nextReviewDate ASC")
    List<UserVocabProgress> findOverdueVocabs(@Param("userId") Long userId, @Param("now") LocalDateTime now);
}