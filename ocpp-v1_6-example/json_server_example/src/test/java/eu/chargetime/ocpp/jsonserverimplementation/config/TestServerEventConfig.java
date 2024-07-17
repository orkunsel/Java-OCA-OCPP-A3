package eu.chargetime.ocpp.jsonserverimplementation.config;

import eu.chargetime.ocpp.ServerEvents;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestServerEventConfig {

    @Bean
    public ServerEvents createServerCoreImpl() {
        return new CustomServerEvents();
    }
}
