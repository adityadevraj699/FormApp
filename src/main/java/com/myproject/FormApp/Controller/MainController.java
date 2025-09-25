package com.myproject.FormApp.Controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.myproject.FormApp.Model.Admin;
import com.myproject.FormApp.Model.Student;
import com.myproject.FormApp.Model.Teacher;
import com.myproject.FormApp.Repository.AdminRepository;
import com.myproject.FormApp.Repository.ProgramRepository;
import com.myproject.FormApp.Repository.StudentsRepository;
import com.myproject.FormApp.Repository.TeacherRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class MainController {

    @Autowired private AdminRepository adminRepo;
    @Autowired private ProgramRepository programRepo;
    @Autowired private StudentsRepository studentRepo;
    @Autowired private TeacherRepository teacherRepo;
    @Autowired private HttpSession session;

    @GetMapping({"/","/index"})
    public String index() {
        return "index";
    }

    @PostMapping("/login")
    public String login(HttpServletRequest request, HttpSession session, RedirectAttributes attr) {
        try {
            String email = request.getParameter("email");
            String password = request.getParameter("password");
            String role = request.getParameter("role");

            if("ADMIN".equals(role) && adminRepo.existsByEmail(email)) {
                Admin admin = adminRepo.findByEmail(email);
                if(admin.getPassword().equals(password) && admin.getRole() == Admin.Role.ADMIN) {
                    session.setAttribute("loggedInAdmin", admin);
                    return "redirect:/admin/dashboard";
                }
                attr.addFlashAttribute("msg","Invalid Password");
            }
            else if("STUDENT".equals(role) && studentRepo.existsByEmail(email)) {
                Student st = studentRepo.findByEmail(email);
                if(st.getPassword().equals(password)) {
                    if(st.getStatus() == Student.Status.APPROVED) {
                        session.setAttribute("loggedInStudent", st);
                        return "redirect:/Student/Dashboard";
                    } else if(st.getStatus() == Student.Status.PENDING) {
                        attr.addFlashAttribute("msg","Login Pending, Wait for Approval!");
                    } else {
                        attr.addFlashAttribute("msg","Login Disabled, Contact Admin!");
                    }
                } else attr.addFlashAttribute("msg","Invalid Password");
            }
            else if("TEACHER".equals(role) && teacherRepo.existsByEmail(email)) {
                Teacher t = teacherRepo.findByEmail(email);
                if(t.getPassword().equals(password)) {
                    if(t.getStatus() == Teacher.Status.APPROVED) {
                        session.setAttribute("loggedInTeacher", t);
                        return "redirect:/Teacher/Dashboard";
                    } else if(t.getStatus() == Teacher.Status.PENDING) {
                        attr.addFlashAttribute("msg","Login Pending, Wait for Approval!");
                    } else {
                        attr.addFlashAttribute("msg","Login Disabled, Contact Admin!");
                    }
                } else attr.addFlashAttribute("msg","Invalid Password");
            }
            else {
                attr.addFlashAttribute("msg","User Not Found!");
            }
            return "redirect:/index";
        } catch(Exception e) {
            attr.addFlashAttribute("msg","Error: "+e.getMessage());
            return "redirect:/index";
        }
    }

   

    @GetMapping("/register")
    public String showRegister() {
    	return "/register";
    }
    
    @PostMapping("/register")
    public String register(
            @RequestParam("role") String role,
            @RequestParam Map<String, String> params,
            RedirectAttributes redirectAttributes) {

        try {
            if ("STUDENT".equalsIgnoreCase(role)) {
                Student student = new Student();
                student.setName(params.get("student_name"));
                student.setRollNo(params.get("rollNo"));
                student.setFatherName(params.get("fatherName"));
                student.setMotherName(params.get("motherName"));
                student.setEmail(params.get("email"));
                student.setPassword(params.get("password"));
                student.setBranch(params.get("branch"));
                student.setYear(params.get("year"));
                student.setGender(params.get("gender"));
                student.setContactNo(params.get("contactNo"));
                student.setAddress(params.get("address"));
                student.setRole(Student.Role.STUDENT);
                student.setStatus(Student.Status.PENDING); // default status
                studentRepo.save(student);

                redirectAttributes.addFlashAttribute("success", "Student registered successfully!, Now wait to Admin Approval");

            } else if ("TEACHER".equalsIgnoreCase(role)) {
                Teacher teacher = new Teacher();
                teacher.setName(params.get("teacher_name"));
                teacher.setEmail(params.get("email"));
                teacher.setPassword(params.get("password"));
                teacher.setDepartment(params.get("department"));
                teacher.setDesignation(params.get("designation"));
                teacher.setQualification(params.get("qualification"));
                teacher.setExperience(params.get("experience") != null ? Integer.parseInt(params.get("experience")) : 0);
                teacher.setGender(params.get("gender"));
                teacher.setEmployeeId("MIT-"+System.currentTimeMillis());
                teacher.setContactNo(params.get("contactNo"));
                teacher.setAddress(params.get("address"));
                teacher.setRole(Teacher.Role.TEACHER);
                teacher.setStatus(Teacher.Status.PENDING); // default status
                teacherRepo.save(teacher);

                redirectAttributes.addFlashAttribute("success", "Teacher Registered Successfully!, Now wait to Admin Approval");
            } else {
                redirectAttributes.addFlashAttribute("msg", "Invalid role selected!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("msg", "Error: " + e.getMessage());
        }

        return "redirect:/register";
    }


    @GetMapping("/student/dashboard")
    public String student(HttpSession s) {
        return "student";
    }

    @GetMapping("/teacher/dashboard")
    public String teacher(HttpSession s) {
        return "teacher";
    }
    
    @GetMapping("/logout")
    public String logout() {
        session.invalidate();
        return "redirect:/index?msg=Logged+out";
    }
    
    
}
