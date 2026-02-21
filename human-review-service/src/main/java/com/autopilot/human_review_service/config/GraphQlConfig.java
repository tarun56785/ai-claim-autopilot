package com.autopilot.human_review_service.config;

import com.autopilot.human_review_service.model.ClaimRecord;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Sinks;

@Configuration
public class GraphQlConfig {

    // This creates our shared funnel. "multicast" means if 5 adjusters are
    // logged in, they will all receive the broadcast!
    @Bean
    public Sinks.Many<ClaimRecord> claimRecordSink() {
        return Sinks.many().multicast().onBackpressureBuffer();
    }
}
