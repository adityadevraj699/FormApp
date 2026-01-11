package com.myproject.FormApp.Controller;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.myproject.FormApp.Model.Admin;
import com.myproject.FormApp.Model.CurriculumTopic;
import com.myproject.FormApp.Model.EnrolledProgram;
import com.myproject.FormApp.Model.EnrolledProgram.ProgramStatus;
import com.myproject.FormApp.Model.FeedBackPhase;
import com.myproject.FormApp.Model.Feedback;
import com.myproject.FormApp.Model.FeedbackAnalysis;
import com.myproject.FormApp.Model.FeedbackQuestionCategory;
import com.myproject.FormApp.Model.Module;
import com.myproject.FormApp.Model.Program;
import com.myproject.FormApp.Model.Question;
import com.myproject.FormApp.Model.QuestionCatrgories;
import com.myproject.FormApp.Model.Student;
import com.myproject.FormApp.Model.StudentFeedbackAnswer;
import com.myproject.FormApp.Model.Teacher;
import com.myproject.FormApp.Model.Teacher.Status;
import com.myproject.FormApp.Model.TeacherAssign;
import com.myproject.FormApp.Repository.AdminRepository;
import com.myproject.FormApp.Repository.CurriculumTopicRepository;
import com.myproject.FormApp.Repository.EnrolledProgramRepository;
import com.myproject.FormApp.Repository.FeedBackPhaseRepository;
import com.myproject.FormApp.Repository.FeedbackAnalysisRepository;
import com.myproject.FormApp.Repository.FeedbackQuestionCategoryRepository;
import com.myproject.FormApp.Repository.FeedbackRepository;
import com.myproject.FormApp.Repository.ModuleRepository;
import com.myproject.FormApp.Repository.ProgramRepository;
import com.myproject.FormApp.Repository.QuestionCatrgoriesRepository;
import com.myproject.FormApp.Repository.QuestionRepository;
import com.myproject.FormApp.Repository.StudentFeedbackAnswerRepository;
import com.myproject.FormApp.Repository.StudentsRepository;
import com.myproject.FormApp.Repository.TeacherAssignRepository;
import com.myproject.FormApp.Repository.TeacherRepository;
import com.myproject.FormApp.Service.EmailService;


import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.HorizontalAlignment;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.SpiderWebPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
@Controller
@RequestMapping("/admin")
public class AdminController {

    private final StudentsRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final EmailService emailService;

    @Autowired
    private ProgramRepository programRepo;

    @Autowired
    private ModuleRepository moduleRepo;

    @Autowired
    private CurriculumTopicRepository curriculumTopicRepo;

    @Autowired
    private HttpSession session;
    @Autowired
    private TeacherAssignRepository teacherAssignRepo;
    
    @Autowired
    private QuestionCatrgoriesRepository questionCategoryRepo;
    
    @Autowired
    private QuestionRepository questionRepo;
    
    @Autowired
    private FeedBackPhaseRepository feedbackRepo;
    
    @Autowired
    private FeedbackRepository feedRepo;
    
    @Autowired
    private FeedbackQuestionCategoryRepository FeedbackQuestionCategoryRepo;
    
    @Autowired
    private AdminRepository adminRepo;
    
    @Autowired
    private EnrolledProgramRepository enrolledProgramRepo;
    
    @Autowired
    private StudentFeedbackAnswerRepository answerRepo;
    
    
    @Autowired
    private FeedbackAnalysisRepository feedbackAnalysisRepo;
    


    public AdminController(StudentsRepository studentRepository, TeacherRepository teacherRepository, EmailService emailService) {
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.emailService = emailService;
    }

    // ✅ Session Check Utility
    private boolean isLoggedIn() {
        return session.getAttribute("loggedInAdmin") != null;
    }

    private String redirectIfNotLoggedIn() {
        return "redirect:/"; // login page
    }

   @GetMapping("/dashboard")
public String showDashboard(Model model) {
    if (!isLoggedIn()) return "redirect:/";

    // --- Stats ---
    long totalStudents = studentRepository.count();
    long approvedTeachers = teacherRepository.findByStatus(Teacher.Status.APPROVED).size();
    long activeFeedbacksCount = feedRepo.findAll().stream()
            .filter(f -> !f.getStartDate().isAfter(LocalDate.now()) && !f.getEndDate().isBefore(LocalDate.now()))
            .count();

    // --- AI Analytics ---
    List<FeedbackAnalysis> allAnalyses = feedbackAnalysisRepo.findAll();
    long posCount = allAnalyses.stream().filter(a -> "POSITIVE".equalsIgnoreCase(a.getSentimentLabel())).count();
    long neuCount = allAnalyses.stream().filter(a -> "NEUTRAL".equalsIgnoreCase(a.getSentimentLabel())).count();
    long negCount = allAnalyses.stream().filter(a -> "NEGATIVE".equalsIgnoreCase(a.getSentimentLabel())).count();

    double globalAvgRating = allAnalyses.stream()
            .filter(a -> a.getAvgNumeric() != null)
            .mapToDouble(FeedbackAnalysis::getAvgNumeric)
            .average().orElse(0.0);

    // --- Dynamic Status Check ---
    Set<Long> analyzedProgramIds = allAnalyses.stream()
            .map(a -> a.getFeedback().getProgram().getId())
            .collect(Collectors.toSet());

    // --- CORRECTED PARTICIPATION LOGIC ---
    // Denominator: Total Enrollments (Agar 1 bacha 5 courses mein hai toh ye 5 count karega)
    long totalPotentialFeedbacks = enrolledProgramRepo.count(); 

    // Numerator: Total actual feedback completions (Student + Feedback combination)
    long totalActualFeedbacks = answerRepo.countTotalSubmittedFeedbacks();
    
    // Debugging print
    System.out.println("Total Potential: " + totalPotentialFeedbacks + " | Total Actual: " + totalActualFeedbacks);

    double participationRate = totalPotentialFeedbacks == 0 ? 0 : (double) totalActualFeedbacks / totalPotentialFeedbacks * 100;

    // --- Dynamic AI Recommendations ---
    List<String> aiSuggestions = new ArrayList<>();
    if (negCount > posCount * 0.15) {
        aiSuggestions.add("Warning: Negative sentiment exceeds 15% threshold. Review module delivery.");
    }
    allAnalyses.stream()
            .filter(a -> "NEGATIVE".equalsIgnoreCase(a.getSentimentLabel()) && a.getSuggestions() != null)
            .limit(2)
            .forEach(a -> aiSuggestions.add("Action: " + a.getSuggestions()));

    // --- Model Attributes ---
    model.addAttribute("totalStudents", totalStudents);
    model.addAttribute("approvedTeachers", approvedTeachers);
    model.addAttribute("activeFeedbacks", activeFeedbacksCount);
    model.addAttribute("globalAvgRating", String.format("%.2f", globalAvgRating));
    model.addAttribute("positivePercent", allAnalyses.isEmpty() ? 0 : (posCount * 100 / allAnalyses.size()));
    model.addAttribute("posCount", posCount);
    model.addAttribute("neuCount", neuCount);
    model.addAttribute("negCount", negCount);
    model.addAttribute("participationRate", String.format("%.1f", participationRate));
    model.addAttribute("aiSuggestions", aiSuggestions);
    model.addAttribute("analyzedProgramIds", analyzedProgramIds);
    model.addAttribute("programList", teacherAssignRepo.findAll());
    model.addAttribute("criticalAlerts", allAnalyses.stream().filter(a -> (a.getAvgNumeric() != null && a.getAvgNumeric() < 3.0) || "NEGATIVE".equalsIgnoreCase(a.getSentimentLabel())).collect(Collectors.toList()));

    return "admin/Dashboard";
}
   
   @GetMapping("/viewAnalysis/{programId}")
public String viewProgramAnalysis(@PathVariable("programId") Long programId, Model model) {
    if (!isLoggedIn()) return "redirect:/";

    // 1. Program details
    Program program = programRepo.findById(programId)
            .orElseThrow(() -> new RuntimeException("Program not found"));

    // 2. Fetch all analyses for this specific program
    List<FeedbackAnalysis> programAnalyses = feedbackAnalysisRepo.findAll().stream()
            .filter(a -> a.getFeedback().getProgram().getId().equals(programId))
            .collect(Collectors.toList());

    if (programAnalyses.isEmpty()) {
        model.addAttribute("error", "Is program ke liye abhi koi AI analysis available nahi hai.");
        model.addAttribute("program", program);
        return "admin/AnalysisReport"; 
    }

    // --- DATA FOR PIE CHART ---
    long posCount = programAnalyses.stream().filter(a -> "POSITIVE".equalsIgnoreCase(a.getSentimentLabel())).count();
    long neuCount = programAnalyses.stream().filter(a -> "NEUTRAL".equalsIgnoreCase(a.getSentimentLabel())).count();
    long negCount = programAnalyses.stream().filter(a -> "NEGATIVE".equalsIgnoreCase(a.getSentimentLabel())).count();

    // --- DATA FOR BAR CHART (Question-wise Rating) ---
    // LinkedHashMap use kiya hai taaki questions ka order maintain rahe
    Map<String, Double> questionRatings = new java.util.LinkedHashMap<>();
    programAnalyses.stream()
            .filter(a -> a.getQuestion().getAnswerType() == Question.AnswerType.NUMBER)
            .forEach(a -> {
                questionRatings.put(a.getQuestion().getQuestionText(), a.getAvgNumeric() != null ? a.getAvgNumeric() : 0.0);
            });

    // --- DATA FOR TEXT ANALYSIS (AI Summaries) ---
    List<Map<String, String>> aiInsights = programAnalyses.stream()
            .map(a -> {
                Map<String, String> map = new HashMap<>();
                // ERROR FIXED: map.put() used instead of map.add()
                map.put("question", a.getQuestion().getQuestionText());
                map.put("summary", a.getSummary() != null ? a.getSummary() : "No summary generated");
                map.put("suggestions", a.getSuggestions() != null ? a.getSuggestions() : "N/A");
                map.put("phrases", a.getKeyPhrases() != null ? a.getKeyPhrases() : "N/A");
                map.put("sentiment", a.getSentimentLabel());
                return map;
            }).collect(Collectors.toList());

    // --- GLOBAL KPI ---
    double programAvg = programAnalyses.stream()
            .filter(a -> a.getAvgNumeric() != null)
            .mapToDouble(FeedbackAnalysis::getAvgNumeric)
            .average().orElse(0.0);

    // 3. Model Attributes
    model.addAttribute("program", program);
    model.addAttribute("posCount", posCount);
    model.addAttribute("neuCount", neuCount);
    model.addAttribute("negCount", negCount);
    model.addAttribute("questionRatings", questionRatings); 
    model.addAttribute("aiInsights", aiInsights);
    model.addAttribute("programAvg", String.format("%.2f", programAvg));

    return "admin/AnalysisReport"; 
}
   
@GetMapping("/export-master-list")
public ResponseEntity<byte[]> exportMasterList() {
    try (Workbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook();
         ByteArrayOutputStream out = new ByteArrayOutputStream()) {

        Sheet sheet = workbook.createSheet("EduInsight Master Analytics");

        // 1. Styling
        org.apache.poi.ss.usermodel.CellStyle headerStyle = createHeaderStyle(workbook);
        org.apache.poi.ss.usermodel.CellStyle percentStyle = workbook.createCellStyle();
        percentStyle.setDataFormat(workbook.createDataFormat().getFormat("0.00%"));

        // 2. Updated Headers with Sentiment Percentages
        String[] columns = {
            "Program ID", "Program Name", "Teacher", "Timeline", 
            "Enrolled", "Phases", "Global KPI (Avg)", 
            "Positive %", "Neutral %", "Negative %", "Majority Sentiment"
        };
        
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerStyle);
        }

        // 3. Data Fetching
        List<TeacherAssign> assignments = teacherAssignRepo.findAll();
        List<FeedbackAnalysis> allAnalyses = feedbackAnalysisRepo.findAll();
        List<EnrolledProgram> allEnrollments = enrolledProgramRepo.findAll();
        List<Feedback> allFeedbacks = feedRepo.findAll();

