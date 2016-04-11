package com.theironyard.listeners;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionConnectedEvent;

/**
 * Created by PiratePowWow on 4/11/16.
 */
@Component
public class StompConnectedEvent implements ApplicationListener<SessionConnectedEvent> {

    private final Log logger = LogFactory.getLog(StompConnectedEvent.class);

    //@EventListener
    public void onApplicationEvent(SessionConnectedEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());

        System.out.println("Connected Event [sessionId:" + sha.getSessionId() + "]");
        logger.debug("Connected event [sessionId: " + sha.getSessionId() + "]");
    }
}

