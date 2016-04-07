package com.theironyard.utils;

import com.sun.tools.javac.util.ArrayUtils;
import com.theironyard.dtos.PlayerDto;
import com.theironyard.entities.GameState;
import com.theironyard.entities.Player;
import com.theironyard.services.GameStateRepository;
import com.theironyard.services.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

/**
 * Created by PiratePowWow on 4/5/16.
 */
public class GameLogic {
    @Autowired
    static GameStateRepository gameStates;
    @Autowired
    static PlayerRepository players;

    public static ArrayList<Integer> rollDice(){
        ArrayList<Integer> dice = new ArrayList<Integer>();
        int i;
        for (i = 0 ; i < 5 ; i++){
            Random r = new Random();
            dice.add(r.nextInt(6) + 1);
        }
        return dice;
    }

    public static Player determineLoser(GameState gameState) {
        ArrayList<Integer> allDice = new ArrayList<>();
        ArrayList<Player> playersInGame = players.findByGameState(gameState);
        boolean isBluffing;
        for (Player player: playersInGame) {
            allDice.addAll(player.getDice());
        }
        ArrayList<Integer> stake = players.findOne(gameState.getLastPlayerId()).getStake();
        int count = 0;
        for (Integer die : allDice){
           if (die == stake.get(1)){
               count += 1;
           }
        }
        if (count < stake.get(0)){
            isBluffing = true;
            return players.findOne(gameState.getLastPlayerId());
        }
        return players.findOne(gameState.getActivePlayerId());
    }

    public static boolean isValidRaise(GameState gameState, ArrayList<Integer> newStake){
        ArrayList<Integer> oldStake = players.findOne(gameState.getLastPlayerId()).getStake();
        if (newStake != null && newStake.get(0) > 0 && newStake.get(1) > 0 && newStake.get(1) < 7 && newStake.size() == 2 && newStake.getClass().getTypeName().equals("java.util.ArrayList")) {
            if (oldStake == null) {
                return true;
            } else if (newStake.get(0) > oldStake.get(0)) {
                return true;
            } else if (newStake.get(0) == oldStake.get(0) && newStake.get(1) > oldStake.get(1)) {
                return true;
            }else{
                return false;
            }
        }
        return false;
    }

    public static void setNextActivePlayer(String roomCode) {
        GameState gameState = gameStates.findOne(roomCode);
        UUID activePlayer = gameState.getActivePlayerId();
        ArrayList<Player> playersInGame = players.findByGameStateOrderBySeatNum(gameState);
        if (activePlayer == null) {
            gameState.setActivePlayerId(players.findByGameStateOrderBySeatNum(gameState).get(0).getId());
            gameStates.save(gameState);
        }else if (activePlayer != null){
            gameState.setLastPlayerId(gameState.getActivePlayerId());
            int nextIndex = playersInGame.indexOf(players.findOne(gameState.getLastPlayerId()));
            gameState.setActivePlayerId(players.findByGameStateOrderBySeatNum(gameState).get(nextIndex +1 >= players.findByGameStateOrderBySeatNum(gameState).size() ? 0 : nextIndex + 1).getId());
            gameStates.save(gameState);
        }
    }

//credit stackoverflow user aquaraga
    public static String makeRoomCode(){
        boolean isPreExistingCode = true;
        String roomCode = "";
        while (isPreExistingCode) {
            String roomCodeChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
            StringBuilder code = new StringBuilder();
            Random rnd = new Random();
            while (code.length() < 4) {
                int index = (int) (rnd.nextFloat() * roomCodeChars.length());
                code.append(roomCodeChars.charAt(index));
            }
            roomCode = code.toString();
            if (gameStates.findOne(roomCode) == null) {
                isPreExistingCode = false;
            }
        }
        return roomCode;
    }

    public static void resetGameState(String roomCode) {
        GameState gameState = gameStates.findOne(roomCode);
        gameState.setActivePlayerId(null);
        gameState.setLastPlayerId(null);
        gameStates.save(gameState);
        ArrayList<Player> playersInGame = players.findByGameState(gameState);
        for (Player player : playersInGame) {
            player.setDice(null);
            player.setStake(null);
            players.save(player);
        }
    }

    public static void createNewGame(HttpSession session, String name){
        String roomCode = makeRoomCode();
        gameStates.save(new GameState(roomCode));
        UUID firstPlayerId = java.util.UUID.randomUUID();
        players.save(new Player(firstPlayerId, name, null, null, 0, 1, gameStates.findOne(roomCode)));
    }

    public static void addPlayer(HttpSession session, String name, String roomCode) {
        UUID playerId = java.util.UUID.randomUUID();
        GameState gameState = gameStates.findOne(roomCode);
        ArrayList<Player> playersInGame = players.findByGameStateOrderBySeatNum(gameState);
        players.save(new Player(playerId, name, null, null, 0, determineSeatNum(playersInGame), gameState));
    }

    public static int determineSeatNum(ArrayList<Player> playersInGame){
        int seatNum = 0;
        int i = 1;
        for (Player player: playersInGame){
            if (player.getSeatNum() != i){
                seatNum = i;
                break;
            }
            i++;
        }
        return seatNum;
    }

    public static void dropPlayer(){

    }
}
