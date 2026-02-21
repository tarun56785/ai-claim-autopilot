package com.autopilot.intake_service.controller;

import com.autopilot.intake_service.model.Incident;
import com.autopilot.intake_service.repository.IncidentRepository;
import com.autopilot.intake_service.service.KafkaProducerService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/intake")
@AllArgsConstructor
public class IntakeController {

    private final IncidentRepository repository;
    private final KafkaProducerService kafkaProducer;

    // A Java Record to hold the incoming JSON request
    public record IncidentRequest(String description) {}

    @PostMapping
    public ResponseEntity<String> submitIncident(@RequestBody IncidentRequest request) {
        // 1. Create the incident
        Incident incident = new Incident();
        incident.setRawDescription(request.description());
        incident.setStatus("RECEIVED");

        // 2. Save to PostgreSQL
        Incident savedIncident = repository.save(incident);

        // 3. Send to Kafka
        kafkaProducer.sendIncidentForAiProcessing(savedIncident);

        // 4. Return success to the user immediately
        return ResponseEntity.accepted().body("Incident received! Tracking ID: " + savedIncident.getId());
    }
}
