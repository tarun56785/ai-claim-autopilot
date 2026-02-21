package com.autopilot.human_review_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.repository.NoRepositoryBean;

@Entity
@Table(name = "triaged_claims")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClaimRecord {
    @Id
    private Long incidentId;

    private String incidentType;
    private String severity;
    private boolean policeReportMentioned;
    private boolean injuriesReported;
    private int aiConfidenceScore;

    @Column(columnDefinition = "TEXT")
    private String missingInformationChecklist;

    private String status; // PENDING_REVIEW, APPROVED
}
