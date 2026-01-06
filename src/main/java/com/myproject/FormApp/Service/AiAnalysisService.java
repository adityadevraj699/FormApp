package com.myproject.FormApp.Service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AiAnalysisService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${ai.api.url}")
    private String aiUrl;

    @Value("${ai.api.key}")
    private String aiKey;

    public AiAnalysisService(WebClient.Builder builder, ObjectMapper objectMapper) {
        this.webClient = builder.build();
        this.objectMapper = objectMapper;
    }

    /**
     * =====================================================
     * MAIN ANALYSIS METHOD (100% SAFE)
     * =====================================================
     */
    public Map<String, Object> analyzeTextQuestion(
            Long feedbackId,
            Long questionId,
            String questionText,
            List<String> texts
    ) {

        // 🛡 No answers
        if (texts == null || texts.isEmpty()) {
            return fallbackAnalyze(questionText);
        }

        // 🧠 YES / NO detection
        boolean isYesNo = texts.stream()
                .allMatch(t -> t != null &&
                        (t.equalsIgnoreCase("yes") || t.equalsIgnoreCase("no")));

        if (isYesNo) {
            return analyzeYesNoQuestion(questionText, texts);
        }

        try {
            // ================= STRONG PROMPT =================
            String prompt = """
            You are an educational feedback analysis expert.

            Question:
            %s

            Student responses:
            %s

            IMPORTANT RULES (MUST FOLLOW):
            1. weak_points MUST contain at least ONE item.
            2. improvement_suggestions MUST contain at least ONE item.
            3. Even if feedback is very positive, provide:
               - one constructive weakness
               - one realistic improvement suggestion
            4. Do NOT return empty arrays.
            5. Do NOT include markdown, explanation, or extra text.
            6. Return ONLY valid JSON.

            STRICT JSON FORMAT:
            {
              "summary": "Brief analytical summary (2–3 sentences)",
              "weak_points": ["At least one realistic weakness"],
              "improvement_suggestions": ["At least one actionable suggestion"],
              "key_phrases": ["keyword1", "keyword2"],
              "sentiment_label": "POSITIVE | NEUTRAL | NEGATIVE",
              "sentiment_avg": number
            }
            """.formatted(questionText, String.join("\n", texts));

            Map<String, Object> payload = Map.of(
                "model", "google/gemma-3-27b-it:free",
                "messages", List.of(
                    Map.of("role", "user", "content", prompt)
                ),
                "temperature", 0.2
            );

            String resp = webClient.post()
                    .uri(aiUrl)
                    .header("Authorization", "Bearer " + aiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(payload)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            if (resp == null || resp.isBlank()) {
                return fallbackAnalyze(questionText);
            }

            Map<String, Object> raw = objectMapper.readValue(resp, Map.class);

            if (!raw.containsKey("choices")) {
                return fallbackAnalyze(questionText);
            }

            Map<String, Object> msg =
                    (Map<String, Object>) ((List<?>) raw.get("choices")).get(0);

            String content =
                    ((Map<String, Object>) msg.get("message")).get("content").toString();

            // 🧹 Clean markdown / junk
            String cleanJson = extractPureJson(content);

            if (cleanJson == null || cleanJson.isBlank()) {
                return fallbackAnalyze(questionText);
            }

            return objectMapper.readValue(cleanJson, Map.class);

        } catch (Exception e) {
            System.err.println("⚠️ AI FAILED | questionId=" + questionId);
            return fallbackAnalyze(questionText);
        }
    }

    /**
     * =====================================================
     * YES / NO HANDLER
     * =====================================================
     */
    private Map<String, Object> analyzeYesNoQuestion(String questionText, List<String> texts) {

        long yes = texts.stream().filter(t -> t.equalsIgnoreCase("yes")).count();
        long no = texts.size() - yes;

        return Map.of(
            "summary",
            "Most students responded positively to the question \"" +
                questionText + "\" (" + yes + " out of " + texts.size() + ").",
            "weak_points",
            List.of("Feedback does not explain the reason behind the choice."),
            "improvement_suggestions",
            List.of("Ask students to briefly explain their Yes/No response."),
            "key_phrases", List.of("Yes", "No"),
            "sentiment_label", yes >= no ? "POSITIVE" : "NEGATIVE",
            "sentiment_avg", (yes - no) / (double) texts.size()
        );
    }

    /**
     * =====================================================
     * FALLBACK (AI DOWN / PARSE FAIL)
     * =====================================================
     */
    private Map<String, Object> fallbackAnalyze(String questionText) {

        return Map.of(
            "summary",
            "Student responses for the question \"" + questionText +
                "\" show mixed or unclear patterns.",
            "weak_points",
            List.of("Responses lack sufficient detail for deep analysis."),
            "improvement_suggestions",
            List.of("Encourage students to provide more descriptive feedback."),
            "key_phrases", List.of(),
            "sentiment_label", "NEUTRAL",
            "sentiment_avg", 0.0
        );
    }
    
    
    
    

    /**
     * =====================================================
     * JSON CLEANER (removes ```json blocks)
     * =====================================================
     */
    private String extractPureJson(String content) {

        if (content == null) return null;

        content = content
                .replaceAll("(?s)```json", "")
                .replaceAll("(?s)```", "")
                .trim();

        int start = content.indexOf('{');
        int end = content.lastIndexOf('}');

        if (start >= 0 && end >= start) {
            return content.substring(start, end + 1);
        }

        return null;
    }
    
    
    
    
    
    
    /**
     * ===============================
     * XAI GROUP SENTIMENT ANALYSIS
     * ONE QUESTION + ALL ANSWERS
     * ===============================
     */
    public Map<String, Integer> analyzeSentimentDistribution(
            String questionText,
            List<String> answers
    ) {

        if (answers == null || answers.isEmpty()) {
            return fallbackDistribution();
        }

        // -------------------------------------------------
        // 1️⃣ YES / NO / NOT SURE FAST PATH (NO AI CALL)
        // -------------------------------------------------
        boolean allBinary = answers.stream().allMatch(a ->
                a.equalsIgnoreCase("yes")
             || a.equalsIgnoreCase("no")
             || a.equalsIgnoreCase("not sure")
        );

        if (allBinary) {

            long yes = answers.stream().filter(a -> a.equalsIgnoreCase("yes")).count();
            long no  = answers.stream().filter(a -> a.equalsIgnoreCase("no")).count();
            long ns  = answers.stream().filter(a -> a.equalsIgnoreCase("not sure")).count();

            QuestionIntent intent = detectIntent(questionText);

            long positive = 0, negative = 0, neutral = ns;

            if (intent == QuestionIntent.POSITIVE_IMPACT) {
                positive = yes;
                negative = no;
            } else if (intent == QuestionIntent.NEGATIVE_CHECK) {
                positive = no;
                negative = yes;
            } else {
                // generic yes/no question
                positive = yes;
                negative = no;
            }

            return Map.of(
                    "positive", (int) positive,
                    "neutral",  (int) neutral,
                    "negative", (int) negative
            );
        }

        // -------------------------------------------------
        // 2️⃣ AI BASED (ONE CALL, CONTEXT AWARE)
        // -------------------------------------------------
        try {
            String prompt = """
            You are an educational feedback analysis expert.

            IMPORTANT:
            - Sentiment depends on the QUESTION CONTEXT.
            - For improvement questions: positive feedback = POSITIVE.
            - For difficulty/problem questions: negative experience = NEGATIVE.

            Question:
            %s

            Student responses:
            %s

            TASK:
            Count responses into POSITIVE, NEUTRAL, NEGATIVE.

            RULES:
            - Return ONLY valid JSON.
            - No explanation.
            - All values must be integers.
            - Sum must equal total responses.

            JSON FORMAT:
            {
              "positive": number,
              "neutral": number,
              "negative": number
            }
            """.formatted(questionText, String.join("\n", answers));

            Map<String, Object> payload = Map.of(
                    "model", "google/gemma-3-27b-it:free",
                    "messages", List.of(Map.of("role", "user", "content", prompt)),
                    "temperature", 0
            );

            String resp = webClient.post()
                    .uri(aiUrl)
                    .header("Authorization", "Bearer " + aiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(payload)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            System.out.println("🧠 AI GROUP RESPONSE => " + resp);

            if (resp == null || resp.isBlank()) {
                return fallbackDistribution();
            }

            Map<String, Object> raw = objectMapper.readValue(resp, Map.class);
            List<?> choices = (List<?>) raw.get("choices");

            if (choices == null || choices.isEmpty()) {
                return fallbackDistribution();
            }

            Map<?, ?> msg =
                    (Map<?, ?>) ((Map<?, ?>) choices.get(0)).get("message");

            String json = extractPureJsons(msg.get("content").toString());

            Map<String, Integer> result =
                    objectMapper.readValue(json, Map.class);

            return Map.of(
                    "positive", Math.max(0, result.getOrDefault("positive", 0)),
                    "neutral",  Math.max(0, result.getOrDefault("neutral", 0)),
                    "negative", Math.max(0, result.getOrDefault("negative", 0))
            );

        } catch (Exception e) {
            System.err.println("❌ AI group sentiment failed, fallback used");
            return fallbackDistribution();
        }
    }

    // ================= HELPER LOGIC =================

    private QuestionIntent detectIntent(String q) {
        String t = q.toLowerCase();

        if (t.contains("improve") || t.contains("effective")
                || t.contains("recommend") || t.contains("confident")
                || t.contains("prepared") || t.contains("useful")
                || t.contains("aligned")) {
            return QuestionIntent.POSITIVE_IMPACT;
        }

        if (t.contains("difficult") || t.contains("problem")
                || t.contains("issue") || t.contains("wrong")) {
            return QuestionIntent.NEGATIVE_CHECK;
        }

        return QuestionIntent.GENERIC;
    }

    private Map<String, Integer> fallbackDistribution() {
        return Map.of("positive", 0, "neutral", 1, "negative", 0);
    }

    private String extractPureJsons(String content) {
        if (content == null) return "{}";

        content = content.replaceAll("(?s)```json", "")
                         .replaceAll("(?s)```", "")
                         .trim();

        int s = content.indexOf('{');
        int e = content.lastIndexOf('}');

        return (s >= 0 && e >= s)
                ? content.substring(s, e + 1)
                : "{}";
    }

    enum QuestionIntent {
        POSITIVE_IMPACT,
        NEGATIVE_CHECK,
        GENERIC
    }
}
