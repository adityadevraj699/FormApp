package com.myproject.FormApp.Model;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table // Explicit table name
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String rollNo;

    @Column(nullable = false)
    private String name;

    private String fatherName;
    private String motherName;
    private String gender;
    private String address;


    @Column(nullable = false)
    private String branch;
    @Column(nullable = false)
    private String year;
    @Column(nullable = false)
    private String contactNo;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String password;

    private LocalDateTime regDate;

    @Enumerated(EnumType.STRING)
    private Role role;

    public enum Role {
        STUDENT, ADMIN, TEACHER
    }

    @Enumerated(EnumType.STRING)
    private Status status;

    public enum Status {
        PENDING, APPROVED, DISABLED
    }

    // Default constructor required by JPA
    public Student() {} 

    // Automatically set regDate before saving
 // Automatically set regDate, role, and status before saving
    @PrePersist
    protected void onCreate() {
        this.regDate = LocalDateTime.now();   // Current timestamp
    }

    
    

    // ----------------- Getters and Setters -----------------
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRollNo() { return rollNo; }
    public void setRollNo(String rollNo) { this.rollNo = rollNo; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getFatherName() { return fatherName; }
    public void setFatherName(String fatherName) { this.fatherName = fatherName; }

    public String getMotherName() { return motherName; }
    public void setMotherName(String motherName) { this.motherName = motherName; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }


    public String getBranch() { return branch; }
    public void setBranch(String branch) { this.branch = branch; }

    public String getYear() { return year; }
    public void setYear(String year) { this.year = year; }

    public String getContactNo() { return contactNo; }
    public void setContactNo(String contactNo) { this.contactNo = contactNo; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public LocalDateTime getRegDate() { return regDate; }
    public void setRegDate(LocalDateTime regDate) { this.regDate = regDate; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
}
