package com.theironyard.dtos;

import com.theironyard.entities.Player;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Created by PiratePowWow on 4/12/16.
 */
public class PlayersDto {
    ArrayList<PlayerDto> playerDtos;

    public PlayersDto(ArrayList<Player> players) {
        this.playerDtos = players.parallelStream().map(player -> {
                    return new PlayerDto(player.getName(), player.getGameState().getRoomCode(), player.getStake(), player.getSeatNum());
        }).collect(Collectors.toCollection(ArrayList<PlayerDto>::new));
    }
}
