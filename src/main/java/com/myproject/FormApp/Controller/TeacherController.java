package com.myproject.FormApp.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/Teacher")
public class TeacherController {

	
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
	public String showAssignProgram() {
		if(session.getAttribute("loggedInTeacher") == null) {
			return "redirect:/";
		}
		return "Teacher/AssignProgram";
	}
	
	
	@GetMapping("/Feedback")
	public String showFeedback() {
		if(session.getAttribute("loggedInTeacher") == null) {
			return "redirect:/";
		}
		return "Teacher/Feedback";
	}
	
	
}
