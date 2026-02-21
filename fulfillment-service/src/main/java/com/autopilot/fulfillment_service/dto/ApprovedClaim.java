package com.autopilot.fulfillment_service.dto;

public record ApprovedClaim(
        Long incidentId,
        String incidentType,
        String severity,
        boolean policeReportMentioned,
        boolean injuriesReported,
        int aiConfidenceScore,
        String missingInformationChecklist,
        String status
) {}
