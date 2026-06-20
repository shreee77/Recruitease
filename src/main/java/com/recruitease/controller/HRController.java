
package com.recruitease.controller;

import com.recruitease.model.*;
import com.recruitease.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.security.Principal;

@Controller
@RequestMapping("/hr")
public class HRController {

    @Autowired private UserService userService;
    @Autowired private JobPostingService jobPostingService;
    @Autowired private ApplicationService applicationService;
    @Autowired private CompanyService companyService;
    @Autowired private ChatService chatService;
    @Autowired private FileStorageService fileStorageService;

    private HR getCurrentHR(Principal principal) {
        return userService.findHRByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("HR not found"));
    }

    @GetMapping("/dashboard")
    public String dashboard(Principal principal, Model model) {
        HR hr = getCurrentHR(principal);
        model.addAttribute("hr", hr);
        model.addAttribute("totalJobs", jobPostingService.countByHR(hr.getId()));
        model.addAttribute("totalApplicants", applicationService.countByHR(hr.getId()));
        model.addAttribute("shortlisted", applicationService.countShortlistedByHR(hr.getId()));
        model.addAttribute("selected", applicationService.countSelectedByHR(hr.getId()));
        model.addAttribute("todaysJobs", jobPostingService.getTodaysPostingsByHR(hr.getId()));
        model.addAttribute("unreadMessages", chatService.countUnread(hr.getId()));
        return "hr/dashboard";
    }

    @GetMapping("/jobs")
    public String listJobs(Principal principal, Model model) {
        model.addAttribute("jobs", jobPostingService.getByHR(getCurrentHR(principal).getId()));
        return "hr/job-list";
    }

    @GetMapping("/jobs/new")
    public String newJobForm(Principal principal, Model model) {
        model.addAttribute("job", new JobPosting());
        model.addAttribute("company", getCurrentHR(principal).getCompany());
        return "hr/job-form";
    }

    @PostMapping("/jobs/new")
    public String createJob(@ModelAttribute JobPosting job, Principal principal, RedirectAttributes ra) {
        HR hr = getCurrentHR(principal);
        job.setPostedBy(hr);
        job.setCompany(hr.getCompany());
        jobPostingService.save(job);
        ra.addFlashAttribute("success", "Job posted successfully!");
        return "redirect:/hr/jobs";
    }

    @GetMapping("/jobs/edit/{id}")
    public String editJobForm(@PathVariable Long id, Principal principal, Model model) {
        HR hr = getCurrentHR(principal);
        jobPostingService.findById(id).ifPresent(j -> {
            model.addAttribute("job", j);
            model.addAttribute("company", hr.getCompany());
        });
        return "hr/job-form";
    }

    @PostMapping("/jobs/edit/{id}")
    public String updateJob(@PathVariable Long id, @ModelAttribute JobPosting job, Principal principal, RedirectAttributes ra) {
        HR hr = getCurrentHR(principal);
        job.setId(id); job.setPostedBy(hr); job.setCompany(hr.getCompany());
        jobPostingService.save(job);
        ra.addFlashAttribute("success", "Job updated!");
        return "redirect:/hr/jobs";
    }

    @PostMapping("/jobs/delete/{id}")
    public String deleteJob(@PathVariable Long id, RedirectAttributes ra) {
        jobPostingService.delete(id);
        ra.addFlashAttribute("success", "Job deleted.");
        return "redirect:/hr/jobs";
    }

    @PostMapping("/jobs/close/{id}")
    public String closeJob(@PathVariable Long id, RedirectAttributes ra) {
        jobPostingService.closeJob(id);
        ra.addFlashAttribute("success", "Job closed.");
        return "redirect:/hr/jobs";
    }

    @GetMapping("/applicants")
    public String allApplicants(Principal principal, Model model) {
        model.addAttribute("applications", applicationService.getByHR(getCurrentHR(principal).getId()));
        return "hr/applicant-list";
    }

    @GetMapping("/jobs/{jobId}/applicants")
    public String applicantsForJob(@PathVariable Long jobId, Model model) {
        model.addAttribute("applications", applicationService.getByJob(jobId));
        jobPostingService.findById(jobId).ifPresent(j -> model.addAttribute("job", j));
        return "hr/applicant-list";
    }

    @PostMapping("/applicants/{appId}/status")
    public String updateStatus(@PathVariable Long appId, @RequestParam String status,
                               @RequestParam(required = false) String remarks, RedirectAttributes ra) {
        applicationService.updateStatus(appId, Application.ApplicationStatus.valueOf(status), remarks);
        ra.addFlashAttribute("success", "Status updated.");
        return "redirect:/hr/applicants";
    }

    @SuppressWarnings("unchecked")
	@GetMapping("/resume/download/{appId}")
    public ResponseEntity<Resource> downloadResume(@PathVariable Long appId) {
        return (ResponseEntity<Resource>) applicationService.findById(appId).map(app -> {
            try {
                Student student = app.getStudent();
                if (student == null || student.getResumePath() == null)
                    return ResponseEntity.<Resource>notFound().build();
                Resource resource = new UrlResource(Paths.get(fileStorageService.getUploadDir()).resolve(student.getResumePath()).toUri());
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"resume.pdf\"")
                        .body(resource);
            } catch (MalformedURLException e) {
                return ResponseEntity.<Resource>badRequest().build();
            }
        }).orElse(ResponseEntity.<Resource>notFound().build());
    }

    @GetMapping("/company")
    public String companyProfile(Principal principal, Model model) {
        model.addAttribute("company", getCurrentHR(principal).getCompany());
        return "hr/company-profile";
    }

    @GetMapping("/chat/{studentId}")
    public String chat(@PathVariable Long studentId, Principal principal, Model model) {
        HR hr = getCurrentHR(principal);
        model.addAttribute("messages", chatService.getConversation(hr.getId(), studentId));
        model.addAttribute("me", hr);
        model.addAttribute("otherId", studentId);
        return "hr/chat";
    }

    @PostMapping("/chat/send")
    public String sendChat(@RequestParam Long receiverId, @RequestParam String message,
                           Principal principal, RedirectAttributes ra) {
        HR hr = getCurrentHR(principal);
        User receiver = userService.findById(receiverId).orElseThrow(() -> new RuntimeException("User not found"));
        ChatMessage msg = new ChatMessage();
        msg.setSender(hr); msg.setReceiver(receiver); msg.setMessage(message);
        chatService.send(msg);
        return "redirect:/hr/chat/" + receiverId;
    }

    @GetMapping("/profile")
    public String profile(Principal principal, Model model) {
        model.addAttribute("hr", getCurrentHR(principal));
        return "hr/profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute HR hrData, Principal principal, RedirectAttributes ra) {
        HR hr = getCurrentHR(principal);
        hr.setFullName(hrData.getFullName());
        hr.setPhone(hrData.getPhone());
        hr.setDesignation(hrData.getDesignation());
        userService.updateHR(hr);
        ra.addFlashAttribute("success", "Profile updated.");
        return "redirect:/hr/profile";
    }
}