        int rowIdx = 1;
        for (TeacherAssign assign : assignments) {
            Row row = sheet.createRow(rowIdx++);
            Program program = assign.getProgram();
            Long pId = program.getId();

            // Basic Information
            row.createCell(0).setCellValue(pId);
            row.createCell(1).setCellValue(program.getTrainingProgram());
            row.createCell(2).setCellValue(assign.getTeacher().getName());
            row.createCell(3).setCellValue(program.getStartDate() + " to " + program.getEndDate());

            // Enrollment & Phases
            long studentCount = allEnrollments.stream()
                    .filter(e -> e.getProgram().getId().equals(pId)).count();
            long phaseCount = allFeedbacks.stream()
                    .filter(f -> f.getProgram().getId().equals(pId)).count();
            
            row.createCell(4).setCellValue(studentCount);
            row.createCell(5).setCellValue(phaseCount);

            // --- AI ANALYTICS DATA ---
            List<FeedbackAnalysis> progAnalyses = allAnalyses.stream()
                    .filter(a -> a.getFeedback().getProgram().getId().equals(pId))
                    .collect(Collectors.toList());

            if (!progAnalyses.isEmpty()) {
                // Global KPI Avg
                double programAvg = progAnalyses.stream()
                        .filter(a -> a.getAvgNumeric() != null)
                        .mapToDouble(FeedbackAnalysis::getAvgNumeric)
                        .average().orElse(0.0);
                row.createCell(6).setCellValue(Double.parseDouble(String.format("%.2f", programAvg)));

                // Sentiment Calculation
                double totalAnalyses = progAnalyses.size();
                long pos = progAnalyses.stream().filter(a -> "POSITIVE".equalsIgnoreCase(a.getSentimentLabel())).count();
                long neu = progAnalyses.stream().filter(a -> "NEUTRAL".equalsIgnoreCase(a.getSentimentLabel())).count();
                long neg = progAnalyses.stream().filter(a -> "NEGATIVE".equalsIgnoreCase(a.getSentimentLabel())).count();

                // 🟢 Percentage Calculation (e.g. 0.85 for 85%)
                Cell cellPos = row.createCell(7);
                cellPos.setCellValue(pos / totalAnalyses);
                cellPos.setCellStyle(percentStyle);

                Cell cellNeu = row.createCell(8);
                cellNeu.setCellValue(neu / totalAnalyses);
                cellNeu.setCellStyle(percentStyle);

                Cell cellNeg = row.createCell(9);
                cellNeg.setCellValue(neg / totalAnalyses);
                cellNeg.setCellStyle(percentStyle);

                // Majority Sentiment Label
                String majority = (pos >= neu && pos >= neg) ? "POSITIVE" : (neu >= neg ? "NEUTRAL" : "NEGATIVE");
                row.createCell(10).setCellValue(majority);

            } else {
                row.createCell(6).setCellValue(0.00);
                row.createCell(7).setCellValue(0);
                row.createCell(8).setCellValue(0);
                row.createCell(9).setCellValue(0);
                row.createCell(10).setCellValue("NO DATA");
            }
        }

        // Auto-sizing columns
        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }

        workbook.write(out);
        String filename = "EduInsight_Sentiment_Audit_" + LocalDate.now() + ".xlsx";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(out.toByteArray());

    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.internalServerError().build();
    }
}

// Helper method for styling
private org.apache.poi.ss.usermodel.CellStyle createHeaderStyle(Workbook workbook) {
    org.apache.poi.ss.usermodel.CellStyle style = workbook.createCellStyle();
    org.apache.poi.ss.usermodel.Font font = workbook.createFont();
    font.setBold(true);
    font.setColor(org.apache.poi.ss.usermodel.IndexedColors.WHITE.getIndex());
    style.setFont(font);
    style.setFillForegroundColor(org.apache.poi.ss.usermodel.IndexedColors.DARK_BLUE.getIndex());
    style.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);
    return style;
}
    
    @GetMapping("/programDetail/{id}")
