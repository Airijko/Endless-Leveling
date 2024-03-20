package com.airijko.endlessskills.mechanics;

import com.airijko.endlesscore.utils.TitleDisplay;

import com.airijko.endlesscore.permissions.Permissions;
import com.airijko.endlessskills.managers.ConfigManager;
import com.airijko.endlessskills.settings.Config;
import org.bukkit.entity.Player;

import java.util.Random;

public class SoloLevelingMechanic {

    private final ConfigManager configManager;
    private final Permissions permissions;

    public SoloLevelingMechanic( ConfigManager configManager, Permissions permissions) {
        this.configManager = configManager;
        this.permissions = permissions;
    }

    public void handleRespawn(Player player) {
        if (!soloPlayer(player)) {
            soloLeveler(player);
        } else {
            removeSoloLeveler(player);
            player.sendMessage("<red> You failed the System.");
        }
    }

    public void soloLeveler(Player player) {
        double soloLevelChance = configManager.getConfig().getDouble(Config.SOLO_LEVEL_CHANCE.getPath(), 0.0);

        if (new Random().nextDouble() < soloLevelChance / 100) {
            TitleDisplay.sendTitle(player, "<aqua><b> THE SYSTEM HAS CHOSEN YOU! </b></aqua>", "<yellow> You are now a Player </yellow>");
            permissions.grantPermission(player, "endlessskills.sololeveling.free");
        }
    }

    public boolean soloPlayer(Player player) {
        return permissions.hasPermission(player, "endlessskills.sololeveling.free");
    }

    public void removeSoloLeveler(Player player) {
        permissions.removePermission(player, "endlessskills.sololeveling.free");
    }
}
