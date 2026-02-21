package com.autopilot.human_review_service.repository;

import com.autopilot.human_review_service.model.ClaimRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClaimRecordRepository extends JpaRepository<ClaimRecord, Long> {}
