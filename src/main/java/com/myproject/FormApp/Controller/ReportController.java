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
import com.myproject.FormApp.Model.Question;
import com.myproject.FormApp.Model.StudentFeedbackAnswer;
import com.myproject.FormApp.Repository.QuestionRepository;
import com.myproject.FormApp.Repository.StudentFeedbackAnswerRepository;
import com.myproject.FormApp.Service.ReportService;

@Controller
@RequestMapping("/feedback/report")
public class ReportController {
	
	@Autowired
	private StudentFeedbackAnswerRepository answerRepo;

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
    
    
   



}
