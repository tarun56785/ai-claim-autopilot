package com.autopilot.ai_triage_service.dto;

public record ClaimExtraction(
        String incidentType,
        String severity,
        boolean policeReportMentioned,
        boolean injuriesReported,
        int aiConfidenceScore,
        String missingInformationChecklist
) {}