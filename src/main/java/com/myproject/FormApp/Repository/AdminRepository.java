package com.myproject.FormApp.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.myproject.FormApp.Model.Admin;

public interface AdminRepository extends JpaRepository<Admin, Long> {

	boolean existsByEmail(String email);

	Admin findByEmail(String email);

	
}
