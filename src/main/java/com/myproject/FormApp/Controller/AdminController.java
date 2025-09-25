package com.myproject.FormApp.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myproject.FormApp.Model.Admin;
import com.myproject.FormApp.Model.CurriculumTopic;
import com.myproject.FormApp.Model.FeedBackPhase;
import com.myproject.FormApp.Model.Feedback;
import com.myproject.FormApp.Model.FeedbackQuestionCategory;
import com.myproject.FormApp.Model.Module;
import com.myproject.FormApp.Model.Program;
import com.myproject.FormApp.Model.Question;
import com.myproject.FormApp.Model.QuestionCatrgories;
import com.myproject.FormApp.Model.Student;
import com.myproject.FormApp.Model.Teacher;
import com.myproject.FormApp.Model.Teacher.Status;
import com.myproject.FormApp.Model.TeacherAssign;
import com.myproject.FormApp.Repository.CurriculumTopicRepository;
import com.myproject.FormApp.Repository.FeedBackPhaseRepository;
import com.myproject.FormApp.Repository.FeedbackQuestionCategoryRepository;
import com.myproject.FormApp.Repository.FeedbackRepository;
import com.myproject.FormApp.Repository.ModuleRepository;
import com.myproject.FormApp.Repository.ProgramRepository;
import com.myproject.FormApp.Repository.QuestionCatrgoriesRepository;
import com.myproject.FormApp.Repository.QuestionRepository;
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

        // DB से सभी TeacherAssign records लाओ
        List<TeacherAssign> programList = teacherAssignRepo.findAll();

        // Model में डालो
        model.addAttribute("programList", programList);

        return "admin/Dashboard"; // Thymeleaf template
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

        model.addAttribute("program", program);
        model.addAttribute("modules", modules);
        model.addAttribute("teacherAssignments", teacherAssignments);
        model.addAttribute("moduleTopicsMap", moduleTopicsMap);

        return "admin/programDetail";
    }







    // ---------------------- PROGRAM ----------------------
    @GetMapping("/program")
    public String showProgram() {
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

        TeacherAssign assign = new TeacherAssign();
        assign.setTeacher(teacher);
        assign.setProgram(program);
        teacherAssignRepo.save(assign);

        // Prepare program details
        String programDetails = "Program Name: " + program.getTrainingProgram() 
                              + "\nDuration: " + program.getStartDate()  // example field
                              + "\nDescription: " + program.getEndDate(); // example field

        // Send email
        emailService.sendTeacherProgramAssignment(teacher.getEmail(), teacher.getName(), program.getTrainingProgram(), programDetails);

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

            for (int i = 0; i < questions.size(); i++) {
                String text = questions.get(i).trim();
                Question.AnswerType type = Question.AnswerType.valueOf(answerTypes.get(i));
                Question q = new Question(category, text, type);

                if (type == Question.AnswerType.NUMBER) {
                    q.setRangeStart(rangeStarts.get(i));
                    q.setRangeEnd(rangeEnds.get(i));
                }

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


}
