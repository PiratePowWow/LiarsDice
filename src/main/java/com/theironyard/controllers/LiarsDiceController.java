package com.theironyard.controllers;

import com.theironyard.dtos.GameStateDto;
import com.theironyard.dtos.PlayerDto;
import com.theironyard.dtos.PlayerDtoSansGameState;
import com.theironyard.dtos.PlayersDto;
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

    /**
     *
     * @param myPlayerId
     * @return
     */
    @MessageMapping("/lobby/{myPlayerId}")
    public PlayerDtoSansGameState myPlayer(@DestinationVariable String myPlayerId) {
        return new PlayerDtoSansGameState(players.findOne(myPlayerId));
    }

    /**
     *
     * @param id
     * @return
     */
    @MessageMapping("/lobby/rollDice")
    @SendTo("/topic/playerList")
    public HashMap rollDice(String id) {
        Player playerRollingDice = players.findOne(id);
        GameState gameState = playerRollingDice.getGameState();
        String roomCode = gameState.getRoomCode();
        if(playerRollingDice.getDice() == null){
            playerRollingDice.setDice(gameLogic.rollDice());
            players.save(playerRollingDice);
        }
        if (gameLogic.allDiceRolled(roomCode)){
            gameLogic.setNextActivePlayer(roomCode);
        }
        PlayersDto playerDtos = new PlayersDto(players.findByGameStateOrderBySeatNum(gameState));
        HashMap playerListAndGameState = new HashMap();
        playerListAndGameState.put("playerList", playerDtos);
        playerListAndGameState.put("gameState", new GameStateDto(gameStates.findOne(roomCode)));
        return playerListAndGameState;
    }

    /**
     *
     * @param id
     * @param newStake
     * @return
     */
    @MessageMapping("/lobby/setStake")
    @SendTo("/topic/playerList")
    public HashMap setStake(String id, ArrayList<Integer> newStake) {
        Player playerSettingStake = players.findOne(id);
        GameState gameState = playerSettingStake.getGameState();
        String roomCode = gameState.getRoomCode();
        if (playerSettingStake.getDice() == null){
            gameLogic.setNextActivePlayer(roomCode);
            PlayersDto playerDtos = new PlayersDto(players.findByGameStateOrderBySeatNum(gameState));
            HashMap playerListAndGameState = new HashMap();
            playerListAndGameState.put("playerList", playerDtos);
            playerListAndGameState.put("gameState", new GameStateDto(gameStates.findOne(roomCode)));
            return playerListAndGameState;
        }
        if(gameLogic.isActivePlayer(id) && gameLogic.isValidRaise(gameState, newStake)){
            playerSettingStake.setStake(newStake);
            players.save(playerSettingStake);
            gameLogic.setNextActivePlayer(roomCode);
        }
        PlayersDto playerDtos = new PlayersDto(players.findByGameStateOrderBySeatNum(gameState));
        HashMap playerListAndGameState = new HashMap();
        playerListAndGameState.put("playerList", playerDtos);
        playerListAndGameState.put("gameState", new GameStateDto(gameStates.findOne(roomCode)));
        return playerListAndGameState;
    }

    /**
     *
     * @param id
     * @return
     */
    @MessageMapping("/lobby/resetGame")
    @SendTo("/topic/playerList")
    public HashMap resetGame(String id){
        Player playerRequestingReset = players.findOne(id);
        GameState gameState = playerRequestingReset.getGameState();
        String roomCode = gameState.getRoomCode();
        gameLogic.resetGameState(roomCode);
        GameState newGame = playerRequestingReset.getGameState();
        gameLogic.setNextActivePlayer(roomCode);
        PlayersDto playerDtos = new PlayersDto(players.findByGameStateOrderBySeatNum(newGame));
        HashMap playerListAndGameState = new HashMap();
        playerListAndGameState.put("playerList", playerDtos);
        playerListAndGameState.put("gameState", new GameStateDto(gameStates.findOne(roomCode)));
        return playerListAndGameState;
    }

    /**
     *
     * @param id
     * @return
     */
    @MessageMapping("/lobby/callBluff")
    @SendTo("/topic/loser")
    public PlayerDto callBluff(String id) {
        Player playerCallingBluff = players.findOne(id);
        GameState gameState = playerCallingBluff.getGameState();
        String roomCode = gameState.getRoomCode();
        PlayerDto loserDto;
        if(gameLogic.isActivePlayer(id)){
            Player loser = gameLogic.determineLoser(gameState);
            loserDto = new PlayerDto(loser.getName(), roomCode, loser.getStake(), loser.getSeatNum(), loser.getScore(), loser.getDice() != null);
            return loserDto;
        }
        loserDto = new PlayerDto(playerCallingBluff.getName(), roomCode, playerCallingBluff.getStake(), playerCallingBluff.getSeatNum(), playerCallingBluff.getScore(), playerCallingBluff.getDice() != null);
        return loserDto;
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
