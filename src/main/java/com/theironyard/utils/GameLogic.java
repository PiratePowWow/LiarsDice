package com.theironyard.utils;

import com.theironyard.entities.GameState;
import com.theironyard.entities.Player;
import com.theironyard.services.GameStateRepository;
import com.theironyard.services.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by PiratePowWow on 4/5/16.
 */
@Component
public class GameLogic {
    @Autowired
    GameStateRepository gameStates;
    @Autowired
    PlayerRepository players;

    public ArrayList<Integer> rollDice(){
        ArrayList<Integer> dice = new ArrayList<Integer>();
        int i;
        for (i = 0 ; i < 5 ; i++){
            Random r = new Random();
            dice.add(r.nextInt(6) + 1);
        }
        return dice;
    }

    public Player determineLoser(GameState gameState) {
        ArrayList<Integer> allDice = new ArrayList<>();
        ArrayList<Player> playersInGame = players.findByGameState(gameState);
        Player loser;
        for (Player player: playersInGame) {
            allDice.addAll(player.getDice());
        }
        ArrayList<Integer> stake = players.findOne(gameState.getLastPlayerId()).getStake();
        int count = 0;
        for (Integer die : allDice){
           if (die.equals(stake.get(1))){
               count += 1;
           }
        }
        if (count < stake.get(0)){
            loser = players.findOne(gameState.getLastPlayerId());
            loser.setScore(loser.getScore() + 1);
            players.save(loser);
            return loser;
        }
        loser = players.findOne(gameState.getActivePlayerId());
        loser.setScore(loser.getScore() + 1);
        players.save(loser);
        return loser;
    }

    public boolean isValidRaise(GameState gameState, ArrayList<Integer> newStake){
        ArrayList<Integer> oldStake = players.findOne(gameState.getLastPlayerId()).getStake();
        if (newStake != null && newStake.get(0) > 0 && newStake.get(1) > 0 && newStake.get(1) < 7 && newStake.size() == 2 && newStake.getClass().getTypeName().equals("java.util.ArrayList")) {
            if (oldStake == null) {
                return true;
            } else if (newStake.get(0) > oldStake.get(0)) {
                return true;
            } else if (newStake.get(0).equals(oldStake.get(0))  && newStake.get(1) > oldStake.get(1)) {
                return true;
            }else{
                return false;
            }
        }
        return false;
    }

    public void setNextActivePlayer(String roomCode) {
        GameState gameState = gameStates.findOne(roomCode);
        String activePlayer = gameState.getActivePlayerId();
        ArrayList<Player> playersInGame = players.findByGameStateOrderBySeatNum(gameState);
        if (activePlayer == null) {
            gameState.setActivePlayerId(players.findByGameStateOrderBySeatNum(gameState).get(0).getId());
            gameStates.save(gameState);
        }
        else{
            gameState.setLastPlayerId(gameState.getActivePlayerId());
            int nextIndex = playersInGame.indexOf(players.findOne(gameState.getLastPlayerId()));
            gameState.setActivePlayerId(players.findByGameStateOrderBySeatNum(gameState).get(nextIndex +1 >= players.findByGameStateOrderBySeatNum(gameState).size() ? 0 : nextIndex + 1).getId());
            gameStates.save(gameState);
            if(players.findOne(gameState.getActivePlayerId()).getDice() == null){
                setNextActivePlayer(roomCode);
            }
        }
    }

//credit stackoverflow user aquaraga
    public String makeRoomCode(){
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

    public void resetGameState(String roomCode) {
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

    public void createNewGame(String name, String id){
        String roomCode = makeRoomCode();
        gameStates.save(new GameState(roomCode));
        players.save(new Player(id, name, null, null, 0, 1, gameStates.findOne(roomCode)));
    }

    public void addPlayer(String name, String roomCode, String id) {
        GameState gameState = gameStates.findOne(roomCode);
        ArrayList<Player> playersInGame = players.findByGameStateOrderBySeatNum(gameState);
        players.save(new Player(id, name, null, null, 0, determineSeatNum(playersInGame), gameState));
    }

    public int determineSeatNum(ArrayList<Player> playersInGame){
        int seatNum = 0;
        int i = 1;
        for (Player player: playersInGame){
            if (player.getSeatNum() != i){
                break;
            }
            i++;
        }
        seatNum = i;
        return seatNum;
    }

    public void dropPlayer(String id){
        GameState gameState = players.findOne(id).getGameState();
        if(players.findByGameState(gameState).size() == 1){
            gameStates.delete(gameState.getRoomCode());
        }else{
            players.delete(id);
        }
    }

    public void setStake(String id, ArrayList<Integer> newStake) {
        Player player = players.findOne(id);
        player.setStake(newStake);
        players.save(player);
    }

    public boolean isActivePlayer(String id){
        return players.findOne(id).getGameState().getActivePlayerId().equals(id);
    }

    public boolean allDiceRolled(String roomCode){
        GameState gameState = gameStates.findOne(roomCode);
        if(players.findDiceByGameState(gameState).size() == players.findByGameState(gameState).size()){
            return true;
        }
        return false;
    }

    public void setDice(String id){
        Player player = players.findOne(id);
        player.setDice(rollDice());
        players.save(player);
    }
}
