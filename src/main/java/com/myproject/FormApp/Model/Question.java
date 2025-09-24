package com.myproject.FormApp.Model;

import jakarta.persistence.*;

@Entity
@Table
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    private QuestionCatrgories category;  // foreign key to QuestionCatrgories

    @Column(nullable = false, length = 500)
    private String questionText;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AnswerType answerType;

    // Enum for answer type
    public enum AnswerType {
        NUMBER, TEXT
    }

    // ---- Constructors ----
    public Question() {}

    public Question(QuestionCatrgories category, String questionText, AnswerType answerType) {
        this.category = category;
        this.questionText = questionText;
        this.answerType = answerType;
    }

    // ---- Getters and Setters ----
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public QuestionCatrgories getCategory() {
        return category;
    }

    public void setCategory(QuestionCatrgories category) {
        this.category = category;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public AnswerType getAnswerType() {
        return answerType;
    }

    public void setAnswerType(AnswerType answerType) {
        this.answerType = answerType;
    }
}
