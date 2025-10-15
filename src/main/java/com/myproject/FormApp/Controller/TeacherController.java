package com.myproject.FormApp.Controller;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.imageio.ImageIO;

import com.itextpdf.layout.element.Paragraph;

import java.awt.Color;
import java.awt.image.BufferedImage;
//✅ Java / Utility
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//✅ Servlet / HTTP
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

//✅ Spring Framework
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

//✅ Apache POI (Excel)
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.PieChart;
import org.knowm.xchart.PieChartBuilder;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;

import org.knowm.xchart.style.markers.SeriesMarkers;

//✅ Cloudinary
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

//✅ Your Project Entities
import com.myproject.FormApp.Model.CurriculumTopic;
import com.myproject.FormApp.Model.EnrolledProgram;
import com.myproject.FormApp.Model.Feedback;
import com.myproject.FormApp.Model.FeedbackAnalysis;
import com.myproject.FormApp.Model.FeedbackQuestionCategory;
import com.myproject.FormApp.Model.Program;
import com.myproject.FormApp.Model.Question;
import com.myproject.FormApp.Model.Student;
import com.myproject.FormApp.Model.StudentFeedbackAnswer;
import com.myproject.FormApp.Model.Module;
import com.myproject.FormApp.Model.Teacher;
import com.myproject.FormApp.Model.TeacherAssign;
import com.myproject.FormApp.Model.EnrolledProgram.ProgramStatus;
import com.myproject.FormApp.Model.Question.AnswerType;

//✅ Your Project Repositories
import com.myproject.FormApp.Repository.CurriculumTopicRepository;
import com.myproject.FormApp.Repository.EnrolledProgramRepository;
import com.myproject.FormApp.Repository.FeedbackRepository;
import com.myproject.FormApp.Repository.ModuleRepository;
import com.myproject.FormApp.Repository.ProgramRepository;
import com.myproject.FormApp.Repository.StudentFeedbackAnswerRepository;
import com.myproject.FormApp.Repository.StudentsRepository;
import com.myproject.FormApp.Repository.TeacherAssignRepository;
import com.myproject.FormApp.Repository.TeacherRepository;

//✅ Your Project Services
import com.myproject.FormApp.Service.FeedbackAnalysisService;

//✅ iText PDF
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.properties.AreaBreakType;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.myproject.FormApp.Model.CurriculumTopic;
import com.myproject.FormApp.Model.EnrolledProgram;
import com.myproject.FormApp.Model.Feedback;
import com.myproject.FormApp.Model.FeedbackAnalysis;
import com.myproject.FormApp.Model.Program;
import com.myproject.FormApp.Model.Question;
import com.myproject.FormApp.Model.Question.AnswerType;
import com.myproject.FormApp.Model.Student;
import com.myproject.FormApp.Model.StudentFeedbackAnswer;
import com.myproject.FormApp.Model.Module;
import com.myproject.FormApp.Model.Teacher;
import com.myproject.FormApp.Model.TeacherAssign;
import com.myproject.FormApp.Model.EnrolledProgram.ProgramStatus;
import com.myproject.FormApp.Repository.CurriculumTopicRepository;
import com.myproject.FormApp.Repository.EnrolledProgramRepository;
import com.myproject.FormApp.Repository.FeedbackRepository;
import com.myproject.FormApp.Repository.ModuleRepository;
import com.myproject.FormApp.Repository.ProgramRepository;
import com.myproject.FormApp.Repository.StudentFeedbackAnswerRepository;
import com.myproject.FormApp.Repository.StudentsRepository;
import com.myproject.FormApp.Repository.TeacherAssignRepository;
import com.myproject.FormApp.Repository.TeacherRepository;
import com.myproject.FormApp.Service.FeedbackAnalysisService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/Teacher")
public class TeacherController {

	
	@Autowired
	private TeacherRepository teacherRepo;
	
	@Autowired
	private ProgramRepository programRepo;
	
	@Autowired
	private ModuleRepository moduleRepo;
	
	 @Autowired
	    private TeacherAssignRepository teacherAssignRepository;
	 
	 
	 @Autowired
	 private StudentFeedbackAnswerRepository studentFeedbackAnswerRepo;
	 
	 @Autowired
	 private FeedbackRepository feedbackRepo;

	 
	 @Autowired
	 private CurriculumTopicRepository curriculumTopicRepo;
	 
	 @Autowired
	 private TeacherAssignRepository teacherAssignRepo;
	 