public String programDetail(
        @PathVariable Long id,
        @RequestParam(value = "status", required = false) String status,
        @RequestParam(value = "rollNo", required = false) String rollNo,
        @RequestParam(value = "page", required = false, defaultValue = "0") int page,
        Model model) {

    Program program = programRepo.findById(id)
                            .orElseThrow(() -> new RuntimeException("Program not found"));

    List<Module> modules = moduleRepo.findByProgramId(id);

    Map<Long, List<CurriculumTopic>> moduleTopicsMap = new HashMap<>();
    for (Module module : modules) {
        List<CurriculumTopic> topics = curriculumTopicRepo.findByModuleId(module.getId());
        moduleTopicsMap.put(module.getId(), topics);
    }

    List<TeacherAssign> teacherAssignments = teacherAssignRepo.findAllByProgramId(id);

    // Enrolled students with pagination + filtering
    Pageable pageable = PageRequest.of(page, 10); // 10 records per page
    Page<EnrolledProgram> enrolledPage;

    if ((status == null || status.equals("ALL")) && (rollNo == null || rollNo.isEmpty())) {
        enrolledPage = enrolledProgramRepo.findByProgramId(id, pageable);
    } else if (status != null && !status.equals("ALL") && (rollNo == null || rollNo.isEmpty())) {
        enrolledPage = enrolledProgramRepo.findByProgramIdAndStatus(id, EnrolledProgram.ProgramStatus.valueOf(status), pageable);
    } else if ((status == null || status.equals("ALL")) && rollNo != null && !rollNo.isEmpty()) {
        enrolledPage = enrolledProgramRepo.findByProgramIdAndStudentRollNoContaining(id, rollNo, pageable);
    } else {
        enrolledPage = enrolledProgramRepo.findByProgramIdAndStatusAndStudentRollNoContaining(
                id, EnrolledProgram.ProgramStatus.valueOf(status), rollNo, pageable);
    }

    model.addAttribute("program", program);
    model.addAttribute("modules", modules);
    model.addAttribute("teacherAssignments", teacherAssignments);
    model.addAttribute("moduleTopicsMap", moduleTopicsMap);
    model.addAttribute("enrolledStudents", enrolledPage.getContent());
    model.addAttribute("enrolledCount", enrolledPage.getTotalElements());
    model.addAttribute("currentPage", page);
    model.addAttribute("totalPages", enrolledPage.getTotalPages());
    model.addAttribute("selectedStatus", status != null ? status : "ALL");
    model.addAttribute("searchRollNo", rollNo != null ? rollNo : "");

    return "admin/programDetail";
}






    // ---------------------- PROGRAM ----------------------
    @GetMapping("/program")
    public String showProgram(Model model) {
        if (!isLoggedIn()) return redirectIfNotLoggedIn();
        return "admin/Program";
    }


    @PostMapping("/program")
    public String createProgram(@ModelAttribute Program program, RedirectAttributes redirectAttributes) {
        programRepo.save(program);
        redirectAttributes.addFlashAttribute("serverMessage", "Program created successfully!");
        return "redirect:/admin/program";
    }

    // ---------------------- MODULE ----------------------
    @GetMapping("/module")
    public String showModule(Model model) {
        if (!isLoggedIn()) return redirectIfNotLoggedIn();

        List<Program> programs = programRepo.findAll();
        model.addAttribute("programs", programs);
        model.addAttribute("module", new Module());
        return "admin/module";
    }

    @PostMapping("/module")
    public String saveModule(@RequestParam("trainingProgram") Long programId,
                             @RequestParam("moduleNames") List<String> moduleNames,
                             RedirectAttributes redirectAttributes) {

        Program program = programRepo.findById(programId).orElseThrow();

        for (String name : moduleNames) {
            if (name != null && !name.trim().isEmpty()) {
                Module module = new Module();
                module.setModuleName(name.trim());
                module.setProgram(program);
                moduleRepo.save(module);
            }
        }

        redirectAttributes.addFlashAttribute("serverMessageModule", "Modules created successfully!");
        return "redirect:/admin/module";
    }


 // ---------------------- CURRICULUM TOPIC ----------------------
    @GetMapping("/curriculumTopic")
    public String showCurriculumTopicForm(Model model) {
        if (!isLoggedIn()) return redirectIfNotLoggedIn();

        List<Module> modules = moduleRepo.findAll();
        model.addAttribute("modules", modules);           // directly send all modules
        model.addAttribute("curriculumTopic", new CurriculumTopic());

        return "admin/CurriculumTopic";
    }

    @PostMapping("/curriculumTopic")
    public String saveCurriculumTopic(@RequestParam Long moduleId,
                                      @RequestParam("topicNames") List<String> topicNames,
                                      RedirectAttributes redirectAttributes) {
        Module module = moduleRepo.findById(moduleId).orElseThrow();

        for (String name : topicNames) {
            if (name != null && !name.trim().isEmpty()) {
                CurriculumTopic topic = new CurriculumTopic();
                topic.setModule(module);
                topic.setTopicName(name.trim());
                curriculumTopicRepo.save(topic);
            }
        }

        redirectAttributes.addFlashAttribute("serverMessage", "Curriculum Topics created successfully!");
        return "redirect:/admin/curriculumTopic";
    }

 // ---------------------- STUDENT ----------------------
    @GetMapping("/student")
    public String showStudent(
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "rollNo", required = false) String rollNo,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            Model model) {

        if (!isLoggedIn()) return redirectIfNotLoggedIn();

        Pageable pageable = PageRequest.of(page, size, Sort.by("rollNo").ascending());
        Page<Student> studentsPage; // Rename kiya taaki confusion na ho

        // Logic for filtering
        if ((status == null || status.equals("ALL")) && (rollNo == null || rollNo.isEmpty())) {
            studentsPage = studentRepository.findAll(pageable);
        } else if (status != null && !status.equals("ALL") && (rollNo == null || rollNo.isEmpty())) {
            studentsPage = studentRepository.findByStatus(Student.Status.valueOf(status), pageable);
        } else if ((status == null || status.equals("ALL")) && rollNo != null && !rollNo.isEmpty()) {
            studentsPage = studentRepository.findByRollNoContaining(rollNo, pageable);
        } else {
            studentsPage = studentRepository.findByStatusAndRollNoContaining(Student.Status.valueOf(status), rollNo, pageable);
        }

        // --- Mukhya Badlav (Total Count Logic) ---
        
        // 1. Total Students based on current filter (Search/Status ke baad kitne bache)
        model.addAttribute("totalFilteredStudents", studentsPage.getTotalElements()); 
        
        // 2. Absolute total in database (Bina kisi filter ke - Badge ke liye)
        model.addAttribute("absoluteTotal", studentRepository.count());

        model.addAttribute("students", studentsPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", studentsPage.getTotalPages());
        model.addAttribute("selectedStatus", status != null ? status : "ALL");
        model.addAttribute("searchRollNo", rollNo != null ? rollNo : "");

        return "admin/Student";
    }
    @PostMapping("/student/updateStatus/{id}")
    public String updateStudentStatus(@PathVariable Long id,
                                      @RequestParam("status") Student.Status status,
                                      RedirectAttributes attr) {
        if (!isLoggedIn()) return redirectIfNotLoggedIn();

        Student student = studentRepository.findById(id).orElseThrow();
        student.setStatus(status);
        studentRepository.save(student);

        emailService.sendStudentStatusUpdate(student.getEmail(), student.getName(), student.getRollNo(),
                student.getRole().name(), status.name());

        attr.addFlashAttribute("msg", "Student Status UPDATED Successfully & Email Sent");
        return "redirect:/admin/student";
    }

	@GetMapping("/student/details/{id}")
public String showStudentDetails(@PathVariable Long id, Model model) {
    if (!isLoggedIn()) return "redirect:/login";

    Student student = studentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Student not found"));

    List<EnrolledProgram> enrollments = enrolledProgramRepo.findByStudent(student);
    
    List<Map<String, Object>> programStats = new ArrayList<>();
    int totalFeedbackCreatedForStudent = 0;
    int totalFeedbackCompletedByStudent = 0;

    for (EnrolledProgram enrollment : enrollments) {
        // 🟢 SAFETY CHECK: Pehle Program nikalien
        Program prog = enrollment.getProgram();
        
        if (prog != null) {
            Map<String, Object> stat = new HashMap<>();
            
            // Yahan error tab aata hai agar trainingProgram field ka getter sahi na ho
            stat.put("programName", prog.getTrainingProgram());

            // A. Total Feedback Phases Created in this Program
            long createdInProg = feedRepo.countByProgramId(prog.getId());
            totalFeedbackCreatedForStudent += createdInProg;

            // B. Feedback phases completed by THIS student
            long completedInProg = answerRepo.countUniqueFeedbacksByProgramAndStudent(prog, student);
            totalFeedbackCompletedByStudent += completedInProg;

            stat.put("created", createdInProg);
            stat.put("completed", completedInProg);
            programStats.add(stat);
        }
    }

    double activityRate = (totalFeedbackCreatedForStudent > 0) 
            ? ((double) totalFeedbackCompletedByStudent / totalFeedbackCreatedForStudent) * 100 
            : 0;

    model.addAttribute("student", student);
    model.addAttribute("enrollments", enrollments);
    model.addAttribute("programStats", programStats);
    model.addAttribute("totalEnrolled", enrollments.size());
    model.addAttribute("totalCreated", totalFeedbackCreatedForStudent);
    model.addAttribute("totalCompleted", totalFeedbackCompletedByStudent);
    model.addAttribute("activityRate", Math.round(activityRate));

    return "admin/StudentDetails";
}

    // ---------------------- TEACHER ----------------------
    @GetMapping("/teacher")
    public String showTeacher(@RequestParam(value = "status", required = false) String status,
                              @RequestParam(value = "employeeId", required = false) String employeeId,
                              Model model) {
        if (!isLoggedIn()) return redirectIfNotLoggedIn();

        List<Teacher> teachers;

        if ((status == null || status.equals("ALL")) && (employeeId == null || employeeId.isEmpty())) {
            teachers = teacherRepository.findAll();
        } else if (status != null && !status.equals("ALL") && (employeeId == null || employeeId.isEmpty())) {
            teachers = teacherRepository.findByStatus(Teacher.Status.valueOf(status));
        } else if ((status == null || status.equals("ALL")) && employeeId != null && !employeeId.isEmpty()) {
            teachers = teacherRepository.findByEmployeeIdContaining(employeeId);
        } else {
            teachers = teacherRepository.findByStatusAndEmployeeIdContaining(Teacher.Status.valueOf(status), employeeId);
        }

        model.addAttribute("teachers", teachers);
        model.addAttribute("selectedStatus", status != null ? status : "ALL");
        model.addAttribute("searchEmployeeId", employeeId != null ? employeeId : "");
        return "admin/Teacher";
    }
    
@GetMapping("/leaderboard")
public String showTeacherLeaderBoard(Model model) {
    if (!isLoggedIn()) return "redirect:/";

    List<Teacher> allTeachers = teacherRepository.findByStatus(Teacher.Status.APPROVED);
    List<Map<String, Object>> leaderboardData = new ArrayList<>();

    for (Teacher teacher : allTeachers) {
        Map<String, Object> stats = new HashMap<>();
        List<TeacherAssign> assignments = teacherAssignRepo.findByTeacherId(teacher.getId());
        
        List<Map<String, Object>> programBreakdown = new ArrayList<>(); 
        // 🟢 Naya Structure: Program wise list of ratings
        Map<String, List<Double>> programTimeline = new LinkedHashMap<>(); 
        List<Map<String, Object>> participationTable = new ArrayList<>();
        
        List<Long> allFeedbackIds = new ArrayList<>();
        long totalTeacherEnrolledStudents = 0;
        
        for (TeacherAssign ta : assignments) {
            Program prog = ta.getProgram();
            long progEnrolled = enrolledProgramRepo.countByProgramAndStatus(prog, EnrolledProgram.ProgramStatus.APPROVED);
            totalTeacherEnrolledStudents += progEnrolled;
            
            Map<String, Object> row = new HashMap<>();
            row.put("programName", prog.getTrainingProgram());
            row.put("totalEnrolled", progEnrolled);
            
            List<Map<String, String>> phaseData = new ArrayList<>();
            List<Double> currentProgRatings = new ArrayList<>();
            
            double progSum = 0;
            int progCount = 0;
            List<Feedback> feedbacks = feedRepo.findByProgramId(prog.getId());
            
            for (Feedback fb : feedbacks) {
                allFeedbackIds.add(fb.getId());
                Double cycleAvg = answerRepo.getAverageRatingByFeedback(fb.getId());
                double safeVal = (cycleAvg != null) ? cycleAvg : 0.0;
                
                long cycleParticipated = answerRepo.countUniqueStudentsByFeedbackId(fb.getId());
                
                Map<String, String> phaseInfo = new HashMap<>();
                phaseInfo.put("phaseName", fb.getFeedbackPhase().getPhaseName());
                phaseInfo.put("ratio", cycleParticipated + " / " + progEnrolled);
                phaseData.add(phaseInfo);

                // 🟢 Rating ko current program ki list mein add karein
                currentProgRatings.add(safeVal);

                if (safeVal > 0) {
                    progSum += safeVal;
                    progCount++;
                }
            }
            
            row.put("phases", phaseData); 
            participationTable.add(row);
            
            // 🟢 Program wise ratings ko timeline map mein save karein
            programTimeline.put(prog.getTrainingProgram(), currentProgRatings);

            programBreakdown.add(Map.of(
                "programName", prog.getTrainingProgram(),
                "avgPerformance", progCount > 0 ? (progSum / progCount) : 0.0
            ));
        }

        // --- Metrics Calculation (Baki same hai) ---
        long totalUniqueActiveStudents = 0;
        long totalPos = 0, totalNeg = 0, totalNeu = 0;
        double globalRatingSum = 0;
        int globalRatingEntries = 0;

        if (!allFeedbackIds.isEmpty()) {
            totalUniqueActiveStudents = answerRepo.countUniqueStudentsInFeedbackList(allFeedbackIds);
            for (Long fid : allFeedbackIds) {
                Double avg = answerRepo.getAverageRatingByFeedback(fid);
                if (avg != null && avg > 0) {
                    globalRatingSum += avg;
                    globalRatingEntries++;
                }
                totalPos += answerRepo.countBySentimentAndFeedback(fid, "POSITIVE");
                totalNeg += answerRepo.countBySentimentAndFeedback(fid, "NEGATIVE");
                totalNeu += answerRepo.countBySentimentAndFeedback(fid, "NEUTRAL");
            }
        }

        double finalGlobalAvg = (globalRatingEntries > 0) ? (globalRatingSum / globalRatingEntries) : 0.0;
        double sentimentPercent = (totalPos + totalNeg + totalNeu > 0) ? (double) totalPos / (totalPos + totalNeg + totalNeu) * 100 : 0;
        double pedagogicalPower = (finalGlobalAvg * 10) + (sentimentPercent * 0.5);

        stats.put("teacher", teacher);
        stats.put("avgRating", String.format("%.2f", finalGlobalAvg));
        stats.put("pedagogicalPower", Math.round(pedagogicalPower * 10) / 10.0);
        stats.put("sentimentScore", Math.round(sentimentPercent));
        stats.put("totalResponses", totalUniqueActiveStudents + " / " + totalTeacherEnrolledStudents);
        stats.put("programBreakdown", programBreakdown);
        // 🟢 Ab timeline map jayega list nahi
        stats.put("cycleTimeline", programTimeline); 
        stats.put("participationTable", participationTable); 
        stats.put("topStrength", (finalGlobalAvg > 4.0) ? "High Student Engagement" : "Consistent Delivery");

        leaderboardData.add(stats);
    }

    leaderboardData.sort((a, b) -> Double.compare(
            Double.parseDouble(b.get("pedagogicalPower").toString()), 
            Double.parseDouble(a.get("pedagogicalPower").toString())));

    model.addAttribute("leaderboard", leaderboardData);
    if (!leaderboardData.isEmpty()) model.addAttribute("topTeacherAnalytics", leaderboardData.get(0));
    model.addAttribute("generatedAt", LocalDateTime.now());

    return "admin/TeacherLeader";
}
   


@PostMapping("/teacher/updateStatus/{id}")
    public String updateTeacherStatus(@PathVariable Long id,
                                      @RequestParam("status") Teacher.Status status,
                                      RedirectAttributes attr) {
        if (!isLoggedIn()) return redirectIfNotLoggedIn();

        Teacher teacher = teacherRepository.findById(id).orElseThrow();
        teacher.setStatus(status);
        teacherRepository.save(teacher);

        emailService.sendTeacherStatusUpdate(teacher.getEmail(), teacher.getName(), teacher.getEmployeeId(),
                status.name(), teacher.getEmail());

        attr.addFlashAttribute("msg", "Teacher Status UPDATED Successfully & Email Sent");
        return "redirect:/admin/teacher";
    }

@GetMapping("/teacher/details/{id}")
public String showTeacherDetails(@PathVariable Long id, Model model) {
    if (!isLoggedIn()) return "redirect:/login";

    Teacher teacher = teacherRepository.findById(id).orElseThrow();
    
    // 1. Teacher ke assigned programs fetch karein
    List<TeacherAssign> assignments = teacherAssignRepo.findByTeacherId(id);
    
    // 2. Performance Stats (Jaise humne report mein calculate kiya tha)
    Map<String, Object> stats = getTeacherStats(id); // Global KPI, Sentiment, etc.
    
    model.addAttribute("teacher", teacher);
    model.addAttribute("assignments", assignments);
    model.addAttribute("stats", stats);
    model.addAttribute("participationData", stats.get("participationTable")); // For Charts
    
    return "admin/TeacherDetails";
}
    
    
 // ---------------------- TEACHER ASSIGN ----------------------
@GetMapping("/teacherManage")
public String showTeacherAssignForm(Model model) {
    if (!isLoggedIn()) return redirectIfNotLoggedIn();

    List<Teacher> teachers = teacherRepository.findByStatus(Teacher.Status.APPROVED);
    List<Program> programs = programRepo.findAll();

    // 🟢 Map to store analytics for each teacher
    Map<Long, Map<String, Object>> teacherAnalyticsMap = new HashMap<>();

    for (Teacher t : teachers) {
        Map<String, Object> stats = new HashMap<>();
        
        // Teacher ke purane assignments dhoondein
        List<TeacherAssign> pastAssigns = teacherAssignRepo.findByTeacherId(t.getId());
        
        if (pastAssigns.isEmpty()) {
            stats.put("status", "NO_ASSIGNMENT");
            stats.put("summary", "New Faculty: No programs assigned yet. Primary audit pending.");
        } else {
            // Stats fetch karein (Humara helper method use karke)
            Map<String, Object> performance = getTeacherStats(t.getId());
            
            // Check karein ki feedback hua hai ya nahi (totalResponses string "0 / X" format mein hota hai)
            String responses = performance.get("totalResponses").toString();
            if (responses.startsWith("0 /")) {
                stats.put("status", "PENDING_FEEDBACK");
                stats.put("summary", "Assignment Active: Programs assigned but no student feedback received yet.");
            } else {
                stats.put("status", "AUDITED");
                stats.put("avgRating", performance.get("avgRating"));
                stats.put("sentiment", performance.get("sentimentScore") + "%");
                stats.put("power", performance.get("pedagogicalPower"));
                
                // 🟢 AI Summary based on SDG-4 Goals
                double power = Double.parseDouble(performance.get("pedagogicalPower").toString());
                String aiSummary;
                if (power >= 85) aiSummary = "Exemplary Performance: Strongly supports SDG-4 targets for quality pedagogy.";
                else if (power >= 70) aiSummary = "Proficient: Consistent delivery. Good fit for advanced modules.";
                else aiSummary = "Improvement Required: Pedagogical gaps identified. Recommend mentoring before complex assignments.";
                
                stats.put("summary", aiSummary);
            }
        }
        teacherAnalyticsMap.put(t.getId(), stats);
    }

    model.addAttribute("teachers", teachers);
    model.addAttribute("programs", programs);
    model.addAttribute("teacherAnalytics", teacherAnalyticsMap); // 🟢 Frontend ko analytics bhejein
    model.addAttribute("teacherAssign", new TeacherAssign());

    return "admin/TeacherManage";
}

    @PostMapping("/teacherManage")
    public String saveTeacherAssign(@RequestParam Long teacherId,
                                    @RequestParam Long programId,
                                    RedirectAttributes redirectAttributes) {

        if (!isLoggedIn()) return redirectIfNotLoggedIn();

        Teacher teacher = teacherRepository.findById(teacherId).orElseThrow();
        Program program = programRepo.findById(programId).orElseThrow();

        // check if program already assigned
        List<TeacherAssign> existingAssigns = teacherAssignRepo.findByProgramId(programId);

        if (!existingAssigns.isEmpty()) {
            TeacherAssign already = existingAssigns.get(0);
            redirectAttributes.addFlashAttribute("serverMessage",
                    "Program '" + program.getTrainingProgram() + "' is already assigned to Teacher '"
                            + already.getTeacher().getName() + "'.");
            return "redirect:/admin/teacherManage";
        }

        // create new assign
        TeacherAssign assign = new TeacherAssign();
        assign.setTeacher(teacher);
        assign.setProgram(program);
        teacherAssignRepo.save(assign);

     // Prepare full program details in professional HTML format
        String programDetails = emailService.buildProgramAssignmentTemplate(
                teacher.getName(),
                program.getTrainingProgram(),
                program.getCourse(),
                program.getBranch(),
                program.getYear(),
                program.getSection(),
                program.getSemester(),
                program.getStartDate(),
                program.getEndDate()
        );

        // Send email using the professional HTML template
        emailService.sendTeacherProgramAssignment(
                teacher.getEmail(),
                "Program Assignment Notification - Meerut Institute of Technology",
                programDetails
        );


        redirectAttributes.addFlashAttribute("serverMessage",
                "Teacher " + teacher.getName() + " assigned to Program " + program.getTrainingProgram() + " successfully!");

        return "redirect:/admin/teacherManage";
    }




    @GetMapping("/feedbackPhase")
    public String showFeedbackPhase() {
    	if (!isLoggedIn()) return redirectIfNotLoggedIn();
    	return "admin/feedbackPhase";
    }
    
    @PostMapping("/feedbackPhase")
    public String createFeedbackPhase(String feedbackPhase, RedirectAttributes redirectAttributes) {
        if (!isLoggedIn()) {
            return redirectIfNotLoggedIn();
        }

        try {
            FeedBackPhase phase = new FeedBackPhase();
            phase.setPhaseName(feedbackPhase);
            feedbackRepo.save(phase);

            redirectAttributes.addFlashAttribute("mgs", "Feedback Phase created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mgs", "Error creating Feedback Phase: " + e.getMessage());
        }

        return "redirect:/admin/feedbackPhase"; // reload same page
    }

    
    @GetMapping("/question")
    public String showQuestion(Model model) {
        if (!isLoggedIn()) return redirectIfNotLoggedIn();

        List<QuestionCatrgories> categories = questionCategoryRepo.findAll();
        model.addAttribute("categories", categories);

        // Only TEXT and NUMBER
        Question.AnswerType[] answerTypes = {Question.AnswerType.TEXT, Question.AnswerType.NUMBER};
        model.addAttribute("answerTypes", answerTypes);

        return "admin/question";
    }

    @PostMapping("/question")
    public String saveQuestions(
            @RequestParam("categoryId") Long categoryId,
            @RequestParam("questions[]") List<String> questions,
            @RequestParam("answerTypes[]") List<String> answerTypes,
            @RequestParam(value = "rangeStart[]", required = false) List<Integer> rangeStarts,
            @RequestParam(value = "rangeEnd[]", required = false) List<Integer> rangeEnds,
            RedirectAttributes redirectAttributes) {

        try {
            QuestionCatrgories category = questionCategoryRepo.findById(categoryId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid category ID"));

            var count =0;
            for (int i = 0; i < questions.size(); i++) {
                String text = questions.get(i).trim();
                System.out.println("THe text of the question is "+text);
                Question.AnswerType type = Question.AnswerType.valueOf(answerTypes.get(i));
                System.out.println("The type of question is "+type);
                Question q = new Question(category, text, type);

                if (type == Question.AnswerType.NUMBER) {
                    q.setRangeStart(rangeStarts.get(count));
                    q.setRangeEnd(rangeEnds.get(count));
                    count++;
                }

                System.out.println("THe range is obtained");
                questionRepo.save(q);
            }

            redirectAttributes.addFlashAttribute("msg", "Questions saved successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("msg", "Error saving questions!");
        }

        return "redirect:/admin/question";
    }

    
    
    
    @GetMapping("/questionCategories")
    public String showQuestionCategories() {
    	if (!isLoggedIn()) return redirectIfNotLoggedIn();
    	return "admin/questionCategories";
    }
    
    @PostMapping("/questionCategories")
    public String saveQuestionCategories(
            @RequestParam("categories[]") List<String> categories,
            RedirectAttributes redirectAttributes) {
    	if (!isLoggedIn()) return redirectIfNotLoggedIn();

        try {
          
            if (categories == null || categories.isEmpty()) {
                redirectAttributes.addFlashAttribute("msg", "Please add at least one category!");
                return "redirect:/admin/questionCategories";
            }

           
            for (String name : categories) {
                if (name != null && !name.trim().isEmpty()) {
                    QuestionCatrgories category = new QuestionCatrgories(name.trim());
                    questionCategoryRepo.save(category);
                }
            }

            redirectAttributes.addFlashAttribute("msg", "Categories saved successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("msg", "Something went wrong!");
        }
        return "redirect:/admin/questionCategories";
    }
    
    
    
    @GetMapping("/ViewProfile")
    public String showViewProfile(Model model) {
    	if (!isLoggedIn()) return redirectIfNotLoggedIn();
    	Admin admin =  (Admin) session.getAttribute("loggedInAdmin");
    	model.addAttribute("admin", admin);
    	return "admin/ViewProfile";
    }
    
    
    @GetMapping("/feedback")
    public String showFeedback(Model model) {
    	if (!isLoggedIn()) return redirectIfNotLoggedIn();
    	
    	 List<QuestionCatrgories> categories = questionCategoryRepo.findAll();
         model.addAttribute("categories", categories);
         
         List<Program> program = programRepo.findAll();
         model.addAttribute("program", program);
         
         List<FeedBackPhase> phase = feedbackRepo.findAll();
         model.addAttribute("phase", phase);
         
    	return "admin/feedback";
    }
    
    @PostMapping("/feedback")
public String saveFeedback(@RequestParam Long programId,
                           @RequestParam Long phaseId,
                           @RequestParam("categoryIds") List<Long> categoryIds,
                           @RequestParam String startDate,
                           @RequestParam String endDate,
                           RedirectAttributes redirectAttributes) {

    if (!isLoggedIn()) return redirectIfNotLoggedIn();

    try {
        // 1. Parse dates
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        LocalDate today = LocalDate.now();

        // 2. Validate dates
        if (start.isBefore(today)) {
            redirectAttributes.addFlashAttribute("serverMessage", "Start date cannot be before today's date!");
            return "redirect:/admin/feedback";
        }

        if (end.isBefore(start)) {
            redirectAttributes.addFlashAttribute("serverMessage", "End date cannot be before start date!");
            return "redirect:/admin/feedback";
        }

        // 3. Fetch Program and Feedback Phase
        Program program = programRepo.findById(programId).orElseThrow();
        FeedBackPhase phase = feedbackRepo.findById(phaseId).orElseThrow();

        // 4. Create Feedback
        Feedback feedback = new Feedback();
        feedback.setProgram(program);
        feedback.setFeedbackPhase(phase);
        feedback.setStartDate(start);
        feedback.setEndDate(end);
        feedRepo.save(feedback);

        // 5. Save FeedbackQuestionCategory for each selected category
        for (Long catId : categoryIds) {
            QuestionCatrgories category = questionCategoryRepo.findById(catId).orElseThrow();
            FeedbackQuestionCategory fqc = new FeedbackQuestionCategory();
            fqc.setFeedback(feedback);
            fqc.setQuestionCategory(category);
            FeedbackQuestionCategoryRepo.save(fqc);
        }

        redirectAttributes.addFlashAttribute("serverMessage", "Feedback created successfully!");
    } catch (Exception e) {
        e.printStackTrace();
        redirectAttributes.addFlashAttribute("serverMessage", "Error creating feedback: " + e.getMessage());
    }

    return "redirect:/admin/feedback";
}

    
    @GetMapping("/totalFeedback")
    public String totalFeedback(Model model) {
        List<Feedback> feedbacks = feedRepo.findAll(); // LAZY fetch hoga
        model.addAttribute("feedbacks", feedbacks);
        return "admin/totalFeedback";
    }


    @GetMapping("/category/details/{id}")
    public String categoryDetail(@PathVariable Long id, Model model) {

        // 1️⃣ FeedbackQuestionCategory से category निकालो
        QuestionCatrgories category = FeedbackQuestionCategoryRepo.findCategoryByFqcId(id);

        if (category == null) {
            throw new IllegalArgumentException("Invalid FeedbackQuestionCategory id: " + id);
        }

        // 2️⃣ Category id से questions लाओ
        List<Question> questions = questionRepo.findByCategoryId(category.getId());

        // 3️⃣ Feedback id निकालो (Back button के लिए)
        Long feedbackId = FeedbackQuestionCategoryRepo.findById(id)
                               .map(FeedbackQuestionCategory::getFeedback)
                               .map(Feedback::getId)
                               .orElse(null);

        // 4️⃣ Model में डालो
        model.addAttribute("category", category);
        model.addAttribute("questions", questions);
        model.addAttribute("feedbackId", feedbackId);  // ✅ यहाँ add करना है

        return "admin/categoryDetail";
    }

    
    
    @GetMapping("/ChangePassword")
    public String showChangePassword() {
    	return "/admin/ChangePassword";
    }
    
    
    
    @PostMapping("/ChangePassword")
    public String changePassword(RedirectAttributes attributes, HttpServletRequest request) {
        String oldPassword = request.getParameter("oldPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        Admin admin = (Admin) session.getAttribute("loggedInAdmin");
        if(admin == null) {
            return "redirect:/";
        }

        if (!newPassword.equals(confirmPassword)) {
            attributes.addFlashAttribute("msg", "New Password and Confirm Password are not same.");
            return "redirect:/admin/ChangePassword";
        }

        if (oldPassword.equals(admin.getPassword())) {
            if(oldPassword.equals(newPassword)) {
                attributes.addFlashAttribute("msg", "New Password cannot be same as Old Password.");
                return "redirect:/admin/ChangePassword";
            }

            admin.setPassword(confirmPassword);
            adminRepo.save(admin);
            session.invalidate();
            attributes.addFlashAttribute("msg", "Password Successfully Changed. Please login again.");
            return "redirect:/";
        } else {
            attributes.addFlashAttribute("msg", "Invalid Old Password!!!");
            return "redirect:/admin/ChangePassword"; 
        }
    }
    
    
    
 // ✅ Toggle Status
    @PostMapping("/updateStatus/{id}")
    public String updateStatus(@PathVariable Long id) {
        EnrolledProgram ep = enrolledProgramRepo.findById(id).orElse(null);
        if (ep != null) {
            if (ep.getStatus() == EnrolledProgram.ProgramStatus.PENDING) {
                ep.setStatus(EnrolledProgram.ProgramStatus.APPROVED);
            } else {
                ep.setStatus(EnrolledProgram.ProgramStatus.PENDING);
            }
            enrolledProgramRepo.save(ep);

            // Redirect to the program detail page using program's id
            Long programId = ep.getProgram().getId();
            return "redirect:/admin/programDetail/" + programId;
        }
        // fallback if enrolled program not found
        return "redirect:/admin/dashboard";
    }

    // ✅ Delete Enrollment
    @PostMapping("/deleteEnrollment/{id}")
    public String deleteEnrollment(@PathVariable Long id) {
        EnrolledProgram ep = enrolledProgramRepo.findById(id).orElse(null);
        if (ep != null) {
            Long programId = ep.getProgram().getId();
            enrolledProgramRepo.deleteById(id);
            return "redirect:/admin/programDetail/" + programId;
        }
        return "redirect:/admin/dashboard";
    }

    
    
    @GetMapping("/studentFeedback")
    public String showStudentFeedback(Model model, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("loggedInAdmin");
        if (admin == null) {
            return "redirect:/";
        }

        // 1. Saare feedbacks fetch karein
        List<Feedback> allFeedbacks = feedRepo.findAll();

        // 2. Feedbacks ko Program ke basis par group karein
        // Key: Program, Value: List of Feedbacks (Phases) for that program
        Map<Program, List<Feedback>> groupedFeedbacks = allFeedbacks.stream()
                .collect(Collectors.groupingBy(
                    Feedback::getProgram,
                    LinkedHashMap::new, // Insertion order maintain karne ke liye
                    Collectors.toList()
                ));

        // 3. Feedback ID -> Unique Students Map (Pehle ki tarah hi)
        Map<Long, List<Student>> feedbackStudentsMap = new HashMap<>();
        for (Feedback feedback : allFeedbacks) {
            List<Student> uniqueStudents = feedback.getStudentFeedbackAnswers().stream()
                    .map(StudentFeedbackAnswer::getStudent)
                    .distinct()
                    .toList();
            feedbackStudentsMap.put(feedback.getId(), uniqueStudents);
        }

        // Model mein attributes add karein
        model.addAttribute("groupedFeedbacks", groupedFeedbacks);
        model.addAttribute("feedbackStudentsMap", feedbackStudentsMap);

        return "admin/studentFeedback";
    }

    // For student feedback detail page
    @GetMapping("/studentFeedback/detail/{studentId}/{feedbackId}")
    public String showStudentFeedbackDetail(@PathVariable Long studentId,
                                            @PathVariable Long feedbackId,
                                            Model model) {
        List<StudentFeedbackAnswer> answers =
                answerRepo.findByStudentIdAndFeedbackId(studentId, feedbackId);

        model.addAttribute("answers", answers);
        return "admin/studentFeedbackDetail";
    }
    
    
 // Manage Program
    @GetMapping("/manage-program")
    public String showManageProgram(Model model){
        if (!isLoggedIn()) return redirectIfNotLoggedIn();

        List<Program> programs = programRepo.findAll();  // <-- Repo se data nikalna
        model.addAttribute("programs", programs);

        return "admin/manage-program";
    }

    @GetMapping("/program/delete/{id}")
    public String deleteProgram(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Program program = programRepo.findById(id)
                .orElse(null);

        if (program == null) {
            redirectAttributes.addFlashAttribute("serverMessage", "Program not found!");
            return "redirect:/admin/manage-program";
        }

     // Check if program has feedback
        boolean hasFeedback = feedRepo.existsByProgram(program);
        if (hasFeedback) {
            redirectAttributes.addFlashAttribute("serverMessage", 
                "❌ Program cannot be deleted because feedback is already created!");
            return "redirect:/admin/manage-program";
        }

        // Safe delete (modules, assignments bhi cascade se delete ho jayenge)
        programRepo.delete(program);

        redirectAttributes.addFlashAttribute("serverMessage", 
            "✅ Program and its modules deleted successfully!");
        return "redirect:/admin/manage-program";
    }


 // Edit Program Form
    @GetMapping("/program/edit/{id}")
    public String editProgram(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Program program = programRepo.findById(id).orElse(null);
        if (program == null) {
            redirectAttributes.addFlashAttribute("serverMessage", "❌ Program not found!");
            return "redirect:/admin/manage-program";
        }
        model.addAttribute("program", program);
        return "admin/edit-program";
    }

    @PostMapping("/program/update")
    public String updateProgram(@ModelAttribute Program updatedProgram, RedirectAttributes redirectAttributes) {
        Program existingProgram = programRepo.findById(updatedProgram.getId()).orElse(null);
        if (existingProgram == null) {
            redirectAttributes.addFlashAttribute("serverMessage", "❌ Program not found!");
            return "redirect:/admin/manage-program";
        }

        // Update only editable fields
        existingProgram.setTrainingProgram(updatedProgram.getTrainingProgram());
        existingProgram.setStartDate(updatedProgram.getStartDate());
        existingProgram.setEndDate(updatedProgram.getEndDate());
        existingProgram.setCourse(updatedProgram.getCourse());
        existingProgram.setBranch(updatedProgram.getBranch());
        existingProgram.setYear(updatedProgram.getYear());
        existingProgram.setSection(updatedProgram.getSection());
        existingProgram.setSemester(updatedProgram.getSemester());

        // Do NOT touch teacherAssignments → existing assignments remain

        programRepo.save(existingProgram);

        redirectAttributes.addFlashAttribute("serverMessage", "✅ Program updated successfully!");
        return "redirect:/admin/manage-program";
    }

    
    
    
    @GetMapping("/module-management")
    public String showModuleManage(Model model) {
        List<Module> modules = moduleRepo.findAll(); // assuming you have ModuleRepository
        model.addAttribute("modules", modules);
        return "admin/module-management";
    }
    
    
    @GetMapping("/module/edit/{id}")
    public String editModule(@PathVariable Long id, Model model) {
        Module module = moduleRepo.findById(id)
                         .orElseThrow(() -> new IllegalArgumentException("Invalid module Id:" + id));
        List<Program> programs = programRepo.findAll(); // get all programs

        model.addAttribute("module", module);
        model.addAttribute("programs", programs);

        return "admin/module-edit";
    }
    
    @PostMapping("module/edit/{id}")
    public String updateModule(@ModelAttribute Module module, RedirectAttributes redirectAttributes) {
        // Fetch the program object using ID from the form
        Program program = programRepo.findById(module.getProgram().getId())
                         .orElseThrow(() -> new IllegalArgumentException("Invalid Program Id"));
        module.setProgram(program);

        moduleRepo.save(module);

        redirectAttributes.addFlashAttribute("serverMessageModule", "✅ Module updated successfully");
        return "redirect:/admin/module-management";
    }



    @GetMapping("/module/delete/{id}")
    public String deleteModule(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        moduleRepo.deleteById(id);
        redirectAttributes.addFlashAttribute("serverMessageModule", "✅ Module deleted successfully");
        return "redirect:/admin/module-management";
    }

    @GetMapping("/curriculum-management")
    public String showCurriculumManagement(Model model) {
        List<CurriculumTopic> topics = curriculumTopicRepo.findAll();
        model.addAttribute("topics", topics);
        return "admin/curriculum-management"; // must match template path
    }


 @GetMapping("/curriculum/edit/{id}")
 public String editCurriculumTopic(@PathVariable Long id, Model model) {
	    CurriculumTopic topic = curriculumTopicRepo.findById(id)
	                            .orElseThrow(() -> new IllegalArgumentException("Invalid Topic Id:" + id));
	    List<Module> modules = moduleRepo.findAll();
	    model.addAttribute("topic", topic);
	    model.addAttribute("modules", modules);
	    return "admin/curriculum-edit";
	}
 
 
 @PostMapping("/curriculum/edit/{id}")
 public String updateCurriculumTopic(@PathVariable Long id,
                                     @ModelAttribute("topic") CurriculumTopic updatedTopic,
                                     RedirectAttributes redirectAttributes) {
     // Fetch the existing topic from DB
     CurriculumTopic existingTopic = curriculumTopicRepo.findById(id)
             .orElseThrow(() -> new IllegalArgumentException("Invalid Topic Id:" + id));

     // Update the topic name
     existingTopic.setTopicName(updatedTopic.getTopicName());

     // Update the module association
     Module selectedModule = moduleRepo.findById(updatedTopic.getModule().getId())
             .orElseThrow(() -> new IllegalArgumentException("Invalid Module Id:" + updatedTopic.getModule().getId()));
     existingTopic.setModule(selectedModule);

     // Save the updated topic
     curriculumTopicRepo.save(existingTopic);

     // Add flash attribute for success message
     redirectAttributes.addFlashAttribute("serverMessage", "✅ Curriculum Topic updated successfully!");

     // Redirect back to the management page
     return "redirect:/admin/curriculum-management";
 }




    // Delete Curriculum Topic
    @GetMapping("/curriculum/delete/{id}")
    public String deleteCurriculumTopic(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        curriculumTopicRepo.deleteById(id);
        redirectAttributes.addFlashAttribute("serverMessage", "✅ Curriculum topic deleted successfully");
        return "redirect:/admin/curriculum-management";
    }

    
    
    @GetMapping("/assignTeacher-manage")
    public String assignTeacher(Model model) {
        List<TeacherAssign> teacherAssignments = teacherAssignRepo.findAll();
        model.addAttribute("teacherAssignments", teacherAssignments);
        return "admin/assignTeacher-manage";
    }

    
    @GetMapping("/assignTeacher/edit/{id}")
    public String editTeacherAssign(@PathVariable Long id, Model model) {
        TeacherAssign assign = teacherAssignRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid assignment ID: " + id));

        List<Teacher> teachers = teacherRepository.findByStatus(Teacher.Status.APPROVED);
        List<Program> programs = programRepo.findAll();

        // 🟢 Step 1: Analytics Map taiyar karein (Same as teacherManage)
        Map<Long, Map<String, Object>> teacherAnalyticsMap = new HashMap<>();

        for (Teacher t : teachers) {
            Map<String, Object> stats = new HashMap<>();
            List<TeacherAssign> pastAssigns = teacherAssignRepo.findByTeacherId(t.getId());
            
            if (pastAssigns.isEmpty()) {
                stats.put("status", "NO_ASSIGNMENT");
                stats.put("summary", "New Faculty: Primary audit pending. No historical pedagogical data found.");
            } else {
                // Humara updated helper method call karein jo Map<String, Object> return karta hai
                Map<String, Object> performance = getTeacherStats(t.getId());
                
                String responses = performance.get("totalResponses").toString();
                if (responses.startsWith("0 /")) {
                    stats.put("status", "PENDING_FEEDBACK");
                    stats.put("summary", "Audit in Progress: Historical assignments found but student feedback is yet to be recorded.");
                } else {
                    stats.put("status", "AUDITED");
                    stats.put("avgRating", performance.get("avgRating"));
                    stats.put("sentiment", performance.get("sentimentScore") + "%");
                    stats.put("power", performance.get("pedagogicalPower"));
                    
                    double power = Double.parseDouble(performance.get("pedagogicalPower").toString());
                    String aiSummary = (power >= 80) ? "Exemplary Match: Faculty demonstrates high pedagogical competence for SDG-4." 
                                     : (power >= 60 ? "Qualified: Suitable for standard delivery. Recommend periodic monitoring." 
                                     : "Caution: Significant pedagogical gaps detected in previous audits.");
                    stats.put("summary", aiSummary);
                }
            }
            teacherAnalyticsMap.put(t.getId(), stats);
        }

        model.addAttribute("assign", assign);
        model.addAttribute("teachers", teachers);
        model.addAttribute("programs", programs);
        model.addAttribute("teacherAnalytics", teacherAnalyticsMap); // 🟢 JavaScript ke liye data

        return "admin/edit-assignTeacher";
    }


    @PostMapping("/assignTeacher/edit/{id}")
    public String updateAssignTeacher(@PathVariable Long id, 
                                      @RequestParam Long teacherId,
                                      @RequestParam Long programId,
                                      RedirectAttributes redirectAttributes) {

        TeacherAssign assign = teacherAssignRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid assignment ID: " + id));

        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid teacher ID: " + teacherId));

        Program program = programRepo.findById(programId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid program ID: " + programId));

        // Check if this teacher is already assigned to this program
        boolean exists = teacherAssignRepo.existsByTeacherAndProgram(teacher, program);

        if (exists && !(assign.getTeacher().getId().equals(teacherId) 
                        && assign.getProgram().getId().equals(programId))) {
            redirectAttributes.addFlashAttribute("serverMessage", 
                    "This teacher is already assigned to the selected program!");
            return "redirect:/admin/assignTeacher/edit/" + id;
        }

        // Update assignment
        assign.setTeacher(teacher);
        assign.setProgram(program);
        teacherAssignRepo.save(assign);

        redirectAttributes.addFlashAttribute("serverMessage", "Assignment updated successfully!");
        return "redirect:/admin/assignTeacher-manage";
    }


    // Delete Assignment (but NOT teacher or program themselves)
    @GetMapping("/assignTeacher/delete/{id}")
    public String deleteAssignTeacher(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        TeacherAssign assign = teacherAssignRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid assignment ID: " + id));

        teacherAssignRepo.delete(assign);
        redirectAttributes.addFlashAttribute("serverMessage", "Assignment Teacher Delete successfully!");
        return "redirect:/admin/assignTeacher-manage";
    }


    @GetMapping("/Manage-feedbackphase")
    public String showManageFeedbackPhase(Model model) {
    	List<FeedBackPhase> phase = feedbackRepo.findAll();
    	model.addAttribute("phase", phase);	
    	return "admin/Manage-feedbackphase";
    }
   
    
    
    @GetMapping("/feedbackphase/delete/{id}")
    public String deleteFeedbackPhase(@PathVariable Long id, RedirectAttributes redirectAttrs) {

        // Check if phase is used in any Feedback
        boolean isPhaseUsed = feedRepo.existsByFeedbackPhase_Id(id);

        if (isPhaseUsed) {
            redirectAttrs.addFlashAttribute("serverMessageModule",
                    "This Feedback Phase cannot be deleted because it is already assigned in Feedback!");
        } else {
            try {
                feedbackRepo.deleteById(id);
                redirectAttrs.addFlashAttribute("serverMessageModule", "Feedback Phase deleted successfully!");
            } catch (Exception e) {
                redirectAttrs.addFlashAttribute("serverMessageModule", "Error deleting Feedback Phase!");
            }
        }

        return "redirect:/admin/Manage-feedbackphase";
    }

 // Edit page open karne ke liye (GET)
    @GetMapping("/feedbackphase/edit/{id}")
    public String editFeedbackPhase(@PathVariable Long id, Model model, RedirectAttributes redirectAttrs) {
        FeedBackPhase phase = feedbackRepo.findById(id).orElse(null);

        if (phase == null) {
            redirectAttrs.addFlashAttribute("serverMessageModule", "Feedback Phase not found!");
            return "redirect:/admin/Manage-feedbackphase";
        }

        model.addAttribute("phase", phase);
        return "admin/edit-feedbackphase"; // ye html file load hogi
    }

    // Form submit karne ke liye (POST)
    @PostMapping("/feedbackphase/update/{id}")
    public String updateFeedbackPhase(@PathVariable Long id,
                                      @ModelAttribute("phase") FeedBackPhase updatedPhase,
                                      RedirectAttributes redirectAttrs) {
        FeedBackPhase existing = feedbackRepo.findById(id).orElse(null);

        if (existing == null) {
            redirectAttrs.addFlashAttribute("serverMessageModule", "Feedback Phase not found!");
            return "redirect:/admin/Manage-feedbackphase";
        }

        try {
            existing.setPhaseName(updatedPhase.getPhaseName());
            feedbackRepo.save(existing);
            redirectAttrs.addFlashAttribute("serverMessageModule", "Feedback Phase updated successfully!");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("serverMessageModule", "Error updating Feedback Phase!");
        }

        return "redirect:/admin/Manage-feedbackphase";
    }
    
    
    @GetMapping("/manageQuestion")
    public String showManageQuestion(Model model) {
        List<Question> questions = questionRepo.findAll();
        model.addAttribute("questions", questions);
        return "admin/manageQuestion";
    }

    @GetMapping("/question/delete/{id}")
    public String deleteQuestion(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        Question q = questionRepo.findById(id).orElse(null);
        if (q == null) {
            redirectAttrs.addFlashAttribute("msg", "Question not found!");
            return "redirect:/admin/manageQuestion";
        }

       
        boolean isInFeedbackAnalysis = feedbackAnalysisRepo.existsByQuestion(q);
        
        if (isInFeedbackAnalysis) {
            // Agar FeedbackAnalysis me hai, delete nahi karenge
            redirectAttrs.addFlashAttribute("msg", "Question cannot be deleted because it is used in feedback analysis!");
        } else {
            // Sirf categories me hai ya kahin aur nahi → delete allowed
            questionRepo.delete(q);
            redirectAttrs.addFlashAttribute("msg", "Question deleted successfully!");
        }

        return "redirect:/admin/manageQuestion";
    }


    
    @GetMapping("/question/edit/{id}")
    public String editQuestionForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttrs) {
        Question question = questionRepo.findById(id).orElse(null);
        if (question == null) {
            redirectAttrs.addFlashAttribute("msg", "Question not found!");
            return "redirect:/admin/manageQuestion";
        }

        // Check if question is used in any feedback analysis
        boolean isInFeedbackAnalysis = feedbackAnalysisRepo.existsByQuestion(question);
        if (isInFeedbackAnalysis) {
            // Redirect immediately with flash message
            redirectAttrs.addFlashAttribute("msg", "Question cannot be edited because it is used in feedback analysis!");
            return "redirect:/admin/manageQuestion";
        }

        // If not in feedback analysis → allow edit
        model.addAttribute("question", question);
        model.addAttribute("categories", questionCategoryRepo.findAll());
        model.addAttribute("isEditable", true); // Editable

        return "admin/editQuestion"; // Thymeleaf template
    }


    // --- POST Mapping for Update ---
    @PostMapping("/question/edit/{id}")
    public String updateQuestion(@PathVariable Long id,
                                 @RequestParam Long categoryId,
                                 @RequestParam String questionText,
                                 @RequestParam String answerType,
                                 @RequestParam(required = false) Integer rangeStart,
                                 @RequestParam(required = false) Integer rangeEnd,
                                 RedirectAttributes redirectAttrs) {

        Question question = questionRepo.findById(id).orElse(null);
        if (question == null) {
            redirectAttrs.addFlashAttribute("msg", "Question not found!");
            return "redirect:/admin/manageQuestion";
        }

        // Check if linked to feedbackAnalysis
        boolean isInFeedbackAnalysis = feedbackAnalysisRepo.existsByQuestion(question);
        if (isInFeedbackAnalysis) {
            redirectAttrs.addFlashAttribute("msg", "Cannot edit question used in feedback analysis!");
            return "redirect:/admin/manageQuestion";
        }

        // Validation for NUMBER type
        Question.AnswerType typeEnum = Question.AnswerType.valueOf(answerType);
        if (typeEnum == Question.AnswerType.NUMBER) {
            if (rangeStart == null || rangeEnd == null) {
                redirectAttrs.addFlashAttribute("msg", "Range start and end are required for NUMBER type questions!");
                return "redirect:/admin/question/edit/" + id;
            }
            question.setRangeStart(rangeStart);
            question.setRangeEnd(rangeEnd);
        } else {
            question.setRangeStart(null);
            question.setRangeEnd(null);
        }

        QuestionCatrgories category = questionCategoryRepo.findById(categoryId).orElse(null);
        if (category == null) {
            redirectAttrs.addFlashAttribute("msg", "Invalid category!");
            return "redirect:/admin/question/edit/" + id;
        }

        question.setCategory(category);
        question.setQuestionText(questionText);
        question.setAnswerType(typeEnum);

        questionRepo.save(question);
        redirectAttrs.addFlashAttribute("msg", "Question updated successfully!");
        return "redirect:/admin/manageQuestion";
    }


    
    @GetMapping("/totalFeedback/delete/{id}")
    public String deleteFeedback(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        Feedback feedback = feedRepo.findById(id).orElse(null);

        if (feedback == null) {
            redirectAttrs.addFlashAttribute("serverMessageModule", "Error: Feedback not found!");
            return "redirect:/admin/totalFeedback";
        }

        // Check if feedback has StudentFeedbackAnswers (then cannot delete)
        if (feedback.getStudentFeedbackAnswers() != null && !feedback.getStudentFeedbackAnswers().isEmpty()) {
            redirectAttrs.addFlashAttribute("serverMessageModule", 
                    "Error! Cannot delete! Feedback is already filled by students.");
            return "redirect:/admin/totalFeedback";
        }

        try {
            // Break the relations first (Program & Phase)
            feedback.setProgram(null);
            feedback.setFeedbackPhase(null);

            // FeedbackQuestionCategories will be auto-deleted because of cascade=ALL + orphanRemoval=true
            feedRepo.delete(feedback);

            redirectAttrs.addFlashAttribute("serverMessageModule", " Feedback deleted successfully!");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("serverMessageModule", " Error deleting feedback: " + e.getMessage());
        }

        return "redirect:/admin/totalFeedback";
    }
    
    @PostMapping("/student/RegisterBulkStudent")
    public String registerBulkStudents(@RequestParam("file") MultipartFile file,
                                       @RequestParam("startRow") int startRow,
                                       RedirectAttributes redirectAttributes) {
        try {
            if (file.isEmpty()) {
                redirectAttributes.addFlashAttribute("msg", "Please upload a valid Excel file!");
                return "redirect:/admin/student";
            }

            // Apache POI to read Excel
            try (InputStream inputStream = file.getInputStream();
                 Workbook workbook = WorkbookFactory.create(inputStream)) {

                Sheet sheet = workbook.getSheetAt(0);
                int addedCount = 0;

                for (int i = startRow - 1; i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                    if (row == null) continue;

                    String rollNo = getCellValue(row.getCell(1)); // Column B
                    String name   = getCellValue(row.getCell(2)); // Column C

                    if (rollNo == null || name == null) continue;

                    // Skip duplicates
                    if ( studentRepository.existsByRollNo(rollNo)) {
                        continue;
                    }

                    Student s = new Student();
                    s.setRollNo(rollNo);
                    s.setName(name);
                    s.setPassword("12345"); // Default password
                    s.setRole(Student.Role.STUDENT);
                    s.setStatus(Student.Status.APPROVED);

                    studentRepository.save(s);
                    addedCount++;
                }

                redirectAttributes.addFlashAttribute("msg", addedCount + " students registered successfully!");
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("msg", "Error while uploading: " + e.getMessage());
            e.printStackTrace();
        }

        return "redirect:/admin/student";
    }

    // Helper method to read cell safely
    private String getCellValue(Cell cell) {
        if (cell == null) return null;
        cell.setCellType(CellType.STRING);
        return cell.getStringCellValue().trim();
    }
    
    
    @PostMapping("/uploadEnrollmentExcel")
    public String uploadEnrollmentExcel(@RequestParam("file") MultipartFile file,
                                        @RequestParam("programId") Long programId,
                                        RedirectAttributes redirectAttributes) {
        try {
            if (file.isEmpty()) {
                redirectAttributes.addFlashAttribute("msg", "Please upload a valid Excel file!");
                return "redirect:/admin/programDetails/" + programId;
            }

            Program program = programRepo.findById(programId)
                    .orElseThrow(() -> new Exception("Program not found!"));

            try (InputStream inputStream = file.getInputStream();
                 Workbook workbook = WorkbookFactory.create(inputStream)) {

                Sheet sheet = workbook.getSheetAt(0);
                int addedCount = 0;

                for (int i = 1; i <= sheet.getLastRowNum(); i++) { // skip header row
                    Row row = sheet.getRow(i);
                    if (row == null) continue;

                    String rollNo = getCellValue(row.getCell(1)); // Column B
                    String name   = getCellValue(row.getCell(2)); // Column C

                    if (rollNo == null || rollNo.isBlank()) continue;

                    Student student = studentRepository.findByRollNo(rollNo);
                    if (student == null) {
                        System.out.println("No student found for Roll No: " + rollNo);
                        continue;
                    }

                    boolean alreadyEnrolled = enrolledProgramRepo.existsByStudentAndProgram(student, program);
                    if (!alreadyEnrolled) {
                        EnrolledProgram ep = new EnrolledProgram();
                        ep.setProgram(program);
                        ep.setStudent(student);
                        ep.setRegDate(LocalDateTime.now());
                        ep.setStatus(ProgramStatus.APPROVED);

                        enrolledProgramRepo.save(ep);
                        addedCount++;
                    }
                }

                redirectAttributes.addFlashAttribute("msg", addedCount + " students enrolled successfully!");
            }

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("msg", "Error while uploading: " + e.getMessage());
        }

        return "redirect:/admin/programDetail/" + programId;
    }
    
    
    
    @GetMapping("/studentFeedbackReport/detail/{studentId}/{feedbackId}")
    public ResponseEntity<byte[]> downloadFeedbackPDF(@PathVariable("feedbackId") Long feedbackId,
                                                      @PathVariable("studentId") Long studentId) {
        // ✅ Admin session check
        Admin admin = (Admin) session.getAttribute("loggedInAdmin");
        if (admin == null) {
            return ResponseEntity.status(403)
                    .body("Please login to download the feedback report.".getBytes());
        }

        // ✅ Fetch student by ID
        Student student = studentRepository.findById(studentId).orElse(null);
        if (student == null) {
            return ResponseEntity.status(404)
                    .body(("Student not found with ID: " + studentId).getBytes());
        }

        // ✅ Fetch feedback by ID
        Feedback feedback = feedRepo.findById(feedbackId).orElse(null);
        if (feedback == null) {
            return ResponseEntity.status(404)
                    .body(("Feedback not found with ID: " + feedbackId).getBytes());
        }

        // ✅ Fetch student answers
        List<StudentFeedbackAnswer> answers = answerRepo.findByFeedbackAndStudent(feedback, student);

        // ✅ Get first teacher assigned (optional: can fetch all if needed)
        String teacherName = "";
        if (feedback.getProgram().getTeacherAssignments() != null &&
            !feedback.getProgram().getTeacherAssignments().isEmpty()) {
            TeacherAssign firstAssign = feedback.getProgram().getTeacherAssignments().get(0);
            if (firstAssign != null && firstAssign.getTeacher() != null) {
                teacherName = firstAssign.getTeacher().getName();
            }
        }

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            PdfFont bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            PdfFont normal = PdfFontFactory.createFont(StandardFonts.HELVETICA);
            PdfFont italic = PdfFontFactory.createFont(StandardFonts.HELVETICA_OBLIQUE);

            // 🔹 Header
            document.add(new Paragraph("MEERUT INSTITUTE OF TECHNOLOGY, MEERUT - 292")
                    .setFont(bold)
                    .setFontSize(16)
                    .setFontColor(ColorConstants.BLUE)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setPadding(5));
            
            Paragraph subheader = new Paragraph("Department of Compter Science and Engineering")
            		.setFont(bold)
            		.setFontSize(15)
            		.setFontColor(ColorConstants.BLACK)
            		.setTextAlignment(TextAlignment.CENTER);
            document.add(subheader);

            document.add(new Paragraph("FEEDBACK REPORT")
                    .setFont(bold)
                    .setFontSize(14)
                    .setFontColor(ColorConstants.RED)
                    .setTextAlignment(TextAlignment.CENTER));

            document.add(new LineSeparator(new SolidLine(1f)));

            // 🔹 Student Info
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            Paragraph studentInfo = new Paragraph()
                    .add("Program Name: " + feedback.getProgram().getTrainingProgram() + "\n")
                    .add("Teacher Name: " + teacherName + "\n")
                    .add("Feedback Phase: " + feedback.getFeedbackPhase().getPhaseName() + "\n")
                    .add("Student Name: " + student.getName() + "\n")
                    .add("Roll No: " + student.getRollNo() + "\n")
                    .add("Branch: " + student.getBranch() +
                            " | Year: " + student.getYear() +
                            " | Course: " + student.getCourse() + "\n")
                    .add("Feedback Period: " + feedback.getStartDate().format(formatter)
                            + " to " + feedback.getEndDate().format(formatter))
                    .setFont(normal)
                    .setFontSize(11)
                    .setMarginBottom(10);

            document.add(studentInfo);
            document.add(new LineSeparator(new SolidLine(1f)));

            // 🔹 Questions & Answers
            for (FeedbackQuestionCategory categoryMapping : feedback.getFeedbackQuestionCategories()) {
                String categoryName = categoryMapping.getQuestionCategory().getCategoryName();
                document.add(new Paragraph(categoryName)
                        .setFont(bold)
                        .setFontSize(12)
                        .setFontColor(ColorConstants.MAGENTA));

                Table table = new Table(UnitValue.createPercentArray(new float[]{1, 5, 4}));
                table.setWidth(UnitValue.createPercentValue(100));

                // Table header
                table.addHeaderCell(new com.itextpdf.layout.element.Cell().add(new Paragraph("#").setFont(bold).setFontColor(ColorConstants.WHITE))
                        .setBackgroundColor(ColorConstants.DARK_GRAY));
                table.addHeaderCell(new com.itextpdf.layout.element.Cell().add(new Paragraph("Question").setFont(bold).setFontColor(ColorConstants.WHITE))
                        .setBackgroundColor(ColorConstants.DARK_GRAY));
                table.addHeaderCell(new com.itextpdf.layout.element.Cell().add(new Paragraph("Answer").setFont(bold).setFontColor(ColorConstants.WHITE))
                        .setBackgroundColor(ColorConstants.DARK_GRAY));

                int qNo = 1;
                for (Question q : categoryMapping.getQuestionCategory().getQuestions()) {
                    String ansText = answers.stream()
                            .filter(a -> a.getQuestion().getId().equals(q.getId()))
                            .map(StudentFeedbackAnswer::getAnswer)
                            .findFirst()
                            .orElse("Not Answered");

                    table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(String.valueOf(qNo)).setFont(normal)));
                    table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(q.getQuestionText()).setFont(normal)));
                    table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(ansText).setFont(italic)));
                    qNo++;
                }
                document.add(table);
                document.add(new Paragraph("\n")); // spacing after table
            }

            // 🔹 Footer
            document.add(new LineSeparator(new SolidLine(1f)));
            document.add(new Paragraph("Generated on: " + LocalDate.now().format(formatter))
                    .setFont(normal)
                    .setFontSize(9)
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setFontColor(ColorConstants.DARK_GRAY));

            document.close();

            byte[] pdfBytes = baos.toByteArray();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment",
                    "Feedback_Report_" +" "+feedback.getProgram().getTrainingProgram()+" "+ student.getRollNo() + ".pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(("Error generating feedback PDF: " + e.getMessage()).getBytes());
        }
    }
    
    
    @GetMapping("/feedbackReport/allStudents/{feedbackId}")
    public ResponseEntity<byte[]> downloadAllStudentsFeedbackPDF(@PathVariable("feedbackId") Long feedbackId) {
        // 1️⃣ Check if admin is logged in
        Admin admin = (Admin) session.getAttribute("loggedInAdmin");
        if (admin == null) {
            return ResponseEntity.status(403)
                    .body("Please login to download the feedback report.".getBytes());
        }

        // 2️⃣ Get feedback
        Feedback feedback = feedRepo.findById(feedbackId).orElse(null);
        if (feedback == null) {
            return ResponseEntity.notFound().build();
        }

        // 3️⃣ Get all students who submitted feedback
        List<Student> students = answerRepo.findDistinctStudentsByFeedback(feedback);

        // 4️⃣ Create PDF
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            PdfFont bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            PdfFont normal = PdfFontFactory.createFont(StandardFonts.HELVETICA);
            PdfFont italic = PdfFontFactory.createFont(StandardFonts.HELVETICA_OBLIQUE);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

            // 🔹 Top Header (once)
            String teacherName = "";
            if (feedback.getProgram().getTeacherAssignments() != null && !feedback.getProgram().getTeacherAssignments().isEmpty()) {
                TeacherAssign firstAssign = feedback.getProgram().getTeacherAssignments().get(0);
                if (firstAssign != null && firstAssign.getTeacher() != null) {
                    teacherName = firstAssign.getTeacher().getName();
                }
            }

            Paragraph header = new Paragraph("MEERUT INSTITUTE OF TECHNOLOGY, MEERUT - 292")
                    .setFont(bold)
                    .setFontSize(16)
                    .setFontColor(ColorConstants.BLUE)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setPadding(5);
            document.add(header);
            
            Paragraph subheader = new Paragraph("Department of Compter Science and Engineering")
            		.setFont(bold)
            		.setFontSize(15)
            		.setFontColor(ColorConstants.BLACK)
            		.setTextAlignment(TextAlignment.CENTER);
            document.add(subheader);

            Paragraph reportTitle = new Paragraph("FEEDBACK REPORT")
                    .setFont(bold)
                    .setFontSize(14)
                    .setFontColor(ColorConstants.RED)
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(reportTitle);

            Paragraph programInfo = new Paragraph()
                    .add("Program Name: " + feedback.getProgram().getTrainingProgram() + "\n")
                    .add("Teacher Name: " + teacherName + "\n")
                    .add("Feedback Phase: " + feedback.getFeedbackPhase().getPhaseName() + "\n")
                    .add("Feedback Period: " + feedback.getStartDate().format(formatter)
                            + " to " + feedback.getEndDate().format(formatter))
                    .setFont(normal)
                    .setFontSize(11)
                    .setMarginBottom(10);
            document.add(programInfo);

            document.add(new LineSeparator(new SolidLine(1f)));

            // 🔹 Loop through each student
            for (Student student : students) {
                // Student Info
                Paragraph studentInfo = new Paragraph()
                        .add("Student Name: " + student.getName() + "\n")
                        .add("Roll No: " + student.getRollNo() + "\n")
                        .add("Branch: " + student.getBranch() +
                                " | Year: " + student.getYear() +
                                " | Course: " + student.getCourse() + "\n")
                        .setFont(normal)
                        .setFontSize(11)
                        .setMarginBottom(5);
                document.add(studentInfo);

                List<StudentFeedbackAnswer> answers = answerRepo.findByFeedbackAndStudent(feedback, student);

                // Questions & Answers
                for (FeedbackQuestionCategory categoryMapping : feedback.getFeedbackQuestionCategories()) {
                    String categoryName = categoryMapping.getQuestionCategory().getCategoryName();
                    Paragraph categoryHeader = new Paragraph(categoryName)
                            .setFont(bold)
                            .setFontSize(12)
                            .setFontColor(ColorConstants.MAGENTA);
                    document.add(categoryHeader);

                    Table table = new Table(UnitValue.createPercentArray(new float[]{1, 5, 4}))
                            .setWidth(UnitValue.createPercentValue(100));

                    // Table Header
                    table.addHeaderCell(new com.itextpdf.layout.element.Cell().add(new Paragraph("#").setFont(bold).setFontColor(ColorConstants.WHITE))
                            .setBackgroundColor(ColorConstants.DARK_GRAY));
                    table.addHeaderCell(new com.itextpdf.layout.element.Cell().add(new Paragraph("Question").setFont(bold).setFontColor(ColorConstants.WHITE))
                            .setBackgroundColor(ColorConstants.DARK_GRAY));
                    table.addHeaderCell(new com.itextpdf.layout.element.Cell().add(new Paragraph("Answer").setFont(bold).setFontColor(ColorConstants.WHITE))
                            .setBackgroundColor(ColorConstants.DARK_GRAY));

                    int qNo = 1;
                    for (Question q : categoryMapping.getQuestionCategory().getQuestions()) {
                        String ansText = answers.stream()
                                .filter(a -> a.getQuestion().getId().equals(q.getId()))
                                .map(StudentFeedbackAnswer::getAnswer)
                                .findFirst()
                                .orElse("Not Answered");

                        table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(String.valueOf(qNo)).setFont(normal)));
                        table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(q.getQuestionText()).setFont(normal)));
                        table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(ansText).setFont(italic)));

                        qNo++;
                    }
                    document.add(table);
                    document.add(new Paragraph("\n")); // spacing after each student
                }

                document.add(new LineSeparator(new SolidLine(1f)));
                document.add(new AreaBreak());
            }

            document.close();

            byte[] pdfBytes = baos.toByteArray();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment",
                    "Feedback_Report_AllStudents_" +" "+feedback.getProgram().getTrainingProgram()+" "+ feedback.getId() + ".pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(("Error generating feedback PDF: " + e.getMessage()).getBytes());
        }
    }

    
    
    



