package com.theironyard.utils;

import com.theironyard.dtos.PlayerDto;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by PiratePowWow on 4/5/16.
 */
public class GameLogic {

    public static ArrayList<Integer> rollDice(){
        ArrayList<Integer> dice = new ArrayList<Integer>();
        int i;
        for (i = 0 ; i < 5 ; i++){
            Random r = new Random();
            dice.add(r.nextInt(6) + 1);
        }
        return dice;
    }

    public static boolean isBluffing(ArrayList<Integer> stake, ArrayList<Integer> allDice) {
        int count = 0;
        for (Integer die : allDice){
           if (die == stake.get(1)){
               count += 1;
           }
        }
        if (count < stake.get(0)){
            return true;
        }
        return false;
    }

    public static boolean isValidRaise(ArrayList<Integer> oldStake, ArrayList<Integer> newStake){
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

    public static PlayerDto nextActivePlayer(PlayerDto previousPlayer, ArrayList<PlayerDto> allPlayersSortedBySeatNum) {
        if (previousPlayer == null) {
            return allPlayersSortedBySeatNum.get(0);
        }
        int nextIndex = allPlayersSortedBySeatNum.indexOf(previousPlayer);
        return allPlayersSortedBySeatNum.get(nextIndex +1 >= allPlayersSortedBySeatNum.size() ? 0 : nextIndex + 1);
    }

    public static PlayerDto determineLoser(PlayerDto lastActivePlayer, PlayerDto playerCallingBluff, ArrayList<Integer> stake, ArrayList<Integer> allDice) {
        return (isBluffing(stake, allDice) ? lastActivePlayer : playerCallingBluff);
    }

}
