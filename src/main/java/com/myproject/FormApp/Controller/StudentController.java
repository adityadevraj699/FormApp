package com.myproject.FormApp.Controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.myproject.FormApp.Model.CurriculumTopic;
import com.myproject.FormApp.Model.EnrolledProgram;
import com.myproject.FormApp.Model.Feedback;
import com.myproject.FormApp.Model.Module;
import com.myproject.FormApp.Model.Program;
import com.myproject.FormApp.Model.Question;
import com.myproject.FormApp.Model.Student;
import com.myproject.FormApp.Model.StudentFeedbackAnswer;
import com.myproject.FormApp.Model.TeacherAssign;
import com.myproject.FormApp.Repository.CurriculumTopicRepository;
import com.myproject.FormApp.Repository.EnrolledProgramRepository;
import com.myproject.FormApp.Repository.FeedBackPhaseRepository;
import com.myproject.FormApp.Repository.FeedbackQuestionCategoryRepository;
import com.myproject.FormApp.Repository.FeedbackRepository;
import com.myproject.FormApp.Repository.ModuleRepository;
import com.myproject.FormApp.Repository.ProgramRepository;
import com.myproject.FormApp.Repository.QuestionCatrgoriesRepository;
import com.myproject.FormApp.Repository.QuestionRepository;
import com.myproject.FormApp.Repository.StudentFeedbackAnswerRepository;
import com.myproject.FormApp.Repository.TeacherAssignRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/Student")
public class StudentController {

	
	@Autowired
	private HttpSession session;
	
	@Autowired
	private TeacherAssignRepository teacherAssignRepo;
	
	@Autowired
    private ProgramRepository programRepo;

    @Autowired
    private ModuleRepository moduleRepo;

    @Autowired
    private CurriculumTopicRepository curriculumTopicRepo;
    
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
    
    @Autowired
    private EnrolledProgramRepository enrolledProgramRepo;
    
    @Autowired
    private StudentFeedbackAnswerRepository studentFeedbackAnswerRepo;
    
    
    @PersistenceContext
    private EntityManager entityManager;
	
	@GetMapping("/Dashboard")
	public String showDashboard() {
		if(session.getAttribute("loggedInStudent") == null) {
			return "redirect:/";
		}
		return "Student/Dashboard";
	}
	
	@GetMapping("/TotalProgram")
	public String showTotalProgram(Model model) {
		if(session.getAttribute("loggedInStudent") == null) {
			return "redirect:/";
		}
		
		List<TeacherAssign> programList = teacherAssignRepo.findAll();

        // Model में डालो
        model.addAttribute("programList", programList);
		return "Student/TotalProgram";
	}
	
	
	@GetMapping("/programDetail/{id}")
	public String programDetail(@PathVariable Long id, Model model) {
	    Student student = (Student) session.getAttribute("loggedInStudent");
	    if(student == null) return "redirect:/";

	    Program program = programRepo.findById(id)
	                         .orElseThrow(() -> new RuntimeException("Program not found"));

	    List<Module> modules = moduleRepo.findByProgramId(id);
	    Map<Long, List<CurriculumTopic>> moduleTopicsMap = new HashMap<>();
	    for(Module module : modules) {
	        moduleTopicsMap.put(module.getId(), curriculumTopicRepo.findByModuleId(module.getId()));
	    }

	    List<TeacherAssign> teacherAssignments = teacherAssignRepo.findAllByProgramId(id);

	    // Check if student already enrolled
	    boolean isEnrolled = enrolledProgramRepo.existsByStudentIdAndProgramId(student.getId(), program.getId());

	    model.addAttribute("program", program);
	    model.addAttribute("modules", modules);
	    model.addAttribute("teacherAssignments", teacherAssignments);
	    model.addAttribute("moduleTopicsMap", moduleTopicsMap);
	    model.addAttribute("isEnrolled", isEnrolled);

	    return "Student/programDetail";
	}

	@PostMapping("/toggleEnroll")
	public String toggleEnroll(@RequestParam Long programId, RedirectAttributes redirectAttributes) {
	    Student student = (Student) session.getAttribute("loggedInStudent");
	    if(student == null) return "redirect:/";

	    Program program = programRepo.findById(programId)
	                         .orElseThrow(() -> new RuntimeException("Program not found"));

	    List<EnrolledProgram> enrolled = enrolledProgramRepo.findByStudentIdAndProgramId(student.getId(), programId);

	    if(!enrolled.isEmpty()) {
	        // Allow unenroll anytime
	        enrolledProgramRepo.delete(enrolled.get(0));
	        redirectAttributes.addFlashAttribute("serverMessage", 
	            "You have successfully unenrolled from " + program.getTrainingProgram());
	    } else {
	        // Only allow new enrollment if program hasn't started
	        LocalDate today = LocalDate.now();
	        if(today.isAfter(program.getStartDate())) {
	            redirectAttributes.addFlashAttribute("serverMessage", 
	                "Cannot enroll. The program has already started.");
	        } else {
	            EnrolledProgram ep = new EnrolledProgram();
	            ep.setStudent(student);
	            ep.setProgram(program);
	            ep.setRegDate(LocalDateTime.now());
	            enrolledProgramRepo.save(ep);
	            redirectAttributes.addFlashAttribute("serverMessage", 
	                "You have successfully enrolled in " + program.getTrainingProgram());
	        }
	    }

	    return "redirect:/Student/programDetail/" + programId;
	}






	
	
