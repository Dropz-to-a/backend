package com.jobmanager.job_manager.repository;

import com.jobmanager.job_manager.entity.UserActivity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserActivityRepository extends JpaRepository<UserActivity, Long> {

    List<UserActivity> findByAccountIdOrderByStartDateDesc(Long accountId);
}