	 @Autowired
	 private EnrolledProgramRepository enrolledProgramRepo;
	 
	  @Autowired
	  private  Cloudinary cloudinary;

	    @Autowired
	    private HttpSession session;
	    
	    @Autowired
	    private FeedbackAnalysisService analysisService;
	    
	    
	    @Autowired
	    private StudentsRepository studentRepo;
	    
	    
	    
	    @GetMapping("/Dashboard")
	    public String showDashboard(Model model) {
	        Teacher teacher = (Teacher) session.getAttribute("loggedInTeacher");
	        if (teacher == null) {
	            return "redirect:/";
	        }

	        List<TeacherAssign> assignedPrograms = teacherAssignRepository.findByTeacherId(teacher.getId());

	        // Feedbacks grouped by program with active/inactive count
	        Map<Long, Map<String, Long>> feedbackStatus = new HashMap<>();
	        for (TeacherAssign assign : assignedPrograms) {
	            List<Feedback> feedbacks = feedbackRepo.findByProgramId(assign.getProgram().getId());
	            long active = feedbacks.stream()
	            	    .filter(f -> 
	            	        ( !f.getStartDate().isAfter(LocalDate.now()) ) &&   // startDate <= today
	            	        ( !f.getEndDate().isBefore(LocalDate.now()) )       // endDate >= today
	            	    )
	            	    .count();

	            long inactive = feedbacks.size() - active;

	            Map<String, Long> statusMap = new HashMap<>();
	            statusMap.put("active", active);
	            statusMap.put("inactive", inactive);
	            feedbackStatus.put(assign.getProgram().getId(), statusMap);
	        }

	        long totalFeedbacks = feedbackStatus.values().stream()
	                                    .mapToLong(s -> s.get("active") + s.get("inactive"))
	                                    .sum();

	        model.addAttribute("loggedInTeacher", teacher);
	        model.addAttribute("assignedPrograms", assignedPrograms);
	        model.addAttribute("feedbackStatus", feedbackStatus);
	        model.addAttribute("totalFeedbacks", totalFeedbacks);

	        return "Teacher/Dashboard";
	    }

	
	@GetMapping("/AssignProgram")
    public String showAssignProgram(Model model) {
        Teacher teacher = (Teacher) session.getAttribute("loggedInTeacher");

        if (teacher == null) {
            return "redirect:/";
        }

        List<TeacherAssign> assignedPrograms = teacherAssignRepository.findByTeacherId(teacher.getId());
        model.addAttribute("assignedPrograms", assignedPrograms);

        return "Teacher/AssignProgram";
    }
	
