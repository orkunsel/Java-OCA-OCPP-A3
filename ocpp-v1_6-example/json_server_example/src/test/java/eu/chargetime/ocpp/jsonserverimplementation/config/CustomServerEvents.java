package eu.chargetime.ocpp.jsonserverimplementation.config;

import eu.chargetime.ocpp.ServerEvents;
import eu.chargetime.ocpp.model.SessionInformation;
import java.util.UUID;

public class CustomServerEvents implements ServerEvents {
    @Override
    public void newSession(UUID sessionIndex, SessionInformation information) {
        System.out.println("New session " + sessionIndex + ": " + information.getIdentifier());
    }

    @Override
    public void lostSession(UUID sessionIndex) {
        System.out.println("Session " + sessionIndex + " lost connection");
    }
}
