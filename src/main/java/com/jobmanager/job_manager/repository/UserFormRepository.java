// src/main/java/com/jobmanager/job_manager/repository/UserFormRepository.java
package com.jobmanager.job_manager.repository;

import com.jobmanager.job_manager.entity.UserForm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserFormRepository extends JpaRepository<UserForm, Long> {
}