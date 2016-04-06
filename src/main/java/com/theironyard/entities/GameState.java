package com.theironyard.entities;

import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by PiratePowWow on 4/5/16.
 */
@Entity
@Table(name = "gamestates")
public class GameState {
    @Id
    private String roomCode;
    @OneToOne(mappedBy = "gameState")
    private Player activePlayer;
    @OneToOne(mappedBy = "gameState")
    private Player lastPlayer;
    @OneToMany(mappedBy = "gameState")
    List<Player> players;

    public GameState(String roomCode, Player activePlayer, Player lastPlayer) {
        this.roomCode = roomCode;
        this.activePlayer = activePlayer;
        this.lastPlayer = lastPlayer;
    }

    public GameState(String roomCode) {
        this.roomCode = roomCode;
    }

    public GameState() {
    }

    public String getRoomCode() {
        return roomCode;
    }

    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
    }

    public Player getActivePlayer() {
        return activePlayer;
    }

    public void setActivePlayer(Player activePlayer) {
        this.activePlayer = activePlayer;
    }

    public Player getLastPlayer() {
        return lastPlayer;
    }

    public void setLastPlayer(Player lastPlayer) {
        this.lastPlayer = lastPlayer;
    }
}
