package com.service.application.repository;

import com.service.application.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    
    Optional<UserProfile> findByUserId(Long userId);
    
    Optional<UserProfile> findByEmail(String email);
    
    boolean existsByUserId(Long userId);
    
    // Simple query without JOIN FETCH to avoid multiple bag fetch exception
    @Query("SELECT p FROM UserProfile p WHERE p.userId = :userId")
    Optional<UserProfile> findByUserIdWithDetails(@Param("userId") Long userId);
}
