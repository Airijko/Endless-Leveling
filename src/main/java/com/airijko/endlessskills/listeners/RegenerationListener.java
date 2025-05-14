package com.airijko.endlessskills.listeners;

import com.airijko.endlessskills.EndlessSkills;
import com.airijko.endlessskills.managers.PlayerDataManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;

public class RegenerationListener implements Listener {
    private final PlayerDataManager playerDataManager;
    private final EndlessSkills plugin;

    public RegenerationListener(PlayerDataManager playerDataManager, EndlessSkills plugin) {
        this.playerDataManager = playerDataManager;
        this.plugin = plugin;
    }

    @EventHandler
    public void playerRegeneration(EntityRegainHealthEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            // Get the player's level
            int playerLevel = playerDataManager.getPlayerLevel(player.getUniqueId());

            // Fetch the base regeneration multiplier from config
            double baseRegenMultiplier = plugin.getConfig().getDouble("skill_attributes.regeneration", 2.0);

            // Calculate the regeneration multiplier (1x at level 1, 3x at level 100)
            double regenMultiplier = 1.0 + (playerLevel / 100.0) * (baseRegenMultiplier - 1.0);

            // Scale the natural regeneration amount
            double scaledRegen = event.getAmount() * regenMultiplier;
            event.setAmount(scaledRegen);
        }
    }
}