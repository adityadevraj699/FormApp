package com.myproject.FormApp.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.myproject.FormApp.Model.CurriculumTopic;

@Repository
public interface CurriculumTopicRepository extends JpaRepository<CurriculumTopic, Long> {
    // Custom queries agar zarurat ho, jaise:
    // List<CurriculumTopic> findByModuleId(Long moduleId);
}