	@GetMapping("/Feedback")
	public String showFeedback(Model model) {
	    Teacher teacher = (Teacher) session.getAttribute("loggedInTeacher");
	    if (teacher == null) {
	        return "redirect:/";
	    }

	    List<TeacherAssign> assignedPrograms = teacherAssignRepository.findByTeacherId(teacher.getId());

	    Map<Long, List<Feedback>> programFeedbacks = new HashMap<>();
	    for (TeacherAssign assign : assignedPrograms) {
	        List<Feedback> feedbacks = feedbackRepo.findByProgramId(assign.getProgram().getId());
	        programFeedbacks.put(assign.getProgram().getId(), feedbacks);
	    }

	    model.addAttribute("assignedPrograms", assignedPrograms);
	    model.addAttribute("programFeedbacks", programFeedbacks);

	    return "Teacher/Feedback";
	}
	
	
	@GetMapping("/programDetail/{id}")
	public String teacherProgramDetail(@PathVariable Long id, Model model, HttpSession session) {
	    // Logged-in teacher check
	    Teacher teacher = (Teacher) session.getAttribute("loggedInTeacher");
	    if (teacher == null) {
	        return "redirect:/";  // login page
	    }

	    // Program detail
	    Program program = programRepo.findById(id)
	            .orElseThrow(() -> new RuntimeException("Program not found"));

	    // Only allow if teacher assigned
	    boolean isAssigned = teacherAssignRepo.existsByProgramIdAndTeacherId(id, teacher.getId());
	    if (!isAssigned) {
	        return "redirect:/teacher/dashboard"; // unauthorized
	    }

	    // Fetch modules & topics
	    List<Module> modules = moduleRepo.findByProgramId(id);
	    Map<Long, List<CurriculumTopic>> moduleTopicsMap = new HashMap<>();
	    for (Module module : modules) {
	        List<CurriculumTopic> topics = curriculumTopicRepo.findByModuleId(module.getId());
	        moduleTopicsMap.put(module.getId(), topics);
	    }

	    // Enrolled students
	    List<EnrolledProgram> enrolledStudents = enrolledProgramRepo.findByProgramId(id);
	    long enrolledCount = enrolledProgramRepo.countByProgramId(id);

	    // ✅ Fetch teacher assignments for this program
	    List<TeacherAssign> teacherAssignments = teacherAssignRepo.findByProgramId(id);

	    // Add to model
	    model.addAttribute("program", program);
	    model.addAttribute("modules", modules);
	    model.addAttribute("moduleTopicsMap", moduleTopicsMap);
	    model.addAttribute("enrolledStudents", enrolledStudents);
	    model.addAttribute("enrolledCount", enrolledCount);
	    model.addAttribute("teacherAssignments", teacherAssignments); // ✅ Add this

	    return "Teacher/programDetail";
	}


	
	@PostMapping("/updateStatus/{id}")
	public String teacherUpdateStatus(@PathVariable Long id, HttpSession session) {
	    Teacher teacher = (Teacher) session.getAttribute("loggedInTeacher");
	    if (teacher == null) {
	        return "redirect:/";
	    }

	    EnrolledProgram ep = enrolledProgramRepo.findById(id).orElse(null);
	    if (ep != null) {
	        // ensure teacher is assigned to this program
	        boolean isAssigned = teacherAssignRepo.existsByProgramIdAndTeacherId(ep.getProgram().getId(), teacher.getId());
	        if (!isAssigned) {
	            return "redirect:/Teacher/Dashboard"; // unauthorized
	        }

	        if (ep.getStatus() == EnrolledProgram.ProgramStatus.PENDING) {
	            ep.setStatus(EnrolledProgram.ProgramStatus.APPROVED);
	        } else {
	            ep.setStatus(EnrolledProgram.ProgramStatus.PENDING);
	        }
	        enrolledProgramRepo.save(ep);

	        return "redirect:/Teacher/programDetail/" + ep.getProgram().getId();
	    }
	    return "redirect:/Teacher/Dashboard";
	}

	@PostMapping("/deleteEnrollment/{id}")
	public String teacherDeleteEnrollment(@PathVariable Long id, HttpSession session) {
	    Teacher teacher = (Teacher) session.getAttribute("loggedInTeacher");
	    if (teacher == null) {
	        return "redirect:/";
	    }

	    EnrolledProgram ep = enrolledProgramRepo.findById(id).orElse(null);
	    if (ep != null) {
	        boolean isAssigned = teacherAssignRepo.existsByProgramIdAndTeacherId(ep.getProgram().getId(), teacher.getId());
	        if (!isAssigned) {
	            return "redirect:/Teacher/Dashboard";
	        }
	        Long programId = ep.getProgram().getId();
	        enrolledProgramRepo.deleteById(id);
	        return "redirect:/Teacher/programDetail/" + programId;
	    }
	    return "redirect:/Teacher/Dashboard";
	}

	
	
	@GetMapping("/ViewProfile")
	public String showViewProfile(Model model) {
		 Teacher teacher = (Teacher) session.getAttribute("loggedInTeacher");
		 model.addAttribute("teacher", teacher);
		return "Teacher/ViewProfile";
	}
	
	
	@GetMapping("/EditProfile")
	public String editProfile(Model model, HttpSession session) {
	    Teacher teacher = (Teacher) session.getAttribute("loggedInTeacher");
	    if (teacher == null) {
	        return "redirect:/";
	    }
	    model.addAttribute("teacher", teacher);
	    return "Teacher/EditProfile";
	}

