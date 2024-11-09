package com.airijko.endlessskills.commands;

import com.airijko.endlessskills.listeners.SkillsGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SkillsCMD implements CommandExecutor {
    private final SkillsGUI gui;

    public SkillsCMD(SkillsGUI gui) {
        this.gui = gui;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            gui.openInventory(player);
            return true;
        }
        return false;
    }
}