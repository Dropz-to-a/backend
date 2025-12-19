package com.jobmanager.job_manager.repository;

import com.jobmanager.job_manager.entity.UserForm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProfileRepository extends JpaRepository<UserForm, Long> {
}