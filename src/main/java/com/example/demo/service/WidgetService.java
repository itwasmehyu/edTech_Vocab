package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.entity.UserDailyWidgetHistory;
import com.example.demo.entity.UserVocabProgress;
import com.example.demo.entity.Vocabulary;
import com.example.demo.repository.UserDailyWidgetHistoryRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.UserVocabProgressRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class WidgetService {

    private final UserDailyWidgetHistoryRepository widgetHistoryRepository;
    private final UserVocabProgressRepository vocabProgressRepository;
    private final UserRepository userRepository;
    private final VocabService vocabService;

    public WidgetService(UserDailyWidgetHistoryRepository widgetHistoryRepository,
                         UserVocabProgressRepository vocabProgressRepository,
                         UserRepository userRepository,
                         VocabService vocabService) {
        this.widgetHistoryRepository = widgetHistoryRepository;
        this.vocabProgressRepository = vocabProgressRepository;
        this.userRepository = userRepository;
        this.vocabService = vocabService;
    }

    /**
     * Lấy từ vựng hiển thị cho Widget ngày hôm nay của một User cụ thể
     */
    @Transactional
    public Vocabulary getDailyVocabularyForUser(Long userId) {
        LocalDate today = LocalDate.now();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng!"));

        // 1. Kiểm tra xem hôm nay hệ thống đã bốc từ nào cho User này chưa
        Optional<UserDailyWidgetHistory> existingHistory = widgetHistoryRepository.findByUserIdAndAssignedDate(userId, today);
        if (existingHistory.isPresent()) {
            return existingHistory.get().getVocabulary();
        }

        // 2. Nếu chưa có từ cho hôm nay, ưu tiên tìm từ cũ cần ôn tập (Đã đến hạn SRS)
        List<UserVocabProgress> overdueVocabs = vocabProgressRepository.findOverdueVocabs(userId, LocalDateTime.now());
        Vocabulary targetVocab;

        if (!overdueVocabs.isEmpty()) {
            // Lấy từ bị quá hạn lâu nhất để ôn tập trước
            targetVocab = overdueVocabs.get(0).getVocabulary();
        } else {
            // Nếu không có từ nào cần ôn tập, bốc một từ mới hoàn toàn từ WordsAPI
            targetVocab = vocabService.getOrFetchRandomVocabulary();

            // Khởi tạo tiến trình học (SRS) cho từ mới này dưới trạng thái mặc định ban đầu
            UserVocabProgress newProgress = UserVocabProgress.builder()
                    .user(user)
                    .vocabulary(targetVocab)
                    .status("LEARNING")
                    .boxLevel(1)
                    .easinessFactor(2.5)
                    .repetitions(0)
                    .intervalDays(0)
                    .nextReviewDate(LocalDateTime.now()) // Có thể ôn tập được ngay
                    .build();

            vocabProgressRepository.save(newProgress);
        }

        // 3. Ghi lại lịch sử cấp phát từ vựng cho ngày hôm nay vào bảng Widget History
        UserDailyWidgetHistory newHistory = UserDailyWidgetHistory.builder()
                .user(user)
                .vocabulary(targetVocab)
                .assignedDate(today)
                .isInteracted(false)
                .build();
        widgetHistoryRepository.save(newHistory);

        return targetVocab;
    }
}