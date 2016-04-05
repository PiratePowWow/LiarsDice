package com.theironyard.utils;

import com.theironyard.dtos.Player;
import org.junit.Test;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Created by PiratePowWow on 4/5/16.
 */
public class GameLogicTest {

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
    public void testIsBluffing() throws Exception {
        ArrayList<Integer> allDice = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 4, 5, 6, 3));
        ArrayList<Integer> stake1 = new ArrayList<>(Arrays.asList(1, 3));
        ArrayList<Integer> stake2 = new ArrayList<>(Arrays.asList(4, 3));
        ArrayList<Integer> stake3 = new ArrayList<>(Arrays.asList(2, 6));
        ArrayList<Integer> stake4 = new ArrayList<>(Arrays.asList(3, 5));
        assertTrue(!GameLogic.isBluffing(stake1, allDice));
        assertTrue(GameLogic.isBluffing(stake2, allDice));
        assertTrue(!GameLogic.isBluffing(stake3, allDice));
        assertTrue(GameLogic.isBluffing(stake4, allDice));
    }

    @Test
    public void testIsValidRaise() throws Exception {
        ArrayList<Integer> oldStake = new ArrayList<Integer>();
        oldStake.add(2);
        oldStake.add(4);
        ArrayList<Integer> newStake = new ArrayList<Integer>();
        newStake.add(2);
        newStake.add(6);
        ArrayList<Integer> addtlStake = new ArrayList<>();
        addtlStake.add(3);
        addtlStake.add(3);
        assertTrue(GameLogic.isValidRaise(oldStake, newStake));
        assertTrue(!GameLogic.isValidRaise(newStake, oldStake));
        assertTrue(GameLogic.isValidRaise(oldStake, addtlStake));
    }

    @Test
    public void testNextActivePlayer() throws Exception {
        Player player1 = new Player("Bob", GameLogic.rollDice(), 1);
        Player player2 = new Player("Jim", GameLogic.rollDice(), 2);
        Player player3 = new Player("Tim", GameLogic.rollDice(), 3);
        Player previousPlayer = null;
        ArrayList<Player> allPlayers = new ArrayList<>();
        allPlayers.add(player1);
        allPlayers.add(player2);
        allPlayers.add(player3);
        assertTrue(GameLogic.nextActivePlayer(previousPlayer, allPlayers).getName().equals("Bob"));
        int i;
        for(i = 0; i < 5; i++){
            previousPlayer = GameLogic.nextActivePlayer(previousPlayer, allPlayers);
        }
        assertTrue(GameLogic.nextActivePlayer(previousPlayer, allPlayers).getName().equals("Tim"));

    }
}