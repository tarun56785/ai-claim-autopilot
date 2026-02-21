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
            You are an expert insurance adjuster. 
            Analyze the following incident report and extract the data strictly as requested.
            Determine the incidentType (e.g., COLLISION, WEATHER, THEFT), 
            severity (LOW, MEDIUM, HIGH), and an aiConfidenceScore (0-100).
            
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
