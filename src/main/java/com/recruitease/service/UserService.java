package com.recruitease.service;

import com.recruitease.model.*;
import com.recruitease.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired private UserRepository userRepository;
    @Autowired private StudentRepository studentRepository;
    @Autowired private HRRepository hrRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    // ── Register Student ──────────────────────────────────────────
    public Student registerStudent(Student student) {
        if (userRepository.existsByEmail(student.getEmail()))
            throw new RuntimeException("Email already registered: " + student.getEmail());
        student.setRole(User.Role.STUDENT);
        student.setPassword(passwordEncoder.encode(student.getPassword()));
        return studentRepository.save(student);
    }

    // ── Register HR ───────────────────────────────────────────────
    public HR registerHR(HR hr) {
        if (userRepository.existsByEmail(hr.getEmail()))
            throw new RuntimeException("Email already registered: " + hr.getEmail());
        hr.setRole(User.Role.HR);
        hr.setPassword(passwordEncoder.encode(hr.getPassword()));
        return hrRepository.save(hr);
    }

    // ── Get current user by email ─────────────────────────────────
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<Student> findStudentByEmail(String email) {
        return studentRepository.findByEmail(email);
    }

    public Optional<HR> findHRByEmail(String email) {
        return hrRepository.findByEmail(email);
    }

    // ── Admin: manage users ───────────────────────────────────────
    public List<User> getAllUsers() { return userRepository.findAll(); }

    public List<User> getAllByRole(User.Role role) { return userRepository.findByRole(role); }

    public List<HR> getAllHR() { return hrRepository.findAll(); }

    public List<Student> getAllStudents() { return studentRepository.findAll(); }

    public void toggleUserActive(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setActive(!user.isActive());
        userRepository.save(user);
    }

    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    // ── Update profile ────────────────────────────────────────────
    public Student updateStudent(Student student) {
        return studentRepository.save(student);
    }

    public HR updateHR(HR hr) {
        return hrRepository.save(hr);
    }

    public java.util.Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public long countHR() { return hrRepository.count(); }
    public long countStudents() { return studentRepository.count(); }
}
