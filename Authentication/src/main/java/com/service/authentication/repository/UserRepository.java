package com.service.authentication.repository;

import com.service.authentication.entity.User;
import com.service.authentication.entity.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    Optional<User> findByExternalUserIdAndUserType(Long externalUserId, UserType userType);
}
