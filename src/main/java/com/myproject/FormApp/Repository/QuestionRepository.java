package com.myproject.FormApp.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.myproject.FormApp.Model.Question;

public interface QuestionRepository extends JpaRepository<Question, Long> {

	List<Question> findByCategoryId(Long categoryId);

}
