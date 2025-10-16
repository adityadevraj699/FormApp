package com.myproject.FormApp.Repository;

import com.myproject.FormApp.Model.Student;
import com.myproject.FormApp.Model.Student.Status;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentsRepository extends JpaRepository<Student, Long> {
    boolean existsByEmail(String email);
    boolean existsByRollNo(String rollNo);
	Student findByEmail(String email);
	List<Student> findByStatus(Status valueOf);
	List<Student> findByRollNoContaining(String rollNo);
	List<Student> findByStatusAndRollNoContaining(Status valueOf, String rollNo);
	Student findByRollNo(String email);
	Page<Student> findByStatus(Student.Status status, Pageable pageable);

    Page<Student> findByRollNoContaining(String rollNo, Pageable pageable);

    Page<Student> findByStatusAndRollNoContaining(Student.Status status, String rollNo, Pageable pageable);
	
	
	
	
}
