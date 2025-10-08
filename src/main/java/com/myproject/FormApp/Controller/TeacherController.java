package com.myproject.FormApp.Controller;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import com.itextpdf.layout.element.Paragraph;


//✅ Java / Utility
import java.io.ByteArrayOutputStream;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

//✅ Apache POI (Excel)
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

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
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.io.font.constants.StandardFonts;

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
                    "Feedback_Report_" + feedback.getId() + ".pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(("Error generating feedback PDF: " + e.getMessage()).getBytes());
        }
    }

	
	
}
