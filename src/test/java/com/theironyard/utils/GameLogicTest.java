package com.theironyard.utils;

import com.theironyard.LiarsDiceApplication;
import com.theironyard.entities.GameState;
import com.theironyard.entities.Player;
import com.theironyard.services.GameStateRepository;
import com.theironyard.services.PlayerRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Created by PiratePowWow on 4/5/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = LiarsDiceApplication.class)
@WebAppConfiguration
public class GameLogicTest {
    @Autowired
    GameStateRepository gameStates;
    @Autowired
    PlayerRepository players;


    @Test
    public void testRollDice() throws Exception {
        ArrayList<Integer> dice = GameLogic.rollDice();
        int count = 0;
        for(Integer die : dice){
            if (die < 7 && die > 0){
                count ++;
            }
        }
        assertTrue(count == 5);
    }

    @Test
    public void testIsValidRaise() throws Exception {
        GameState newGame = new GameState(GameLogic.makeRoomCode(gameStates));
        Player bob = new Player(java.util.UUID.randomUUID(), "Bob", GameLogic.rollDice(), new ArrayList<Integer>(Arrays.asList(3, 4)), 1, 2, newGame);
        Player tim = new Player(java.util.UUID.randomUUID(), "Tim", GameLogic.rollDice(), new ArrayList<Integer>(Arrays.asList(5, 4)), 1, 2, newGame);
        newGame.setLastPlayerId(bob.getId());
        newGame.setActivePlayerId(tim.getId());
        gameStates.save(newGame);
        players.save(bob);
        players.save(tim);
        ArrayList<Integer> allDice = new ArrayList<>();
        ArrayList<Player> playersInGame = players.findByGameState(newGame);
        for (Player player: playersInGame) {
            allDice.addAll(player.getDice());
        }
        assertTrue(!GameLogic.isValidRaise(newGame, bob.getStake(), players));
        assertTrue(GameLogic.isValidRaise(newGame, tim.getStake(), players));
        players.deleteAll();
        gameStates.deleteAll();
    }

    @Test
    public void testSetNextActivePlayer() throws Exception {
        GameState newGame = new GameState(GameLogic.makeRoomCode(gameStates));
        Player bob = new Player(java.util.UUID.randomUUID(), "Bob", GameLogic.rollDice(), new ArrayList<Integer>(Arrays.asList(3, 4)), 1, 2, newGame);
        Player tim = new Player(java.util.UUID.randomUUID(), "Tim", GameLogic.rollDice(), new ArrayList<Integer>(Arrays.asList(5, 4)), 1, 3, newGame);
        newGame.setLastPlayerId(bob.getId());
        newGame.setActivePlayerId(tim.getId());
        gameStates.save(newGame);
        players.save(bob);
        players.save(tim);
        GameLogic.setNextActivePlayer(newGame.getActivePlayerId(), players, gameStates);
        assertTrue(gameStates.findOne(newGame.getRoomCode()).getActivePlayerId() == bob.getId());
        Player previousPlayerId = players.findOne(gameStates.findOne(newGame.getRoomCode()).getLastPlayerId());
        int i;
        for(i = 0; i < 5; i++){
            GameLogic.setNextActivePlayer(gameStates.findOne(newGame.getRoomCode()).getActivePlayerId(), players, gameStates);
        }
        assertTrue(players.findOne(gameStates.findOne(newGame.getRoomCode()).getActivePlayerId()).getName().equals("Tim"));
        players.deleteAll();
        gameStates.deleteAll();

    }

    @Test
    public void testDetermineLoser() throws Exception {
        GameState newGame = new GameState(GameLogic.makeRoomCode(gameStates));
        Player bob = new Player(java.util.UUID.randomUUID(), "Bob", new ArrayList<Integer>(Arrays.asList(3, 4, 5, 2, 6)), new ArrayList<Integer>(Arrays.asList(3, 4)), 1, 2, newGame);
        Player tim = new Player(java.util.UUID.randomUUID(), "Tim", new ArrayList<Integer>(Arrays.asList(3, 4, 5, 5, 5)), new ArrayList<Integer>(Arrays.asList(5, 4)), 1, 3, newGame);
        newGame.setLastPlayerId(bob.getId());
        newGame.setActivePlayerId(tim.getId());
        gameStates.save(newGame);
        players.save(bob);
        players.save(tim);
        assertTrue(GameLogic.determineLoser(newGame, players).getName().equals("bob") );
        players.deleteAll();
        gameStates.deleteAll();
    }

    @Test
    public void testMakeRoomCode() throws Exception {
        int i;
        boolean uniqueCode = false;
        for (i = 0; i < 1000; i++) {
            String newRoomCode = GameLogic.makeRoomCode(gameStates);
            assertTrue(newRoomCode.length() == 4);
            if (gameStates.findOne(newRoomCode) != null){
                uniqueCode = false;
                break;
            }else{
                GameState newGame = new GameState(newRoomCode);
                Player bob = new Player(java.util.UUID.randomUUID(), "Bob", GameLogic.rollDice(), new ArrayList<Integer>(Arrays.asList(3, 4)), 1, 2, newGame);
                Player tim = new Player(java.util.UUID.randomUUID(), "Tim", GameLogic.rollDice(), new ArrayList<Integer>(Arrays.asList(5, 4)), 1, 2, newGame);
                newGame.setLastPlayerId(bob.getId());
                newGame.setActivePlayerId(tim.getId());
                gameStates.save(newGame);
                players.save(bob);
                players.save(tim);
                uniqueCode = true;
            }
        }
        assertTrue(uniqueCode);
        players.deleteAll();
        gameStates.deleteAll();
    }

    @Test
    public void testResetGameState() throws Exception {
        String roomCode = "XXXX";
        GameState gameState = new GameState(roomCode);
        Player bob = new Player(java.util.UUID.randomUUID(), "Bob", GameLogic.rollDice(), new ArrayList<Integer>(Arrays.asList(3, 4)), 1, 2, gameState);
        Player tim = new Player(java.util.UUID.randomUUID(), "Tim", GameLogic.rollDice(), new ArrayList<Integer>(Arrays.asList(5, 4)), 1, 2, gameState);
        gameState.setActivePlayerId(bob.getId());
        gameState.setLastPlayerId(tim.getId());
        gameStates.save(gameState);
        players.save(bob);
        players.save(tim);
        ArrayList<Player> playerList = (ArrayList<Player>) players.findAll();
        GameState oldGame = gameStates.findOne(roomCode);
        assertTrue(oldGame.getRoomCode().equals(roomCode));
        assertTrue(playerList.size() == 2);
        GameLogic.resetGameState(gameStates, players, roomCode);
        assertTrue(gameStates.findOne(roomCode).getActivePlayerId() == null);
        assertTrue(((ArrayList<Player>) players.findAll()).get(0).getDice() == null);
        assertTrue(((ArrayList<Player>) players.findAll()).get(1).getStake() == null);
        players.deleteAll();
        gameStates.deleteAll();
    }
}