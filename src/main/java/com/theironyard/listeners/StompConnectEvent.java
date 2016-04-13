package com.theironyard.listeners;

import com.theironyard.controllers.LiarsDiceController;
import com.theironyard.dtos.GameStateDto;
import com.theironyard.dtos.PlayersDto;
import com.theironyard.entities.GameState;
import com.theironyard.services.GameStateRepository;
import com.theironyard.services.PlayerRepository;
import com.theironyard.utils.GameLogic;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;

import java.util.HashMap;

/**
 * Created by PiratePowWow on 4/11/16.
 */
@Component
public class StompConnectEvent implements ApplicationListener<SessionConnectEvent> {
    @Autowired
    PlayerRepository players;
    @Autowired
    GameStateRepository gameStates;
    @Autowired
    GameLogic gameLogic;

    private final Log logger = LogFactory.getLog(StompConnectEvent.class);

    @Override
    public void onApplicationEvent(SessionConnectEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = sha.getSessionId();
        String roomCode = sha.getNativeHeader("roomCode").get(0);
        String  name = sha.getNativeHeader("name").get(0);
        if(roomCode.equals("undefined")){
            gameLogic.createNewGame(name, sessionId);
            GameState gameState = players.findOne(sessionId).getGameState();
            PlayersDto playerDtos = new PlayersDto(players.findByGameStateOrderBySeatNum(gameState));
            HashMap playerListAndGameState = new HashMap();
            playerListAndGameState.put("playerList", playerDtos);
            playerListAndGameState.put("gameState", new GameStateDto(gameState, players));
            LiarsDiceController.messenger.convertAndSend("/topic/playerList", playerListAndGameState);
        }else {
            gameLogic.addPlayer(name, roomCode, sessionId);
            GameState gameState = gameStates.findOne(roomCode);
            PlayersDto playerDtos = new PlayersDto(players.findByGameStateOrderBySeatNum(gameState));
            HashMap playerListAndGameState = new HashMap();
            playerListAndGameState.put("playerList", playerDtos);
            playerListAndGameState.put("gameState", new GameStateDto(gameState, players));
            LiarsDiceController.messenger.convertAndSend("/topic/playerList", playerListAndGameState);
        }
        System.out.println("Connect event [sessionId: " + sessionId +"; name: "+ name + " ]");
        logger.debug("Connect event [sessionId: " + sessionId +"; name: "+ name + " ]");
    }
}
