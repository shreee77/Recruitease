package com.recruitease.controller;

import com.recruitease.model.*;
import com.recruitease.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired private UserService userService;
    @Autowired private CompanyService companyService;
    @Autowired private JobPostingService jobPostingService;
    @Autowired private ApplicationService applicationService;
    @Autowired private FeedbackService feedbackService;
    @Autowired private PasswordEncoder passwordEncoder;

    // ── Dashboard ──────────────────────────────────────────────────
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalHR", userService.countHR());
        model.addAttribute("totalStudents", userService.countStudents());
        model.addAttribute("totalJobs", jobPostingService.countAll());
        model.addAttribute("totalApplications", applicationService.countAll());
        model.addAttribute("totalCompanies", companyService.count());
        model.addAttribute("todaysPostings", jobPostingService.countTodaysPostings());
        model.addAttribute("recentJobs", jobPostingService.getAll().stream().limit(5).toList());
        return "admin/dashboard";
    }

    // ── Manage HR ──────────────────────────────────────────────────
    @GetMapping("/hr")
    public String manageHR(Model model) {
        model.addAttribute("hrList", userService.getAllHR());
        return "admin/hr-list";
    }

    @GetMapping("/hr/add")
    public String addHRForm(Model model) {
        model.addAttribute("hr", new HR());
        model.addAttribute("companies", companyService.getAll());
        return "admin/hr-add";
    }

    @PostMapping("/hr/add")
    public String addHR(@ModelAttribute HR hr,
                        @RequestParam Long companyId,
                        RedirectAttributes ra) {
        try {
            companyService.findById(companyId).ifPresent(hr::setCompany);
            userService.registerHR(hr);
            ra.addFlashAttribute("success", "HR added successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/hr";
    }

    @PostMapping("/hr/toggle/{id}")
    public String toggleHR(@PathVariable Long id, RedirectAttributes ra) {
        userService.toggleUserActive(id);
        ra.addFlashAttribute("success", "HR status updated.");
        return "redirect:/admin/hr";
    }

    @PostMapping("/hr/delete/{id}")
    public String deleteHR(@PathVariable Long id, RedirectAttributes ra) {
        userService.deleteUser(id);
        ra.addFlashAttribute("success", "HR deleted.");
        return "redirect:/admin/hr";
    }

    // ── Manage Students ────────────────────────────────────────────
    @GetMapping("/students")
    public String manageStudents(Model model) {
        model.addAttribute("students", userService.getAllStudents());
        return "admin/student-list";
    }

    // ── Manage Companies ───────────────────────────────────────────
    @GetMapping("/companies")
    public String manageCompanies(Model model) {
        model.addAttribute("companies", companyService.getAll());
        return "admin/company-list";
    }

    @GetMapping("/companies/add")
    public String addCompanyForm(Model model) {
        model.addAttribute("company", new Company());
        return "admin/company-add";
    }

    @PostMapping("/companies/add")
    public String addCompany(@ModelAttribute Company company,
                             @RequestParam(required = false) MultipartFile logo,
                             RedirectAttributes ra) {
        try {
            if (logo != null && !logo.isEmpty()) {
                String path = companyService.saveLogoFile(logo, "uploads");
                company.setLogoPath(path);
            }
            companyService.save(company);
            ra.addFlashAttribute("success", "Company added successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/companies";
    }

    @GetMapping("/companies/edit/{id}")
    public String editCompanyForm(@PathVariable Long id, Model model) {
        companyService.findById(id).ifPresent(c -> model.addAttribute("company", c));
        return "admin/company-edit";
    }

    @PostMapping("/companies/edit/{id}")
    public String editCompany(@PathVariable Long id,
                              @ModelAttribute Company company,
                              @RequestParam(required = false) MultipartFile logo,
                              RedirectAttributes ra) {
        try {
            company.setId(id);
            if (logo != null && !logo.isEmpty()) {
                String path = companyService.saveLogoFile(logo, "uploads");
                company.setLogoPath(path);
            }
            companyService.update(company);
            ra.addFlashAttribute("success", "Company updated.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/companies";
    }

    @PostMapping("/companies/toggle/{id}")
    public String toggleCompany(@PathVariable Long id, RedirectAttributes ra) {
        companyService.toggleActive(id);
        ra.addFlashAttribute("success", "Company status updated.");
        return "redirect:/admin/companies";
    }

    // ── Job Postings ───────────────────────────────────────────────
    @GetMapping("/jobs")
    public String viewJobs(Model model) {
        model.addAttribute("jobs", jobPostingService.getAll());
        return "admin/job-list";
    }

    // ── Reports ────────────────────────────────────────────────────
    @GetMapping("/reports")
    public String reports(Model model) {
        model.addAttribute("hrList", userService.getAllHR());
        model.addAttribute("todaysPostings", jobPostingService.countTodaysPostings());
        model.addAttribute("totalJobs", jobPostingService.countAll());
        model.addAttribute("totalApplications", applicationService.countAll());
        return "admin/reports";
    }

    // ── Feedback ───────────────────────────────────────────────────
    @GetMapping("/feedback")
    public String viewFeedback(Model model) {
        model.addAttribute("feedbacks", feedbackService.getAll());
        return "admin/feedback";
    }

    // ── Profile ────────────────────────────────────────────────────
    @GetMapping("/profile")
    public String profile(Principal principal, Model model) {
        userService.findByEmail(principal.getName()).ifPresent(u -> model.addAttribute("user", u));
        return "admin/profile";
    }
}
