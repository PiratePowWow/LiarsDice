package com.theironyard.listeners;

import com.theironyard.controllers.LiarsDiceController;
import com.theironyard.dtos.GameStateDto;
import com.theironyard.dtos.PlayersDto;
import com.theironyard.entities.GameState;
import com.theironyard.services.PlayerRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;

import java.util.HashMap;

/**
 * Created by PiratePowWow on 4/11/16.
 */
@Component
public class StompConnectedEvent implements ApplicationListener<SessionConnectedEvent> {
    @Autowired
    PlayerRepository players;

    private final Log logger = LogFactory.getLog(StompConnectedEvent.class);

    public void onApplicationEvent(SessionConnectedEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = sha.getSessionId();
//        GameState gameState = players.findOne(sessionId).getGameState();
//        PlayersDto playerDtos = new PlayersDto(players.findByGameStateOrderBySeatNum(gameState));
//        HashMap playerListAndGameState = new HashMap();
//        playerListAndGameState.put("playerList", playerDtos);
//        playerListAndGameState.put("gameState", new GameStateDto(gameState, players));
//        LiarsDiceController.messenger.convertAndSend("/topic/playerList", playerListAndGameState);

        System.out.println("Connected Event [sessionId:" + sha.getSessionId() + "]");
        logger.debug("Connected event [sessionId: " + sha.getSessionId() + "]");
    }

//    public void onApplicationEvent(SessionConnectedEvent event) {
//        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
//        String sessionId = sha.getSessionId();
//        GameState gameState = players.findOne(sessionId).getGameState();
//        PlayersDto playerDtos = new PlayersDto(players.findByGameStateOrderBySeatNum(gameState));
//        HashMap playerListAndGameState = new HashMap();
//        playerListAndGameState.put("playerList", playerDtos);
//        playerListAndGameState.put("gameState", new GameStateDto(gameState, players));
//        LiarsDiceController.messenger.convertAndSend("/topic/playerList", playerListAndGameState);
//
//        System.out.println("Connected Event [sessionId:" + sha.getSessionId() + "]");
//        logger.debug("Connected event [sessionId: " + sha.getSessionId() + "]");
//    }
}

