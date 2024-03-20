package com.airijko.endlessskills.listeners;

import com.airijko.endlessskills.mechanics.SoloLevelingMechanic;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.entity.Player;

public class PlayerDeathListener implements Listener {
    private final SoloLevelingMechanic soloLevelingMechanic;

    public PlayerDeathListener(SoloLevelingMechanic soloLevelingMechanic) {
        this.soloLevelingMechanic = soloLevelingMechanic;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        soloLevelingMechanic.handlePlayerDeath(player);
    }
}
