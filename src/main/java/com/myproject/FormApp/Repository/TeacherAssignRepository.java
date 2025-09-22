package com.myproject.FormApp.Repository;

import com.myproject.FormApp.Model.TeacherAssign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeacherAssignRepository extends JpaRepository<TeacherAssign, Long> {

//    // Optional: kisi teacher ke assigned programs dekhne ke liye
//    List<TeacherAssign> findByTeacherId(Long teacherId);
//
//    // Optional: kisi program me assigned teachers dekhne ke liye
//    List<TeacherAssign> findByProgramId(Long programId);
}
