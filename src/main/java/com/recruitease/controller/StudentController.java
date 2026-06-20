package com.recruitease.controller;

import com.recruitease.model.*;
import com.recruitease.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/student")
public class StudentController {

    @Autowired private UserService userService;
    @Autowired private JobPostingService jobPostingService;
    @Autowired private ApplicationService applicationService;
    @Autowired private FeedbackService feedbackService;
    @Autowired private ChatService chatService;
    @Autowired private FileStorageService fileStorageService;

    private Student getCurrentStudent(Principal principal) {
        return userService.findStudentByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Student not found"));
    }

    @GetMapping("/dashboard")
    public String dashboard(Principal principal, Model model) {
        Student student = getCurrentStudent(principal);
        List<Application> apps = applicationService.getByStudent(student.getId());
        model.addAttribute("student", student);
        model.addAttribute("totalApplied", apps.size());
        model.addAttribute("shortlisted", apps.stream()
                .filter(a -> a.getStatus() == Application.ApplicationStatus.SHORTLISTED ||
                             a.getStatus() == Application.ApplicationStatus.SELECTED).count());
        model.addAttribute("recentApplications", apps.stream().limit(5).toList());
        model.addAttribute("activeJobs", jobPostingService.getAllActive().stream().limit(5).toList());
        model.addAttribute("unreadMessages", chatService.countUnread(student.getId()));
        return "student/dashboard";
    }

    @GetMapping("/jobs")
    public String browseJobs(@RequestParam(required = false) String location,
                             @RequestParam(required = false) String department,
                             @RequestParam(required = false) String company,
                             Model model) {
        List<JobPosting> jobs;
        boolean anyFilter = (location != null && !location.isBlank()) ||
                            (department != null && !department.isBlank()) ||
                            (company != null && !company.isBlank());
        jobs = anyFilter ? jobPostingService.search(location, department, company)
                         : jobPostingService.getAllActive();
        model.addAttribute("jobs", jobs);
        model.addAttribute("location", location);
        model.addAttribute("department", department);
        model.addAttribute("company", company);
        return "student/job-list";
    }

    @GetMapping("/jobs/{id}")
    public String viewJob(@PathVariable Long id, Principal principal, Model model) {
        Student student = getCurrentStudent(principal);
        jobPostingService.findById(id).ifPresent(j -> model.addAttribute("job", j));
        model.addAttribute("hasApplied", applicationService.hasApplied(student.getId(), id));
        return "student/job-detail";
    }

    @PostMapping("/jobs/{jobId}/apply")
    public String applyForJob(@PathVariable Long jobId,
                              @RequestParam(required = false) String coverLetter,
                              Principal principal, RedirectAttributes ra) {
        Student student = getCurrentStudent(principal);
        try {
            JobPosting job = jobPostingService.findById(jobId)
                    .orElseThrow(() -> new RuntimeException("Job not found"));
            Application app = new Application();
            app.setStudent(student);
            app.setJobPosting(job);
            app.setCoverLetter(coverLetter);
            app.setResumePathAtApply(student.getResumePath());
            applicationService.apply(app);
            ra.addFlashAttribute("success", "Application submitted successfully!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/student/jobs/" + jobId;
    }

    @GetMapping("/applications")
    public String myApplications(Principal principal, Model model) {
        model.addAttribute("applications", applicationService.getByStudent(getCurrentStudent(principal).getId()));
        return "student/application-history";
    }

    @GetMapping("/profile")
    public String profile(Principal principal, Model model) {
        model.addAttribute("student", getCurrentStudent(principal));
        return "student/profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute Student studentData,
                                @RequestParam(required = false) MultipartFile resume,
                                Principal principal, RedirectAttributes ra) {
        Student student = getCurrentStudent(principal);
        student.setFullName(studentData.getFullName());
        student.setPhone(studentData.getPhone());
        student.setBranch(studentData.getBranch());
        student.setDepartment(studentData.getDepartment());
        student.setCgpa(studentData.getCgpa());
        student.setSkills(studentData.getSkills());
        student.setGraduationYear(studentData.getGraduationYear());
        student.setCollegeName(studentData.getCollegeName());

        if (resume != null && !resume.isEmpty()) {
            try {
                String filename = fileStorageService.storeResume(resume, student.getEmail());
                student.setResumePath(filename);
            } catch (Exception e) {
                ra.addFlashAttribute("error", "Resume upload failed: " + e.getMessage());
                return "redirect:/student/profile";
            }
        }
        userService.updateStudent(student);
        ra.addFlashAttribute("success", "Profile updated successfully!");
        return "redirect:/student/profile";
    }

    @GetMapping("/feedback")
    public String feedbackPage() { return "student/feedback"; }

    @PostMapping("/feedback/submit")
    public String submitFeedback(@RequestParam String message, @RequestParam Integer rating,
                                 Principal principal, RedirectAttributes ra) {
        Student student = getCurrentStudent(principal);
        Feedback fb = new Feedback();
        fb.setStudent(student); fb.setMessage(message); fb.setRating(rating);
        feedbackService.save(fb);
        ra.addFlashAttribute("success", "Thank you for your feedback!");
        return "redirect:/student/dashboard";
    }

    @GetMapping("/chat/{hrId}")
    public String chat(@PathVariable Long hrId, Principal principal, Model model) {
        Student student = getCurrentStudent(principal);
        model.addAttribute("messages", chatService.getConversation(student.getId(), hrId));
        model.addAttribute("me", student);
        model.addAttribute("otherId", hrId);
        return "student/chat";
    }

    @PostMapping("/chat/send")
    public String sendChat(@RequestParam Long receiverId, @RequestParam String message,
                           Principal principal) {
        Student student = getCurrentStudent(principal);
        User receiver = userService.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        ChatMessage msg = new ChatMessage();
        msg.setSender(student); msg.setReceiver(receiver); msg.setMessage(message);
        chatService.send(msg);
        return "redirect:/student/chat/" + receiverId;
    }
}
