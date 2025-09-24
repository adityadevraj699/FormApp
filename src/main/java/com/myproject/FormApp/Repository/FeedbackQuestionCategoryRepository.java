package com.myproject.FormApp.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.myproject.FormApp.Model.FeedbackQuestionCategory;

public interface FeedbackQuestionCategoryRepository extends JpaRepository<FeedbackQuestionCategory, Long> {

}
