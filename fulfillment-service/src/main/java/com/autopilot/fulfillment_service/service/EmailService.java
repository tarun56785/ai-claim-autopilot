package com.autopilot.fulfillment_service.service;

import com.autopilot.fulfillment_service.dto.ApprovedClaim;
import jakarta.mail.internet.MimeMessage;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendClaimEmail(ApprovedClaim claim, byte[] pdfAttachment) {
        try {
            // MimeMessage allows us to send attachments, unlike SimpleMailMessage
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom("autopilot@insurance.com");
            helper.setTo("customer@example.com"); // Hardcoded for our test
            helper.setSubject("Your Claim #" + claim.incidentId() + " has been Approved");
            helper.setText("Hello,\n\nYour incident report has been reviewed and approved by our adjusters. Please find your official dossier attached.\n\nBest,\nAutopilot Claims");

            // Attach the in-memory PDF!
            helper.addAttachment("Claim_Dossier_" + claim.incidentId() + ".pdf", new ByteArrayResource(pdfAttachment));

            mailSender.send(message);
            System.out.println("Email successfully sent for Claim " + claim.incidentId());

        } catch (Exception e) {
            System.err.println("Failed to send email: " + e.getMessage());
        }
    }
}
