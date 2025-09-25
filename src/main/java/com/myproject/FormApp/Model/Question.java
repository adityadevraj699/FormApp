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
    private QuestionCatrgories category;

    @Column(nullable = false, length = 500)
    private String questionText;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AnswerType answerType;

    // Only needed if type is NUMBER
    private Integer rangeStart;
    private Integer rangeEnd;

    public enum AnswerType {
        NUMBER, TEXT
    }

    public Question() {}

    public Question(QuestionCatrgories category, String questionText, AnswerType answerType) {
        this.category = category;
        this.questionText = questionText;
        this.answerType = answerType;
    }

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

	public Integer getRangeStart() {
		return rangeStart;
	}

	public void setRangeStart(Integer rangeStart) {
		this.rangeStart = rangeStart;
	}

	public Integer getRangeEnd() {
		return rangeEnd;
	}

	public void setRangeEnd(Integer rangeEnd) {
		this.rangeEnd = rangeEnd;
	}

    // Getters and setters for all fields including rangeStart and rangeEnd
    
    
}
