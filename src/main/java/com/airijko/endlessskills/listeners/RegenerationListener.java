package com.airijko.endlessskills.listeners;

import com.airijko.endlessskills.EndlessSkills;
import com.airijko.endlessskills.managers.PlayerDataManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;

import java.util.logging.Level;

public class RegenerationListener implements Listener {
    private final PlayerDataManager playerDataManager;
    private final EndlessSkills plugin;

    public RegenerationListener(PlayerDataManager playerDataManager, EndlessSkills plugin) {
        this.playerDataManager = playerDataManager;
        this.plugin = plugin;
        plugin.getLogger().log(Level.INFO, "RegenerationListener has been registered.");
    }

    @EventHandler
    public void playerRegeneration(EntityRegainHealthEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            // plugin.getLogger().log(Level.INFO, "Player detected: " + player.getName());

            // Get the player's level
            int playerLevel = playerDataManager.getPlayerLevel(player.getUniqueId());
            // plugin.getLogger().log(Level.INFO, "Player level: " + playerLevel);

            // Fetch the base regeneration multiplier from config
            double baseRegenMultiplier = plugin.getConfig().getDouble("skill_attributes.regeneration", 2.0);
            // plugin.getLogger().log(Level.INFO, "Base regeneration multiplier: " + baseRegenMultiplier);

            // Calculate the regeneration multiplier
            double regenMultiplier = 1.0 + (playerLevel / 100.0) * (baseRegenMultiplier - 1.0);
            // plugin.getLogger().log(Level.INFO, "Calculated regeneration multiplier: " + regenMultiplier);

            // Scale the natural regeneration amount
            double scaledRegen = event.getAmount() * regenMultiplier;
            // plugin.getLogger().log(Level.INFO, "Original regen amount: " + event.getAmount() + ", Scaled regen amount: " + scaledRegen);

            event.setAmount(scaledRegen);
        } else {
            // plugin.getLogger().log(Level.INFO, "Entity is not a player.");
        }
    }
}