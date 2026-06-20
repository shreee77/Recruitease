package com.recruitease.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "companies")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String companyName;

    @Column(unique = true)
    private String companyEmail;

    private String address;
    private String website;
    private String industry;
    private String description;
    private String logoPath;
    

    @Column(nullable = false)
    private boolean active = true;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<HR> hrList;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<JobPosting> jobPostings;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    
    public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public String getCompanyName() {
		return companyName;
	}


	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}


	public String getCompanyEmail() {
		return companyEmail;
	}


	public void setCompanyEmail(String companyEmail) {
		this.companyEmail = companyEmail;
	}


	public String getAddress() {
		return address;
	}


	public void setAddress(String address) {
		this.address = address;
	}


	public String getWebsite() {
		return website;
	}


	public void setWebsite(String website) {
		this.website = website;
	}


	public String getIndustry() {
		return industry;
	}


	public void setIndustry(String industry) {
		this.industry = industry;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public String getLogoPath() {
		return logoPath;
	}


	public void setLogoPath(String logoPath) {
		this.logoPath = logoPath;
	}


	public boolean isActive() {
		return active;
	}


	public void setActive(boolean active) {
		this.active = active;
	}


	public List<HR> getHrList() {
		return hrList;
	}


	public void setHrList(List<HR> hrList) {
		this.hrList = hrList;
	}


	public List<JobPosting> getJobPostings() {
		return jobPostings;
	}


	public void setJobPostings(List<JobPosting> jobPostings) {
		this.jobPostings = jobPostings;
	}


	public LocalDateTime getCreatedAt() {
		return createdAt;
	}


	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}


	@PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
}
