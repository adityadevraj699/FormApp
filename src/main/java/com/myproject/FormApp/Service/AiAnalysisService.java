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
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${ai.api.url}")
    private String aiUrl;

    @Value("${ai.api.key}")
    private String aiKey;

    public AiAnalysisService(WebClient.Builder builder) {
        this.webClient = builder.build();
    }

    public Map<String, Object> analyzeTextQuestion(Long feedbackId, Long questionId, List<String> texts) {
        try {
            String userPrompt = "Summarize and provide sentiment for the following feedback texts:\n"
                    + String.join("\n", texts);

            // ✅ Payload for Grok 4 Fast free
            Map<String, Object> payload = Map.of(
                    "model", "deepseek/deepseek-chat-v3.1:free",
                    "messages", List.of(
                            Map.of(
                                    "role", "user",
                                    "content", List.of(
                                            Map.of("type", "text", "text", userPrompt)
                                    )
                            )
                    )
            );

            System.out.println("🔹 Sending OpenRouter request for feedbackId=" + feedbackId + ", questionId=" + questionId);

            String resp = webClient.post()
                    .uri(aiUrl)
                    .header("Authorization", "Bearer " + aiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(payload)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            System.out.println("✅ OpenRouter API raw response: " + resp);

            // Parse response
            Map<String, Object> map = objectMapper.readValue(resp, Map.class);
            List<Map<String, Object>> choices = (List<Map<String, Object>>) map.get("choices");
            Map<String, Object> firstChoice = choices.get(0);
            Map<String, Object> message = (Map<String, Object>) firstChoice.get("message");

            // ✅ Grok 4 Fast content is a String
            Object contentObj = message.get("content");
            String summary;
            if (contentObj instanceof String s) {
                summary = s;
            } else {
                summary = contentObj.toString();
            }

            return Map.of(
                    "summary", summary,
                    "model", "x-ai/grok-4-fast:free",
                    "per_response", texts.stream().map(t -> Map.of("text", t)).toList(),
                    "suggestions", List.of(),
                    "key_phrases", List.of(),
                    "sentiment_label", "NEUTRAL",
                    "sentiment_avg", 0.0
            );

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("OpenRouter Grok API call failed", e);
        }
    }

    public String basicSentiment(String text) {
        String s = text.toLowerCase();
        int score = 0;
        List<String> pos = List.of("good","great","excellent","helpful","clear","understand");
        List<String> neg = List.of("bad","slow","boring","confusing","fast","difficult","poor");
        for (String p : pos) if (s.contains(p)) score++;
        for (String n : neg) if (s.contains(n)) score--;
        if (score > 0) return "POSITIVE";
        if (score < 0) return "NEGATIVE";
        return "NEUTRAL";
    }
}
