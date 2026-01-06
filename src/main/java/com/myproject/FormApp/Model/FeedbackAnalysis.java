package com.myproject.FormApp.Model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "feedback_analysis",
    uniqueConstraints = @UniqueConstraint(columnNames = {"feedback_id", "question_id"})
)
public class FeedbackAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JoinColumn(nullable = false)
    private Feedback feedback;

    @ManyToOne @JoinColumn(nullable = false)
    private Question question;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private String modelName;
    private Double avgNumeric;
    private Double sentimentAvg;
    private String sentimentLabel;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(columnDefinition = "TEXT")
    private String suggestions;

    @Column(columnDefinition = "TEXT")
    private String keyPhrases;

    @Column(columnDefinition = "LONGTEXT")
    private String perResponseJson;

    public FeedbackAnalysis() {}
    public FeedbackAnalysis(Feedback f, Question q) {
        this.feedback = f;
        this.question = q;
    }

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
    }
	public Long getId() {
		return id;
	}
	public Feedback getFeedback() {
		return feedback;
	}
	public Question getQuestion() {
		return question;
	}
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public String getModelName() {
		return modelName;
	}
	public Double getAvgNumeric() {
		return avgNumeric;
	}
	public Double getSentimentAvg() {
		return sentimentAvg;
	}
	public String getSentimentLabel() {
		return sentimentLabel;
	}
	public String getSummary() {
		return summary;
	}
	public String getSuggestions() {
		return suggestions;
	}
	public String getKeyPhrases() {
		return keyPhrases;
	}
	public String getPerResponseJson() {
		return perResponseJson;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public void setFeedback(Feedback feedback) {
		this.feedback = feedback;
	}
	public void setQuestion(Question question) {
		this.question = question;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	public void setModelName(String modelName) {
		this.modelName = modelName;
	}
	public void setAvgNumeric(Double avgNumeric) {
		this.avgNumeric = avgNumeric;
	}
	public void setSentimentAvg(Double sentimentAvg) {
		this.sentimentAvg = sentimentAvg;
	}
	public void setSentimentLabel(String sentimentLabel) {
		this.sentimentLabel = sentimentLabel;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public void setSuggestions(String suggestions) {
		this.suggestions = suggestions;
	}
	public void setKeyPhrases(String keyPhrases) {
		this.keyPhrases = keyPhrases;
	}
	public void setPerResponseJson(String perResponseJson) {
		this.perResponseJson = perResponseJson;
	}

    // getters & setters (normal)
    
    
}
