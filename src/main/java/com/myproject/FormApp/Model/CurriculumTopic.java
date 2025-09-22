package com.myproject.FormApp.Model;

import jakarta.persistence.*;

@Entity
@Table
public class CurriculumTopic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String topicName;

    // Foreign key for Module
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id", nullable = false)
    private Module module;

    // Default constructor
    public CurriculumTopic() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTopicName() { return topicName; }
    public void setTopicName(String topicName) { this.topicName = topicName; }

    public Module getModule() { return module; }
    public void setModule(Module module) { this.module = module; }
}
