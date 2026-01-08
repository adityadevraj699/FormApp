package com.myproject.FormApp.Service;

import com.myproject.FormApp.Dto.ChartDataDTO;
import com.myproject.FormApp.Dto.ChartVisualizationDTO;
import com.myproject.FormApp.Model.*;
import com.myproject.FormApp.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FeedbackReportService {

    @Autowired private StudentFeedbackAnswerRepository answerRepo;
    @Autowired private QuestionRepository questionRepo;
    @Autowired private FeedbackRepository feedbackRepo;
    @Autowired private FeedbackAnalysisRepository analysisRepo;
    @Autowired private AiAnalysisService aiService;
    @Autowired private ReportExportService reportExportService;

    /**
     * 1. Individual Charts (Bar for Numbers, Pie for Text)
     * केवल वही सवाल रेंडर करता है जिनका डेटा इस फीडबैक आईडी के लिए मौजूद है।
     */
    @Transactional(readOnly = true)
    public List<ChartVisualizationDTO> buildIndividualQuestionCharts(Long feedbackId) {
        List<ChartVisualizationDTO> charts = new ArrayList<>();
        List<Question> questions = questionRepo.findAll();

        for (Question q : questions) {
            List<StudentFeedbackAnswer> answers = answerRepo.findByFeedbackIdAndQuestionId(feedbackId, q.getId());
            
            // 🛡️ सुरक्षा: केवल वही सवाल लें जिसका इस feedbackId में जवाब हो
            if (answers == null || answers.isEmpty()) continue;

            if (q.getAnswerType() == Question.AnswerType.NUMBER) {
                charts.add(prepareBarChart(q, answers));
            } else if (q.getAnswerType() == Question.AnswerType.TEXT) {
                // पहले DB Cache चेक करें
                Optional<FeedbackAnalysis> existing = analysisRepo.findByFeedbackIdAndQuestionId(feedbackId, q.getId());
                
                if (existing.isPresent()) {
                    charts.add(preparePieChartFromDB(q, existing.get(), answers.size()));
                } else {
                    charts.add(preparePieChartWithAI(q, answers));
                }
            }
        }
        return charts;
    }

    private ChartVisualizationDTO prepareBarChart(Question q, List<StudentFeedbackAnswer> answers) {
        Map<Integer, Long> counts = new TreeMap<>();
        for (int i = 1; i <= 5; i++) counts.put(i, 0L);

        for (StudentFeedbackAnswer a : answers) {
            try {
                int val = Integer.parseInt(a.getAnswer().trim());
                if (val >= 1 && val <= 5) counts.put(val, counts.get(val) + 1);
            } catch (Exception ignored) {}
        }
        return new ChartVisualizationDTO("bar", q.getQuestionText(), 
            counts.keySet().stream().map(k -> "Rating " + k).collect(Collectors.toList()),
            counts.values().stream().map(Long::doubleValue).collect(Collectors.toList()));
    }

    private ChartVisualizationDTO preparePieChartFromDB(Question q, FeedbackAnalysis fa, int count) {
        List<String> labels = Arrays.asList("Positive", "Neutral", "Negative");
        double total = (double) count;

        List<Double> values;
        String sentiment = fa.getSentimentLabel() != null ? fa.getSentimentLabel().toUpperCase() : "NEUTRAL";
        
        if (sentiment.contains("POSITIVE")) values = Arrays.asList(total, 0.0, 0.0);
        else if (sentiment.contains("NEGATIVE")) values = Arrays.asList(0.0, 0.0, total);
        else values = Arrays.asList(0.0, total, 0.0);

        return new ChartVisualizationDTO("pie", q.getQuestionText(), labels, values);
    }

    private ChartVisualizationDTO preparePieChartWithAI(Question q, List<StudentFeedbackAnswer> answers) {
        List<String> texts = answers.stream().map(StudentFeedbackAnswer::getAnswer).collect(Collectors.toList());
        Map<String, Integer> sentimentMap = aiService.analyzeSentimentDistribution(q.getQuestionText(), texts);
        
        return new ChartVisualizationDTO("pie", q.getQuestionText(), 
            Arrays.asList("Positive", "Neutral", "Negative"),
            Arrays.asList((double)sentimentMap.getOrDefault("positive", 0), 
                          (double)sentimentMap.getOrDefault("neutral", 0), 
                          (double)sentimentMap.getOrDefault("negative", 0)));
    }

    /**
     * 2. Overall Trend (Line Chart)
     * सभी पैरामीटर्स का औसत स्कोर दिखाता है।
     */
    public ChartVisualizationDTO buildOverallTrend(Long feedbackId) {
        List<Question> numQuestions = questionRepo.findByAnswerType(Question.AnswerType.NUMBER);
        List<String> labels = new ArrayList<>();
        List<Double> averages = new ArrayList<>();

        for (Question q : numQuestions) {
            List<StudentFeedbackAnswer> answers = answerRepo.findByFeedbackIdAndQuestionId(feedbackId, q.getId());
            
            // 🛡️ केवल वही सवाल लें जिनका डेटा मौजूद है
            if (answers == null || answers.isEmpty()) continue;

            double avg = answers.stream()
                .mapToDouble(a -> {
                    try { return Double.parseDouble(a.getAnswer()); } catch (Exception e) { return 0.0; }
                }).average().orElse(0.0);

            labels.add(q.getQuestionText());
            averages.add(avg);
        }
        return new ChartVisualizationDTO("line", "Average Rating per Parameter", labels, averages);
    }

    /**
     * 3. Range Distribution (Matrix Chart) - FIXED
     * यह सुनिश्चित करता है कि अन्य फीडबैक के सवाल यहाँ न आएं।
     */
    public ChartDataDTO buildRangeDistributionChart(Long feedbackId) {
        List<Question> numberQuestions = questionRepo.findByAnswerType(Question.AnswerType.NUMBER);
        ChartDataDTO dto = new ChartDataDTO();
        List<String> ratings = Arrays.asList("1", "2", "3", "4", "5");
        List<String> titles = new ArrayList<>();
        List<List<Integer>> distribution = new ArrayList<>();

        for (Question q : numberQuestions) {
            List<StudentFeedbackAnswer> answers = answerRepo.findByFeedbackIdAndQuestionId(feedbackId, q.getId());
            
            // 🛡️ FIXED: अगर इस feedbackId के लिए कोई जवाब नहीं है, तो skip करें
            if (answers == null || answers.isEmpty()) continue;

            int[] counts = new int[5];
            for (StudentFeedbackAnswer a : answers) {
                try {
                    int v = Integer.parseInt(a.getAnswer().trim());
                    if (v >= 1 && v <= 5) counts[v - 1]++;
                } catch (Exception ignored) {}
            }
            
            titles.add(q.getQuestionText());
            distribution.add(Arrays.stream(counts).boxed().collect(Collectors.toList()));
        }

        dto.setChartType("range");
        dto.setTitle("Rating Distribution Matrix");
        dto.setLabels(ratings);
        dto.setQuestions(titles);
        dto.setValues(distribution);
        return dto;
    }

    /**
     * 4. PDF Export
     */
    public byte[] exportToPDFForVisuals(Long feedbackId) throws Exception {
        List<ChartVisualizationDTO> visuals = buildIndividualQuestionCharts(feedbackId);
        visuals.add(buildOverallTrend(feedbackId));
        ChartDataDTO rangeChart = buildRangeDistributionChart(feedbackId);
        return reportExportService.exportProfessionalPDF(visuals, rangeChart);
    }
}