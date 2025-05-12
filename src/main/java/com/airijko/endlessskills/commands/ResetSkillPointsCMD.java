package com.airijko.endlessskills.commands;

import com.airijko.endlessskills.skills.SkillAttributes;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ResetSkillPointsCMD implements CommandExecutor {
    private final SkillAttributes skillAttributes;

    public ResetSkillPointsCMD(SkillAttributes skillAttributes) {
        this.skillAttributes = skillAttributes;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("all")) {
                // Reset skill points for all players (online and offline)
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    skillAttributes.resetSkillPoints(onlinePlayer);
                }
                for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
                    if (offlinePlayer.hasPlayedBefore()) {
                        skillAttributes.resetSkillPoints(offlinePlayer);
                    }
                }
                sender.sendMessage("Reset skill points for all players (online and offline).");
            }
        } else {
            if (!(sender instanceof Player)) {
                sender.sendMessage("This command can only be run by a player.");
                return true;
            }
            Player player = (Player) sender;
            skillAttributes.resetSkillPoints(player);
            player.sendMessage("All attributes have been reset to their default values.");
        }
        return true;
    }
}