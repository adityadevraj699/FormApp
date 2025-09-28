package com.myproject.FormApp.Controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.myproject.FormApp.Model.Program;
import com.myproject.FormApp.Model.Student;
import com.myproject.FormApp.Model.Module;
import com.myproject.FormApp.Model.Teacher;
import com.myproject.FormApp.Model.TeacherAssign;
import com.myproject.FormApp.Repository.CurriculumTopicRepository;
import com.myproject.FormApp.Repository.EnrolledProgramRepository;
import com.myproject.FormApp.Repository.FeedbackRepository;
import com.myproject.FormApp.Repository.ModuleRepository;
import com.myproject.FormApp.Repository.ProgramRepository;
import com.myproject.FormApp.Repository.TeacherAssignRepository;
import com.myproject.FormApp.Repository.TeacherRepository;

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
	                                   .filter(f -> f.getEndDate().isAfter(LocalDate.now()) && f.getStartDate().isBefore(LocalDate.now()))
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
	public String programDetail(@PathVariable Long id, Model model) {

	    Teacher teacher = (Teacher) session.getAttribute("loggedInTeacher");
	    if (teacher == null) return "redirect:/";

	    // Program fetch
	    Program program = programRepo.findById(id)
	            .orElseThrow(() -> new RuntimeException("Program not found"));

	    // Modules & topics
	    List<Module> modules = moduleRepo.findByProgramId(id);
	    Map<Long, List<CurriculumTopic>> moduleTopicsMap = new HashMap<>();
	    for (Module module : modules) {
	        moduleTopicsMap.put(module.getId(), curriculumTopicRepo.findByModuleId(module.getId()));
	    }

	    // Assigned Teachers
	    List<TeacherAssign> teacherAssignments = teacherAssignRepo.findAllByProgramId(id);

	    // Student count
	    long enrolledCount = enrolledProgramRepo.countByProgramId(id);

	    // Fetch enrolled students
	    List<EnrolledProgram> enrolledList = enrolledProgramRepo.findByProgramId(id);
	    List<Student> enrolledStudents = enrolledList.stream()
	                                                 .map(EnrolledProgram::getStudent)
	                                                 .toList();

	    // Model attributes
	    model.addAttribute("program", program);
	    model.addAttribute("modules", modules);
	    model.addAttribute("teacherAssignments", teacherAssignments);
	    model.addAttribute("moduleTopicsMap", moduleTopicsMap);
	    model.addAttribute("enrolledCount", enrolledCount);
	    model.addAttribute("enrolledStudents", enrolledStudents);

	    return "Teacher/programDetail";
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

	
	
}
