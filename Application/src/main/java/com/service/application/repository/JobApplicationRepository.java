package com.service.application.repository;

import com.service.application.model.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
    
    List<JobApplication> findByJobId(Long jobId);
    
    List<JobApplication> findByApplicantId(Long applicantId);
    
    Optional<JobApplication> findByJobIdAndApplicantId(Long jobId, Long applicantId);
    
    boolean existsByJobIdAndApplicantId(Long jobId, Long applicantId);
}
