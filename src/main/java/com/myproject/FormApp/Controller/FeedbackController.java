package com.myproject.FormApp.Controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.myproject.FormApp.Model.Feedback;
import com.myproject.FormApp.Model.FeedbackQuestionCategory;
import com.myproject.FormApp.Model.Question;
import com.myproject.FormApp.Repository.FeedBackPhaseRepository;
import com.myproject.FormApp.Repository.FeedbackQuestionCategoryRepository;
import com.myproject.FormApp.Repository.FeedbackRepository;
import com.myproject.FormApp.Repository.ProgramRepository;
import com.myproject.FormApp.Repository.QuestionCatrgoriesRepository;
import com.myproject.FormApp.Repository.QuestionRepository;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin/feedback")
public class FeedbackController {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private FeedbackQuestionCategoryRepository fqcRepo;

    @Autowired
    private QuestionRepository questionRepository; // <-- Add this
    
    @Autowired
    private ProgramRepository programRepo;
    
    @Autowired
    private FeedBackPhaseRepository phaseRepo;
    
    @Autowired
    private QuestionCatrgoriesRepository catRepo;
   

    // पहले वाला Feedback Detail
    @GetMapping("/details/{id}")
    public String getFeedbackDetails(@PathVariable("id") Long id, Model model) {
        Feedback feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Feedback not found with id: " + id));

        model.addAttribute("feedback", feedback);
        model.addAttribute("categories", feedback.getFeedbackQuestionCategories());
        return "admin/feedbackdetail";
    }
    
    
    
   @GetMapping("/edit/{id}")
public String showEditFeedback(@PathVariable("id") Long id,
                               Model model,
                               HttpSession session,
                               RedirectAttributes redirectAttrs) {

    if (session.getAttribute("loggedInAdmin") == null) {
        return "redirect:/"; // home page pe redirect
    }

    Feedback feedback = feedbackRepository.findById(id).orElse(null);
    if (feedback == null) {
        redirectAttrs.addFlashAttribute("serverMessageModule", "Error! Feedback not found.");
        return "redirect:/admin/totalFeedback";
    }

    // Agar feedback start ho gaya ya students fill kar chuke
    if ((feedback.getStartDate() != null && feedback.getStartDate().isBefore(java.time.LocalDate.now()))
            || (feedback.getStudentFeedbackAnswers() != null && !feedback.getStudentFeedbackAnswers().isEmpty())) {

        redirectAttrs.addFlashAttribute("serverMessageModule",
                "Error! Cannot edit. Feedback already started or filled by students.");
        return "redirect:/admin/totalFeedback";
    }

    // Load master data
    model.addAttribute("feedback", feedback);
    model.addAttribute("programs", programRepo.findAll());
    model.addAttribute("phases", phaseRepo.findAll());
    model.addAttribute("categories", catRepo.findAll());

    // Already selected categories ka IDs bhejna
    java.util.Set<Long> selectedCatIds = feedback.getFeedbackQuestionCategories()
            .stream()
            .map(fqc -> fqc.getQuestionCategory().getId())
            .collect(java.util.stream.Collectors.toSet());
    model.addAttribute("selectedCatIds", selectedCatIds);

    return "admin/EditFeedback"; 
}


   @PostMapping("/edit/{id}")
public String updateFeedback(@PathVariable("id") Long id,
                             @RequestParam("program.id") Long programId,
                             @RequestParam("feedbackPhase.id") Long phaseId,
                             @RequestParam(value="categoryIds", required=false) List<Long> categoryIds,
                             @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                             @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                             HttpSession session,
                             RedirectAttributes redirectAttrs) {

    if (session.getAttribute("loggedInAdmin") == null) {
        return "redirect:/";
    }

    Feedback feedback = feedbackRepository.findById(id).orElse(null);
    if (feedback == null) {
        redirectAttrs.addFlashAttribute("serverMessageModule", "Error! Feedback not found.");
        return "redirect:/admin/totalFeedback";
    }

    // Again safety check
    if ((feedback.getStartDate() != null && feedback.getStartDate().isBefore(LocalDate.now()))
            || (feedback.getStudentFeedbackAnswers() != null && !feedback.getStudentFeedbackAnswers().isEmpty())) {
        redirectAttrs.addFlashAttribute("serverMessageModule", "Error! Cannot update. Already started/filled.");
        return "redirect:/admin/totalFeedback";
    }

    // Update Program & Phase
    feedback.setProgram(programRepo.findById(programId).orElse(null));
    feedback.setFeedbackPhase(phaseRepo.findById(phaseId).orElse(null));
    feedback.setStartDate(startDate);
    feedback.setEndDate(endDate);

    // Reset categories
    fqcRepo.deleteAll(feedback.getFeedbackQuestionCategories());
    feedback.getFeedbackQuestionCategories().clear();

    if (categoryIds != null) {
        for (Long catId : categoryIds) {
            FeedbackQuestionCategory fqc = new FeedbackQuestionCategory();
            fqc.setFeedback(feedback);
            fqc.setQuestionCategory(catRepo.findById(catId).orElse(null));
            feedback.getFeedbackQuestionCategories().add(fqc);
        }
    }

    feedbackRepository.save(feedback);

    redirectAttrs.addFlashAttribute("serverMessageModule", "Feedback updated successfully!");
    return "redirect:/admin/totalFeedback";
}

 
}
