package com.myproject.FormApp.Model;

import jakarta.persistence.*;

@Entity
@Table // explicit table name
public class QuestionCatrgories {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto-increment ID
    private Long id;

    @Column(nullable = false, unique = true, length = 100) // category name
    private String categoryName;

    // ---- Constructors ----
    public QuestionCatrgories() {}

    public QuestionCatrgories(String categoryName) {
        this.categoryName = categoryName;
    }

    // ---- Getters and Setters ----
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
