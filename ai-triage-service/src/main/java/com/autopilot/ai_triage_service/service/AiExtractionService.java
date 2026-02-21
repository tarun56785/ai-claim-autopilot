package com.autopilot.ai_triage_service.service;

import com.autopilot.ai_triage_service.dto.ClaimExtraction;
import lombok.AllArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.stereotype.Service;

@Service
public class AiExtractionService {

    private final ChatClient chatClient;

    public AiExtractionService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public ClaimExtraction analyzeIncident (String rawDescription){
        // 1. Tell Spring AI what shape we want the data in
        var outputConverter = new BeanOutputConverter<>(ClaimExtraction.class);
        String formatInstructions = outputConverter.getFormat();

        // 2. Write our prompt to the LLM
        String prompt = """
            You are an expert, highly critical insurance adjuster. 
            Analyze the following incident report and extract the data strictly as requested.
            
            CRITICAL INSTRUCTIONS:
            1. If the incident report is empty, contains garbage text, or is NOT related to a vehicle/property incident, you MUST set incidentType to 'UNKNOWN', severity to 'NONE', and aiConfidenceScore to 0.
            2. Do not guess or assume. If information is missing, use the missingInformationChecklist to ask for it.
            
            Incident Report: %s
            
            %s
            """.formatted(rawDescription, formatInstructions);
        // 3. Call Ollama (Qwen)
        String aiResponse = chatClient.prompt()
                .user(prompt)
                .call()
                .content();

        return outputConverter.convert(aiResponse);
    }
}
