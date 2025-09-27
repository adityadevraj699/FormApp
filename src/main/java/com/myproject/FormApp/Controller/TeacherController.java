package com.myproject.FormApp.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.myproject.FormApp.Model.CurriculumTopic;
import com.myproject.FormApp.Model.Program;
import com.myproject.FormApp.Model.Module;
import com.myproject.FormApp.Model.Teacher;
import com.myproject.FormApp.Model.TeacherAssign;
import com.myproject.FormApp.Repository.CurriculumTopicRepository;
import com.myproject.FormApp.Repository.EnrolledProgramRepository;
import com.myproject.FormApp.Repository.ModuleRepository;
import com.myproject.FormApp.Repository.ProgramRepository;
import com.myproject.FormApp.Repository.TeacherAssignRepository;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/Teacher")
public class TeacherController {

	
	@Autowired
	private ProgramRepository programRepo;
	
	@Autowired
	private ModuleRepository moduleRepo;
	
	 @Autowired
	    private TeacherAssignRepository teacherAssignRepository;
	 
	 @Autowired
	 private CurriculumTopicRepository curriculumTopicRepo;
	 
	 @Autowired
	 private TeacherAssignRepository teacherAssignRepo;
	 
	 @Autowired
	 private EnrolledProgramRepository enrolledProgramRepo;

	    @Autowired
	    private HttpSession session;
	
	@GetMapping("/Dashboard")
	public String showDashboard() {
		if(session.getAttribute("loggedInTeacher") == null) {
			return "redirect:/";
		}
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
	public String showFeedback() {
		if(session.getAttribute("loggedInTeacher") == null) {
			return "redirect:/";
		}
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

	    // Model attributes
	    model.addAttribute("program", program);
	    model.addAttribute("modules", modules);
	    model.addAttribute("teacherAssignments", teacherAssignments);
	    model.addAttribute("moduleTopicsMap", moduleTopicsMap);
	    model.addAttribute("enrolledCount", enrolledCount);

	    return "Teacher/programDetail";
	}
	
	
}
