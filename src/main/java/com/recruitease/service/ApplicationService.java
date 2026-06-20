package com.recruitease.service;

import com.recruitease.model.*;
import com.recruitease.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ApplicationService {

    @Autowired private ApplicationRepository applicationRepository;
    @Autowired private EmailService emailService;

    public Application apply(Application application) {
        if (applicationRepository.existsByStudentIdAndJobPostingId(
                application.getStudent().getId(),
                application.getJobPosting().getId())) {
            throw new RuntimeException("You have already applied for this job.");
        }
        Application saved = applicationRepository.save(application);
        // Send confirmation email
        try {
            emailService.sendApplicationConfirmation(
                    saved.getStudent().getEmail(),
                    saved.getStudent().getFullName(),
                    saved.getJobPosting().getTitle(),
                    saved.getJobPosting().getCompany().getCompanyName()
            );
        } catch (Exception ignored) {}
        return saved;
    }

    public List<Application> getByStudent(Long studentId) {
        return applicationRepository.findByStudentId(studentId);
    }

    public List<Application> getByJob(Long jobId) {
        return applicationRepository.findByJobPostingId(jobId);
    }

    public List<Application> getByHR(Long hrId) {
        return applicationRepository.findByJobPostingPostedById(hrId);
    }

    public Optional<Application> findById(Long id) {
        return applicationRepository.findById(id);
    }

    public boolean hasApplied(Long studentId, Long jobId) {
        return applicationRepository.existsByStudentIdAndJobPostingId(studentId, jobId);
    }

    public Application updateStatus(Long applicationId, Application.ApplicationStatus newStatus, String remarks) {
        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));
        app.setStatus(newStatus);
        if (remarks != null && !remarks.isBlank()) app.setHrRemarks(remarks);
        Application updated = applicationRepository.save(app);

        // Email student on shortlist / selection / rejection
        try {
            if (newStatus == Application.ApplicationStatus.SHORTLISTED ||
                newStatus == Application.ApplicationStatus.SELECTED ||
                newStatus == Application.ApplicationStatus.REJECTED) {
                emailService.sendStatusUpdate(
                        updated.getStudent().getEmail(),
                        updated.getStudent().getFullName(),
                        updated.getJobPosting().getTitle(),
                        updated.getJobPosting().getCompany().getCompanyName(),
                        newStatus.name()
                );
            }
        } catch (Exception ignored) {}
        return updated;
    }

    public long countAll() { return applicationRepository.count(); }

    public long countByHR(Long hrId) { return applicationRepository.countApplicationsByHR(hrId); }

    public long countSelectedByHR(Long hrId) { return applicationRepository.countSelectedByHR(hrId); }

    public long countShortlistedByHR(Long hrId) { return applicationRepository.countShortlistedByHR(hrId); }
}
