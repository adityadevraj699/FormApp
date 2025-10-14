package com.myproject.FormApp.Service;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cloudinary.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ChartAnalysisService {

    @Value("${ai.api.url}")
    private String aiApiUrl;

    @Value("${ai.api.key}")
    private String aiApiKey;

    public Map<String, Long> analyzeSentiments(List<String> answers) {
        Map<String, Long> sentimentCounts = new HashMap<>();
        sentimentCounts.put("Positive", 0L);
        sentimentCounts.put("Negative", 0L);
        sentimentCounts.put("Neutral", 0L);

        for (String text : answers) {
            try {
                String sentiment = callAIForSentiment(text);
                sentimentCounts.put(sentiment, sentimentCounts.get(sentiment) + 1);
            } catch (Exception e) {
                sentimentCounts.put("Neutral", sentimentCounts.get("Neutral") + 1);
            }
        }

        return sentimentCounts;
    }

    private String callAIForSentiment(String text) throws Exception {
        URL url = new URL(aiApiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "Bearer " + aiApiKey);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        String requestBody = "{\"model\": \"gpt-3.5-turbo\", \"messages\": [{\"role\": \"user\", \"content\": \"Classify sentiment as Positive, Negative, or Neutral: " + text + "\"}]}";

        try (OutputStream os = conn.getOutputStream()) {
            os.write(requestBody.getBytes());
        }

        String response = new String(conn.getInputStream().readAllBytes());
        JSONObject json = new JSONObject(response);
        String result = json.getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content")
                .trim();

        if (result.toLowerCase().contains("positive")) return "Positive";
        if (result.toLowerCase().contains("negative")) return "Negative";
        return "Neutral";
    }
}
