package com.myproject.FormApp.Service;

import java.util.*;

import org.springframework.stereotype.Service;

import com.myproject.FormApp.Dto.ChartDataDTO;
import com.myproject.FormApp.Model.FeedbackAnalysis;
import com.myproject.FormApp.Model.Question;
import com.myproject.FormApp.Model.StudentFeedbackAnswer;
import com.myproject.FormApp.Repository.FeedbackAnalysisRepository;
import com.myproject.FormApp.Repository.StudentFeedbackAnswerRepository;

@Service
public class ReportService {

    private final StudentFeedbackAnswerRepository answerRepo;
    private final FeedbackAnalysisRepository analysisRepo;

    public ReportService(StudentFeedbackAnswerRepository answerRepo,
                         FeedbackAnalysisRepository analysisRepo) {
        this.answerRepo = answerRepo;
        this.analysisRepo = analysisRepo;
    }

    public ChartDataDTO buildStudentLevelChartFor(Long feedbackId, Question question) {

        ChartDataDTO dto = new ChartDataDTO();

        // ================= NUMBER =================
        if (question.getAnswerType() == Question.AnswerType.NUMBER) {
            return buildNumericChart(feedbackId, question, dto);
        }

        // ================= TEXT / YES-NO =================
        FeedbackAnalysis fa = analysisRepo
                .findByFeedbackIdAndQuestionId(feedbackId, question.getId())
                .orElse(null);

        long positive = 0, neutral = 0, negative = 0;
        long total = Math.max(1,
                answerRepo.countByFeedbackIdAndQuestionId(feedbackId, question.getId()));

        if (fa != null) {
            switch (fa.getSentimentLabel()) {
                case "POSITIVE" -> positive = total;
                case "NEGATIVE" -> negative = total;
                default -> neutral = total;
            }
        } else {
            neutral = total; // safe fallback
        }

        dto.setChartType("pie");
        dto.setLabels(List.of(
                "Positive (" + percent(positive, total) + "%)",
                "Neutral ("  + percent(neutral, total)  + "%)",
                "Negative (" + percent(negative, total) + "%)"
        ));
        dto.setData(List.of(
                (double) positive,
                (double) neutral,
                (double) negative
        ));
        dto.setBackgroundColors(List.of("#4CAF50", "#9E9E9E", "#F44336"));
        dto.setMeta(Map.of(
                "questionText", question.getQuestionText(),
                "analysisSource", "Stored XAI Insight"
        ));

        return dto;
    }

    // ---------- helpers ----------
    private ChartDataDTO buildNumericChart(Long feedbackId, Question question, ChartDataDTO dto) {
        var answers = answerRepo.findByFeedbackIdAndQuestionId(feedbackId, question.getId());

        dto.setChartType("bar");
        dto.setLabels(Collections.nCopies(answers.size(), ""));
        dto.setData(answers.stream()
                .map(a -> parseDoubleSafe(a.getAnswer()))
                .toList());

        dto.setMeta(Map.of(
                "questionText", question.getQuestionText(),
                "rangeStart", question.getRangeStart(),
                "rangeEnd", question.getRangeEnd()
        ));
        return dto;
    }

    private Double parseDoubleSafe(String s) {
        try { return Double.parseDouble(s.trim()); }
        catch (Exception e) { return 0.0; }
    }

    private int percent(long v, long t) {
        return (int) Math.round((v * 100.0) / t);
    }
}
