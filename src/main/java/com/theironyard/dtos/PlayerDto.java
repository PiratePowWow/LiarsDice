package com.theironyard.dtos;

import java.util.ArrayList;

/**
 * Created by PiratePowWow on 4/5/16.
 */
public class PlayerDto {
    private String name;
    private ArrayList<Integer> dice;
    private int seatNum;

    public PlayerDto(String name, ArrayList<Integer> dice, int seatNum) {
        this.name = name;
        this.dice = dice;
        this.seatNum = seatNum;
    }

    public PlayerDto(String name, ArrayList<Integer> dice) {
        this.name = name;
        this.dice = dice;
    }

    public PlayerDto() {
    }

    public int getSeatNum() {
        return seatNum;
    }

    public void setSeatNum(int seatNum) {
        this.seatNum = seatNum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Integer> getDice() {
        return dice;
    }

    public void setDice(ArrayList<Integer> dice) {
        this.dice = dice;
    }
}
