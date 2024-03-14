package com.airijko.endlessskills.commands;

import com.airijko.endlessskills.gui.EndlessSkillsGUI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import java.util.Map;

import java.util.HashMap;

public class EndlessCMD implements CommandExecutor {
    private final Map<String, CommandExecutor> subCommands = new HashMap<>();

    public EndlessCMD(EndlessSkillsGUI gui, ReloadCMD reloadCMD , DefaultResetVanillaCMD defaultResetVanillaCMD, LevelCMD levelCMD, ResetSkillPointsCMD resetSkillPointsCMD) {
        subCommands.put("reload", reloadCMD);
        subCommands.put("skills", new SkillsCMD(gui));
        subCommands.put("reset default", defaultResetVanillaCMD);
        subCommands.put("level", levelCMD);
        subCommands.put("resetskillpoints", resetSkillPointsCMD);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length > 0) {
            String subCommand = args[0].toLowerCase();
            CommandExecutor executor = subCommands.get(subCommand);
            if (executor != null) {
                return executor.onCommand(sender, command, label, args);
            }
        }
        sender.sendMessage(Component.text("Usage: /endless [reload|skills|resetattributes|level]", NamedTextColor.RED));
        return false;
    }
}