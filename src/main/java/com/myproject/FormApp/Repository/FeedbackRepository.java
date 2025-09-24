package com.myproject.FormApp.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.myproject.FormApp.Model.Feedback;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

	@Query("SELECT f FROM Feedback f " +
		       "LEFT JOIN FETCH f.feedbackQuestionCategories fqc " +
		       "LEFT JOIN FETCH fqc.questionCategory qc " +
		       "LEFT JOIN FETCH qc.questions")
		List<Feedback> findAllWithCategoriesAndQuestions();


}
