package com.myproject.FormApp.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myproject.FormApp.Model.Admin;
import com.myproject.FormApp.Model.CurriculumTopic;
import com.myproject.FormApp.Model.EnrolledProgram;
import com.myproject.FormApp.Model.FeedBackPhase;
import com.myproject.FormApp.Model.Feedback;
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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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

    // ---------------------- DASHBOARD ----------------------
    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        if (!isLoggedIn()) return redirectIfNotLoggedIn();

        // Teacher Assignments
        List<TeacherAssign> programList = teacherAssignRepo.findAll();
        model.addAttribute("programList", programList);

        // Quick Stats
        long totalStudents = studentRepository.count();
        long approvedTeachers = teacherRepository.findByStatus(Teacher.Status.APPROVED).size();
        long activeFeedbacks = feedRepo.findAll().stream()
                .filter(f -> !f.getStartDate().isAfter(LocalDate.now()) && !f.getEndDate().isBefore(LocalDate.now()))
                .count();

        model.addAttribute("totalStudents", totalStudents);
        model.addAttribute("approvedTeachers", approvedTeachers);
        model.addAttribute("activeFeedbacks", activeFeedbacks);

        return "admin/Dashboard";
    }


    
    @GetMapping("/programDetail/{id}")
    public String programDetail(@PathVariable Long id, Model model) {

        Program program = programRepo.findById(id)
                            .orElseThrow(() -> new RuntimeException("Program not found"));

        List<Module> modules = moduleRepo.findByProgramId(id);

        Map<Long, List<CurriculumTopic>> moduleTopicsMap = new HashMap<>();
        for (Module module : modules) {
            List<CurriculumTopic> topics = curriculumTopicRepo.findByModuleId(module.getId());
            moduleTopicsMap.put(module.getId(), topics);
        }

        List<TeacherAssign> teacherAssignments = teacherAssignRepo.findAllByProgramId(id);

        // Enrolled students
        List<EnrolledProgram> enrolledStudents = enrolledProgramRepo.findByProgramId(id);
        long enrolledCount = enrolledProgramRepo.countByProgramId(id);

        model.addAttribute("program", program);
        model.addAttribute("modules", modules);
        model.addAttribute("teacherAssignments", teacherAssignments);
        model.addAttribute("moduleTopicsMap", moduleTopicsMap);
        model.addAttribute("enrolledStudents", enrolledStudents);
        model.addAttribute("enrolledCount", enrolledCount);

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
                             @RequestParam("moduleName") String moduleName,
                             RedirectAttributes redirectAttributes) {

        Program program = programRepo.findById(programId).orElseThrow();
        Module module = new Module();
        module.setModuleName(moduleName);
        module.setProgram(program);
        moduleRepo.save(module);

        redirectAttributes.addFlashAttribute("serverMessageModule", "Module created successfully!");
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
                                      @RequestParam String topicName,
                                      RedirectAttributes redirectAttributes) {
        Module module = moduleRepo.findById(moduleId).orElseThrow();
        CurriculumTopic topic = new CurriculumTopic();
        topic.setModule(module);
        topic.setTopicName(topicName);
        curriculumTopicRepo.save(topic);

        redirectAttributes.addFlashAttribute("serverMessage", "Curriculum Topic created successfully!");
        return "redirect:/admin/curriculumTopic";
    }



    // ---------------------- STUDENT ----------------------
    @GetMapping("/student")
    public String showStudent(@RequestParam(value = "status", required = false) String status,
                              @RequestParam(value = "rollNo", required = false) String rollNo,
                              Model model) {
        if (!isLoggedIn()) return redirectIfNotLoggedIn();

        List<Student> students;

        if ((status == null || status.equals("ALL")) && (rollNo == null || rollNo.isEmpty())) {
            students = studentRepository.findAll();
        } else if (status != null && !status.equals("ALL") && (rollNo == null || rollNo.isEmpty())) {
            students = studentRepository.findByStatus(Student.Status.valueOf(status));
        } else if ((status == null || status.equals("ALL")) && rollNo != null && !rollNo.isEmpty()) {
            students = studentRepository.findByRollNoContaining(rollNo);
        } else {
            students = studentRepository.findByStatusAndRollNoContaining(Student.Status.valueOf(status), rollNo);
        }

        model.addAttribute("students", students);
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
        if (!isLoggedIn()) return redirectIfNotLoggedIn();

        Student student = studentRepository.findById(id).orElseThrow();
        model.addAttribute("student", student);
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
        if (!isLoggedIn()) return redirectIfNotLoggedIn();

        Teacher teacher = teacherRepository.findById(id).orElseThrow();
        model.addAttribute("teacher", teacher);
        return "admin/TeacherDetails";
    }
    
    
 // ---------------------- TEACHER ASSIGN ----------------------
    @GetMapping("/teacherManage")
    public String showTeacherAssignForm(Model model) {
        if (!isLoggedIn()) return redirectIfNotLoggedIn();

        List<Teacher> teachers = teacherRepository.findByStatus(Teacher.Status.APPROVED);

        List<Program> programs = programRepo.findAll();

        model.addAttribute("teachers", teachers);
        model.addAttribute("programs", programs);
        model.addAttribute("teacherAssign", new TeacherAssign());

        return "admin/TeacherManage"; // Ye html file ka naam
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

        // Prepare program details
        String programDetails = "Program Name: " + program.getTrainingProgram()
                              + "\nDuration: " + program.getStartDate()
                              + "\nDescription: " + program.getEndDate();

        // Send email
        emailService.sendTeacherProgramAssignment(
                teacher.getEmail(),
                teacher.getName(),
                program.getTrainingProgram(),
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

        List<Feedback> feedbacks = feedRepo.findAll();

        // Create a map of feedbackId -> unique students
        Map<Long, List<Student>> feedbackStudentsMap = new HashMap<>();
        for (Feedback feedback : feedbacks) {
            List<Student> uniqueStudents = feedback.getStudentFeedbackAnswers().stream()
                    .map(StudentFeedbackAnswer::getStudent)
                    .distinct()
                    .toList();
            feedbackStudentsMap.put(feedback.getId(), uniqueStudents);
        }

        model.addAttribute("feedbacks", feedbacks);
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

    // Update Program
    @PostMapping("/program/update")
    public String updateProgram(@ModelAttribute Program program, RedirectAttributes redirectAttributes) {
        // Save updated program
        programRepo.save(program);

        // Add success message
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

        List<Teacher> teachers = teacherRepository.findAllByStatus(Teacher.Status.APPROVED);
        List<Program> programs = programRepo.findAll();

        model.addAttribute("assign", assign);
        model.addAttribute("teachers", teachers);
        model.addAttribute("programs", programs);

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
    
    
   

    
}