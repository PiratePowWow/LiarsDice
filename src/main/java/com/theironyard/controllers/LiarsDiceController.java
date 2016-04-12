package com.theironyard.controllers;

import com.theironyard.dtos.PlayerDto;
import com.theironyard.entities.GameState;
import com.theironyard.entities.Player;
import com.theironyard.services.GameStateRepository;
import com.theironyard.services.PlayerRepository;
import com.theironyard.utils.GameLogic;
import org.h2.tools.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;


import javax.annotation.PostConstruct;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by PiratePowWow on 4/5/16.
 */
@Controller
public class LiarsDiceController {
    public static SimpMessagingTemplate messenger;

    @Autowired
    public LiarsDiceController(SimpMessagingTemplate template) {
        messenger = template;
    }

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

    @MessageMapping("/lobby/{myPlayerId}")
    public Player myPlayer(@DestinationVariable String myPlayerId) {
        return players.findOne(myPlayerId);
    }

    @MessageMapping("/lobby/rollDice")
    @SendTo("/topic/playerList")
    public HashMap rollDice(String id) {
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
        HashMap playerListAndGameState = new HashMap();
        playerListAndGameState.put("playerList", playerDtos);
        playerListAndGameState.put("gameState", gameStates.findOne(gameState.getRoomCode()));
        return playerListAndGameState;
    }

    @MessageMapping("/lobby/setStake")
    @SendTo("/topic/playerList")
    public HashMap setStake(String id, ArrayList<Integer> dice) {
        Player playerSettingStake = players.findOne(id);
        GameState gameState = playerSettingStake.getGameState();
        if(gameLogic.isActivePlayer(id) && gameLogic.isValidRaise(gameState, dice)){
            playerSettingStake.setStake(dice);
            players.save(playerSettingStake);
            gameLogic.setNextActivePlayer(gameState.getRoomCode());
        }
        ArrayList<PlayerDto> playerDtos = new ArrayList<>();
        for (Player player: players.findByGameStateOrderBySeatNum(gameState)){
            playerDtos.add(new PlayerDto(player.getName(), gameState.getRoomCode(), player.getStake(), player.getSeatNum()));
        }
        HashMap playerListAndGameState = new HashMap();
        playerListAndGameState.put("playerList", playerDtos);
        playerListAndGameState.put("gameState", gameStates.findOne(gameState.getRoomCode()));
        return playerListAndGameState;
    }

    @MessageMapping("/lobby/resetGame")
    @SendTo("/topic/playerList")
    public HashMap resetGame(String id){
        Player playerRequestingReset = players.findOne(id);
        GameState gameState = playerRequestingReset.getGameState();
        gameLogic.resetGameState(gameState.getRoomCode());
        GameState newGame = playerRequestingReset.getGameState();
        gameLogic.setNextActivePlayer(newGame.getRoomCode());
        ArrayList<PlayerDto> playerDtos = new ArrayList<>();
        for (Player player: players.findByGameStateOrderBySeatNum(newGame)){
            playerDtos.add(new PlayerDto(player.getName(), gameState.getRoomCode(), player.getStake(), player.getSeatNum()));
        }
        HashMap playerListAndGameState = new HashMap();
        playerListAndGameState.put("playerList", playerDtos);
        playerListAndGameState.put("gameState", gameStates.findOne(gameState.getRoomCode()));
        return playerListAndGameState;
    }

    @MessageMapping("/lobby/callBluff")
    @SendTo("/topic/loser")
    public PlayerDto callBluff(String id) {
        Player playerCallingBluff = players.findOne(id);
        GameState gameState = playerCallingBluff.getGameState();
        if(gameLogic.isActivePlayer(id)){
            Player loser = gameLogic.determineLoser(gameState);
            PlayerDto loserDto = new PlayerDto(loser.getName(), gameState.getRoomCode(), loser.getStake(), loser.getSeatNum());
            return loserDto;
        }

        return new PlayerDto(playerCallingBluff.getName(), gameState.getRoomCode(), playerCallingBluff.getStake(), playerCallingBluff.getSeatNum());
    }

//    @MessageMapping("/lobby/")
//    public ArrayList<Player> scoreboard(@DestinationVariable String roomCode){
//        return new ArrayList<Player>(players.findByGameStateOrderBySeatNum(gameStates.findOne(roomCode)));
//    }



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
