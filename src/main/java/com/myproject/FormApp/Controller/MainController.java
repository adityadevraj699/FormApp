package com.myproject.FormApp.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.myproject.FormApp.Model.Admin;
import com.myproject.FormApp.Model.Admin.Role;
import com.myproject.FormApp.Model.Student;
import com.myproject.FormApp.Model.Student.Status;
import com.myproject.FormApp.Model.Teacher;
import com.myproject.FormApp.Repository.AdminRepository;
import com.myproject.FormApp.Repository.ProgramRepository;
import com.myproject.FormApp.Repository.StudentsRepository;
import com.myproject.FormApp.Repository.TeacherRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class MainController {
	
	@Autowired
	private AdminRepository adminRepo;
	@Autowired
	private ProgramRepository programRepo;
	@Autowired
	private StudentsRepository studentRepo;
	
	@Autowired
	private TeacherRepository teacherRepo;

    @GetMapping({"/", "/index"})
    public String showIndex() {
        return "index";
    }

    @PostMapping("/login")
    public String Login(RedirectAttributes attributes, HttpServletRequest request, HttpSession session) {
    	try {
    		String email = request.getParameter("email");
    		String password = request.getParameter("password");
    		String role = request.getParameter("role");
    		if(role.equals("ADMIN") && adminRepo.existsByEmail(email)) {
    			
    			Admin admin = adminRepo.findByEmail(email);
    			if(password.equals(admin.getPassword()) && admin.getRole().equals(Admin.Role.ADMIN)) {
    				session.setAttribute("loggedInAdmin", admin);
    				return "redirect:/admin";
    			}
    			else {
    				attributes.addFlashAttribute("msg", "Invalid Password");
    			}
    		}
    		else if(role.equals("STUDENT") && studentRepo.existsByEmail(email)) {
    			Student student = studentRepo.findByEmail(email);
    			if(password.equals(student.getPassword())) {
    				if(student.getStatus().equals(Student.Status.APPROVED)) {
    					session.setAttribute("loggedInStudent", student);
    					return "redirect:/student";
    				}
    				else if(student.getStatus().equals(Student.Status.PENDING)) {
    					attributes.addFlashAttribute("msg", "Login Pending, Please Wait To Admin Approval !!");
    				}
    				else {
    					attributes.addFlashAttribute("msg", "Login Disabled, Please Contact Admin !!!");
    				}
    				
    			}
    			else {
    				attributes.addFlashAttribute("msg", "Invalid Password");
    			}
    			
    		}
    		else if(role.equals("TEACHER") && teacherRepo.existsByEmail(email)) {
    			Teacher teacher= teacherRepo.findByEmail(email);
    			if(password.equals(teacher.getPassword())) {
    				if(teacher.getStatus().equals(Teacher.Status.APPROVED)) {
    					session.setAttribute("LoggedInTeacher", teacher);
    				}
    				else if(teacher.getStatus().equals(Teacher.Status.PENDING)) {
    					attributes.addFlashAttribute("msg", "Login Pending, Please Wait To Admin Approval !!");
    				}
    				else {
    					attributes.addFlashAttribute("msg", "Login Disabled, Please Contact Admin !!!");
    				}
    			}
    			
    		}
    		else {
    			attributes.addFlashAttribute("msg", "User not Found!!");
    		}
    		
    		//System.out.println(email +" "+password +" "+ role);
			return "redirect:/index";
		} catch (Exception e) {
			attributes.addFlashAttribute("msg",e.getMessage());
			return "redirect:/index";
		}
    }
    
    
    
  @PostMapping("/register")
public String Registration(RedirectAttributes attributes, HttpServletRequest request) {
    try {
        String role = request.getParameter("role");
        System.out.println("Role: " + role);

        if (role.equals("STUDENT")) {
            String name = request.getParameter("student_name");
            String rollNo = request.getParameter("rollNo");
            String email = request.getParameter("email");

            if (studentRepo.existsByEmail(email)) {
                attributes.addFlashAttribute("msg", "Email already registered!");
                return "redirect:/register";
            }
            if (studentRepo.existsByRollNo(rollNo)) {
                attributes.addFlashAttribute("msg", "Roll Number already registered!");
                return "redirect:/register";
            }

            Student student = new Student();
            student.setRole(Student.Role.STUDENT);
            student.setStatus(Student.Status.PENDING);
            student.setName(name);
            student.setRollNo(rollNo);
            student.setFatherName(request.getParameter("fatherName"));
            student.setMotherName(request.getParameter("motherName"));
            student.setEmail(email);
            student.setPassword(request.getParameter("password"));
            student.setBranch(request.getParameter("branch"));
            student.setYear(request.getParameter("year"));
            student.setGender(request.getParameter("gender"));
            student.setContactNo(request.getParameter("contactNo"));
            student.setAddress(request.getParameter("address"));
            studentRepo.save(student);

            attributes.addFlashAttribute("success", "Student Registered Successfully! Wait for Admin approval.");
        } 
        else if (role.equals("TEACHER")) {
    String name = request.getParameter("teacher_name");
    String email = request.getParameter("email");

    if (teacherRepo.existsByEmail(email)) {
        attributes.addFlashAttribute("msg", "Email already registered!");
        return "redirect:/register";
    }

    // Generate unique Employee ID
    String employeeId;
    do {
        long randomId = System.currentTimeMillis() % 100000; // Simple unique number
        employeeId = "MIT-" + randomId;
    } while (teacherRepo.existsByEmployeeId(employeeId));

    Teacher teacher = new Teacher();
    teacher.setRole(Teacher.Role.TEACHER);
    teacher.setStatus(Teacher.Status.PENDING);
    teacher.setName(name);
    teacher.setEmployeeId(employeeId); // Auto-generated ID
    teacher.setEmail(email);
    teacher.setPassword(request.getParameter("password"));
    teacher.setDepartment(request.getParameter("department"));
    teacher.setDesignation(request.getParameter("designation"));
    teacher.setQualification(request.getParameter("qualification"));
    teacher.setExperience(request.getParameter("experience") != null && !request.getParameter("experience").isEmpty() 
        ? Integer.parseInt(request.getParameter("experience")) 
        : 0);
    teacher.setGender(request.getParameter("gender"));
    teacher.setContactNo(request.getParameter("contactNo"));
    teacher.setAddress(request.getParameter("address"));

    teacherRepo.save(teacher);
    attributes.addFlashAttribute("success", "Teacher Registered Successfully! Employee ID: " + employeeId);
}

        else {
            attributes.addFlashAttribute("msg", "Please Select Correct Role!!");
        }

    } catch (Exception e) {
        e.printStackTrace();
        attributes.addFlashAttribute("msg", "Something Went Wrong!!");
    }

    return "redirect:/register";
}

   
    
    
    
    @GetMapping("/admin")
    public String showAdmin() {
        return "admin";
    }

    @GetMapping("/student")
    public String showStudent() {
        return "student";
    }
    
    @GetMapping("/register")
    public String showRegister() {
    	return "register";
    }
}
