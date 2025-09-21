package com.myproject.FormApp.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.myproject.FormApp.Model.Teacher;
import com.myproject.FormApp.Model.Teacher.Status;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {

	boolean existsByEmail(String email);

	Teacher findByEmail(String email);

	boolean existsByEmployeeId(String employeeId);

	List<Teacher> findByStatus(Status valueOf);

	List<Teacher> findByEmployeeIdContaining(String employeeId);

	List<Teacher> findByStatusAndEmployeeIdContaining(Status valueOf, String employeeId);

}
