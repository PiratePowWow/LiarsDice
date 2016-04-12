package com.theironyard.controllers;

import com.theironyard.dtos.PlayerDto;
import com.theironyard.entities.GameState;
import com.theironyard.entities.Player;
import com.theironyard.services.GameStateRepository;
import com.theironyard.services.PlayerRepository;
import com.theironyard.utils.GameLogic;
import org.h2.tools.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by PiratePowWow on 4/5/16.
 */
@Controller
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

//    @MessageMapping("/lobby/")
//    public ArrayList<Player> scoreboard(@DestinationVariable String roomCode){
//        return new ArrayList<Player>(players.findByGameStateOrderBySeatNum(gameStates.findOne(roomCode)));
//    }

    @MessageMapping("/lobby/rollDice")
    @SendTo("/topic/playerList")
    public ArrayList<PlayerDto> rollDice(String id) {
        Player playerRollingDice = players.findOne(id);
        GameState gameState = playerRollingDice.getGameState();
        if(playerRollingDice.getDice() == null){
            playerRollingDice.setDice(gameLogic.rollDice());
            players.save(playerRollingDice);
        }
        if (gameLogic.allDiceRolled(gameState.getRoomCode())){
            gameLogic.setNextActivePlayer(gameState.getRoomCode());
        }
        ArrayList<PlayerDto> playerDtos = new ArrayList<>();
        for (Player player: players.findByGameStateOrderBySeatNum(gameState)){
            playerDtos.add(new PlayerDto(player.getName(), gameState.getRoomCode(), player.getStake(), player.getSeatNum()));
        }
        return playerDtos;
    }

    @MessageMapping("/lobby/rollDice/{myPlayerId}")
    public Player myDice(@DestinationVariable String id) {
        return players.findOne(id);
    }


    @MessageMapping("/lobby/setStake")
    @SendTo("/topic/playerList")
    public ArrayList<Player> setStake(String id, ArrayList<Integer> dice) {
        Player playerSettingStake = players.findOne(id);
        GameState gameState = playerSettingStake.getGameState();
        if(gameLogic.isActivePlayer(id) && gameLogic.isValidRaise(gameState, dice)){
            playerSettingStake.setStake(dice);
            players.save(playerSettingStake);
            gameLogic.setNextActivePlayer(gameState.getRoomCode());
        }
        return players.findByGameStateOrderBySeatNum(playerSettingStake.getGameState());
    }

    @MessageMapping("/lobby/callBluff")
    @SendTo("/topic/loser")
    public Player callBluff(String id) {
        Player playerCallingBluff = players.findOne(id);
        GameState gameState = playerCallingBluff.getGameState();
        if(gameLogic.isActivePlayer(id)){
            Player loser = gameLogic.determineLoser(gameState);
            return loser;
        }
        return playerCallingBluff;
    }

    @MessageMapping("/lobby/resetGame")
    @SendTo("/topic/playerList")
    public ArrayList<Player> resetGame(String id){
        Player playerRequestingReset = players.findOne(id);
        GameState gameState = playerRequestingReset.getGameState();
        gameLogic.resetGameState(gameState.getRoomCode());
        GameState newGame = playerRequestingReset.getGameState();
        gameLogic.setNextActivePlayer(newGame.getRoomCode());
        return players.findByGameStateOrderBySeatNum(newGame);
    }




//    @RequestMapping(path = "/newGame", method = RequestMethod.POST)
//    public ResponseEntity<Object> newGame(String name){
//        return new ResponseEntity<Object>(name, HttpStatus.OK);
//    }




//    @MessageMapping("/hello")
//    @SendTo("/topic/greetings")
//    public Greeting greeting(HelloMessage message){
//        return new Greeting("Hello, " + message.getName() + "!");
//    }
}
