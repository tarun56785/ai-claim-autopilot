package com.autopilot.human_review_service.service;

import com.autopilot.human_review_service.model.ClaimRecord;
import com.autopilot.human_review_service.repository.ClaimRecordRepository;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import org.springframework.kafka.support.KafkaHeaders;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import reactor.core.publisher.Sinks;

@Service
@AllArgsConstructor
public class AiResultConsumer {

    private final ClaimRecordRepository repository;
    private final ObjectMapper objectMapper;
    private final Sinks.Many<ClaimRecord> claimSink;

    @KafkaListener(topics = "claim.ai.triaged", groupId = "human-review-group")
    public void consumeAiResult(String aiResultJson, @Header(KafkaHeaders.RECEIVED_KEY) String incidentId) {
        try {
            System.out.println("Saving AI results for human review...");

            // Convert the JSON string back into usable data
            JsonNode node = objectMapper.readTree(aiResultJson);

            ClaimRecord claim = new ClaimRecord();
            claim.setIncidentId(Long.parseLong(incidentId));
            claim.setIncidentType(node.get("incidentType").asText());
            claim.setSeverity(node.get("severity").asText());
            claim.setPoliceReportMentioned(node.get("policeReportMentioned").asBoolean());
            claim.setInjuriesReported(node.get("injuriesReported").asBoolean());
            claim.setAiConfidenceScore(node.get("aiConfidenceScore").asInt());
            claim.setMissingInformationChecklist(node.get("missingInformationChecklist").asText());
            claim.setStatus("PENDING_REVIEW");

            ClaimRecord savedClaim = repository.save(claim);
            System.out.println("Claim " + incidentId + " is ready for human review!");

            // NEW: Pour the saved claim into the top of the funnel!
            claimSink.tryEmitNext(savedClaim);

        } catch (Exception e) {
            System.err.println("Failed to save claim: " + e.getMessage());
        }
    }
}
