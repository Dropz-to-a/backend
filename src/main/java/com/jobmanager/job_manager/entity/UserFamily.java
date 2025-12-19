package com.jobmanager.job_manager.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_family")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserFamily {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @Column(nullable = false, length = 50)
    private String role;   // 엄마, 아빠, 형, 누나 등

    @Column(nullable = false, length = 100)
    private String name;
}