@GetMapping("/teacher/leaderboard/report/{teacherId}")
public ResponseEntity<byte[]> generateTeacherPerformancePDF(@PathVariable("teacherId") Long teacherId) {
    Teacher teacher = teacherRepository.findById(teacherId).orElse(null);
    if (teacher == null) return ResponseEntity.notFound().build();

    try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.setDefaultPageSize(PageSize.A4.rotate());
        Document document = new Document(pdfDoc);

        PdfFont bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        PdfFont normal = PdfFontFactory.createFont(StandardFonts.HELVETICA);

     // --- PAGE 1: CENTERED HEADER (NO LOGO) ---
        Paragraph headerName = new Paragraph("MEERUT INSTITUTE OF TECHNOLOGY, MEERUT")
                .setFont(bold).setFontSize(24).setFontColor(ColorConstants.BLUE).setTextAlignment(TextAlignment.CENTER);
        Paragraph headerTagline = new Paragraph("National Quality Audit: SDG-4 Pedagogical Excellence Index")
                .setFontSize(10).setItalic().setFontColor(ColorConstants.DARK_GRAY).setTextAlignment(TextAlignment.CENTER);
        Paragraph deptName = new Paragraph("Department of Computer Science and Engineering")
                .setFont(bold).setFontSize(12).setTextAlignment(TextAlignment.CENTER).setMarginBottom(10);
        
        document.add(headerName);
        document.add(headerTagline);
        document.add(deptName);
        document.add(new LineSeparator(new SolidLine(1.5f)).setMarginBottom(15));

      
        // Faculty Profile
        document.add(new Paragraph("FACULTY COMPREHENSIVE PROFILE").setFont(bold).setUnderline());
        Table teacherTable = new Table(UnitValue.createPercentArray(new float[]{15, 35, 15, 35})).useAllAvailableWidth();
        addInfoCell(teacherTable, "Faculty Name:", bold); addInfoCell(teacherTable, teacher.getName(), normal);
        addInfoCell(teacherTable, "Designation:", bold); addInfoCell(teacherTable, teacher.getDesignation() != null ? teacher.getDesignation() : "Assistant Professor", normal);
        addInfoCell(teacherTable, "Employee ID:", bold); addInfoCell(teacherTable, teacher.getEmployeeId(), normal);
        addInfoCell(teacherTable, "Email Address:", bold); addInfoCell(teacherTable, teacher.getEmail(), normal);
        document.add(teacherTable);

        // --- KPI TILES ---
        Map<String, Object> stats = getTeacherStats(teacherId);
        document.add(new Paragraph("\nCOMPOSITE PERFORMANCE SUMMARY").setFont(bold).setFontSize(14).setFontColor(ColorConstants.RED));
        Table kpiTable = new Table(UnitValue.createPercentArray(new float[]{25, 25, 25, 25})).useAllAvailableWidth();
        renderKPICell(kpiTable, "GLOBAL KPI RATING", stats.get("avgRating").toString(), ColorConstants.BLUE);
        renderKPICell(kpiTable, "AI SENTIMENT SCORE", stats.get("sentimentScore") + "%", ColorConstants.GREEN);
        renderKPICell(kpiTable, "PEDAGOGICAL POWER", stats.get("pedagogicalPower").toString(), ColorConstants.ORANGE);
        renderKPICell(kpiTable, "AUDIT SAMPLES", stats.get("totalResponses").toString(), ColorConstants.CYAN);
        document.add(kpiTable);

        // --- DETAILED AUDIT TABLE ---
        document.add(new Paragraph("\nDETAILED FEEDBACK AUDIT LOGS").setFont(bold).setMarginTop(10));
        List<Map<String, Object>> partData = (List<Map<String, Object>>) stats.get("participationTable");
        Table detailedTable = new Table(UnitValue.createPercentArray(new float[]{30, 15, 25, 15, 15})).useAllAvailableWidth();
        String[] headers = {"Program Name", "Enrolled", "Feedback Phase", "Ratio", "Rating"};
        for(String h : headers) detailedTable.addHeaderCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(h).setBold().setFontColor(ColorConstants.WHITE)).setBackgroundColor(ColorConstants.DARK_GRAY));

        for (Map<String, Object> row : partData) {
            List<Map<String, Object>> phases = (List<Map<String, Object>>) row.get("phaseFullData");
            for (Map<String, Object> ph : phases) {
                detailedTable.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(row.get("programName").toString()).setFontSize(8)));
                detailedTable.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(row.get("totalEnrolled").toString()).setFontSize(8)).setTextAlignment(TextAlignment.CENTER));
                detailedTable.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(ph.get("phaseName").toString()).setFontSize(8)));
                detailedTable.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(ph.get("ratio").toString()).setFontSize(8)));
                detailedTable.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(ph.get("rating").toString()).setFontSize(8).setBold()));
            }
        }
        document.add(detailedTable);

        // --- PAGE 2: BAR CHART (P-INDEX) ---
        document.add(new AreaBreak());
        document.add(new Paragraph("P-INDEX: PROGRAM PERFORMANCE ANALYTICS").setFont(bold).setFontSize(18).setFontColor(ColorConstants.BLUE));
        byte[] barImg = createBarChart(partData);
        document.add(new Image(ImageDataFactory.create(barImg)).scaleToFit(750, 450).setHorizontalAlignment(HorizontalAlignment.CENTER));

     // --- PAGE 3: LINE CHART (F-INDEX) ---
        document.add(new AreaBreak());
        document.add(new Paragraph("F-INDEX: MULTI-PROGRAM FEEDBACK TIMELINE")
                .setFont(bold).setFontSize(18).setFontColor(ColorConstants.GREEN));

        // 🟢 Casting fixed: List ki jagah Map use karein
        Map<String, List<Double>> timelineMap = (Map<String, List<Double>>) stats.get("cycleTimeline");

        // Naya method call
        byte[] lineImg = createLineChart(timelineMap); 

        document.add(new Image(ImageDataFactory.create(lineImg))
                .scaleToFit(750, 450)
                .setHorizontalAlignment(HorizontalAlignment.CENTER));
        // --- PAGE 4: RADAR CHART (COMPETENCY) ---
        document.add(new AreaBreak());
        document.add(new Paragraph("FACULTY COMPETENCY RADAR (360 DEGREE)").setFont(bold).setFontSize(18).setFontColor(ColorConstants.DARK_GRAY));
        byte[] radarImg = createRadarChart(partData);
        document.add(new Image(ImageDataFactory.create(radarImg)).scaleToFit(500, 450).setHorizontalAlignment(HorizontalAlignment.CENTER));

        document.close();
        byte[] pdfBytes = baos.toByteArray();
        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_PDF);
        h.setContentDispositionFormData("attachment", "Audit_Report_" + teacher.getName().replace(" ","_") + ".pdf");
        return ResponseEntity.ok().headers(h).body(pdfBytes);

    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.internalServerError().build();
    }
}

