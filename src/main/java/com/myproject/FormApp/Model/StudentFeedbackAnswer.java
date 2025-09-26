package com.myproject.FormApp.Model;

import jakarta.persistence.*;

@Entity
@Table
public class StudentFeedbackAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // कौन student ने दिया
    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    // कौन feedback (phase/program) है
    @ManyToOne
    @JoinColumn(name = "feedback_id", nullable = false)
    private Feedback feedback;

    // कौन सा question
    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    // Answer (text या number दोनों store करने के लिए string रख सकते हैं)
    @Column(nullable = false, length = 1000)
    private String answer;

    public StudentFeedbackAnswer() {}

    public StudentFeedbackAnswer(Student student, Feedback feedback, Question question, String answer) {
        this.student = student;
        this.feedback = feedback;
        this.question = question;
        this.answer = answer;
    }

    // getters & setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public Feedback getFeedback() { return feedback; }
    public void setFeedback(Feedback feedback) { this.feedback = feedback; }

    public Question getQuestion() { return question; }
    public void setQuestion(Question question) { this.question = question; }

    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }
}
