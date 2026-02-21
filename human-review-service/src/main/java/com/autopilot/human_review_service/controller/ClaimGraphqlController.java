package com.autopilot.human_review_service.controller;

import com.autopilot.human_review_service.model.ClaimRecord;
import com.autopilot.human_review_service.repository.ClaimRecordRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.List;

@Controller
@AllArgsConstructor
public class ClaimGraphqlController {

    private final ClaimRecordRepository repository;
    private final Sinks.Many<ClaimRecord> claimSink;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    // Maps to "getAllPendingClaims" in the Query section of our schema
    @QueryMapping
    public List<ClaimRecord> getAllPendingClaims() {
        return repository.findAll();
    }

    // Maps to "approveClaim" in the Mutation section of our schema
    @MutationMapping
    public ClaimRecord approveClaim(@Argument Long incidentId) {
        ClaimRecord claim = repository.findById(incidentId)
                .orElseThrow(() -> new RuntimeException("Claim not found"));

        claim.setStatus("APPROVED");
        ClaimRecord savedClaim = repository.save(claim);

        // NEW: Send the approved claim to the final Kafka topic!
        try {
            kafkaTemplate.send("claim.approved", String.valueOf(savedClaim.getIncidentId()), objectMapper.writeValueAsString(savedClaim));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Claim " + incidentId + " Approved and sent to Fulfillment!");

        return savedClaim;
    }
    // NEW: The WebSocket endpoint!
    @SubscriptionMapping
    public Flux<ClaimRecord> claimAdded() {
        // This streams the data out of the bottom of the funnel to the UI
        return claimSink.asFlux();
    }
}
