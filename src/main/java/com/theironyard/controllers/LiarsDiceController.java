package com.theironyard.controllers;

import com.theironyard.dtos.*;
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
import org.springframework.web.bind.annotation.RequestParam;


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
        PlayerDtoSansGameState player = new PlayerDtoSansGameState(players.findOne(myPlayerId));
        if (player == null){
            messenger.convertAndSend("/topic/lobby/error/" + myPlayerId, "Your player Id could Not be found");
        }
        System.out.println("Returning Player Object");
        return  player;
    }

    /**
     *
     * @param id
     * @return
     */
    @MessageMapping("/lobby/JoinGame")
    @SendTo("/topic/playerList")
    public HashMap joinGame(String id) {
        Player playerJoiningGame = players.findOne(id);
        if (playerJoiningGame == null){
            messenger.convertAndSend("/topic/lobby/error/" + id, "Your player Id could Not be found");
        }
        GameState gameState = playerJoiningGame.getGameState();
        String roomCode = gameState.getRoomCode();
        PlayersDto playerDtos = new PlayersDto(players.findByGameStateOrderBySeatNum(gameState));
        HashMap playerListAndGameState = new HashMap();
        playerListAndGameState.put("playerList", playerDtos);
        playerListAndGameState.put("gameState", new GameStateDto(gameStates.findOne(roomCode), players));
        System.out.println("Joining Game");
        return playerListAndGameState;
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
        if (playerRollingDice == null){
            messenger.convertAndSend("/topic/lobby/error/" + id, "Your player Id could Not be found");
        }
        GameState gameState = playerRollingDice.getGameState();
        String roomCode = gameState.getRoomCode();
        if(playerRollingDice.getDice() == null){
            playerRollingDice.setDice(gameLogic.rollDice());
            players.save(playerRollingDice);
        }else{
            messenger.convertAndSend("/topic/lobby/error/" + id, "You already have dice. Please reset the game if you wish to get new dice");
        }
        if (gameLogic.allDiceRolled(roomCode) && players.findOne(gameState.getLoserId()) == null){
            gameLogic.setNextActivePlayer(roomCode);
        }else if(gameLogic.allDiceRolled(roomCode)){
            gameState.setActivePlayerId(gameState.getLoserId());
            gameStates.save(gameState);
        }
        PlayersDto playerDtos = new PlayersDto(players.findByGameStateOrderBySeatNum(gameState));
        HashMap playerListAndGameState = new HashMap();
        playerListAndGameState.put("playerList", playerDtos);
        playerListAndGameState.put("gameState", new GameStateDto(gameStates.findOne(roomCode), players));
        messenger.convertAndSend("/topic/lobby/" + id, new PlayerDtoSansGameState(playerRollingDice));
        System.out.println("Rolling Dice");
        return playerListAndGameState;
    }

    /**
     *
     * @param stake
     * @return
     */
    @MessageMapping("/lobby/setStake")
    @SendTo("/topic/playerList")
    public HashMap setStake(HashMap<String, Object> stake) throws InterruptedException {
        String id = (String) stake.get("playerId");
        ArrayList<Integer> newStake = (ArrayList<Integer>) stake.get("newStake");
        Player playerSettingStake = players.findOne(id);
        if (playerSettingStake == null){
            messenger.convertAndSend("/topic/lobby/error/" + id, "Your player Id could Not be found");
        }
        GameState gameState = playerSettingStake.getGameState();
        String roomCode = gameState.getRoomCode();
        if (playerSettingStake.getDice() == null){
            gameLogic.setNextActivePlayer(roomCode);
            PlayersDto playerDtos = new PlayersDto(players.findByGameStateOrderBySeatNum(gameState));
            HashMap playerListAndGameState = new HashMap();
            playerListAndGameState.put("playerList", playerDtos);
            playerListAndGameState.put("gameState", new GameStateDto(gameStates.findOne(roomCode), players));
            messenger.convertAndSend("/topic/lobby/error/" + id, "You must have Dice in order to raise the stake, your turn is being skipped");
            System.out.println("Setting Stake");
            return playerListAndGameState;
        }
        if(gameLogic.isActivePlayer(id) && gameLogic.isValidRaise(gameState, newStake)){
            playerSettingStake.setStake(newStake);
            players.save(playerSettingStake);
            gameLogic.setNextActivePlayer(roomCode);
        }else if (gameLogic.isActivePlayer(id)){
            messenger.convertAndSend("/topic/lobby/error/" + id, "You are the active player, but you did not submit a valid stake");
        }else if(gameLogic.isValidRaise(gameState, newStake)){
            messenger.convertAndSend("/topic/lobby/error/" + id, "The stake is valid, but you are not the active player");
        }
        PlayersDto playerDtos = new PlayersDto(players.findByGameStateOrderBySeatNum(gameState));
        HashMap playerListAndGameState = new HashMap();
        playerListAndGameState.put("playerList", playerDtos);
        playerListAndGameState.put("gameState", new GameStateDto(gameStates.findOne(roomCode), players));
        messenger.convertAndSend("/topic/lobby/" + id, new PlayerDtoSansGameState(playerSettingStake));
        System.out.println("Setting Stake");
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
        if (playerRequestingReset == null){
            messenger.convertAndSend("/topic/lobby/error/" + id, "Your player Id could Not be found");
        }
        GameState gameState = playerRequestingReset.getGameState();
        String roomCode = gameState.getRoomCode();
        gameLogic.resetGameState(roomCode);
        GameState newGame = playerRequestingReset.getGameState();
        //gameLogic.setNextActivePlayer(roomCode);
        PlayersDto playerDtos = new PlayersDto(players.findByGameStateOrderBySeatNum(newGame));
        HashMap playerListAndGameState = new HashMap();
        playerListAndGameState.put("playerList", playerDtos);
        playerListAndGameState.put("gameState", new GameStateDto(gameStates.findOne(roomCode), players));
        System.out.println("Resetting Game");
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
        if (playerCallingBluff == null){
            messenger.convertAndSend("/topic/lobby/error/" + id, "Your player Id could Not be found");
        }
        GameState gameState = playerCallingBluff.getGameState();
        String roomCode = gameState.getRoomCode();
        PlayerDto loserDto;
        if(gameLogic.isActivePlayer(id)){
            Player loser = gameLogic.determineLoser(gameState);
            gameState.setLoserId(loser.getId());
            gameStates.save(gameState);
            loserDto = new PlayerDto(loser);
            return loserDto;
        }
        gameState.setLoserId(playerCallingBluff.getId());
        gameStates.save(gameState);
        loserDto = new PlayerDto(playerCallingBluff);
        return loserDto;
    }
}
