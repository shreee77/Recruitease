package com.recruitease.repository;

import com.recruitease.model.HR;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HRRepository extends JpaRepository<HR, Long> {
    Optional<HR> findByEmail(String email);
    List<HR> findByCompanyId(Long companyId);
}
