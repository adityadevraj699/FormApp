package com.myproject.FormApp.Service;

import com.myproject.FormApp.Dto.ChartDataDTO;
import com.myproject.FormApp.Dto.ChartVisualizationDTO;
import com.myproject.FormApp.Model.Question;
import com.myproject.FormApp.Model.StudentFeedbackAnswer;
import com.myproject.FormApp.Repository.QuestionRepository;
import com.myproject.FormApp.Repository.StudentFeedbackAnswerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FeedbackReportService {

    @Autowired
    private StudentFeedbackAnswerRepository answerRepo;

    @Autowired
    private QuestionRepository questionRepo;

    @Autowired
    private ChartAnalysisService aiAnalysisService;

    @Autowired
    private ReportExportService reportExportService;

    /**
     * Build BAR charts for numeric questions
     */
    public List<ChartVisualizationDTO> buildBarCharts(Long feedbackId) {
        List<Question> questions = questionRepo.findByAnswerType(Question.AnswerType.NUMBER);
        List<ChartVisualizationDTO> charts = new ArrayList<ChartVisualizationDTO>();

        for (Question q : questions) {
            List<StudentFeedbackAnswer> answers = answerRepo.findByFeedbackIdAndQuestionId(feedbackId, q.getId());

            Map<Integer, Long> countMap = new HashMap<Integer, Long>();
            for (StudentFeedbackAnswer a : answers) {
                if (a.getAnswer() != null && !a.getAnswer().trim().isEmpty()) {
                    try {
                        Integer val = Integer.parseInt(a.getAnswer().trim());
                        Long current = countMap.get(val);
                        countMap.put(val, (current == null ? 1L : current + 1));
                    } catch (NumberFormatException e) {
                        // ignore invalid numeric input
                    }
                }
            }

            List<String> labels = new ArrayList<String>();
            List<Double> values = new ArrayList<Double>();
            List<Integer> sortedKeys = new ArrayList<Integer>(countMap.keySet());
            Collections.sort(sortedKeys);

            for (Integer key : sortedKeys) {
                labels.add(String.valueOf(key));
                values.add(countMap.get(key).doubleValue());
            }

            charts.add(new ChartVisualizationDTO("bar", q.getQuestionText(), labels, values));
        }

        return charts;
    }

    /**
     * Build LINE chart for average per numeric question
     */
    public ChartVisualizationDTO buildAverageLineChart(Long feedbackId) {
        List<Question> questions = questionRepo.findByAnswerType(Question.AnswerType.NUMBER);

        List<String> labels = new ArrayList<String>();
        List<Double> averages = new ArrayList<Double>();

        for (Question q : questions) {
            List<StudentFeedbackAnswer> answers = answerRepo.findByFeedbackIdAndQuestionId(feedbackId, q.getId());
            List<Integer> numericAnswers = new ArrayList<Integer>();

            for (StudentFeedbackAnswer a : answers) {
                if (a.getAnswer() != null && !a.getAnswer().trim().isEmpty()) {
                    try {
                        numericAnswers.add(Integer.parseInt(a.getAnswer().trim()));
                    } catch (NumberFormatException e) {
                        // ignore invalid
                    }
                }
            }

            double avg = 0.0;
            if (!numericAnswers.isEmpty()) {
                double sum = 0.0;
                for (Integer n : numericAnswers) {
                    sum += n;
                }
                avg = sum / numericAnswers.size();
            }

            labels.add(q.getQuestionText());
            averages.add(avg);
        }

        return new ChartVisualizationDTO("line", "Average Rating per Question", labels, averages);
    }

    /**
     * Build PIE chart for sentiment analysis on text answers
     */
    public ChartVisualizationDTO buildSentimentPieChart(Long feedbackId) {
        List<Question> textQuestions = questionRepo.findByAnswerType(Question.AnswerType.TEXT);
        List<String> allTextAnswers = new ArrayList<String>();

        for (Question q : textQuestions) {
            List<StudentFeedbackAnswer> answers = answerRepo.findByFeedbackIdAndQuestionId(feedbackId, q.getId());
            for (StudentFeedbackAnswer a : answers) {
                if (a.getAnswer() != null && !a.getAnswer().trim().isEmpty()) {
                    allTextAnswers.add(a.getAnswer().trim());
                }
            }
        }

        Map<String, Long> sentimentCounts = aiAnalysisService.analyzeSentiments(allTextAnswers);

        List<String> labels = new ArrayList<String>(sentimentCounts.keySet());
        List<Double> values = new ArrayList<Double>();
        for (String key : labels) {
            values.add(sentimentCounts.get(key).doubleValue());
        }

        return new ChartVisualizationDTO("pie", "Sentiment Analysis of Text Feedback", labels, values);
    }

    /**
     * Build RANGE DISTRIBUTION chart for numeric answers
     */
    public ChartDataDTO buildRangeDistributionChart(Long feedbackId) {
        List<Question> numberQuestions = questionRepo.findByAnswerType(Question.AnswerType.NUMBER);
        List<String> ratings = Arrays.asList("1", "2", "3", "4", "5");
        List<String> questionTitles = new ArrayList<String>();
        List<List<Integer>> distribution = new ArrayList<List<Integer>>();

        for (Question q : numberQuestions) {
            List<StudentFeedbackAnswer> answers = answerRepo.findByFeedbackIdAndQuestionId(feedbackId, q.getId());
            Map<Integer, Long> countMap = new HashMap<Integer, Long>();

            for (StudentFeedbackAnswer a : answers) {
                if (a.getAnswer() != null && !a.getAnswer().trim().isEmpty()) {
                    try {
                        Integer val = Integer.parseInt(a.getAnswer().trim());
                        Long current = countMap.get(val);
                        countMap.put(val, (current == null ? 1L : current + 1));
                    } catch (NumberFormatException e) {
                        // ignore invalid
                    }
                }
            }

            List<Integer> counts = new ArrayList<Integer>();
            for (int i = 1; i <= 5; i++) {
                counts.add(countMap.get(i) == null ? 0 : countMap.get(i).intValue());
            }

            questionTitles.add(q.getQuestionText());
            distribution.add(counts);
        }

        ChartDataDTO dto = new ChartDataDTO();
        dto.setChartType("range");
        dto.setTitle("Rating Distribution per Question");
        dto.setLabels(ratings);
        dto.setQuestions(questionTitles);
        dto.setValues(distribution);

        return dto;
    }

    /**
     * Export all visuals to PDF
     */
    public byte[] exportToPDFForVisuals(Long feedbackId) throws Exception {
        List<ChartVisualizationDTO> visuals = new ArrayList<ChartVisualizationDTO>();
        visuals.addAll(buildBarCharts(feedbackId));
        visuals.add(buildAverageLineChart(feedbackId));
        visuals.add(buildSentimentPieChart(feedbackId));

        // Include the range distribution chart
        ChartDataDTO rangeChart = buildRangeDistributionChart(feedbackId);

        // Call the correct method
        return reportExportService.exportProfessionalPDF(visuals, rangeChart);
    }

}
