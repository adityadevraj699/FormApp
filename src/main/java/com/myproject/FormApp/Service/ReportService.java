package com.myproject.FormApp.Service;

import java.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.myproject.FormApp.Dto.ChartDataDTO;
import com.myproject.FormApp.Model.Question;
import com.myproject.FormApp.Model.StudentFeedbackAnswer;
import com.myproject.FormApp.Repository.StudentFeedbackAnswerRepository;

@Service
public class ReportService {

    private final StudentFeedbackAnswerRepository answerRepo;
    private final WebClient webClient;

    // Inject URL and key from application.properties
    public ReportService(StudentFeedbackAnswerRepository answerRepo,
                         @Value("${ai.api.url}") String aiUrl,
                         @Value("${ai.api.key}") String aiKey) {
        this.answerRepo = answerRepo;
        this.webClient = WebClient.builder()
                .baseUrl(aiUrl)
                .defaultHeader("Authorization", "Bearer " + aiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    public ChartDataDTO buildStudentLevelChartFor(Long feedbackId, Question question) {
        List<StudentFeedbackAnswer> answers = answerRepo.findByFeedbackIdAndQuestionId(feedbackId, question.getId());
        ChartDataDTO dto = new ChartDataDTO();

        if (question.getAnswerType() == Question.AnswerType.NUMBER) {
            dto.setChartType("bar");
            List<String> labels = new ArrayList<>();
            List<Double> data = new ArrayList<>();
            List<String> colors = new ArrayList<>();
            for (StudentFeedbackAnswer a : answers) {
                Double val = parseDoubleSafe(a.getAnswer());
                labels.add(""); // hide student names
                data.add(val != null ? val : 0.0);
                colors.add(null);
            }
            Map<String,Object> meta = new HashMap<>();
            if (question.getRangeStart() != null) meta.put("rangeStart", question.getRangeStart());
            if (question.getRangeEnd() != null) meta.put("rangeEnd", question.getRangeEnd());
            dto.setMeta(meta);
            dto.setLabels(labels);
            dto.setData(data);
            dto.setBackgroundColors(colors);

        } else {
            dto.setChartType("pie");
            long positive = answers.stream().filter(a -> aiSentimentScore(a.getAnswer()) > 0).count();
            long negative = answers.stream().filter(a -> aiSentimentScore(a.getAnswer()) < 0).count();
            long neutral  = answers.stream().filter(a -> aiSentimentScore(a.getAnswer()) == 0).count();
            dto.setLabels(Arrays.asList("Positive","Neutral","Negative"));
            dto.setData(Arrays.asList((double) positive,(double) neutral,(double) negative));
            dto.setBackgroundColors(Arrays.asList("green","gray","red"));
            dto.setMeta(Map.of("sentimentLegend","1=Positive,0=Neutral,-1=Negative"));
        }

        return dto;
    }

    private Double parseDoubleSafe(String s) {
        try { return s == null ? null : Double.parseDouble(s.trim()); } 
        catch (Exception e) { return null; }
    }

    private int aiSentimentScore(String text) {
        if (text == null || text.isBlank()) return 0;

        try {
            String prompt = "Classify this student feedback text sentiment as Positive, Neutral, or Negative. Respond with one word only: "
                    + text;

            Map<String,Object> requestBody = Map.of(
                "model", "google/gemma-3-27b-it:free",
                "messages", List.of(Map.of("role", "user", "content", prompt))
            );

            Map<?,?> response = webClient.post()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response != null) {
                List<?> choices = (List<?>) response.get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map<?,?> firstChoice = (Map<?,?>) choices.get(0);
                    Map<?,?> message = (Map<?,?>) firstChoice.get("message");
                    String aiText = (String) message.get("content");
                    if (aiText != null) aiText = aiText.toLowerCase().trim();
                    if (aiText.contains("positive")) return 1;
                    else if (aiText.contains("negative")) return -1;
                    else return 0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0; // fallback neutral
    }
}
