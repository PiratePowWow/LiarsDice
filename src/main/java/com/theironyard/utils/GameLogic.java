package com.theironyard.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.function.Predicate;

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
        System.out.println(dice);
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



    public static void main(String[] args) {
        ArrayList<Integer> stake = new ArrayList<Integer>();
        stake.add(1);
        stake.add(4);
        System.out.println(isBluffing(stake, rollDice()));

    }
}
