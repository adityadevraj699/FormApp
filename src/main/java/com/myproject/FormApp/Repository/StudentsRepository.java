package com.myproject.FormApp.Repository;

import com.myproject.FormApp.Model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentsRepository extends JpaRepository<Student, Long> {
    boolean existsByEmail(String email);
    boolean existsByRollNo(String rollNo);
	Student findByEmail(String email);
}
