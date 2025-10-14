package com.myproject.FormApp.Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.myproject.FormApp.Dto.ChartDataDTO;
import com.myproject.FormApp.Dto.ChartDataDTOAll;
import com.myproject.FormApp.Dto.ChartVisualizationDTO;
import com.myproject.FormApp.Model.Question;
import com.myproject.FormApp.Model.StudentFeedbackAnswer;
import com.myproject.FormApp.Repository.QuestionRepository;
import com.myproject.FormApp.Repository.StudentFeedbackAnswerRepository;
import com.myproject.FormApp.Service.FeedbackReportService;
import com.myproject.FormApp.Service.ReportService;

@Controller
@RequestMapping("/feedback/report")
public class ReportController {
	
	@Autowired
	private StudentFeedbackAnswerRepository answerRepo;
	
	@Autowired
    private FeedbackReportService feedbackReportService;

    private final ReportService reportService;
    private final QuestionRepository questionRepo;

    public ReportController(ReportService reportService, QuestionRepository questionRepo) {
        this.reportService = reportService;
        this.questionRepo = questionRepo;
    }

    @GetMapping("/{feedbackId}/question/{questionId}/student-chart")
    @ResponseBody
    public ResponseEntity<ChartDataDTO> getStudentLevelChart(
            @PathVariable Long feedbackId,
            @PathVariable Long questionId) {

        Question q = questionRepo.findById(questionId).orElseThrow();
        ChartDataDTO dto = reportService.buildStudentLevelChartFor(feedbackId, q);
        return ResponseEntity.ok(dto);
    }
    
    
    @GetMapping("/{feedbackId}")
    public String showFeedbackDashboard(@PathVariable Long feedbackId, Model model) {
        List<ChartVisualizationDTO> barCharts = feedbackReportService.buildBarCharts(feedbackId);
        ChartVisualizationDTO lineChart = feedbackReportService.buildAverageLineChart(feedbackId);
        ChartVisualizationDTO pieChart = feedbackReportService.buildSentimentPieChart(feedbackId);
        ChartDataDTO rangeChart = feedbackReportService.buildRangeDistributionChart(feedbackId); // ✅ Added

        model.addAttribute("barCharts", barCharts);
        model.addAttribute("lineChart", lineChart);
        model.addAttribute("pieChart", pieChart);
        model.addAttribute("rangeChart", rangeChart); // ✅ Added

        return "Teacher/reportVisualization";
    }


    @GetMapping("/{feedbackId}/download")
    public ResponseEntity<byte[]> downloadReport(@PathVariable Long feedbackId) throws Exception {
        byte[] pdfBytes = feedbackReportService.exportToPDFForVisuals(feedbackId);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=feedback-report.pdf")
                .header("Content-Type", "application/pdf")
                .body(pdfBytes);
    }


    

}
