package me.airijko.endlessskills.managers;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;

public class PlayerDataManager {
    private final JavaPlugin plugin;

    public PlayerDataManager(JavaPlugin plugin) {
        this.plugin = plugin;
        loadPlayerDataFolder();
    }

    public void loadPlayerDataFolder() {
        File playerDataFolder = new File(plugin.getDataFolder(), "playerdata");
        if (!playerDataFolder.exists()) {
            if (!playerDataFolder.mkdir()) {
                plugin.getLogger().log(Level.SEVERE, "Failed to create the playerdata folder.");
            }
        }
    }
    public File getPlayerDataFile(UUID playerUUID) {
        File playerDataFolder = new File(plugin.getDataFolder(), "playerdata");
        if (!playerDataFolder.exists()) {
            playerDataFolder.mkdir();
        }
        File playerDataFile = new File(playerDataFolder, playerUUID.toString() + ".yml");
        if (!playerDataFile.exists()) {
            try {
                playerDataFile.createNewFile();
                YamlConfiguration playerDataConfig = YamlConfiguration.loadConfiguration(playerDataFile);
                playerDataConfig.set("UUID", playerUUID.toString());
                Player player = Bukkit.getPlayer(playerUUID);
                String playerName = player != null ? player.getName() : "Unknown Player";
                playerDataConfig.set("PlayerName", playerName);
                playerDataConfig.set("XP", 0);
                playerDataConfig.set("Level", 1);
                playerDataConfig.set("Skill_Points", 5);
                playerDataConfig.createSection("Attributes");
                playerDataConfig.set("Attributes.Life_Force", 1);
                playerDataConfig.set("Attributes.Strength", 1);
                playerDataConfig.set("Attributes.Tenacity", 1);
                playerDataConfig.set("Attributes.Haste", 1);
                playerDataConfig.set("Attributes.Focus", 1);
                playerDataConfig.save(playerDataFile);
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to save player data", e);
            }
        }
        return playerDataFile;
    }

    // Method to get the player's level
    public int getPlayerLevel(UUID playerUUID) {
        File playerDataFile = getPlayerDataFile(playerUUID);
        YamlConfiguration playerDataConfig = YamlConfiguration.loadConfiguration(playerDataFile);
        return playerDataConfig.getInt("Level", 1); // Default to 1 if "Level" is not set
    }

    public int getPlayerXP(UUID playerUUID) {
        File playerDataFile = getPlayerDataFile(playerUUID);
        YamlConfiguration playerDataConfig = YamlConfiguration.loadConfiguration(playerDataFile);
        return playerDataConfig.getInt("XP", 0);
    }

    public void setPlayerLevel(UUID playerUUID, int level) {
        File playerDataFile = getPlayerDataFile(playerUUID);
        YamlConfiguration playerDataConfig = YamlConfiguration.loadConfiguration(playerDataFile);
        playerDataConfig.set("Level", level);
        try {
            playerDataConfig.save(playerDataFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to save player data", e);
        }
    }

    public void setPlayerXP(UUID playerUUID, int xp) {
        File playerDataFile = getPlayerDataFile(playerUUID);
        YamlConfiguration playerDataConfig = YamlConfiguration.loadConfiguration(playerDataFile);
        playerDataConfig.set("XP", xp);
        try {
            playerDataConfig.save(playerDataFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to save player data", e);
        }
    }

    public int getPlayerSkillPoints(UUID playerUUID) {
        File playerDataFile = getPlayerDataFile(playerUUID);
        YamlConfiguration playerDataConfig = YamlConfiguration.loadConfiguration(playerDataFile);
        return playerDataConfig.getInt("Skill_Points", 5); // Default to 5 if "Skill_Points" is not set
    }

    // Method to set the player's skill points
    public void setPlayerSkillPoints(UUID playerUUID, int skillPoints) {
        File playerDataFile = getPlayerDataFile(playerUUID);
        YamlConfiguration playerDataConfig = YamlConfiguration.loadConfiguration(playerDataFile);
        playerDataConfig.set("Skill_Points", skillPoints);
        try {
            playerDataConfig.save(playerDataFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to save player data", e);
        }
    }
}
