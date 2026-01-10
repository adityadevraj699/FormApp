package com.myproject.FormApp.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.myproject.FormApp.Model.Feedback;
import com.myproject.FormApp.Model.FeedbackAnalysis;
import com.myproject.FormApp.Model.Question;

public interface FeedbackAnalysisRepository extends JpaRepository<FeedbackAnalysis, Long> {

    Optional<FeedbackAnalysis> findByFeedbackIdAndQuestionId(Long feedbackId, Long questionId);

    Optional<FeedbackAnalysis> findByFeedbackAndQuestion(Feedback feedback, Question q);

	
	boolean existsByQuestion(Question q);

	FeedbackAnalysis findByQuestionId(Long qid);
	
	// FeedbackAnalysisRepository.java
		@Query("SELECT a FROM FeedbackAnalysis a WHERE a.feedback.id IN :feedbackIds")
		List<FeedbackAnalysis> findAnalysisByFeedbackIds(@Param("feedbackIds") List<Long> feedbackIds);


	

}
