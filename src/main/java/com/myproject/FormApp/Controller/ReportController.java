package com.myproject.FormApp.Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.myproject.FormApp.Dto.ChartDataDTO;
import com.myproject.FormApp.Dto.ChartDataDTOAll;
import com.myproject.FormApp.Dto.ChartVisualizationDTO;
import com.myproject.FormApp.Model.Question;
import com.myproject.FormApp.Model.StudentFeedbackAnswer;
import com.myproject.FormApp.Model.Teacher;
import com.myproject.FormApp.Repository.QuestionRepository;
import com.myproject.FormApp.Repository.StudentFeedbackAnswerRepository;
import com.myproject.FormApp.Service.FeedbackReportService;
import com.myproject.FormApp.Service.ReportService;

import jakarta.servlet.http.HttpSession;

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
    
    
    /**
     * 📊 Advanced Dashboard Visualization
     */
    @GetMapping("/{feedbackId}")
    public String showFeedbackDashboard(@PathVariable Long feedbackId, Model model, HttpSession session) {
        
        // 1. Security Check
        Teacher teacher = (Teacher) session.getAttribute("loggedInTeacher");
        if (teacher == null) return "redirect:/login";

        // 2. Data Fetching
        List<ChartVisualizationDTO> individualCharts = feedbackReportService.buildIndividualQuestionCharts(feedbackId);
        ChartVisualizationDTO trendChart = feedbackReportService.buildOverallTrend(feedbackId);
        ChartDataDTO rangeChart = feedbackReportService.buildRangeDistributionChart(feedbackId);

        // 3. UI Model Attributes
        model.addAttribute("individualCharts", individualCharts);
        model.addAttribute("trendChart", trendChart);
        model.addAttribute("rangeChart", rangeChart);
        model.addAttribute("feedbackId", feedbackId);

        return "Teacher/reportVisualization";
    }

    /**
     * 📥 Download PDF Report
     */
    @GetMapping("/{feedbackId}/download")
    public ResponseEntity<byte[]> downloadReport(@PathVariable Long feedbackId, HttpSession session) {
        Teacher teacher = (Teacher) session.getAttribute("loggedInTeacher");
        if (teacher == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        try {
            byte[] pdfBytes = feedbackReportService.exportToPDFForVisuals(feedbackId);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=EduInsight_Report.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    

}
