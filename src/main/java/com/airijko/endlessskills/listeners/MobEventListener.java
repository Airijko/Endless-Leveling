package com.airijko.endlessskills.listeners;

import com.airijko.endlesscore.permissions.Permissions;

import com.airijko.endlessskills.leveling.LevelingManager;
import com.airijko.endlessskills.leveling.XPConfiguration;
import com.airijko.endlessskills.settings.Config;
import com.airijko.endlessskills.managers.ConfigManager;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class MobEventListener implements Listener {
    private final ConfigManager configManager;
    private final Permissions permissions;
    private final XPConfiguration xpConfiguration;
    private final LevelingManager levelingManager;

    public MobEventListener(ConfigManager configManager, Permissions permissions, XPConfiguration xpConfiguration, LevelingManager levelingManager) {
        this.configManager = configManager;
        this.permissions = permissions;
        this.xpConfiguration = xpConfiguration;
        this.levelingManager = levelingManager;
    }
    @EventHandler
    public void onMobKill(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        Player killer = entity.getKiller();

        // Check if the entity was killed by an arrow
        if (killer instanceof Arrow) {
            Arrow arrow = (Arrow) killer;

            // Check if the arrow was shot by a player
            if (arrow.getShooter() instanceof Player) {
                Player player = (Player) arrow.getShooter();
                if (shouldHandleEntityDeath(player)) {
                    handleEntityDeath(player, entity);
                }
            }
        }

        // Check if the entity was killed by a player
        else if (killer != null) {
            if (shouldHandleEntityDeath(killer)) {
                handleEntityDeath(killer, entity);
            }
        }
    }

    private boolean shouldHandleEntityDeath(Player player) {
        boolean soloLevelingEnabled = configManager.getConfig().getBoolean(Config.ENABLE_SOLO_LEVELING.getPath(), true);
        return !soloLevelingEnabled
                || permissions.hasPermission(player, "endlessskills.gainxp.onmobkill")
                || permissions.hasPermission(player, "endlessskills.sololeveling.premium");
    }

    private void handleEntityDeath(Player player, LivingEntity entity) {
        // Get the mob's name
        String mobName = entity.getType().name();

        // Use the getXPForMob method from XPConfiguration to get the XP value for the mob
        double xpForMob = xpConfiguration.getXPForMob(mobName);

        // Use the handleXP method from LevelingManager to add XP and handle level-ups
        levelingManager.handleXP(player, xpForMob, false);
    }
}
