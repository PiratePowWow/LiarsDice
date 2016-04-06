package com.theironyard.services;

import com.theironyard.entities.Player;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

/**
 * Created by PiratePowWow on 4/5/16.
 */
public interface PlayerRepository extends CrudRepository<Player, UUID> {
}
