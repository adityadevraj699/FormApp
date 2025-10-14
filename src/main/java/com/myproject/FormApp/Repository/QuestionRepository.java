package com.myproject.FormApp.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.myproject.FormApp.Model.Question;
import com.myproject.FormApp.Model.Question.AnswerType;

public interface QuestionRepository extends JpaRepository<Question, Long> {

	List<Question> findByCategoryId(Long categoryId);

	List<Question> findByAnswerType(AnswerType number);

}
