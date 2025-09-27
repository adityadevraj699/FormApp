package com.myproject.FormApp.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.myproject.FormApp.Model.EnrolledProgram;

public interface EnrolledProgramRepository extends JpaRepository<EnrolledProgram, Long>{

	boolean existsByStudentIdAndProgramId(Long id, Long id2);

	List<EnrolledProgram> findByStudentIdAndProgramId(Long id, Long programId);

	List<EnrolledProgram> findByStudentId(Long id);

	long countByProgramId(Long id);

}
