package com.myproject.FormApp.Model;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Foreign Key: Program
    @ManyToOne
    @JoinColumn(name = "program_id", nullable = false)
    private Program program;

    // Foreign Key: FeedbackPhase
    @ManyToOne
    @JoinColumn(name = "feedbackphase_id", nullable = false)
    private FeedBackPhase feedbackPhase;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    // Optional: mapped categories
    @OneToMany(mappedBy = "feedback", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FeedbackQuestionCategory> feedbackQuestionCategories;

    // ----- Constructors -----
    public Feedback() {}
    


    // ----- Getters & Setters -----
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Program getProgram() { return program; }
    public void setProgram(Program program) { this.program = program; }

    public FeedBackPhase getFeedbackPhase() { return feedbackPhase; }
    public void setFeedbackPhase(FeedBackPhase feedbackPhase) { this.feedbackPhase = feedbackPhase; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public List<FeedbackQuestionCategory> getFeedbackQuestionCategories() { return feedbackQuestionCategories; }
    public void setFeedbackQuestionCategories(List<FeedbackQuestionCategory> feedbackQuestionCategories) {
        this.feedbackQuestionCategories = feedbackQuestionCategories;
    }
}
