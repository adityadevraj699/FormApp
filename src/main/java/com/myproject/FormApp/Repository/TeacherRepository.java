package com.myproject.FormApp.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.myproject.FormApp.Model.Teacher;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {

	boolean existsByEmail(String email);

	Teacher findByEmail(String email);

	boolean existsByEmployeeId(String employeeId);

}
