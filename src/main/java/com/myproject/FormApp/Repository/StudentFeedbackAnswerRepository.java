package com.myproject.FormApp.Repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.myproject.FormApp.Model.Feedback;
import com.myproject.FormApp.Model.Student;
import com.myproject.FormApp.Model.StudentFeedbackAnswer;

public interface StudentFeedbackAnswerRepository extends JpaRepository<StudentFeedbackAnswer, Long> {
    List<StudentFeedbackAnswer> findByStudentIdAndFeedbackId(Long studentId, Long feedbackId);


	boolean existsByStudentIdAndFeedbackId(Long id, Long id2);
}
