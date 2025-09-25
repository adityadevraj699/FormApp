package com.myproject.FormApp.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.myproject.FormApp.Model.Module;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Long> {

	List<Module> findByProgramId(Long id);
    // Custom queries agar zarurat ho, jaise:
    // List<Module> findByProgramId(Long programId);
}
