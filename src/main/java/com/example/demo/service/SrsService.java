package com.example.demo.service;

import com.example.demo.dto.ReviewRequest;
import com.example.demo.entity.User;
import com.example.demo.entity.UserStreak;
import com.example.demo.entity.UserVocabProgress;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.UserStreakRepository;
import com.example.demo.repository.UserVocabProgressRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class SrsService {

    private final UserVocabProgressRepository progressRepository;
    private final UserStreakRepository streakRepository;
    private final UserRepository userRepository;

    public SrsService(UserVocabProgressRepository progressRepository,
                      UserStreakRepository streakRepository,
                      UserRepository userRepository) {
        this.progressRepository = progressRepository;
        this.streakRepository = streakRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public UserVocabProgress processReview(ReviewRequest request) {
        Long userId = request.getUserId();
        Long vocabId = request.getVocabId();
        int q = request.getQuality();

        // Dùng hàm tìm kiếm trực tiếp dưới DB cực kỳ an toàn
        UserVocabProgress progress = progressRepository.findByUserIdAndVocabId(userId, vocabId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tiến trình học cho userId: " + userId + " và vocabId: " + vocabId));
        // 2. Áp dụng thuật toán SM-2 (Spaced Repetition) để tính thông số mới
        double ef = progress.getEasinessFactor();
        int repetitions = progress.getRepetitions();
        int interval = progress.getIntervalDays();

        if (q >= 3) { // Trả lời đúng (độ nhớ từ trung bình trở lên)
            if (repetitions == 0) {
                interval = 1;
            } else if (repetitions == 1) {
                interval = 6;
            } else {
                interval = (int) Math.round(interval * ef);
            }
            repetitions++;
        } else { // Trả lời sai (quên từ)
            repetitions = 0;
            interval = 1; // Yêu cầu ôn tập lại ngay vào ngày mai
        }

        // Cập nhật hệ số dễ mới (Easiness Factor) theo công thức SM-2 chuẩn
        ef = ef + (0.1 - (5 - q) * (0.08 + (5 - q) * 0.02));
        if (ef < 1.3) ef = 1.3; // Giới hạn sàn EF tối thiểu là 1.3

        // Lưu trạng thái SRS mới
        progress.setEasinessFactor(ef);
        progress.setRepetitions(repetitions);
        progress.setIntervalDays(interval);
        progress.setLastReviewedAt(LocalDateTime.now());
        progress.setNextReviewDate(LocalDateTime.now().plusDays(interval));

        if (progress.getBoxLevel() < 5 && q >= 4) {
            progress.setBoxLevel(progress.getBoxLevel() + 1);
        }

        progressRepository.save(progress);

        // 3. Xử lý cập nhật Streak học tập cho User
        updateUserStreak(userId);

        return progress;
    }

    private void updateUserStreak(Long userId) {
        LocalDate today = LocalDate.now();

        UserStreak streak = streakRepository.findById(userId).orElseGet(() -> {
            // Lấy thực thể User thật từ database lên
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng khi cập nhật streak"));

            // BẮT BUỘC gán cả object user vào đây để @MapsId tự động map khóa chính
            return UserStreak.builder()
                    .user(user)
                    .currentStreak(0)
                    .longestStreak(0)
                    .build();
        });

        LocalDate lastLearned = streak.getLastLearnedDate();

        if (lastLearned == null) {
            streak.setCurrentStreak(1);
            streak.setLongestStreak(1);
        } else if (lastLearned.equals(today.minusDays(1))) {
            int newStreak = streak.getCurrentStreak() + 1;
            streak.setCurrentStreak(newStreak);
            if (newStreak > streak.getLongestStreak()) {
                streak.setLongestStreak(newStreak);
            }
        } else if (!lastLearned.equals(today)) {
            streak.setCurrentStreak(1);
        }
        streak.setLastLearnedDate(today);
        streakRepository.save(streak);
    }
}