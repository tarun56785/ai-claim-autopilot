package com.autopilot.intake_service.service;

import com.autopilot.intake_service.model.Incident;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class KafkaProducerService {

    @Autowired
    private KafkaTemplate<String, Incident> kafkaTemplate;

    public void sendIncidentForAiProcessing(Incident incident) {
        // "incident.submitted" is the name of the topic (the specific conveyor belt)
        kafkaTemplate.send("incident.submitted", String.valueOf(incident.getId()), incident);
        System.out.println("Sent to Kafka: Incident ID " + incident.getId());
    }
}
