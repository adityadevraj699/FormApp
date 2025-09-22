package com.myproject.FormApp.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myproject.FormApp.Model.CurriculumTopic;
import com.myproject.FormApp.Model.Module;
import com.myproject.FormApp.Model.Program;
import com.myproject.FormApp.Model.Student;
import com.myproject.FormApp.Model.Teacher;
import com.myproject.FormApp.Model.Teacher.Status;
import com.myproject.FormApp.Model.TeacherAssign;
import com.myproject.FormApp.Repository.CurriculumTopicRepository;
import com.myproject.FormApp.Repository.ModuleRepository;
import com.myproject.FormApp.Repository.ProgramRepository;
import com.myproject.FormApp.Repository.StudentsRepository;
import com.myproject.FormApp.Repository.TeacherAssignRepository;
import com.myproject.FormApp.Repository.TeacherRepository;
import com.myproject.FormApp.Service.EmailService;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
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
    public String showDashboard() {
        if (!isLoggedIn()) return redirectIfNotLoggedIn();
        return "admin/Dashboard";
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


}
