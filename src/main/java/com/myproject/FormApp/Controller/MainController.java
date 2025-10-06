package com.myproject.FormApp.Controller;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

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
import com.myproject.FormApp.Service.EmailService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class MainController {

    @Autowired private AdminRepository adminRepo;
    @Autowired private ProgramRepository programRepo;
    @Autowired private StudentsRepository studentRepo;
    @Autowired private TeacherRepository teacherRepo;
    @Autowired private HttpSession session;
    @Autowired
    private ExecutorService virtualExecutor;
    
    @Autowired
    private EmailService emailService;

    @GetMapping({"/","/index"})
    public Callable<String> index(HttpSession session) {
        return () -> {
            // Check if any user is already logged in
            if (session.getAttribute("loggedInAdmin") != null) {
            	return "redirect:/admin/dashboard";
            } else if (session.getAttribute("loggedInStudent") != null) {
            	return "redirect:/Student/Dashboard";
            } else if (session.getAttribute("loggedInTeacher") != null) {
            	return "redirect:/Teacher/Dashboard";
            }

            // Otherwise, show index page
            return "index";
        };
    }



@PostMapping("/login")
public Callable<String> loginAsync(HttpServletRequest request, HttpSession session, RedirectAttributes attr) {
    return () -> {
        try {
            String email = request.getParameter("email");
            String password = request.getParameter("password");
            String role = request.getParameter("role");

            // ✅ Virtual thread me DB call execute
            return virtualExecutor.submit(() -> {
                if ("ADMIN".equals(role) && adminRepo.existsByEmail(email)) {
                    Admin admin = adminRepo.findByEmail(email);
                    if (admin.getPassword().equals(password)) {
                        session.setAttribute("loggedInAdmin", admin);
                        return "redirect:/admin/dashboard";
                    } else attr.addFlashAttribute("msg", "Invalid Password");
                } 
                else if ("STUDENT".equals(role) && (studentRepo.existsByEmail(email) || studentRepo.existsByRollNo(email))) {
                    Student st = studentRepo.findByEmail(email);
                    if(st == null) {
                    	st = studentRepo.findByRollNo(email);
                    }
                    if (st.getPassword().equals(password)) {
                        if (st.getStatus() == Student.Status.APPROVED) {
                            session.setAttribute("loggedInStudent", st);
                            return "redirect:/Student/Dashboard";
                        } else if (st.getStatus() == Student.Status.PENDING) {
                            attr.addFlashAttribute("msg", "Login Pending, Wait for Approval!");
                        } else {
                            attr.addFlashAttribute("msg", "Login Disabled, Contact Admin!");
                        }
                    } else attr.addFlashAttribute("msg", "Invalid Password");
                } 
                else if ("TEACHER".equals(role) && (teacherRepo.existsByEmail(email) || teacherRepo.existsByEmployeeId(email))) {
                    Teacher t = teacherRepo.findByEmail(email);
                    if(t== null) {
                    	t = teacherRepo.findByEmployeeId(email);
                    }
                    if (t.getPassword().equals(password)) {
                        if (t.getStatus() == Teacher.Status.APPROVED) {
                            session.setAttribute("loggedInTeacher", t);
                            return "redirect:/Teacher/Dashboard";
                        } else if (t.getStatus() == Teacher.Status.PENDING) {
                            attr.addFlashAttribute("msg", "Login Pending, Wait for Approval!");
                        } else {
                            attr.addFlashAttribute("msg", "Login Disabled, Contact Admin!");
                        }
                    } else attr.addFlashAttribute("msg", "Invalid Password");
                } 
                else {
                    attr.addFlashAttribute("msg", "User Not Found!");
                }
                return "redirect:/index";
            }).get(); // .get() = wait for result and return String
        } catch (Exception e) {
            attr.addFlashAttribute("msg", "Error: " + e.getMessage());
            return "redirect:/index";
        }
    };
}


    @GetMapping("/register")
    public Callable<String> showRegister(HttpSession session) {
        return () -> {
            // Optional: redirect if already logged in
            if (session.getAttribute("loggedInAdmin") != null) {
                return "redirect:/admin/dashboard";
            } else if (session.getAttribute("loggedInStudent") != null) {
                return "redirect:/student/dashboard";
            } else if (session.getAttribute("loggedInTeacher") != null) {
                return "redirect:/teacher/dashboard";
            }
            return "/register";
        };
    }

    


@PostMapping("/register")
public Callable<String> register(
        @RequestParam("role") String role,
        @RequestParam Map<String, String> params,
        RedirectAttributes redirectAttributes) {

    return () -> {
        try {
            return virtualExecutor.submit(() -> { // ✅ virtual thread start
                if ("STUDENT".equalsIgnoreCase(role)) {
                    String email = params.get("email");
                    if (studentRepo.existsByEmail(email)) {
                        redirectAttributes.addFlashAttribute("msg", "Email Already Exists");
                        return "redirect:/register";
                    } else if (!email.contains("@") || !email.split("@")[1].equalsIgnoreCase("mitmeerut.ac.in")) {
                        redirectAttributes.addFlashAttribute("msg", "Please use college or official email");
                        return "redirect:/register";
                    }

                    Student student = new Student();
                    student.setName(params.get("student_name"));
                    student.setRollNo(params.get("rollNo"));
                    student.setFatherName(params.get("fatherName"));
                    student.setMotherName(params.get("motherName"));
                    student.setEmail(email);
                    student.setPassword(params.get("password"));
                    student.setBranch(params.get("branch"));
                    student.setYear(params.get("year"));
                    student.setGender(params.get("gender"));
                    student.setContactNo(params.get("contactNo"));
                    student.setAddress(params.get("address"));
                    student.setCourse(params.get("course"));
                    student.setSemester(params.get("semester"));
                    student.setSection(params.get("section") != null ? params.get("section").toUpperCase() : "");
                    student.setRole(Student.Role.STUDENT);
                    student.setStatus(Student.Status.PENDING);
                    studentRepo.save(student);

                    redirectAttributes.addFlashAttribute("success",
                            "Student registered successfully!, Now wait for Admin Approval");
                }
                else if ("TEACHER".equalsIgnoreCase(role)) {
                    String email = params.get("email");
                    if (teacherRepo.existsByEmail(email)) {
                        redirectAttributes.addFlashAttribute("msg", "Error: Email Already Exists");
                        return "redirect:/register";
                    } else if (!email.contains("@") || !email.split("@")[1].equalsIgnoreCase("mitmeerut.ac.in")) {
                        redirectAttributes.addFlashAttribute("msg", "Error: Please use college or official email");
                        return "redirect:/register";
                    }

                    Teacher teacher = new Teacher();
                    teacher.setName(params.get("teacher_name"));
                    teacher.setEmail(email);
                    teacher.setPassword(params.get("password"));
                    teacher.setDepartment(params.get("department"));
                    teacher.setDesignation(params.get("designation"));
                    teacher.setQualification(params.get("qualification"));
                    teacher.setExperience(params.get("experience") != null ?
                            Integer.parseInt(params.get("experience")) : 0);
                    teacher.setGender(params.get("gender"));
                    teacher.setEmployeeId("MIT-" + System.currentTimeMillis());
                    teacher.setContactNo(params.get("contactNo"));
                    teacher.setAddress(params.get("address"));
                    teacher.setRole(Teacher.Role.TEACHER);
                    teacher.setStatus(Teacher.Status.PENDING);
                    teacherRepo.save(teacher);

                    redirectAttributes.addFlashAttribute("success",
                            "Teacher Registered Successfully!, Now wait for Admin Approval");
                } else {
                    redirectAttributes.addFlashAttribute("msg", "Invalid role selected!");
                }

                return "redirect:/register";
            }).get(); // wait for virtual thread result
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("msg", "Error: " + e.getMessage());
            return "redirect:/register";
        }
    };
}


    @GetMapping("/student/dashboard")
    public Callable<String> studentDashboard(HttpSession session) {
        return () -> {
            if (session.getAttribute("loggedInStudent") != null) {
                return "student";
            }
            return "redirect:/index";
        };
    }

    @GetMapping("/teacher/dashboard")
    public Callable<String> teacherDashboard(HttpSession session) {
        return () -> {
            if (session.getAttribute("loggedInTeacher") != null) {
                return "teacher";
            }
            return "redirect:/index";
        };
    }

    
    @GetMapping("/logout")
    public Callable<String> logout(HttpSession session) {
        return () -> {
            session.invalidate();
            return "redirect:/index?msg=Logged+out";
        };
    }
    
    
 

@GetMapping("/forgot-password")
public String showForgotPassword(HttpSession session) {
    // Clear any previous OTP/session data
    session.removeAttribute("otp");
    session.removeAttribute("otpEmail");
    session.removeAttribute("otpRole");
    session.removeAttribute("otpVerified");
    return "/forgot-password";
}

@PostMapping("/forgot-password/request")
public String sendOtp(@RequestParam String role,
                      @RequestParam String email,
                      RedirectAttributes redirectAttributes,
                      HttpSession session) {

    String name = "";
    if ("STUDENT".equalsIgnoreCase(role)) {
        if (!studentRepo.existsByEmail(email)) {
            redirectAttributes.addFlashAttribute("msg", "Email not found for Student");
            return "redirect:/forgot-password";
        }
        name = studentRepo.findByEmail(email).getName();
    } else if ("TEACHER".equalsIgnoreCase(role)) {
        if (!teacherRepo.existsByEmail(email)) {
            redirectAttributes.addFlashAttribute("msg", "Email not found for Teacher");
            return "redirect:/forgot-password";
        }
        name = teacherRepo.findByEmail(email).getName();
    }

    int otp = (int)(Math.random() * 900000) + 100000;
    session.setAttribute("otp", otp);
    session.setAttribute("otpEmail", email);
    session.setAttribute("otpRole", role);

    // Send OTP email
    emailService.sendOtpEmail(email, name, otp);

    redirectAttributes.addFlashAttribute("msg", "OTP sent to your email!");
    return "redirect:/forgot-password-verify";
}

@GetMapping("/forgot-password-verify")
public String showOtpVerify(HttpSession session, RedirectAttributes redirectAttributes) {
    if (session.getAttribute("otp") == null) {
        return "redirect:/forgot-password";
    }
    return "/forgot-password-verify";
}

@PostMapping("/forgot-password/verify")
public String verifyOtp(@RequestParam int otp,
                        RedirectAttributes redirectAttributes,
                        HttpSession session) {

    Integer sessionOtp = (Integer) session.getAttribute("otp");
    if (sessionOtp == null || sessionOtp != otp) {
        redirectAttributes.addFlashAttribute("msg", "Invalid OTP");
        return "redirect:/forgot-password-verify";
    }

    // OTP verified flag
    session.setAttribute("otpVerified", true);
    redirectAttributes.addFlashAttribute("msg", "OTP Verified! Set new password.");
    return "redirect:/forgot-password-reset";
}

@GetMapping("/forgot-password-reset")
public String showResetForm(HttpSession session, RedirectAttributes redirectAttributes) {
    Boolean otpVerified = (Boolean) session.getAttribute("otpVerified");
    if (otpVerified == null || !otpVerified) {
        return "redirect:/forgot-password";
    }
    return "/forgot-password-reset";
}

@PostMapping("/forgot-password/reset")
public String resetPassword(@RequestParam String newPassword,
                            @RequestParam String confirmPassword,
                            RedirectAttributes redirectAttributes,
                            HttpSession session) {

    Boolean otpVerified = (Boolean) session.getAttribute("otpVerified");
    if (otpVerified == null || !otpVerified) {
        redirectAttributes.addFlashAttribute("msg", "Unauthorized access!");
        return "redirect:/forgot-password";
    }

    String email = (String) session.getAttribute("otpEmail");
    String role = (String) session.getAttribute("otpRole");

    if (!newPassword.equals(confirmPassword)) {
        redirectAttributes.addFlashAttribute("msg", "Passwords do not match!");
        return "redirect:/forgot-password-reset";
    }

    if ("STUDENT".equalsIgnoreCase(role)) {
        Student st = studentRepo.findByEmail(email);
        if (st.getPassword().equals(newPassword)) {
            redirectAttributes.addFlashAttribute("msg", "New password cannot be same as old password");
            return "redirect:/forgot-password-reset";
        }
        st.setPassword(newPassword);
        studentRepo.save(st);
    } else if ("TEACHER".equalsIgnoreCase(role)) {
        Teacher t = teacherRepo.findByEmail(email);
        if (t.getPassword().equals(newPassword)) {
            redirectAttributes.addFlashAttribute("msg", "New password cannot be same as old password");
            return "redirect:/forgot-password-reset";
        }
        t.setPassword(newPassword);
        teacherRepo.save(t);
    }

    // Clear OTP/session flags
    session.removeAttribute("otp");
    session.removeAttribute("otpEmail");
    session.removeAttribute("otpRole");
    session.removeAttribute("otpVerified");

    redirectAttributes.addFlashAttribute("msg", "Password reset successfully!");
    return "redirect:/index";
}


    
    
}
