package com.myproject.FormApp.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.myproject.FormApp.Model.Feedback;
import com.myproject.FormApp.Model.FeedbackAnalysis;
import com.myproject.FormApp.Model.Question;

public interface FeedbackAnalysisRepository extends JpaRepository<FeedbackAnalysis, Long> {

    Optional<FeedbackAnalysis> findByFeedbackIdAndQuestionId(Long feedbackId, Long questionId);

    Optional<FeedbackAnalysis> findByFeedbackAndQuestion(Feedback feedback, Question q);

	
	boolean existsByQuestion(Question q);

}
