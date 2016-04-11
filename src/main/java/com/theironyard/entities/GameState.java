package com.theironyard.entities;

import javax.persistence.*;
import java.util.List;

/**
 * Created by PiratePowWow on 4/5/16.
 */
@Entity
@Table(name = "gamestates")
public class GameState {
    @Id
    private String roomCode;
    private String activePlayerId;
    private String lastPlayerId;
    @OneToMany(mappedBy = "gameState", cascade = CascadeType.ALL)
    List<Player> players;

    public GameState(String roomCode, String activePlayerId, String lastPlayerId) {
        this.roomCode = roomCode;
        this.activePlayerId = activePlayerId;
        this.lastPlayerId = lastPlayerId;
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

    public String getActivePlayerId() {
        return activePlayerId;
    }

    public void setActivePlayerId(String activePlayerId) {
        this.activePlayerId = activePlayerId;
    }

    public String getLastPlayerId() {
        return lastPlayerId;
    }

    public void setLastPlayerId(String lastPlayerId) {
        this.lastPlayerId = lastPlayerId;
    }
}
