package com.airijko.endlessskills.managers;

import com.airijko.endlessskills.EndlessSkills;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;

public class ConfigManager {
    private final EndlessSkills plugin;
    private FileConfiguration config;

    public ConfigManager(EndlessSkills plugin) {
        this.plugin = plugin;
        reload();
    }

    public void loadConfig() {
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            plugin.saveDefaultConfig();
        }

        plugin.reloadConfig();
        config = plugin.getConfig();
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public void reload() {
        loadConfig();
    }
}