	@GetMapping("/EnrolledProgram")
	public String showEnrolledProgram(Model model) {
	    Student student = (Student) session.getAttribute("loggedInStudent");
	    if(student == null) return "redirect:/";

	    // Fetch all programs this student is enrolled in
	    List<EnrolledProgram> enrolledPrograms = enrolledProgramRepo.findByStudentId(student.getId());

	    model.addAttribute("enrolledPrograms", enrolledPrograms);
	    return "Student/EnrolledProgram";
	}

	
	// Feedback list page
    @GetMapping("/Feedback")
    public String showFeedback(Model model) {
        Student loggedInStudent = (Student) session.getAttribute("loggedInStudent");
        if (loggedInStudent == null) {
            return "redirect:/"; // अगर login नहीं किया
        }

        // Student के enrolled programs लाओ
        List<EnrolledProgram> enrolledPrograms =
        		enrolledProgramRepo.findByStudentId(loggedInStudent.getId());

        // Program list निकालो
        List<Program> programs = enrolledPrograms.stream()
                .map(EnrolledProgram::getProgram)
                .toList();

        // Feedbacks सिर्फ उन्हीं programs के लाओ
        List<Feedback> feedbacks = programs.isEmpty()
                ? List.of()
                : feedRepo.findByProgramIn(programs);

        model.addAttribute("feedbacks", feedbacks);
        return "Student/Feedback";
    }
    
    
    @GetMapping("/feedbackDetail/{id}")
    public String showFeedbackDetail(@PathVariable Long id, Model model, HttpSession session, 
                                     RedirectAttributes redirectAttrs) {
        // 1️⃣ Get logged-in student from session
        Student student = (Student) session.getAttribute("loggedInStudent");
        if (student == null) {
            return "redirect:/";  // Not logged in
        }

        // 2️⃣ Get feedback by ID
        Feedback feedback = feedRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Feedback not found"));

        LocalDate today = LocalDate.now();

        // 3️⃣ Check if feedback is not yet started
        if (today.isBefore(feedback.getStartDate())) {
            redirectAttrs.addFlashAttribute(
                "serverMessage", 
                "Feedback coming soon! Starts on: " + feedback.getStartDate()
            );
            return "redirect:/Student/Feedback";
        }

        // 4️⃣ Check if feedback has ended
        if (today.isAfter(feedback.getEndDate())) {
            redirectAttrs.addFlashAttribute(
                "serverMessage", 
                "Feedback expired! Ended on: " + feedback.getEndDate()
            );
            return "redirect:/Student/Feedback";
        }

        // 5️⃣ Check if student has already submitted
        boolean alreadySubmitted = studentFeedbackAnswerRepo.existsByStudentIdAndFeedbackId(
            student.getId(), feedback.getId()
        );

        if (alreadySubmitted) {
            redirectAttrs.addFlashAttribute(
                "serverMessage", 
                "You have already submitted this feedback!"
            );
            return "redirect:/Student/Feedback";  // redirect to feedback list page
        }

        // 6️⃣ If all checks pass, show feedback form
        model.addAttribute("feedback", feedback);
        return "Student/feedbackDetail";
    }




    // POST - Save answers
    @PostMapping("/feedbackDetail/{id}")
    public String saveFeedbackAnswers(@PathVariable Long id,
                                      @RequestParam Map<String, String> answers,
                                      HttpSession session, RedirectAttributes redirectAttrs) {
        Student student = (Student) session.getAttribute("loggedInStudent");
        if (student == null) {
            return "redirect:/";
        }

        Feedback feedback = feedRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Feedback not found"));

        answers.forEach((key, value) -> {
            if (key.startsWith("q_")) {
                Long questionId = Long.parseLong(key.substring(2));
                Question question = questionRepo.findById(questionId)
                        .orElseThrow(() -> new RuntimeException("Question not found"));

                StudentFeedbackAnswer sfa = new StudentFeedbackAnswer(student, feedback, question, value);
                studentFeedbackAnswerRepo.save(sfa);
            }
        });

        redirectAttrs.addFlashAttribute("serverMessage", "You have Successfully submitted this feedback!");
        return "redirect:/Student/Feedback";
    }



    
	@GetMapping("/HistoryFeedback")
	public String showHistoryFeedback() {
		if(session.getAttribute("loggedInStudent") == null) {
			return "redirect:/";
		}
		return "Student/HistoryFeedback";
	}
	
	
	
}
