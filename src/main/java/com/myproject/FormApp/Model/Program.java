package com.myproject.FormApp.Model;

import java.time.LocalDate;

import jakarta.persistence.*;

@Entity
@Table(name = "programs") // explicit table name
public class Program {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // lowercase field name

    @Column(nullable = false)
    private String trainingProgram;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    // Default constructor required by JPA
    public Program() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTrainingProgram() { return trainingProgram; }
    public void setTrainingProgram(String trainingProgram) { this.trainingProgram = trainingProgram; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
}
