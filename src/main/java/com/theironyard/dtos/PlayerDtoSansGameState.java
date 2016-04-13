package com.theironyard.dtos;

import com.theironyard.entities.Player;

import java.util.ArrayList;

/**
 * Created by PiratePowWow on 4/12/16.
 */
public class PlayerDtoSansGameState {
    private String id;
    private String name;
    private String roomCode;
    private ArrayList<Integer> dice;
    private ArrayList<Integer> stake;
    private int score;
    private int seatNum;

    public PlayerDtoSansGameState(Player player) {
        this.id = player.getId();
        this.name = player.getName();
        this.roomCode = player.getGameState().getRoomCode();
        this.dice = player.getDice();
        this.stake = player.getStake();
        this.score = player.getScore();
        this.seatNum = player.getSeatNum();
    }
}