	@PostMapping("/EditProfile")
	public String updateProfile(@ModelAttribute("teacher") Teacher updatedTeacher,
	                            @RequestParam("profileImageFile") MultipartFile file,
	                            HttpSession session,
	                            RedirectAttributes redirectAttrs) {
	    Teacher teacher = (Teacher) session.getAttribute("loggedInTeacher");
	    if (teacher == null) {
	        return "redirect:/";
	    }
	    updatedTeacher.setEmail(teacher.getEmail());
	    updatedTeacher.setEmployeeId(teacher.getEmployeeId());

	    try {
	        if (file != null && !file.isEmpty()) {
	            Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
	                    ObjectUtils.asMap("folder", "teacher_profiles"));
	            String imageUrl = (String) uploadResult.get("secure_url");
	            teacher.setProfileImage(imageUrl);
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        redirectAttrs.addFlashAttribute("error", "Image upload failed!");
	    }

	    // Baaki fields update
	    teacher.setName(updatedTeacher.getName());
	    teacher.setContactNo(updatedTeacher.getContactNo());
	    teacher.setDepartment(updatedTeacher.getDepartment());
	    teacher.setDesignation(updatedTeacher.getDesignation());
	    teacher.setQualification(updatedTeacher.getQualification());
	    teacher.setExperience(updatedTeacher.getExperience());
	    teacher.setGender(updatedTeacher.getGender());
	    teacher.setAddress(updatedTeacher.getAddress());

	    // DB save
	    teacherRepo.save(teacher);

	    // session update
	    session.setAttribute("loggedInTeacher", teacher);

	    redirectAttrs.addFlashAttribute("success", "Profile updated successfully!");
	    return "redirect:/Teacher/ViewProfile";
	}

	
	// ✅ GET mapping to show the change password page
	@GetMapping("/ChangePassword")
	public String showChangePassword() {
	    if(session.getAttribute("loggedInTeacher") == null) {
	        return "redirect:/";
	    }
	    return "Teacher/ChangePassword"; // Thymeleaf template path
	}

