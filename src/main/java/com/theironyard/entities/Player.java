package com.theironyard.entities;

import org.hibernate.annotations.Type;
import org.springframework.http.ResponseEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by PiratePowWow on 4/5/16.
 */
@Entity
@Table(name = "players")
public class Player {
    @Id
    @Type(type="uuid-char")
    private UUID id;
    @NotNull
    private String name;
    private ArrayList<Integer> dice;
    private ArrayList<Integer> stake;
    private int score;
    @NotNull
    private int seatNum;
    @ManyToOne
    GameState gameState;

    public Player(UUID id, String name, ArrayList<Integer> dice, ArrayList<Integer> stake, int score, int seatNum, GameState gameState) {
        this.id = id;
        this.name = name;
        this.dice = dice;
        this.stake = stake;
        this.score = score;
        this.seatNum = seatNum;
        this.gameState = gameState;
    }

    public Player(UUID id, String name, ArrayList<Integer> dice, ArrayList<Integer> stake, int score, int seatNum) {
        this.id = id;
        this.name = name;
        this.dice = dice;
        this.stake = stake;
        this.score = score;
        this.seatNum = seatNum;
    }

    public Player(UUID id, String name, int seatNum) {
        this.id = id;
        this.name = name;
        this.seatNum = seatNum;
    }



    public Player() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public ArrayList<Integer> getStake() {
        return stake;
    }

    public void setStake(ArrayList<Integer> stake) {
        this.stake = stake;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getSeatNum() {
        return seatNum;
    }

    public void setSeatNum(int seatNum) {
        this.seatNum = seatNum;
    }
}
