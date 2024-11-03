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
import java.util.Arrays;
import java.util.List;

public class EndlessCMD implements CommandExecutor {
    private final Map<String, CommandExecutor> subCommands = new HashMap<>();
    private static final List<String> PREFIXES = Arrays.asList("endless", "e");

    public EndlessCMD(EndlessSkillsGUI gui, LevelCMD levelCMD, DefaultResetVanillaCMD resetAttributesCommand, ResetSkillPointsCMD resetSkillPointsCMD, ReloadCMD reloadCMD) {
        subCommands.put("skills", new SkillsCMD(gui));
        subCommands.put("level", levelCMD);
        subCommands.put("resetattributes", resetAttributesCommand);
        subCommands.put("resetskillpoints", resetSkillPointsCMD);
        subCommands.put("reload", reloadCMD);
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
        sender.sendMessage(Component.text("Usage: /endless [reload|profile|resetattributes|level]", NamedTextColor.RED));
        return false;
    }

    public static int getBaseIndex(String label) {
        return PREFIXES.contains(label.toLowerCase()) ? 1 : 0;
    }
}