package com.myproject.FormApp.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.myproject.FormApp.Model.CurriculumTopic;

@Repository
public interface CurriculumTopicRepository extends JpaRepository<CurriculumTopic, Long> {

	List<CurriculumTopic> findByModuleId(Long id);
    // Custom queries agar zarurat ho, jaise:
    // List<CurriculumTopic> findByModuleId(Long moduleId);
	
	
}
