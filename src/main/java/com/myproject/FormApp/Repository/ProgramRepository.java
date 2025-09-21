package com.myproject.FormApp.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.myproject.FormApp.Model.Program;

public interface ProgramRepository extends JpaRepository<Program, Long> {

}
