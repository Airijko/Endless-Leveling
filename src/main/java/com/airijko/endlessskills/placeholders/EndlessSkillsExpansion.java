package com.airijko.endlessskills.placeholders;

import com.airijko.endlessskills.managers.PlayerDataManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EndlessSkillsExpansion extends PlaceholderExpansion {
    private final PlayerDataManager playerDataManager;

    public EndlessSkillsExpansion(PlayerDataManager playerDataManager) {
        this.playerDataManager = playerDataManager;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "endlesslevels";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Airijko";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true; // This ensures the expansion is not unloaded on reload
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        if (player == null) {
            return null;
        }

        switch (params.toLowerCase()) {
            case "level":
                return String.valueOf(playerDataManager.getPlayerLevel(player.getUniqueId()));
            case "strength":
                return String.valueOf(playerDataManager.getAttributeLevel(player.getUniqueId(), "Strength"));
            case "life_force":
                return String.valueOf(playerDataManager.getAttributeLevel(player.getUniqueId(), "Life_Force"));
            case "tenacity":
                return String.valueOf(playerDataManager.getAttributeLevel(player.getUniqueId(), "Tenacity"));
            case "haste":
                return String.valueOf(playerDataManager.getAttributeLevel(player.getUniqueId(), "Haste"));
            case "precision":
                return String.valueOf(playerDataManager.getAttributeLevel(player.getUniqueId(), "Precision"));
            case "ferocity":
                return String.valueOf(playerDataManager.getAttributeLevel(player.getUniqueId(), "Ferocity"));
            default:
                return null;
        }
    }
}