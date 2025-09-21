package com.myproject.FormApp.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
                        return "redirect:/student/dashboard";
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
                        return "redirect:/teacher/dashboard";
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

   


    @GetMapping("/student/dashboard")
    public String student(HttpSession s) {
        return "student";
    }

    @GetMapping("/teacher/dashboard")
    public String teacher(HttpSession s) {
        return "teacher";
    }
}
