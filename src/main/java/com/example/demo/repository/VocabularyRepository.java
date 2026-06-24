package com.example.demo.repository;

import com.example.demo.entity.Vocabulary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface VocabularyRepository extends JpaRepository<Vocabulary, Long> {
    Optional<Vocabulary> findByWord(String word); // Dùng để check trùng từ hoặc tìm kiếm từ vựng
}