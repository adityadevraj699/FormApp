package com.myproject.FormApp.Repository;

import java.awt.print.Pageable;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.myproject.FormApp.Model.Feedback;
import com.myproject.FormApp.Model.Program;
import com.myproject.FormApp.Model.Student;
import com.myproject.FormApp.Model.StudentFeedbackAnswer;

public interface StudentFeedbackAnswerRepository extends JpaRepository<StudentFeedbackAnswer, Long> {
    
    // --- Aapke Existing Methods (Unchanged) ---
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

    int countByFeedbackIdAndQuestionId(Long feedbackId, Long id);

    // --- Analytics Methods Fix (Innovation Dashboard) ---
    
    // Fix: Field name 'answerText' ko badal kar 'answer' kiya gaya hai jo aapke model se match karta hai
    @Query("SELECT AVG(CAST(sfa.answer AS double)) FROM StudentFeedbackAnswer sfa " +
           "WHERE sfa.feedback.program.id = :pId AND sfa.question.answerType = 'NUMBER'")
    Double getAverageRatingByProgram(@Param("pId") Long pId);

    // Fix: Sentiment count ke liye hum 'FeedbackAnalysis' table ka use karenge jisme sentimentLabel hai
    @Query("SELECT COUNT(fa) FROM FeedbackAnalysis fa " +
           "WHERE fa.feedback.program.id = :pId AND fa.sentimentLabel = :sentiment")
    long countBySentimentAndProgram(@Param("pId") Long pId, @Param("sentiment") String sentiment);

    @Query("SELECT COUNT(DISTINCT sfa.feedback.id) FROM StudentFeedbackAnswer sfa WHERE sfa.student.id = :sId AND sfa.feedback.program.id = :pId")
    long countDistinctFeedbackByStudentAndProgram(@Param("sId") Long sId, @Param("pId") Long pId);

    @Query("SELECT AVG(CAST(sfa.answer AS double)) FROM StudentFeedbackAnswer sfa " +
    	       "WHERE sfa.feedback.id = :fbId AND sfa.question.answerType = 'NUMBER'")
    	Double getAverageRatingByFeedback(@Param("fbId") Long fbId);

 // Sahi tarika: FeedbackAnalysis table ka use karke count nikalna
    @Query("SELECT COUNT(fa) FROM FeedbackAnalysis fa WHERE fa.feedback.id = :fbId AND fa.sentimentLabel = :sentiment")
    long countBySentimentAndFeedback(@Param("fbId") Long fbId, @Param("sentiment") String sentiment);

 // Sahi Query: Yeh check karega ki total kitne UNIQUE students ne feedback diya hai
    @Query("SELECT COUNT(DISTINCT s.student.id) FROM StudentFeedbackAnswer s")
    long countUniqueStudents();

    @Query("SELECT COUNT(DISTINCT s.student.id, s.feedback.id) FROM StudentFeedbackAnswer s")
    long countTotalSubmittedFeedbacks();

	long countTotalSubmittedFeedbacksByFeedbackId(Long id);

	@Query("SELECT AVG(CAST(s.answer AS double)) FROM StudentFeedbackAnswer s " +
       "WHERE s.feedback.id IN (SELECT f.id FROM Feedback f WHERE f.program.id IN " +
       "(SELECT ta.program.id FROM TeacherAssign ta WHERE ta.teacher.id = :teacherId))")
Double getGlobalAverageRatingForTeacher(@Param("teacherId") Long teacherId);
	
	// StudentFeedbackAnswerRepository.java
	@Query("SELECT AVG(CAST(s.answer AS double)) FROM StudentFeedbackAnswer s WHERE s.feedback.id IN :feedbackIds")
	Double getAverageRatingForFeedbacks(@Param("feedbackIds") List<Long> feedbackIds);

	@Query("SELECT COUNT(s) FROM StudentFeedbackAnswer s WHERE s.feedback.id IN :feedbackIds")
	long countTotalResponsesForFeedbacks(@Param("feedbackIds") List<Long> feedbackIds);

	// Ek specific feedback phase me kitne UNIQUE students ne answer kiya
	@Query("SELECT COUNT(DISTINCT s.student.id) FROM StudentFeedbackAnswer s WHERE s.feedback.id = :feedbackId")
	long countUniqueStudentsByFeedbackId(@Param("feedbackId") Long feedbackId);

	// Ek teacher ke saare programs me total kitne UNIQUE students ne participate kiya (Active Students)
	@Query("SELECT COUNT(DISTINCT s.student.id) FROM StudentFeedbackAnswer s WHERE s.feedback.id IN :feedbackIds")
	long countUniqueStudentsInFeedbackList(@Param("feedbackIds") List<Long> feedbackIds);

	// Har student ke liye count nikalna ki usne kitne unique feedback phases me participate kiya
	@Query("SELECT s.student.id, COUNT(DISTINCT s.feedback.id) FROM StudentFeedbackAnswer s WHERE s.feedback.id IN :feedbackIds GROUP BY s.student.id")
	List<Object[]> countPhasesPerStudent(@Param("feedbackIds") List<Long> feedbackIds);

	@Query("SELECT COUNT(DISTINCT a.feedback) FROM StudentFeedbackAnswer a " +
	           "WHERE a.feedback.program = :prog AND a.student = :student")
	    long countUniqueFeedbacksByProgramAndStudent(@Param("prog") Program prog, @Param("student") Student student);

	
	
	
}