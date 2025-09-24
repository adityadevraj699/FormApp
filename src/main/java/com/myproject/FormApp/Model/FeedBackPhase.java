package com.myproject.FormApp.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table
public class FeedBackPhase {

	
	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    @Column(nullable = false)
	    private String phaseName;

		public Long getId() {
			return id;
		}
		

		public void setId(Long id) {
			this.id = id;
		}

		public String getPhaseName() {
			return phaseName;
		}

		public void setPhaseName(String phaseName) {
			this.phaseName = phaseName;
		}
	    
	    
	    
}
