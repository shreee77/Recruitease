package com.recruitease.repository;

import com.recruitease.model.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    List<Application> findByStudentId(Long studentId);

    List<Application> findByJobPostingId(Long jobId);

    Optional<Application> findByStudentIdAndJobPostingId(Long studentId, Long jobId);

    boolean existsByStudentIdAndJobPostingId(Long studentId, Long jobId);

    List<Application> findByStatus(Application.ApplicationStatus status);

    @Query("SELECT COUNT(a) FROM Application a WHERE a.jobPosting.postedBy.id = :hrId")
    long countApplicationsByHR(@Param("hrId") Long hrId);

    @Query("SELECT COUNT(a) FROM Application a WHERE a.status = 'SELECTED' AND a.jobPosting.postedBy.id = :hrId")
    long countSelectedByHR(@Param("hrId") Long hrId);

    @Query("SELECT COUNT(a) FROM Application a WHERE a.status = 'SHORTLISTED' AND a.jobPosting.postedBy.id = :hrId")
    long countShortlistedByHR(@Param("hrId") Long hrId);

    List<Application> findByJobPostingPostedById(Long hrId);
}