@GetMapping("/teacher/leaderboard/report/all")
public ResponseEntity<byte[]> generateAllTeachersPerformancePDF() {
    List<Teacher> allTeachers = teacherRepository.findByStatus(Teacher.Status.APPROVED);
    if (allTeachers.isEmpty()) return ResponseEntity.noContent().build();

    try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.setDefaultPageSize(PageSize.A4.rotate());
        Document document = new Document(pdfDoc);

        PdfFont bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        PdfFont normal = PdfFontFactory.createFont(StandardFonts.HELVETICA);

        // --- GLOBAL COVER PAGE ---
        document.add(new Paragraph("GLOBAL FACULTY PEDAGOGICAL AUDIT REPORT")
                .setFont(bold).setFontSize(26).setTextAlignment(TextAlignment.CENTER).setMarginTop(200));
        document.add(new Paragraph("Consolidated Quality Audit: SDG-4 Compliance")
                .setFontSize(14).setTextAlignment(TextAlignment.CENTER).setItalic());
        document.add(new Paragraph("MEERUT INSTITUTE OF TECHNOLOGY, MEERUT").setFont(bold).setTextAlignment(TextAlignment.CENTER));
        
        List<Teacher> inactiveTeachers = new ArrayList<>();

        // 🟢 LOOP START
        for (Teacher teacher : allTeachers) {
            Map<String, Object> stats = getTeacherStats(teacher.getId());
            List<Map<String, Object>> partData = (List<Map<String, Object>>) stats.get("participationTable");

            // CHECK: Kya teacher ka koi feedback data hai?
            boolean hasFeedback = false;
            for (Map<String, Object> prog : partData) {
                List<Map<String, Object>> phases = (List<Map<String, Object>>) prog.get("phaseFullData");
                if (!phases.isEmpty()) { hasFeedback = true; break; }
            }

            if (!hasFeedback) {
                inactiveTeachers.add(teacher);
                continue; 
            }

            // --- ACTIVE TEACHER DETAILED SECTION ---
            document.add(new AreaBreak()); // Start new section on new page
            
            document.add(new Paragraph("MEERUT INSTITUTE OF TECHNOLOGY, MEERUT")
                    .setFont(bold).setFontSize(20).setFontColor(ColorConstants.BLUE).setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("National Quality Audit: SDG-4 Pedagogical Excellence Index")
                    .setFontSize(9).setItalic().setTextAlignment(TextAlignment.CENTER));
            document.add(new LineSeparator(new SolidLine(1.5f)).setMarginBottom(10));

            Table tTable = new Table(UnitValue.createPercentArray(new float[]{15, 35, 15, 35})).useAllAvailableWidth();
            addInfoCell(tTable, "Faculty:", bold); 
            addInfoCell(tTable, teacher.getName(), normal);
            addInfoCell(tTable, "Employee ID:", bold); 
            addInfoCell(tTable, teacher.getEmployeeId(), normal);
            document.add(tTable);

            // KPI TILES
            Table kpiTable = new Table(UnitValue.createPercentArray(new float[]{25, 25, 25, 25})).useAllAvailableWidth().setMarginTop(10);
            renderKPICell(kpiTable, "GLOBAL KPI RATING", stats.get("avgRating").toString(), ColorConstants.BLUE);
            renderKPICell(kpiTable, "AI SENTIMENT SCORE", stats.get("sentimentScore") + "%", ColorConstants.GREEN);
            renderKPICell(kpiTable, "PEDAGOGICAL POWER", stats.get("pedagogicalPower").toString(), ColorConstants.ORANGE);
            renderKPICell(kpiTable, "AUDIT SAMPLES", stats.get("totalResponses").toString(), ColorConstants.CYAN);
            document.add(kpiTable);

            // Detailed Audit Table
            document.add(new Paragraph("\nDETAILED FEEDBACK AUDIT LOGS").setFont(bold).setFontSize(10));
            Table detTable = new Table(UnitValue.createPercentArray(new float[]{30, 15, 25, 15, 15})).useAllAvailableWidth();
            String[] heads = {"Program", "Enrolled", "Phase", "Ratio", "Rating"};
            
            for(String h : heads) {
                detTable.addHeaderCell(new com.itextpdf.layout.element.Cell()
                    .add(new Paragraph(h).setBold().setFontColor(ColorConstants.WHITE))
                    .setBackgroundColor(ColorConstants.DARK_GRAY));
            }

            for (Map<String, Object> row : partData) {
                List<Map<String, Object>> phases = (List<Map<String, Object>>) row.get("phaseFullData");
                for (Map<String, Object> ph : phases) {
                    detTable.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(row.get("programName").toString()).setFontSize(8)));
                    detTable.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(row.get("totalEnrolled").toString()).setFontSize(8)).setTextAlignment(TextAlignment.CENTER));
                    detTable.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(ph.get("phaseName").toString()).setFontSize(8)));
                    detTable.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(ph.get("ratio").toString()).setFontSize(8)));
                    detTable.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(ph.get("rating").toString()).setFontSize(8).setBold()));
                }
            }
            document.add(detTable);

            // Individual Pages for Charts
            document.add(new AreaBreak());
            document.add(new Paragraph("P-INDEX PERFORMANCE: " + teacher.getName()).setFont(bold).setFontSize(14));
            document.add(new Image(ImageDataFactory.create(createBarChart(partData))).scaleToFit(750, 400).setHorizontalAlignment(HorizontalAlignment.CENTER));

            document.add(new AreaBreak());
            document.add(new Paragraph("F-INDEX TIMELINE: " + teacher.getName()).setFont(bold).setFontSize(14));
         // Casting ko List se badal kar Map karein kyunki humne getTeacherStats ko update kiya hai
            Map<String, List<Double>> timelineMap = (Map<String, List<Double>>) stats.get("cycleTimeline");

            document.add(new Image(ImageDataFactory.create(createLineChart(timelineMap)))
                    .scaleToFit(750, 400)
                    .setHorizontalAlignment(HorizontalAlignment.CENTER));
            document.add(new AreaBreak());
            document.add(new Paragraph("COMPETENCY RADAR: " + teacher.getName()).setFont(bold).setFontSize(14));
            document.add(new Image(ImageDataFactory.create(createRadarChart(partData))).scaleToFit(450, 400).setHorizontalAlignment(HorizontalAlignment.CENTER));
        }

        // --- 🟢 INACTIVE TEACHERS SUMMARY SECTION ---
        if (!inactiveTeachers.isEmpty()) {
            document.add(new AreaBreak());
            document.add(new Paragraph("PENDING AUDIT & INACTIVE FACULTY SUMMARY")
                    .setFont(bold).setFontSize(18).setFontColor(ColorConstants.RED).setMarginTop(20));
            document.add(new Paragraph("The following faculty members have no feedback created or no action performed yet.")
                    .setFontSize(11).setItalic().setMarginBottom(10));

            Table inactiveTable = new Table(UnitValue.createPercentArray(new float[]{25, 20, 55})).useAllAvailableWidth();
            inactiveTable.addHeaderCell(new com.itextpdf.layout.element.Cell().add(new Paragraph("Faculty Name").setBold()));
            inactiveTable.addHeaderCell(new com.itextpdf.layout.element.Cell().add(new Paragraph("Employee ID").setBold()));
            inactiveTable.addHeaderCell(new com.itextpdf.layout.element.Cell().add(new Paragraph("Status / Assigned Programs").setBold()));

            for (Teacher inTe : inactiveTeachers) {
                inactiveTable.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(inTe.getName()).setFontSize(10)));
                inactiveTable.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(inTe.getEmployeeId()).setFontSize(10)));
                
                List<TeacherAssign> assigns = teacherAssignRepo.findByTeacherId(inTe.getId());
                if (assigns.isEmpty()) {
                    inactiveTable.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph("STATUS: No Programs Assigned").setFontColor(ColorConstants.RED).setFontSize(9)));
                } else {
                    String progNames = assigns.stream().map(a -> a.getProgram().getTrainingProgram()).collect(Collectors.joining(", "));
                    inactiveTable.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph("ACTION PENDING | Programs: " + progNames).setFontSize(9)));
                }
            }
            document.add(inactiveTable);
        }

        document.close();
        byte[] pdfBytes = baos.toByteArray();
        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_PDF);
        h.setContentDispositionFormData("attachment", "Global_Faculty_Audit_Report.pdf");
        return ResponseEntity.ok().headers(h).body(pdfBytes);

    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.internalServerError().build();
    }
}

