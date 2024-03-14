package com.airijko.endlessskills.managers;

import com.airijko.endlesscore.EndlessCore;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class PluginManager {

    private final JavaPlugin plugin;

    public PluginManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void initializePlugin() {
        EndlessCore endlessCore = (EndlessCore) Bukkit.getPluginManager().getPlugin("EndlessCore");
        if (endlessCore == null) {
            plugin.getLogger().severe("EndlessCore not found, disabling EndlessSkills...");
            Bukkit.getPluginManager().disablePlugin(plugin);
            return;
        }
        plugin.getLogger().info("EndlessCore found and initialized.");
    }
}
