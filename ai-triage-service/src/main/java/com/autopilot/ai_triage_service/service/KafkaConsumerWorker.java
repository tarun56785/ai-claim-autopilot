package com.autopilot.ai_triage_service.service;

import com.autopilot.ai_triage_service.dto.ClaimExtraction;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class KafkaConsumerWorker {

    private final AiExtractionService aiService;
    private final KafkaTemplate<String, ClaimExtraction> kafkaTemplate;
    private final ObjectMapper objectMapper;

    // NEW: Automatically retry 3 times. If it fails 3 times, send to a Dead Letter Topic.
    @RetryableTopic(attempts = "3")
    @KafkaListener(topics = "incident.submitted", groupId = "ai-triage-group")
    public void consumeIncident(String incidentJson){
        try {
            System.out.println("Picked up new job from conveyor belt!");

            // Extract the raw text from the JSON coming from the Intake Service
            JsonNode jsonNode = objectMapper.readTree(incidentJson);
            String rawDescription = jsonNode.path("rawDescription").asText();
            String incidentId = jsonNode.path("id").asText();

            // Ask the AI to process it
            ClaimExtraction result = aiService.analyzeIncident(rawDescription);
            System.out.println("AI successfully structured the data: " + result);

            // Put the finished work back on the next conveyor belt
            kafkaTemplate.send("claim.ai.triaged", incidentId, result);

        }catch (Exception e) {
            System.err.println("Error processing incident. Spring will retry automatically...");
            // We MUST throw the exception so Spring knows it failed and triggers the retry!
            throw new RuntimeException("AI Processing failed", e);
        }
    }

    // NEW: This is the safety net. If all 3 attempts fail, the message lands here.
    @DltHandler
    public void handleDeadLetterQueue(String incidentJson, @Header(KafkaHeaders.EXCEPTION_MESSAGE) String errorMessage) {
        System.err.println("ALERT: Message permanently failed after 3 retries!");
        System.err.println("Bad Message: " + incidentJson);
        System.err.println("Reason: " + errorMessage);

        // In a real production system, we would also update our PostgreSQL
        // database here to change the status to "FAILED_REQUIRES_HUMAN"
    }
}