// --- CORRECTED DASHBOARD LOGIC HELPER ---
private Map<String, Object> getTeacherStats(Long teacherId) {
    Teacher teacher = teacherRepository.findById(teacherId).orElse(null);
    Map<String, Object> stats = new HashMap<>();
    if (teacher == null) return stats;

    List<TeacherAssign> assignments = teacherAssignRepo.findByTeacherId(teacher.getId());
    List<Map<String, Object>> participationTable = new ArrayList<>();
    
    // 🟢 CHANGES HERE: List ki jagah LinkedHashMap use karein multi-line data ke liye
    Map<String, List<Double>> programTimelineMap = new LinkedHashMap<>(); 
    
    List<Long> allFeedbackIds = new ArrayList<>();
    long totalEnroll = 0;

    for (TeacherAssign ta : assignments) {
        Program prog = ta.getProgram();
        long enroll = enrolledProgramRepo.countByProgramAndStatus(prog, EnrolledProgram.ProgramStatus.APPROVED);
        totalEnroll += enroll;
        
        List<Map<String, Object>> phaseFullData = new ArrayList<>();
        List<Double> currentProgramRatings = new ArrayList<>(); // 🟢 Ek specific program ki list
        
        double pSum = 0; int pCount = 0;
        List<Feedback> feedbacks = feedRepo.findByProgramId(prog.getId());

        for (Feedback fb : feedbacks) {
            allFeedbackIds.add(fb.getId());
            Double avg = answerRepo.getAverageRatingByFeedback(fb.getId());
            double safeVal = (avg != null) ? avg : 0.0;
            long part = answerRepo.countUniqueStudentsByFeedbackId(fb.getId());

            phaseFullData.add(Map.of(
                "phaseName", fb.getFeedbackPhase().getPhaseName(), 
                "ratio", part + "/" + enroll, 
                "rating", String.format("%.2f", safeVal)
            ));

            // 🟢 Rating ko current program ki list mein add karein
            currentProgramRatings.add(safeVal);

            if(safeVal > 0) { pSum += safeVal; pCount++; }
        }
        
        participationTable.add(Map.of(
            "programName", prog.getTrainingProgram(), 
            "totalEnrolled", enroll, 
            "phaseFullData", phaseFullData, 
            "avgPerformance", pCount > 0 ? pSum/pCount : 0.0
        ));

        // 🟢 Program name ke against ratings ki list map mein save karein
        programTimelineMap.put(prog.getTrainingProgram(), currentProgramRatings);
    }

    // --- Metrics Calculation ---
    long activeStudents = allFeedbackIds.isEmpty() ? 0 : answerRepo.countUniqueStudentsInFeedbackList(allFeedbackIds);
    long totalPos = 0, totalNeg = 0, totalNeu = 0;
    double globalSum = 0; int globalEntries = 0;

    for (Long fid : allFeedbackIds) {
        Double avg = answerRepo.getAverageRatingByFeedback(fid);
        if (avg != null && avg > 0) { globalSum += avg; globalEntries++; }
        totalPos += answerRepo.countBySentimentAndFeedback(fid, "POSITIVE");
        totalNeg += answerRepo.countBySentimentAndFeedback(fid, "NEGATIVE");
        totalNeu += answerRepo.countBySentimentAndFeedback(fid, "NEUTRAL");
    }

    double finalAvg = (globalEntries > 0) ? (globalSum / globalEntries) : 0.0;
    double sentPercent = (totalPos + totalNeg + totalNeu > 0) ? (double) totalPos / (totalPos + totalNeg + totalNeu) * 100 : 0;
    double power = (finalAvg * 10) + (sentPercent * 0.5);

    stats.put("avgRating", String.format("%.2f", finalAvg));
    stats.put("pedagogicalPower", Math.round(power * 10) / 10.0);
    stats.put("sentimentScore", Math.round(sentPercent));
    stats.put("totalResponses", activeStudents + " / " + totalEnroll);
    stats.put("participationTable", participationTable);
    
    // 🟢 AB YEH MAP RETURN KAREGA (Error fixed)
    stats.put("cycleTimeline", programTimelineMap); 
    
    return stats;
}

