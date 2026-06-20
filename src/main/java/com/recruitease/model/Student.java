package com.recruitease.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "students")
@Data
@NoArgsConstructor
@AllArgsConstructor
@PrimaryKeyJoinColumn(name = "user_id")
public class Student extends User {

    private String branch;       // e.g. Computer Science, IT, Mechanical
    private String department;
    private String collegeName;
    private Integer graduationYear;
    private Double cgpa;
    private String skills;
    private String resumePath;   // path to uploaded resume file
    private String profilePhoto;
    

    public String getBranch() {
		return branch;
	}


	public void setBranch(String branch) {
		this.branch = branch;
	}


	public String getDepartment() {
		return department;
	}


	public void setDepartment(String department) {
		this.department = department;
	}


	public String getCollegeName() {
		return collegeName;
	}


	public void setCollegeName(String collegeName) {
		this.collegeName = collegeName;
	}


	public Integer getGraduationYear() {
		return graduationYear;
	}


	public void setGraduationYear(Integer graduationYear) {
		this.graduationYear = graduationYear;
	}


	public Double getCgpa() {
		return cgpa;
	}


	public void setCgpa(Double cgpa) {
		this.cgpa = cgpa;
	}


	public String getSkills() {
		return skills;
	}


	public void setSkills(String skills) {
		this.skills = skills;
	}


	public String getResumePath() {
		return resumePath;
	}


	public void setResumePath(String resumePath) {
		this.resumePath = resumePath;
	}


	public String getProfilePhoto() {
		return profilePhoto;
	}


	public void setProfilePhoto(String profilePhoto) {
		this.profilePhoto = profilePhoto;
	}


	public List<Application> getApplications() {
		return applications;
	}


	public void setApplications(List<Application> applications) {
		this.applications = applications;
	}


	@OneToMany(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Application> applications;
}
