package com.example.demo.repository;

import com.example.demo.entity.UserDailyWidgetHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface UserDailyWidgetHistoryRepository extends JpaRepository<UserDailyWidgetHistory, Long> {
    // Hàm kiểm tra xem hôm nay User đã được phát từ vựng nào trên Widget chưa
    Optional<UserDailyWidgetHistory> findByUserIdAndAssignedDate(Long userId, LocalDate assignedDate);
}