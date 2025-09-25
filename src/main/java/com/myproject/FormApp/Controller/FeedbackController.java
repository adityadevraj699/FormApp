package com.myproject.FormApp.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.myproject.FormApp.Model.Feedback;
import com.myproject.FormApp.Model.FeedbackQuestionCategory;
import com.myproject.FormApp.Model.Question;
import com.myproject.FormApp.Repository.FeedbackQuestionCategoryRepository;
import com.myproject.FormApp.Repository.FeedbackRepository;
import com.myproject.FormApp.Repository.QuestionRepository;

@Controller
@RequestMapping("/admin/feedback")
public class FeedbackController {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private FeedbackQuestionCategoryRepository fqcRepo;

    @Autowired
    private QuestionRepository questionRepository; // <-- Add this

    // पहले वाला Feedback Detail
    @GetMapping("/details/{id}")
    public String getFeedbackDetails(@PathVariable("id") Long id, Model model) {
        Feedback feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Feedback not found with id: " + id));

        model.addAttribute("feedback", feedback);
        model.addAttribute("categories", feedback.getFeedbackQuestionCategories());
        return "admin/feedbackdetail";
    }

 
}
