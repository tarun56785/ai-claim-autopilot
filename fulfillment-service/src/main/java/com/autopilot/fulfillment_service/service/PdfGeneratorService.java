package com.autopilot.fulfillment_service.service;

import com.autopilot.fulfillment_service.dto.ApprovedClaim;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class PdfGeneratorService {

    public byte[] generateClaimDossier(ApprovedClaim claim) {
        // We use a ByteArrayOutputStream to keep the PDF completely in memory!
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph("OFFICIAL CLAIM DOSSIER").setBold().setFontSize(24));
            document.add(new Paragraph("Status: " + claim.status()));
            document.add(new Paragraph("Incident ID: " + claim.incidentId()));
            document.add(new Paragraph("Incident Type: " + claim.incidentType()));
            document.add(new Paragraph("Severity: " + claim.severity()));
            document.add(new Paragraph("AI Confidence Score: " + claim.aiConfidenceScore() + "/100"));

            if (claim.injuriesReported()) {
                document.add(new Paragraph("MEDICAL REVIEW REQUIRED: Injuries were reported."));
            }
            document.close();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF", e);
        }
        return outputStream.toByteArray();
    }
}
