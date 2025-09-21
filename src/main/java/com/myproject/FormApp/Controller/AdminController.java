package com.myproject.FormApp.Controller;

import com.myproject.FormApp.Model.Student;
import com.myproject.FormApp.Model.Teacher;
import com.myproject.FormApp.Repository.StudentsRepository;
import com.myproject.FormApp.Repository.TeacherRepository;
import com.myproject.FormApp.Service.EmailService;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final StudentsRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final EmailService emailService;


    public AdminController(StudentsRepository studentRepository, TeacherRepository teacherRepository, EmailService emailService) {
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.emailService = emailService;
    }

    @GetMapping("/dashboard")
    public String showDashboard() {
        return "admin/Dashboard";
    }

    @GetMapping("/program")
    public String showProgram() {
        return "admin/Program";
    }

 // ✅ Student List with optional status filter & rollNo search
    @GetMapping("/student")
    public String showStudent(
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "rollNo", required = false) String rollNo,
            Model model) {

        List<Student> students;

        if ((status == null || status.equals("ALL")) && (rollNo == null || rollNo.isEmpty())) {
            students = studentRepository.findAll();
        } else if (status != null && !status.equals("ALL") && (rollNo == null || rollNo.isEmpty())) {
            students = studentRepository.findByStatus(Student.Status.valueOf(status));
        } else if ((status == null || status.equals("ALL")) && rollNo != null && !rollNo.isEmpty()) {
            students = studentRepository.findByRollNoContaining(rollNo);
        } else {
            // Both filter and search applied
            students = studentRepository.findByStatusAndRollNoContaining(
                    Student.Status.valueOf(status), rollNo);
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
        Student student = studentRepository.findById(id).orElseThrow();
        student.setStatus(status);
        studentRepository.save(student);

        // Send Student Email
        emailService.sendStudentStatusUpdate(
                student.getEmail(),
                student.getName(),
                student.getRollNo(),
                student.getRole().name(),
                status.name()
        );

        attr.addFlashAttribute("msg", "Student Status UPDATED Successfully & Email Sent");
        return "redirect:/admin/student";
    }


    
 // ✅ Show Student Details
    @GetMapping("/student/details/{id}")
    public String showStudentDetails(@PathVariable Long id, Model model) {
        Student student = studentRepository.findById(id).orElseThrow();
        model.addAttribute("student", student);
        return "admin/StudentDetails"; // Thymeleaf page to show student info
    }


 // ✅ Teacher List with optional Status & EmployeeId filter
    @GetMapping("/teacher")
    public String showTeacher(
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "employeeId", required = false) String employeeId,
            Model model) {

        List<Teacher> teachers;

        if ((status == null || status.equals("ALL")) && (employeeId == null || employeeId.isEmpty())) {
            teachers = teacherRepository.findAll();
        } else if (status != null && !status.equals("ALL") && (employeeId == null || employeeId.isEmpty())) {
            teachers = teacherRepository.findByStatus(Teacher.Status.valueOf(status));
        } else if ((status == null || status.equals("ALL")) && employeeId != null && !employeeId.isEmpty()) {
            teachers = teacherRepository.findByEmployeeIdContaining(employeeId);
        } else {
            teachers = teacherRepository.findByStatusAndEmployeeIdContaining(
                    Teacher.Status.valueOf(status), employeeId);
        }

        model.addAttribute("teachers", teachers);
        model.addAttribute("selectedStatus", status != null ? status : "ALL");
        model.addAttribute("searchEmployeeId", employeeId != null ? employeeId : "");

        return "admin/Teacher"; // Thymeleaf page
    }

    @PostMapping("/teacher/updateStatus/{id}")
    public String updateTeacherStatus(@PathVariable Long id,
                                      @RequestParam("status") Teacher.Status status,
                                      RedirectAttributes attr) {
        Teacher teacher = teacherRepository.findById(id).orElseThrow();
        teacher.setStatus(status);
        teacherRepository.save(teacher);

        // Send Teacher Email
        emailService.sendTeacherStatusUpdate(
                teacher.getEmail(),
                teacher.getName(),
                teacher.getEmployeeId(),
                status.name(),
                teacher.getEmail()
        );

        attr.addFlashAttribute("msg", "Teacher Status UPDATED Successfully & Email Sent");
        return "redirect:/admin/teacher";
    }

    
 // ✅ Show Teacher Details
    @GetMapping("/teacher/details/{id}")
    public String showTeacherDetails(@PathVariable Long id, Model model) {
        Teacher teacher = teacherRepository.findById(id).orElseThrow();
        model.addAttribute("teacher", teacher);
        return "admin/TeacherDetails"; // Thymeleaf page
    }


    
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/index?msg=Logged+out";
    }
}
