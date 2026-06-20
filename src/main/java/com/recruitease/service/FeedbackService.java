package com.recruitease.service;

import com.recruitease.model.Feedback;
import com.recruitease.repository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeedbackService {

    @Autowired private FeedbackRepository feedbackRepository;

    public Feedback save(Feedback feedback) {
        return feedbackRepository.save(feedback);
    }

    public List<Feedback> getAll() {
        return feedbackRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<Feedback> getByStudent(Long studentId) {
        return feedbackRepository.findByStudentId(studentId);
    }

    public long count() { return feedbackRepository.count(); }
}
