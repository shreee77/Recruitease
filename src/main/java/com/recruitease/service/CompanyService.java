package com.recruitease.service;

import com.recruitease.model.Company;
import com.recruitease.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CompanyService {

    @Autowired private CompanyRepository companyRepository;

    public Company save(Company company) {
        return companyRepository.save(company);
    }

    public List<Company> getAllActive() {
        return companyRepository.findByActiveTrue();
    }

    public List<Company> getAll() {
        return companyRepository.findAll();
    }

    public Optional<Company> findById(Long id) {
        return companyRepository.findById(id);
    }

    public Company update(Company company) {
        return companyRepository.save(company);
    }

    public void toggleActive(Long id) {
        Company c = companyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Company not found"));
        c.setActive(!c.isActive());
        companyRepository.save(c);
    }

    public void delete(Long id) {
        companyRepository.deleteById(id);
    }

    public long count() {
        return companyRepository.count();
    }

    // Save company logo file
    public String saveLogoFile(MultipartFile file, String uploadDir) throws IOException {
        if (file == null || file.isEmpty()) return null;
        Path dir = Paths.get(uploadDir + "/logos");
        Files.createDirectories(dir);
        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Files.copy(file.getInputStream(), dir.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
        return "logos/" + filename;
    }
}
