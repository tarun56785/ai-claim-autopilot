package com.autopilot.human_review_service.controller;

import com.autopilot.human_review_service.model.ClaimRecord;
import com.autopilot.human_review_service.repository.ClaimRecordRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/review")
public class ReviewController {

    private final ClaimRecordRepository repository;

    public ReviewController(ClaimRecordRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/claims")
    public List<ClaimRecord> getAllPendingClaims() {
        return repository.findAll();
    }
}
