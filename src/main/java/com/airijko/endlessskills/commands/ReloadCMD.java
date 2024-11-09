package com.airijko.endlessskills.commands;

import com.airijko.endlessskills.leveling.LevelConfiguration;
import com.airijko.endlessskills.leveling.XPConfiguration;
import com.airijko.endlessskills.listeners.SkillsGUI;
import com.airijko.endlessskills.managers.ConfigManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.jetbrains.annotations.NotNull;

public class ReloadCMD implements CommandExecutor {
    private final ConfigManager configManager;
    private final SkillsGUI skillsGUI;
    private final XPConfiguration xpConfiguration;
    private final LevelConfiguration levelConfiguration;

    public ReloadCMD(ConfigManager configManager, SkillsGUI skillsGUI, XPConfiguration xpConfiguration, LevelConfiguration levelConfiguration) {
        this.configManager = configManager;
        this.skillsGUI = skillsGUI;
        this.xpConfiguration = xpConfiguration;
        this.levelConfiguration = levelConfiguration;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command command, @NotNull String label, String[] args) {
        if (command.getName().equalsIgnoreCase("endless") && args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            if (sender.hasPermission("endless.reload")) {
                // Close the skills menu for any player who has it open
                skillsGUI.closeForAllPlayers();
                // Reload the XP configuration
                xpConfiguration.loadXPConfiguration();
                // Reload the leveling formula configuration
                levelConfiguration.loadLevelingConfiguration();
                // Reload the plugin configuration
                configManager.reload();

                sender.sendMessage("EndlessSkills configuration has been reloaded!");
                return true;
            } else {
                sender.sendMessage("You do not have permission to use this command.");
                return false;
            }
        }
        return false;
    }
}