// --- JFREECHART HELPERS WITH RATING DIGITS ---
private byte[] createBarChart(List<Map<String, Object>> data) throws IOException {
    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    for (Map<String, Object> row : data) dataset.addValue((Double)row.get("avgPerformance"), "Rating", (String)row.get("programName"));
    JFreeChart chart = ChartFactory.createBarChart("", "Programs", "Pedagogical Rating", dataset);
    CategoryPlot plot = chart.getCategoryPlot();
    BarRenderer renderer = (BarRenderer) plot.getRenderer();
    renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator());
    renderer.setDefaultItemLabelsVisible(true); // Show digits
    chart.setBackgroundPaint(java.awt.Color.WHITE);
    return chartToByteArray(chart);
}

private byte[] createLineChart(Map<String, List<Double>> timelineMap) throws IOException {
    DefaultCategoryDataset dataset = new DefaultCategoryDataset();

    // 1. Har Program (Key) ke liye data points add karein
    for (Map.Entry<String, List<Double>> entry : timelineMap.entrySet()) {
        String programName = entry.getKey();
        List<Double> ratings = entry.getValue();
        
        for (int i = 0; i < ratings.size(); i++) {
            // dataset.addValue(Value, Series_Name, Category_Name)
            // Series_Name hi line ka naam (Legend) banta hai
            dataset.addValue(ratings.get(i), programName, "Phase " + (i + 1));
        }
    }

    // 2. Chart Create karein
    JFreeChart chart = ChartFactory.createLineChart(
            "",                      // Chart Title
            "Feedback Phases",       // X-Axis Label
            "Rating Score",          // Y-Axis Label
            dataset                  // Dataset
    );

    // 3. Visual Styling
    CategoryPlot plot = chart.getCategoryPlot();
    LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
    
    // Sabhi lines (Series) ke liye settings apply karein
    for (int i = 0; i < dataset.getRowCount(); i++) {
        renderer.setSeriesShapesVisible(i, true);      // Dots (Shapes) dikhayein
        renderer.setSeriesStroke(i, new java.awt.BasicStroke(2.5f)); // Line thodi moti
    }

    renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator());
    renderer.setDefaultItemLabelsVisible(true); // Digits dikhayein
    
    chart.setBackgroundPaint(java.awt.Color.WHITE);
    
    // Legend ko bottom par set karein taaki graph area bada dikhe
    chart.getLegend().setFrame(org.jfree.chart.block.BlockBorder.NONE);
    
    return chartToByteArray(chart);
}

