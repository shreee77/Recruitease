package com.recruitease.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Async
    public void sendApplicationConfirmation(String toEmail, String studentName,
                                            String jobTitle, String companyName) {
        if (mailSender == null) return;
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(toEmail);
        msg.setSubject("Application Submitted – " + jobTitle + " at " + companyName);
        msg.setText("Dear " + studentName + ",\n\n"
                + "Your application for the position of \"" + jobTitle + "\" at " + companyName
                + " has been successfully submitted.\n\n"
                + "You can track your application status on RecruitEase.\n\n"
                + "Best regards,\nRecruitEase Team");
        mailSender.send(msg);
    }

    @Async
    public void sendStatusUpdate(String toEmail, String studentName,
                                 String jobTitle, String companyName, String status) {
        if (mailSender == null) return;
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(toEmail);
        msg.setSubject("Application Update – " + jobTitle + " at " + companyName);
        msg.setText("Dear " + studentName + ",\n\n"
                + "Your application for \"" + jobTitle + "\" at " + companyName
                + " has been updated.\n\n"
                + "New Status: " + status.replace("_", " ") + "\n\n"
                + "Login to RecruitEase for more details.\n\n"
                + "Best regards,\nRecruitEase Team");
        mailSender.send(msg);
    }

    @Async
    public void sendWelcomeEmail(String toEmail, String name, String role) {
        if (mailSender == null) return;
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(toEmail);
        msg.setSubject("Welcome to RecruitEase!");
        msg.setText("Dear " + name + ",\n\n"
                + "Welcome to RecruitEase – the Campus Recruitment Portal.\n"
                + "Your account has been created as: " + role + "\n\n"
                + "Login at: http://localhost:8080/login\n\n"
                + "Best regards,\nRecruitEase Team");
        mailSender.send(msg);
    }
}