	// ✅ POST mapping to handle the form submission
	@PostMapping("/ChangePassword")
	public String changePassword(RedirectAttributes attributes, HttpServletRequest request) {
		
	    String oldPassword = request.getParameter("oldPassword");
	    String newPassword = request.getParameter("newPassword");
	    String confirmPassword = request.getParameter("confirmPassword");

	    Teacher teacher = (Teacher) session.getAttribute("loggedInTeacher");
	    if(teacher == null) {
	        return "redirect:/";
	    }

	    if (!newPassword.equals(confirmPassword)) {
	        attributes.addFlashAttribute("msg", "New Password and Confirm Password are not same.");
	        return "redirect:/Teacher/ChangePassword";
	    }

	    if (oldPassword.equals(teacher.getPassword())) {
	        if(oldPassword.equals(newPassword)) {
	            attributes.addFlashAttribute("msg", "New Password cannot be same as Old Password.");
	            return "redirect:/Teacher/ChangePassword";
	        }

	        teacher.setPassword(confirmPassword);
	        teacherRepo.save(teacher);
	        session.invalidate();
	        attributes.addFlashAttribute("msg", "Password Successfully Changed. Please login again.");
	        return "redirect:/";
	    } else {
	        attributes.addFlashAttribute("msg", "Invalid Old Password!!!");
	        return "redirect:/Teacher/ChangePassword"; 
	    }
	}
	
	
	
	
	@GetMapping("/FeedbackDetail/{id}")
	public String showFeedbackDetail(@PathVariable Long id, Model model, HttpSession session) {
	    Teacher teacher = (Teacher) session.getAttribute("loggedInTeacher");
	    if (teacher == null) return "redirect:/";

	    // Fetch feedback
	    Feedback feedback = feedbackRepo.findById(id)
	        .orElseThrow(() -> new RuntimeException("Feedback not found"));

	    // Fetch all answers for this feedback
	    List<StudentFeedbackAnswer> answers = studentFeedbackAnswerRepo.findByFeedback(feedback);

	    // Group answers by question
	    Map<Question, List<StudentFeedbackAnswer>> answersByQuestion =
	        answers.stream().collect(Collectors.groupingBy(StudentFeedbackAnswer::getQuestion));

	    // Maps to hold analysis & averages
	    Map<Long, FeedbackAnalysis> analysisByQuestion = new HashMap<>();
	    Map<Long, Double> avgByQuestion = new HashMap<>();

	    for (Map.Entry<Question, List<StudentFeedbackAnswer>> entry : answersByQuestion.entrySet()) {
	        Question question = entry.getKey();
	        List<StudentFeedbackAnswer> ansList = entry.getValue();

	        // AI Analysis
	        FeedbackAnalysis fa = analysisService.computeAndSaveTextAnalysis(feedback, question, ansList);
	        analysisByQuestion.put(question.getId(), fa);

	        if (question.getAnswerType() == Question.AnswerType.NUMBER) {
	            double avg = ansList.stream()
	                .mapToDouble(ans -> {
	                    try { return Double.parseDouble(ans.getAnswer()); } 
	                    catch (NumberFormatException e) { return 0.0; }
	                })
	                .average()
	                .orElse(0.0);

	            avgByQuestion.put(question.getId(), avg);

	            fa.setAvgNumeric(avg);  // store in DB
	        }

	    }

	    model.addAttribute("feedback", feedback);
	    model.addAttribute("answersByQuestion", answersByQuestion);
	    model.addAttribute("analysisByQuestion", analysisByQuestion);
	    model.addAttribute("avgByQuestion", avgByQuestion);

	    return "Teacher/feedbackDetail";
	}
	
	
	@GetMapping("/FeedbackDetailGraph/{id}")
	public String showFeedbackDetailGraph(@PathVariable Long id, Model model, HttpSession session) {
	    Teacher teacher = (Teacher) session.getAttribute("loggedInTeacher");
	    if (teacher == null) return "redirect:/";

	    // Fetch feedback
	    Feedback feedback = feedbackRepo.findById(id)
	            .orElseThrow(() -> new RuntimeException("Feedback not found"));

	    // Fetch all student answers for this feedback
	    List<StudentFeedbackAnswer> answers = studentFeedbackAnswerRepo.findByFeedback(feedback);

	    // Group by question
	    Map<Question, List<StudentFeedbackAnswer>> answersByQuestion =
	            answers.stream().collect(Collectors.groupingBy(StudentFeedbackAnswer::getQuestion));

	    // Map for averages (only numeric)
	    Map<Long, Double> avgByQuestion = new HashMap<>();

	    for (Map.Entry<Question, List<StudentFeedbackAnswer>> entry : answersByQuestion.entrySet()) {
	        Question question = entry.getKey();
	        List<StudentFeedbackAnswer> ansList = entry.getValue();

	        if (question.getAnswerType() == Question.AnswerType.NUMBER) {
	            double avg = ansList.stream()
	                    .mapToDouble(ans -> {
	                        try {
	                            return Double.parseDouble(ans.getAnswer());
	                        } catch (NumberFormatException e) {
	                            return 0.0;
	                        }
	                    })
	                    .average()
	                    .orElse(0.0);

	            avgByQuestion.put(question.getId(), avg);
	        }
	    }

	    model.addAttribute("feedback", feedback);
	    model.addAttribute("answersByQuestion", answersByQuestion);
	    model.addAttribute("avgByQuestion", avgByQuestion);

	    return "Teacher/feedbackDetailGraph";
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
    	
    	Teacher teacher = (Teacher) session.getAttribute("loggedInTeacher");
        if (teacher == null) {
            return "redirect:/";
        }
        try {
            if (file.isEmpty()) {
                redirectAttributes.addFlashAttribute("msg", "Please upload a valid Excel file!");
                return "redirect:/Teacher/programDetails/" + programId;
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
                    String email  = getCellValue(row.getCell(3)); // Column D

                    if (rollNo == null || rollNo.isBlank()) continue;

                    Student student = studentRepo.findByRollNo(rollNo);
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

        return "redirect:/Teacher/programDetail/" + programId;
    }
    
    
    
    @GetMapping("/feedbackReport/allStudents/{feedbackId}")
    public ResponseEntity<byte[]> downloadAllStudentsFeedbackPDFForTeacher(@PathVariable("feedbackId") Long feedbackId) {
        Teacher teacher = (Teacher) session.getAttribute("loggedInTeacher");
        if (teacher == null) {
            return ResponseEntity.status(403).body("Please login to download the feedback report.".getBytes());
        }

        Feedback feedback = feedbackRepo.findById(feedbackId).orElse(null);
        if (feedback == null) {
            return ResponseEntity.notFound().build();
        }

        boolean isAssigned = teacherAssignRepo.existsByProgramIdAndTeacherId(
                feedback.getProgram().getId(), teacher.getId());
        if (!isAssigned) {
            return ResponseEntity.status(403).body("You are not assigned to this program.".getBytes());
        }

        List<StudentFeedbackAnswer> answers = studentFeedbackAnswerRepo.findByFeedback(feedback);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            PdfFont bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            PdfFont normal = PdfFontFactory.createFont(StandardFonts.HELVETICA);
            PdfFont italic = PdfFontFactory.createFont(StandardFonts.HELVETICA_OBLIQUE);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

            // --- Header ---
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

            String teacherName = "";
            if (feedback.getProgram().getTeacherAssignments() != null &&
                    !feedback.getProgram().getTeacherAssignments().isEmpty()) {
                TeacherAssign firstAssign = feedback.getProgram().getTeacherAssignments().get(0);
                if (firstAssign != null && firstAssign.getTeacher() != null) {
                    teacherName = firstAssign.getTeacher().getName();
                }
            }

            Paragraph programInfo = new Paragraph()
                    .add("Program Name: " + feedback.getProgram().getTrainingProgram() + "\n")
                    .add("Teacher Name: " + teacherName + "\n")
                    .add("Feedback Phase: " + feedback.getFeedbackPhase().getPhaseName() + "\n")
                    .add("Feedback Period: " + feedback.getStartDate().format(formatter) +
                            " to " + feedback.getEndDate().format(formatter))
                    .setFont(normal)
                    .setFontSize(11)
                    .setMarginBottom(10);
            document.add(programInfo);

            document.add(new LineSeparator(new SolidLine(1f)));

            // --- Questions & Answers ---
            if (feedback.getFeedbackQuestionCategories() != null) {
                for (FeedbackQuestionCategory categoryMapping : feedback.getFeedbackQuestionCategories()) {
                    if (categoryMapping == null || categoryMapping.getQuestionCategory() == null) continue;

                    String categoryName = categoryMapping.getQuestionCategory().getCategoryName();
                    Paragraph categoryHeader = new Paragraph(categoryName != null ? categoryName : "Category")
                            .setFont(bold)
                            .setFontSize(12)
                            .setFontColor(ColorConstants.MAGENTA);
                    document.add(categoryHeader);

                    if (categoryMapping.getQuestionCategory().getQuestions() != null) {
                        int qNo = 1;
                        for (Question q : categoryMapping.getQuestionCategory().getQuestions()) {
                            if (q == null) continue;

                            // Question (black)
                            document.add(new Paragraph(qNo + ". " + (q.getQuestionText() != null ? q.getQuestionText() : ""))
                                    .setFont(bold)
                                    .setFontColor(ColorConstants.BLACK)
                                    .setFontSize(11));

                            // Answers (blue)
                            List<String> ansList = answers.stream()
                                    .filter(a -> a.getQuestion() != null && a.getQuestion().getId().equals(q.getId()))
                                    .map(StudentFeedbackAnswer::getAnswer)
                                    .collect(Collectors.toList());

                            int ansNo = 1;
                            for (String ans : ansList) {
                                document.add(new Paragraph("Answer " + ansNo + " : " + ans)
                                        .setFont(normal)
                                        .setFontColor(ColorConstants.BLUE)
                                        .setFontSize(11));
                                ansNo++;
                            }

                            if (ansList.isEmpty()) {
                                document.add(new Paragraph("No answers submitted")
                                        .setFont(normal)
                                        .setFontColor(ColorConstants.BLUE)
                                        .setFontSize(11));
                            }

                            document.add(new Paragraph("\n")); // spacing
                            qNo++;
                        }
                    }
                }
            }

            document.add(new LineSeparator(new SolidLine(1f)));
            Paragraph footer = new Paragraph("Generated on: " + LocalDate.now().format(formatter))
                    .setFont(normal)
                    .setFontSize(9)
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setFontColor(ColorConstants.DARK_GRAY);
            document.add(footer);

            document.close();

            byte[] pdfBytes = baos.toByteArray();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment",
                    "Feedback_Report_" +" "+ feedback.getProgram().getTrainingProgram() +" "+ feedback.getId() + ".pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(("Error generating feedback PDF: " + e.getMessage()).getBytes());
        }
    }

    
  @GetMapping("/feedbackReport/full/{feedbackId}")
public ResponseEntity<byte[]> downloadNumberOnlyFeedbackReport(@PathVariable Long feedbackId) {
    Teacher teacher = (Teacher) session.getAttribute("loggedInTeacher");
    if (teacher == null) {
        return ResponseEntity.status(403)
                .body("Please login to download the feedback report.".getBytes());
    }

    Feedback feedback = feedbackRepo.findById(feedbackId).orElse(null);
    if (feedback == null) {
        return ResponseEntity.notFound().build();
    }

    boolean isAssigned = teacherAssignRepo.existsByProgramIdAndTeacherId(
            feedback.getProgram().getId(), teacher.getId());
    if (!isAssigned) {
        return ResponseEntity.status(403).body("You are not assigned to this program.".getBytes());
    }

    List<StudentFeedbackAnswer> answers = studentFeedbackAnswerRepo.findByFeedback(feedback);

    try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc, PageSize.A4);
        document.setMargins(20, 20, 20, 20);

        PdfFont bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        PdfFont normal = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        // --- HEADER ---
        document.add(new Paragraph("MEERUT INSTITUTE OF TECHNOLOGY, MEERUT - 292")
                .setFont(bold).setFontSize(16).setFontColor(ColorConstants.BLUE)
                .setTextAlignment(TextAlignment.CENTER));
        document.add(new Paragraph("Department of Computer Science and Engineering")
                .setFont(bold).setFontSize(14).setTextAlignment(TextAlignment.CENTER));
        document.add(new Paragraph("FEEDBACK REPORT (NUMBER TYPE QUESTIONS ONLY)")
                .setFont(bold).setFontSize(13).setFontColor(ColorConstants.RED)
                .setTextAlignment(TextAlignment.CENTER).setMarginBottom(10));

        // --- PROGRAM INFO ---
        document.add(new Paragraph("Program Name: " + feedback.getProgram().getTrainingProgram()).setFont(normal));
        document.add(new Paragraph("Teacher Name: " + teacher.getName()).setFont(normal));
        document.add(new Paragraph("Feedback Phase: " + feedback.getFeedbackPhase().getPhaseName()).setFont(normal));
        document.add(new Paragraph("Feedback Period: " + feedback.getStartDate().format(fmt) +
                " to " + feedback.getEndDate().format(fmt)).setFont(normal));
        document.add(new Paragraph("Total Students Responded: " + answers.stream()
                .map(StudentFeedbackAnswer::getStudent).distinct().count()).setFont(normal));
        document.add(new LineSeparator(new SolidLine(1f)));

        Map<String, Double> avgPerQuestion = new LinkedHashMap<>();
        int qNoGlobal = 1;

        // --- NUMBER QUESTIONS ONLY ---
        for (FeedbackQuestionCategory catMap : feedback.getFeedbackQuestionCategories()) {
            if (catMap == null || catMap.getQuestionCategory() == null) continue;

            for (Question q : catMap.getQuestionCategory().getQuestions()) {
                if (!q.getAnswerType().equals(Question.AnswerType.NUMBER)) continue;

                document.add(new Paragraph(qNoGlobal + ". " + q.getQuestionText())
                        .setFont(bold).setFontColor(ColorConstants.BLACK));

                List<Double> numericVals = answers.stream()
                        .filter(a -> a.getQuestion() != null && a.getQuestion().getId().equals(q.getId()))
                        .map(a -> {
                            try { return Double.parseDouble(a.getAnswer()); }
                            catch(Exception e) { return 0.0; }
                        })
                        .toList();

                if (numericVals.isEmpty()) {
                    document.add(new Paragraph("No answers submitted").setFont(normal));
                    qNoGlobal++;
                    continue;
                }

                // --- Print Student Answers ---
                int ansNo = 1;
                for (Double val : numericVals) {
                    document.add(new Paragraph("→ Student " + ansNo + ": " + val)
                            .setFont(normal).setFontColor(ColorConstants.BLUE).setFontSize(9));
                    ansNo++;
                }

                // --- Chart height in points ---
                float chartHeight = 180;
                float remainingHeight = document.getRenderer().getCurrentArea().getBBox().getHeight();

                // Move chart to next page if not enough space
                if (remainingHeight < chartHeight + 100) {
                    document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
                }

                // --- Charts per question ---
                CategoryChart barChart = new CategoryChartBuilder()
                        .width(280).height((int) chartHeight)
                        .title("Student Ratings")
                        .xAxisTitle("Student No.").yAxisTitle("Rating").build();
                barChart.getStyler().setChartBackgroundColor(Color.WHITE);
                barChart.getStyler().setLegendVisible(false);
                barChart.addSeries("Ratings",
                        IntStream.range(1, numericVals.size() + 1).boxed().toList(),
                        numericVals);

                XYChart lineChart = new XYChartBuilder()
                        .width(280).height((int) chartHeight)
                        .title("Ratings Trend")
                        .xAxisTitle("Student No.").yAxisTitle("Rating").build();
                lineChart.getStyler().setChartBackgroundColor(Color.WHITE);
                lineChart.getStyler().setLegendVisible(false);
                lineChart.addSeries("Trend",
                        IntStream.range(1, numericVals.size() + 1).boxed().toList(),
                        numericVals);

                Table table = new Table(UnitValue.createPercentArray(new float[]{1, 1})).useAllAvailableWidth();
                table.addCell(new com.itextpdf.layout.element.Cell()
                        .add(new Image(ImageDataFactory.create(toByteArray(BitmapEncoder.getBufferedImage(barChart)))).setAutoScale(true))
                        .setBorder(Border.NO_BORDER));
                table.addCell(new com.itextpdf.layout.element.Cell()
                        .add(new Image(ImageDataFactory.create(toByteArray(BitmapEncoder.getBufferedImage(lineChart)))).setAutoScale(true))
                        .setBorder(Border.NO_BORDER));
                document.add(table);

                // --- Save average for final chart ---
                avgPerQuestion.put("Q" + qNoGlobal, numericVals.stream().mapToDouble(Double::doubleValue).average().orElse(0.0));
                qNoGlobal++;
            }
        }

        // --- Final summary charts ---
      // --- Final summary charts ---
if (!avgPerQuestion.isEmpty()) {
    float chartHeight = 300; // chart height in points
    float remainingHeight = document.getRenderer().getCurrentArea().getBBox().getHeight();
    
    // Move to next page if not enough space for both charts + some margin
    if (remainingHeight < chartHeight + 50) {
        document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
    }
    
 // Small gap before average section header
    document.add(new Paragraph("\n"));

    // --- Overall Summary heading ---
    document.add(new Paragraph("OVERALL SUMMARY")
            .setFont(bold)
            .setFontColor(ColorConstants.BLUE)
            .setFontSize(16)
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginTop(10f)
            .setMarginBottom(15f));

    // --- Average Ratings per Question header ---
    document.add(new Paragraph("AVERAGE RATINGS PER QUESTION")
            .setFont(bold)
            .setFontColor(ColorConstants.RED)
            .setFontSize(14)
            .setMarginTop(5f)
            .setMarginBottom(5f));

    List<String> labels = new ArrayList<>(avgPerQuestion.keySet());
    List<Double> averages = new ArrayList<>(avgPerQuestion.values());

    CategoryChart finalBar = new CategoryChartBuilder()
            .width(500).height((int) chartHeight)
            .title("Average Ratings per Question")
            .xAxisTitle("Question").yAxisTitle("Average").build();
    finalBar.getStyler().setChartBackgroundColor(Color.WHITE);
    finalBar.getStyler().setLegendVisible(false);
    finalBar.addSeries("Average", labels, averages);

    XYChart finalLine = new XYChartBuilder()
            .width(500).height((int) chartHeight)
            .title("Average Ratings Trend")
            .xAxisTitle("Question").yAxisTitle("Average").build();
    finalLine.getStyler().setChartBackgroundColor(Color.WHITE);
    finalLine.getStyler().setLegendVisible(false);
    finalLine.addSeries("Trend", IntStream.range(0, averages.size()).boxed().toList(), averages);

    Table finalTable = new Table(UnitValue.createPercentArray(new float[]{1, 1})).useAllAvailableWidth();
    finalTable.addCell(new com.itextpdf.layout.element.Cell()
            .add(new Image(ImageDataFactory.create(toByteArray(BitmapEncoder.getBufferedImage(finalBar)))).setAutoScale(true))
            .setBorder(Border.NO_BORDER));
    finalTable.addCell(new com.itextpdf.layout.element.Cell()
            .add(new Image(ImageDataFactory.create(toByteArray(BitmapEncoder.getBufferedImage(finalLine)))).setAutoScale(true))
            .setBorder(Border.NO_BORDER));
    document.add(finalTable);
}



        document.add(new LineSeparator(new SolidLine(1f)));
        document.add(new Paragraph("Generated on: " + LocalDate.now().format(fmt))
                .setFont(normal).setFontSize(9).setTextAlignment(TextAlignment.RIGHT));

        document.close();
        byte[] pdfBytes = baos.toByteArray();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment",
                feedback.getProgram().getTrainingProgram() + " " + feedback.getId() + ".pdf");

        return ResponseEntity.ok().headers(headers).body(pdfBytes);

    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.internalServerError()
                .body(("Error generating PDF: " + e.getMessage()).getBytes());
    }
}

private byte[] toByteArray(BufferedImage img) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ImageIO.write(img, "png", baos);
    return baos.toByteArray();
}




    
   
	
}
