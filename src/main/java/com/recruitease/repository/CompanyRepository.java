package com.recruitease.repository;

import com.recruitease.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> findByCompanyEmail(String email);
    List<Company> findByActiveTrue();
    boolean existsByCompanyEmail(String email);
}
