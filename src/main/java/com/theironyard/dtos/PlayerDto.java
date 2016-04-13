package com.theironyard.dtos;

import java.util.ArrayList;

/**
 * Created by PiratePowWow on 4/5/16.
 */
public class PlayerDto {
    private String name;
    private String roomCode;
    private ArrayList<Integer> stake;
    private int seatNum;

    public PlayerDto(String name, String roomCode, ArrayList<Integer> stake, int seatNum) {
        this.name = name;
        this.roomCode = roomCode;
        this.stake = stake;
        this.seatNum = seatNum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRoomCode() {
        return roomCode;
    }

    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
    }

    public ArrayList<Integer> getStake() {
        return stake;
    }

    public void setStake(ArrayList<Integer> stake) {
        this.stake = stake;
    }

    public int getSeatNum() {
        return seatNum;
    }

    public void setSeatNum(int seatNum) {
        this.seatNum = seatNum;
    }
}
