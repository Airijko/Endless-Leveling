package com.airijko.endlessskills.listeners;

import com.airijko.endlessskills.mechanics.SoloLevelingMechanic;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PlayerRespawnListener implements Listener {
    private final SoloLevelingMechanic soloLevelingMechanic;

    public PlayerRespawnListener(SoloLevelingMechanic soloLevelingMechanic) {
        this.soloLevelingMechanic = soloLevelingMechanic;
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        soloLevelingMechanic.handleRespawn(player);
    }
}
