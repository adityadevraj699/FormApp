package com.myproject.FormApp.Model;

import jakarta.persistence.*;

@Entity
@Table
public class FeedbackQuestionCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Foreign Key: Feedback
    @ManyToOne
    @JoinColumn(name = "feedback_id", nullable = false)
    private Feedback feedback;

    // Foreign Key: Question Category
    @ManyToOne
    @JoinColumn(name = "question_category_id", nullable = false)
    private QuestionCatrgories questionCategory;

    // ----- Constructors -----
    public FeedbackQuestionCategory() {}

    public FeedbackQuestionCategory(Feedback feedback, QuestionCatrgories questionCategory) {
        this.feedback = feedback;
        this.questionCategory = questionCategory;
    }
    
    


    

    // ----- Getters & Setters -----
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Feedback getFeedback() { return feedback; }
    public void setFeedback(Feedback feedback) { this.feedback = feedback; }

    public QuestionCatrgories getQuestionCategory() { return questionCategory; }
    public void setQuestionCategory(QuestionCatrgories questionCategory) { this.questionCategory = questionCategory; }
}
