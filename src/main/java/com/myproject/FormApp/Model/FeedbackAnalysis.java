package com.myproject.FormApp.Model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "feedback_analysis",
       uniqueConstraints = {@UniqueConstraint(columnNames = {"feedback_id", "question_id"})})
public class FeedbackAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Feedback reference
    @ManyToOne
    @JoinColumn(name = "feedback_id", nullable = false)
    private Feedback feedback;

    // Question reference
    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    // Timestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // Model used for AI / analysis
    @Column(name = "model_name")
    private String modelName;

    @Column(name = "avg_numeric")
    private Double avgNumeric;

  
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }


    // Sentiment info
    @Column(name = "sentiment_avg")
    private Double sentimentAvg;

    @Column(name = "sentiment_label", length = 32)
    private String sentimentLabel;

    // AI generated summary
    @Column(columnDefinition = "TEXT")
    private String summary;

    // AI generated suggestions (JSON or newline separated)
    @Column(columnDefinition = "TEXT")
    private String suggestions;

    // Key phrases from analysis
    @Column(columnDefinition = "TEXT")
    private String keyPhrases;

    // Per-response analysis JSON (anonymized)
    @Column(columnDefinition = "LONGTEXT")
    private String perResponseJson;

    // Constructors
    public FeedbackAnalysis() {}

    public FeedbackAnalysis(Feedback feedback, Question question) {
        this.feedback = feedback;
        this.question = question;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Feedback getFeedback() { return feedback; }
    public void setFeedback(Feedback feedback) { this.feedback = feedback; }

    public Question getQuestion() { return question; }
    public void setQuestion(Question question) { this.question = question; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getModelName() { return modelName; }
    public void setModelName(String modelName) { this.modelName = modelName; }

    public Double getAvgNumeric() { return avgNumeric; }
    public void setAvgNumeric(Double avgNumeric) { this.avgNumeric = avgNumeric; }

    public Double getSentimentAvg() { return sentimentAvg; }
    public void setSentimentAvg(Double sentimentAvg) { this.sentimentAvg = sentimentAvg; }

    public String getSentimentLabel() { return sentimentLabel; }
    public void setSentimentLabel(String sentimentLabel) { this.sentimentLabel = sentimentLabel; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public String getSuggestions() { return suggestions; }
    public void setSuggestions(String suggestions) { this.suggestions = suggestions; }

    public String getKeyPhrases() { return keyPhrases; }
    public void setKeyPhrases(String keyPhrases) { this.keyPhrases = keyPhrases; }

    public String getPerResponseJson() { return perResponseJson; }
    public void setPerResponseJson(String perResponseJson) { this.perResponseJson = perResponseJson; }
}
