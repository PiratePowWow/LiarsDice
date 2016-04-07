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
    private UUID activePlayerId;
    private UUID lastPlayerId;
    @OneToMany(mappedBy = "gameState", cascade = CascadeType.ALL)
    List<Player> players;

    public GameState(String roomCode, UUID activePlayerId, UUID lastPlayerId) {
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

    public UUID getActivePlayerId() {
        return activePlayerId;
    }

    public void setActivePlayerId(UUID activePlayerId) {
        this.activePlayerId = activePlayerId;
    }

    public UUID getLastPlayerId() {
        return lastPlayerId;
    }

    public void setLastPlayerId(UUID lastPlayerId) {
        this.lastPlayerId = lastPlayerId;
    }
}
