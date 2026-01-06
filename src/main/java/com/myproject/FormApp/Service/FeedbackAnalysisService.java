package com.myproject.FormApp.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myproject.FormApp.Model.*;

import com.myproject.FormApp.Repository.FeedbackAnalysisRepository;

@Service
public class FeedbackAnalysisService {

    @Autowired
    private FeedbackAnalysisRepository analysisRepo;

    @Autowired
    private AiAnalysisService aiService;

    @Autowired
    private ObjectMapper objectMapper;

    public FeedbackAnalysis computeAndSaveTextAnalysis(
            Feedback feedback,
            Question question,
            List<StudentFeedbackAnswer> answers
    ) {
    	
    	  // =================================================
        // 1️⃣ CHECK IF ALREADY ANALYZED (NO AI CALL)
        // =================================================
        Optional<FeedbackAnalysis> existing =
                analysisRepo.findByFeedbackAndQuestion(feedback, question);

        if (existing.isPresent()) {
            // Already analyzed → return cached result
            return existing.get();
        }

        FeedbackAnalysis fa = analysisRepo
                .findByFeedbackAndQuestion(feedback, question)
                .orElseGet(() -> new FeedbackAnalysis(feedback, question));

        List<String> texts = answers.stream()
                .map(StudentFeedbackAnswer::getAnswer)
                .map(this::sanitize)
                .filter(s -> !s.isBlank())
                .toList();

        Map<String, Object> aiResp =
                aiService.analyzeTextQuestion(
                        feedback.getId(),
                        question.getId(),
                        question.getQuestionText(),
                        texts
                );

        fa.setSummary(String.valueOf(aiResp.get("summary")));
        fa.setSentimentLabel(String.valueOf(aiResp.get("sentiment_label")));
        fa.setSentimentAvg(
                Double.parseDouble(aiResp.get("sentiment_avg").toString())
        );
        fa.setModelName("gemma-3-27b-it");

        try {
            fa.setSuggestions(objectMapper.writeValueAsString(
                    aiResp.get("improvement_suggestions")));
            fa.setKeyPhrases(objectMapper.writeValueAsString(
                    aiResp.get("weak_points")));
            fa.setPerResponseJson(objectMapper.writeValueAsString(texts));
        } catch (Exception e) {
            fa.setSuggestions("[]");
            fa.setKeyPhrases("[]");
            fa.setPerResponseJson("[]");
        }

        return analysisRepo.save(fa);
    }

    private String sanitize(String s) {
        if (s == null) return "";
        s = s.replaceAll("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-z]{2,}", "[email]");
        s = s.replaceAll("\\b\\d{6,}\\b", "[number]");
        return s.trim();
    }
}