private byte[] createRadarChart(List<Map<String, Object>> data) throws IOException {
    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    for (Map<String, Object> row : data) dataset.addValue((Double)row.get("avgPerformance"), "Proficiency", (String)row.get("programName"));
    SpiderWebPlot plot = new SpiderWebPlot(dataset);
    plot.setBaseSeriesPaint(new java.awt.Color(6, 78, 59, 180));
    return chartToByteArray(new JFreeChart("", JFreeChart.DEFAULT_TITLE_FONT, plot, false));
}

private byte[] chartToByteArray(JFreeChart chart) throws IOException {
    BufferedImage img = chart.createBufferedImage(1000, 600);
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    ImageIO.write(img, "png", bos);
    return bos.toByteArray();
}

private void addInfoCell(Table table, String text, PdfFont font) {
    table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(text != null ? text : "N/A").setFont(font).setFontSize(10)).setBorder(Border.NO_BORDER));
}

private void renderKPICell(Table table, String label, String value, com.itextpdf.kernel.colors.Color color) {
    table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(label).setFontSize(8).setFontColor(ColorConstants.GRAY))
            .add(new Paragraph(value).setFontSize(22).setBold().setFontColor(color)).setTextAlignment(TextAlignment.CENTER).setPadding(15).setBorder(new com.itextpdf.layout.borders.SolidBorder(0.5f)));
}

}