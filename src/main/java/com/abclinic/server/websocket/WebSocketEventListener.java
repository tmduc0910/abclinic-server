package com.abclinic.server.websocket;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

/**
 * @author tmduc
 * @package com.abclinic.server.websocket
 * @created 5/31/2020 10:39 AM
 */

@Component
public class WebSocketEventListener {

    @EventListener
    public void handleSessionSubscribeEvent(SessionSubscribeEvent event) {
        GenericMessage message = (GenericMessage) event.getMessage();
        String simpDestination = (String) message.getHeaders().get("simpDestination");

        if (simpDestination.startsWith("/topic/users")) {
            // do stuff
        }
    }
}