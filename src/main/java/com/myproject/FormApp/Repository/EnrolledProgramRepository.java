package com.myproject.FormApp.Repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.myproject.FormApp.Model.EnrolledProgram;
import com.myproject.FormApp.Model.EnrolledProgram.ProgramStatus;
import com.myproject.FormApp.Model.Program;
import com.myproject.FormApp.Model.Student;

public interface EnrolledProgramRepository extends JpaRepository<EnrolledProgram, Long>{

	boolean existsByStudentIdAndProgramId(Long id, Long id2);

	List<EnrolledProgram> findByStudentIdAndProgramId(Long id, Long programId);

	List<EnrolledProgram> findByStudentId(Long id);

	long countByProgramId(Long id);

	List<EnrolledProgram> findByProgramId(Long id);

	List<EnrolledProgram> findByStudentIdAndStatus(Long id, ProgramStatus status);

	boolean existsByStudentAndProgram(Student student, Program program);

	Page<EnrolledProgram> findByProgramId(Long programId, Pageable pageable);
	Page<EnrolledProgram> findByProgramIdAndStatus(Long programId, ProgramStatus status, Pageable pageable);
	Page<EnrolledProgram> findByProgramIdAndStudentRollNoContaining(Long programId, String rollNo, Pageable pageable);
	Page<EnrolledProgram> findByProgramIdAndStatusAndStudentRollNoContaining(Long programId, ProgramStatus status, String rollNo, Pageable pageable);

	long countByProgramAndStatus(Program p, ProgramStatus approved);

	List<EnrolledProgram> findByStudent(Student student);



}
