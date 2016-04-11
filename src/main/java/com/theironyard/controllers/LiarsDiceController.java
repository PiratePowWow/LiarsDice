package com.theironyard.controllers;

import com.theironyard.entities.Player;
import com.theironyard.services.GameStateRepository;
import com.theironyard.services.PlayerRepository;
import com.theironyard.utils.GameLogic;
import org.h2.tools.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by PiratePowWow on 4/5/16.
 */
@RestController
public class LiarsDiceController {
    @Autowired
    PlayerRepository players;
    @Autowired
    GameStateRepository gameStates;
    @Autowired
    GameLogic gameLogic;
    @PostConstruct
    public void init() throws SQLException {
        Server.createWebServer().start();
    }

    @MessageMapping("/lobby/{roomCode}")
    public ArrayList<Player> scoreboard(@DestinationVariable String roomCode){
        return new ArrayList<Player>(players.findByGameStateOrderBySeatNum(gameStates.findOne(roomCode)));
    }


//    @MessageMapping("/hello")
//    @SendTo("/topic/greetings")
//    public Greeting greeting(HelloMessage message){
//        return new Greeting("Hello, " + message.getName() + "!");
//    }
}
