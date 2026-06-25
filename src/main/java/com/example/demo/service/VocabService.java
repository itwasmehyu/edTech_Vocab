package com.example.demo.service;

import com.example.demo.dto.WordsApiResponse;
import com.example.demo.entity.Vocabulary;
import com.example.demo.repository.VocabularyRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class VocabService {

    private final WordsApiService wordsApiService;
    private final TranslationService translationService;
    private final VocabularyRepository vocabularyRepository;

    public VocabService(WordsApiService wordsApiService,
                        TranslationService translationService,
                        VocabularyRepository vocabularyRepository) {
        this.wordsApiService = wordsApiService;
        this.translationService = translationService;
        this.vocabularyRepository = vocabularyRepository;
    }

    /**
     * Logic thông minh: Kiểm tra DB xem từ đã có chưa.
     * Nếu chưa có -> Gọi WordsAPI -> Dịch sang tiếng Việt -> Lưu vào DB -> Trả về kết quả sạch.
     */
    public Vocabulary getOrFetchVocabulary(String word) {
        // 1. Kiểm tra kho DB local trước để tiết kiệm request RapidAPI
        Optional<Vocabulary> existingVocab = vocabularyRepository.findByWord(word.toLowerCase().trim());
        if (existingVocab.isPresent()) {
            return existingVocab.get();
        }

        // 2. Nếu DB chưa có, gọi sang WordsAPI để bốc dữ liệu gốc tiếng Anh
        WordsApiResponse apiResponse = wordsApiService.fetchWordDetails(word.toLowerCase().trim());

        if (apiResponse == null || apiResponse.getResults() == null || apiResponse.getResults().isEmpty()) {
            throw new RuntimeException("Không tìm thấy dữ liệu cho từ: " + word);
        }

        // Lấy nghĩa đầu tiên (phổ biến nhất) từ danh sách kết quả để làm dữ liệu hiển thị
        WordsApiResponse.Result topResult = apiResponse.getResults().get(0);

        // Trích xuất ví dụ đầu tiên nếu có
        String exampleEn = (topResult.getExamples() != null && !topResult.getExamples().isEmpty())
                ? topResult.getExamples().get(0) : "No example available.";

        // 3. Tiến hành "Làm giàu" dữ liệu bằng cách dịch sang tiếng Việt
        String definitionVi = translationService.translateEnToVi(topResult.getDefinition());
        String exampleVi = translationService.translateEnToVi(exampleEn);

        String phonetic = apiResponse.getPronunciation() != null ? apiResponse.getPronunciation().getAll() : "";

        // 4. Đóng gói vào Entity và lưu xuống Postgres
        Vocabulary newVocab = Vocabulary.builder()
                .id(System.currentTimeMillis()) // Tạm thời tự sinh ID duy nhất bằng timestamp (hoặc lấy ID ngẫu nhiên)
                .word(apiResponse.getWord())
                .partOfSpeech(topResult.getPartOfSpeech())
                .phonetic(phonetic)
                .definitionEn(topResult.getDefinition())
                .definitionVi(definitionVi)
                .exampleEn(exampleEn)
                .exampleVi(exampleVi)
                .build();

        return vocabularyRepository.save(newVocab);
    }

    public Vocabulary getOrFetchRandomVocabulary() {
        WordsApiResponse randomApiResponse = wordsApiService.fetchRandomWord();
        if (randomApiResponse == null || randomApiResponse.getWord() == null) {
            throw new RuntimeException("Không thể bốc được từ ngẫu nhiên từ WordsAPI");
        }
        return this.getOrFetchVocabulary(randomApiResponse.getWord());
    }
}