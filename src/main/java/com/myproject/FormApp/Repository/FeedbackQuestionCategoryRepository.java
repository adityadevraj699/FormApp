package com.myproject.FormApp.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.myproject.FormApp.Model.FeedbackQuestionCategory;
import com.myproject.FormApp.Model.QuestionCatrgories;

public interface FeedbackQuestionCategoryRepository extends JpaRepository<FeedbackQuestionCategory, Long> {
	
	@Query("SELECT fqc.questionCategory FROM FeedbackQuestionCategory fqc WHERE fqc.id = :id")
    QuestionCatrgories findCategoryByFqcId(@Param("id") Long fqcId);

}
