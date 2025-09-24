package com.myproject.FormApp.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.myproject.FormApp.Model.Question;

public interface QuestionRepository extends JpaRepository<Question, Long> {

}
