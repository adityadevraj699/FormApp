package com.myproject.FormApp.Repository;

import com.myproject.FormApp.Model.Program;
import com.myproject.FormApp.Model.Teacher;
import com.myproject.FormApp.Model.TeacherAssign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherAssignRepository extends JpaRepository<TeacherAssign, Long> {

    // Program के सभी assigned teachers लाने के लिए
    List<TeacherAssign> findAllByProgramId(Long programId);

    // Optional: किसी teacher के assigned programs देखने के लिए
    List<TeacherAssign> findByTeacherId(Long teacherId);

	List<TeacherAssign> findByProgramId(Long programId);

	boolean existsByProgramIdAndTeacherId(Long id, Long id2);

	boolean existsByTeacherAndProgram(Teacher teacher, Program program);

	 @Query("""
	           SELECT ta FROM TeacherAssign ta
	           JOIN FETCH ta.program p
	           """)
	List<TeacherAssign> findAllWithProgram();
}

