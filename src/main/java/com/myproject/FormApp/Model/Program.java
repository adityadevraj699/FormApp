package com.myproject.FormApp.Model;

import java.time.LocalDate;
import java.util.List;

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
    
    private String course;
    private String branch;
    private String year;
    private String section;
    private String semester;

    public String getCourse() {
		return course;
	}

	public void setCourse(String course) {
		this.course = course;
	}

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getSection() {
		return section;
	}

	public void setSection(String section) {
		this.section = section;
	}

	public String getSemester() {
		return semester;
	}

	public void setSemester(String semester) {
		this.semester = semester;
	}

	public List<TeacherAssign> getTeacherAssignments() {
		return teacherAssignments;
	}

	public void setTeacherAssignments(List<TeacherAssign> teacherAssignments) {
		this.teacherAssignments = teacherAssignments;
	}

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
    
    @OneToMany(mappedBy = "program", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TeacherAssign> teacherAssignments;

 // Program -> Module (OneToMany)
    @OneToMany(mappedBy = "program", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<Module> modules = new java.util.ArrayList<>();

	public java.util.List<Module> getModules() {
		return modules;
	}

	public void setModules(java.util.List<Module> modules) {
		this.modules = modules;
	}
}
