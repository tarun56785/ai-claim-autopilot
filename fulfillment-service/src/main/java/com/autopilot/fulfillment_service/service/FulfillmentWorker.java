package com.autopilot.fulfillment_service.service;

import com.autopilot.fulfillment_service.dto.ApprovedClaim;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class FulfillmentWorker {

    private final PdfGeneratorService pdfService;
    private final EmailService emailService;
    private final ObjectMapper objectMapper;

    public FulfillmentWorker(PdfGeneratorService pdfService, EmailService emailService) {
        this.pdfService = pdfService;
        this.emailService = emailService;
        this.objectMapper = new ObjectMapper();
    }

    @KafkaListener(topics = "claim.approved", groupId = "fulfillment-group")
    public void processApprovedClaim(String claimJson) {
        try {
            System.out.println("Received Approved Claim. Starting fulfillment...");

            // 1. Read the JSON from the manager
            ApprovedClaim claim = objectMapper.readValue(claimJson, ApprovedClaim.class);

            // 2. Generate the PDF entirely in RAM
            byte[] pdfBytes = pdfService.generateClaimDossier(claim);

            // 3. Send the Email with the attachment
            emailService.sendClaimEmail(claim, pdfBytes);

            System.out.println("Fulfillment complete for Claim " + claim.incidentId() + "!");

        } catch (Exception e) {
            System.err.println("Fulfillment failed: " + e.getMessage());
        }
    }
}