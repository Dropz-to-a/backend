package com.jobmanager.job_manager.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "companies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Company {

    @Id
    private Long accountId;   // companies.account_id (PK & FK)

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    private String companyName;   // company_name
    private String description;   // description
    private String location;      // location
    private String logoUrl;       // logo_url

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
