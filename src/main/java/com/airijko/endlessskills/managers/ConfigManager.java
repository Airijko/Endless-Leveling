package com.airijko.endlessskills.managers;

import com.airijko.endlessskills.EndlessSkills;
import com.airijko.endlessskills.settings.Config;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {
    private final EndlessSkills plugin;
    private FileConfiguration config;

    public ConfigManager(EndlessSkills plugin) {
        this.plugin = plugin;
        reload();
    }

    public void loadConfig() {
        plugin.saveDefaultConfig();
        config = plugin.getConfig();
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public void reload() {
        plugin.reloadConfig();
        config = plugin.getConfig();
    }
}