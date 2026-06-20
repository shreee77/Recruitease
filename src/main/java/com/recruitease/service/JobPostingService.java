package com.recruitease.service;

import com.recruitease.model.*;
import com.recruitease.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class JobPostingService {

    @Autowired private JobPostingRepository jobPostingRepository;
    @Autowired private ApplicationRepository applicationRepository;

    public JobPosting save(JobPosting job) {
        return jobPostingRepository.save(job);
    }

    public Optional<JobPosting> findById(Long id) {
        return jobPostingRepository.findById(id);
    }

    public List<JobPosting> getAll() {
        return jobPostingRepository.findAll();
    }

    public List<JobPosting> getAllActive() {
        return jobPostingRepository.findActiveJobsNotExpired(LocalDate.now());
    }

    public List<JobPosting> getByHR(Long hrId) {
        return jobPostingRepository.findByPostedById(hrId);
    }

    public List<JobPosting> getByCompany(Long companyId) {
        return jobPostingRepository.findByCompanyId(companyId);
    }

    public List<JobPosting> search(String location, String department, String company) {
        return jobPostingRepository.searchJobs(
                (location != null && location.isBlank()) ? null : location,
                (department != null && department.isBlank()) ? null : department,
                (company != null && company.isBlank()) ? null : company
        );
    }

    public void delete(Long id) {
        jobPostingRepository.deleteById(id);
    }

    public void closeJob(Long id) {
        JobPosting job = jobPostingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));
        job.setStatus(JobPosting.JobStatus.CLOSED);
        jobPostingRepository.save(job);
    }

    public long countAll() { return jobPostingRepository.count(); }

    public long countTodaysPostings() { return jobPostingRepository.countTodaysPostings(); }

    public long countByHR(Long hrId) { return jobPostingRepository.countByPostedById(hrId); }

    public List<JobPosting> getTodaysPostingsByHR(Long hrId) {
        return jobPostingRepository.findTodaysPostingsByHR(hrId);
    }

    // Count applicants for a job
    public long countApplicants(Long jobId) {
        return applicationRepository.findByJobPostingId(jobId).size();
    }
}
