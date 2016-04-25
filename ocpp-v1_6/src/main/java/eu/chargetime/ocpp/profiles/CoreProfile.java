package eu.chargetime.ocpp.profiles;

import eu.chargetime.ocpp.model.AuthorizeRequest;
import eu.chargetime.ocpp.model.BootNotificationRequest;

/**
 * Created by Thomas Volden on 25-Apr-16.
 */
public class CoreProfile implements Profile
{
    ClientCoreEventHandler eventHandler;
    public CoreProfile(ClientCoreEventHandler handler) {
        eventHandler = handler;
    }

    public AuthorizeRequest createAuthorizeRequest(String idToken) {
        return new AuthorizeRequest(idToken);
    }

    public BootNotificationRequest createBootNotificationRequest(String vendor, String model) {
        return new BootNotificationRequest(vendor, model);
    }
}
