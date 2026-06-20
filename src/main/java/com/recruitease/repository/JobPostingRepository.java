package com.recruitease.repository;

import com.recruitease.model.JobPosting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface JobPostingRepository extends JpaRepository<JobPosting, Long> {

    List<JobPosting> findByStatus(JobPosting.JobStatus status);

    List<JobPosting> findByPostedById(Long hrId);

    List<JobPosting> findByCompanyId(Long companyId);

    @Query("SELECT j FROM JobPosting j WHERE j.status = 'ACTIVE' AND " +
           "(:location IS NULL OR LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
           "(:department IS NULL OR LOWER(j.department) LIKE LOWER(CONCAT('%', :department, '%'))) AND " +
           "(:company IS NULL OR LOWER(j.company.companyName) LIKE LOWER(CONCAT('%', :company, '%')))")
    List<JobPosting> searchJobs(@Param("location") String location,
                                @Param("department") String department,
                                @Param("company") String company);

    @Query("SELECT j FROM JobPosting j WHERE j.status = 'ACTIVE' AND j.lastDateToApply >= :today")
    List<JobPosting> findActiveJobsNotExpired(@Param("today") LocalDate today);

    @Query("SELECT COUNT(j) FROM JobPosting j WHERE DATE(j.createdAt) = CURRENT_DATE")
    long countTodaysPostings();

    @Query("SELECT j FROM JobPosting j WHERE j.postedBy.id = :hrId AND DATE(j.createdAt) = CURRENT_DATE")
    List<JobPosting> findTodaysPostingsByHR(@Param("hrId") Long hrId);

    long countByPostedById(Long hrId);
}
