package com.service.job.repository;

import com.service.job.model.Job;
import com.service.job.model.Job.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
    
    List<Job> findByEmployerId(Long employerId);
    
    List<Job> findByStatus(JobStatus status);
    
    @Query("SELECT j FROM Job j WHERE " +
           "(:title IS NULL OR LOWER(j.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
           "(:location IS NULL OR LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
           "(:companyName IS NULL OR LOWER(j.company) LIKE LOWER(CONCAT('%', :companyName, '%'))) AND " +
           "(:minSalary IS NULL OR j.salaryMax >= :minSalary) AND " +
           "(:maxSalary IS NULL OR j.salaryMin <= :maxSalary) AND " +
           "j.status = :status")
    List<Job> findJobsWithFilters(@Param("title") String title,
                                  @Param("location") String location,
                                  @Param("companyName") String companyName,
                                  @Param("minSalary") Double minSalary,
                                  @Param("maxSalary") Double maxSalary,
                                  @Param("status") JobStatus status);
    
    @Query("SELECT j FROM Job j WHERE LOWER(j.title) = LOWER(:title) AND LOWER(j.location) = LOWER(:location) AND LOWER(j.company) = LOWER(:company) AND j.salaryMin = :salaryMin AND j.salaryMax = :salaryMax AND j.status = :status")
    List<Job> findExactJobMatch(@Param("title") String title,
                                @Param("location") String location,
                                @Param("company") String company,
                                @Param("salaryMin") Double salaryMin,
                                @Param("salaryMax") Double salaryMax,
                                @Param("status") Job.JobStatus status);
}
