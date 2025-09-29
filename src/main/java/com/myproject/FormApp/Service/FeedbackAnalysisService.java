package com.myproject.FormApp.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myproject.FormApp.Model.Feedback;
import com.myproject.FormApp.Model.FeedbackAnalysis;
import com.myproject.FormApp.Model.Question;
import com.myproject.FormApp.Model.StudentFeedbackAnswer;
import com.myproject.FormApp.Repository.FeedbackAnalysisRepository;

@Service
public class FeedbackAnalysisService {

    @Autowired
    private FeedbackAnalysisRepository analysisRepo;

    @Autowired
    private AiAnalysisService aiService;

    @Autowired
    private ObjectMapper objectMapper;

   public FeedbackAnalysis computeAndSaveTextAnalysis(Feedback feedback, Question q, List<StudentFeedbackAnswer> answers) {

    FeedbackAnalysis fa = analysisRepo.findByFeedbackAndQuestion(feedback, q)
            .orElseGet(() -> new FeedbackAnalysis(feedback, q));

    // Create anonymized list of texts
    List<Map<String, Object>> anonymized = new ArrayList<>();
    int counter = 1;
    for (StudentFeedbackAnswer a : answers) {
        Map<String, Object> m = new HashMap<>();
        m.put("rid", "r" + counter++);
        m.put("text", sanitizeAnswer(a.getAnswer()));
        anonymized.add(m);
    }
    List<String> texts = anonymized.stream().map(m -> (String) m.get("text")).toList();

    // ✅ AI Service call
    Map<String, Object> aiResp;
    try {
        System.out.println("🔹 Calling AI API for feedbackId=" + feedback.getId() + ", questionId=" + q.getId());
        aiResp = aiService.analyzeTextQuestion(feedback.getId(), q.getId(), texts);
        System.out.println("✅ AI API response received: " + aiResp.get("summary"));
    } catch (Exception ex) {
        System.out.println("⚠️ AI API call failed, using fallback summary");
        aiResp = fallbackAnalyze(texts);
        System.out.println("Fallback summary: " + aiResp.get("summary"));
    }

    // Populate FeedbackAnalysis entity
    fa.setModelName((String) aiResp.getOrDefault("model", "unknown"));
    fa.setSummary((String) aiResp.getOrDefault("summary", ""));
    try {
        fa.setSuggestions(objectMapper.writeValueAsString(aiResp.getOrDefault("suggestions", List.of())));
        fa.setKeyPhrases(objectMapper.writeValueAsString(aiResp.getOrDefault("key_phrases", List.of())));
        fa.setPerResponseJson(objectMapper.writeValueAsString(aiResp.getOrDefault("per_response", List.of())));
        Object sAvg = aiResp.get("sentiment_avg");
        if (sAvg != null) fa.setSentimentAvg(Double.valueOf(sAvg.toString()));
        fa.setSentimentLabel((String) aiResp.getOrDefault("sentiment_label", null));
    } catch (Exception e) {
        e.printStackTrace();
    }

    // Numeric question average
    if (q.getAnswerType() == Question.AnswerType.NUMBER) {
        double avg = answers.stream()
                .mapToDouble(ans -> {
                    try { return Double.parseDouble(ans.getAnswer()); } 
                    catch (NumberFormatException e) { return 0.0; }
                })
                .average()
                .orElse(0.0);
        fa.setAvgNumeric(avg);
    }

    return analysisRepo.save(fa);
}


    private Map<String, Object> fallbackAnalyze(List<String> texts) {
        List<Map<String, Object>> per = new ArrayList<>();
        int pos = 0, neg = 0, neu = 0;
        for (int i = 0; i < texts.size(); i++) {
            String t = texts.get(i);
            String sentiment = aiService.basicSentiment(t);
            switch(sentiment) {
                case "POSITIVE": pos++; break;
                case "NEGATIVE": neg++; break;
                case "NEUTRAL": neu++; break;
            }
            Map<String, Object> m = Map.of(
                    "id", "r" + (i + 1),
                    "text", t,
                    "sentiment_label", sentiment,
                    "sentiment_score", 0.0
            );
            per.add(m);
        }

        String summary = texts.stream().collect(Collectors.joining(" | "));


        // Compute overall sentiment
        String overallSentiment;
        if (pos >= neg && pos >= neu) overallSentiment = "POSITIVE";
        else if (neg >= pos && neg >= neu) overallSentiment = "NEGATIVE";
        else overallSentiment = "NEUTRAL";

        return Map.of(
                "per_response", per,
                "summary", summary,
                "suggestions", List.of("Review pace", "Share examples"),
                "sentiment_label", overallSentiment,
                "sentiment_avg", (pos - neg) / (double) texts.size()
        );
    }


    private String sanitizeAnswer(String s) {
        if (s == null) return "";
        s = s.replaceAll("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-z]{2,}", "[redacted]");
        s = s.replaceAll("\\b\\d{6,}\\b", "[redacted]");
        return s.trim();
    }
}
