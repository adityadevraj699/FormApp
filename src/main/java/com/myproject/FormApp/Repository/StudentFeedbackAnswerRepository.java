package com.myproject.FormApp.Repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.myproject.FormApp.Model.Feedback;
import com.myproject.FormApp.Model.Student;
import com.myproject.FormApp.Model.StudentFeedbackAnswer;

public interface StudentFeedbackAnswerRepository extends JpaRepository<StudentFeedbackAnswer, Long> {
    List<StudentFeedbackAnswer> findByStudentIdAndFeedbackId(Long studentId, Long feedbackId);


	boolean existsByStudentIdAndFeedbackId(Long id, Long id2);
	
	@Query("SELECT DISTINCT sfa.feedback.id FROM StudentFeedbackAnswer sfa WHERE sfa.student.id = :studentId")
    List<Long> findAnsweredFeedbackIdsByStudent(@Param("studentId") Long studentId);
	
	
	 @Query("SELECT DISTINCT sfa.feedback FROM StudentFeedbackAnswer sfa WHERE sfa.student.id = :studentId")
	    List<Feedback> findDistinctFeedbacksByStudentId(@Param("studentId") Long studentId);


	 List<StudentFeedbackAnswer> findByFeedback(Feedback feedback);


	 long countDistinctByFeedback(Feedback feedback);
	 
	 
	 


	 List<StudentFeedbackAnswer> findByFeedbackAndStudent(Feedback feedback, Student student);
	 
	 @Query("SELECT DISTINCT s.student FROM StudentFeedbackAnswer s WHERE s.feedback = :feedback")
	    List<Student> findDistinctStudentsByFeedback(@Param("feedback") Feedback feedback);


	 List<StudentFeedbackAnswer> findByFeedbackIdAndQuestionId(Long feedbackId, Long id);


	 List<StudentFeedbackAnswer> findByFeedbackId(Long feedbackId);
}
