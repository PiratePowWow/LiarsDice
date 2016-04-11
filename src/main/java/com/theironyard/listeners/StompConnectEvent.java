package com.theironyard.listeners;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.socket.messaging.SessionConnectEvent;

/**
 * Created by PiratePowWow on 4/11/16.
 */
public class StompConnectEvent implements ApplicationListener<SessionConnectEvent> {

    private final Log logger = LogFactory.getLog(StompConnectEvent.class);

    public void onApplicationEvent(SessionConnectEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());

        String  name = sha.getNativeHeader("name").get(0);
        logger.debug("Connect event [sessionId: " + sha.getSessionId() +"; name: "+ name + " ]");
    }
}
