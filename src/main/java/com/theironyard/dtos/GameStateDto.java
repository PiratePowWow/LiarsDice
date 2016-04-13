package com.theironyard.dtos;

import com.theironyard.entities.GameState;
import com.theironyard.services.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by PiratePowWow on 4/5/16.
 */
public class GameStateDto {
    @Autowired
    PlayerRepository players;
    private String roomCode;
    private int activePlayerSeatNum;
    private int lastPlayerSeatNum;

    public GameStateDto(GameState gameState) {
        this.roomCode = gameState.getRoomCode();
        this.activePlayerSeatNum = players.findOne(gameState.getActivePlayerId()).getSeatNum();
        this.lastPlayerSeatNum = players.findOne(gameState.getLastPlayerId()).getSeatNum();
    }

    public String getRoomCode() {
        return roomCode;
    }

    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
    }

    public int getActivePlayerSeatNum() {
        return activePlayerSeatNum;
    }

    public void setActivePlayerSeatNum(int activePlayerSeatNum) {
        this.activePlayerSeatNum = activePlayerSeatNum;
    }

    public int getLastPlayerSeatNum() {
        return lastPlayerSeatNum;
    }

    public void setLastPlayerSeatNum(int lastPlayerSeatNum) {
        this.lastPlayerSeatNum = lastPlayerSeatNum;
    }
}
