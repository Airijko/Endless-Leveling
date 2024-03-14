package com.airijko.endlessskills.skills;

import com.airijko.endlesscore.EndlessCore;
import com.airijko.endlessskills.leveling.LevelingManager;
import com.airijko.endlessskills.managers.ConfigManager;
import com.airijko.endlessskills.managers.PlayerDataManager;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import java.util.UUID;
import java.util.ArrayList;
import java.util.List;

public class SkillAttributes {
    private final ConfigManager configManager;
    private final PlayerDataManager playerDataManager;
    private final LevelingManager levelingManager;

    public static final String LIFE_FORCE = "Life_Force";
    public static final String STRENGTH = "Strength";
    public static final String TENACITY = "Tenacity";
    public static final String HASTE = "Haste";
    public static final String PRECISION = "Precision";
    public static final String FEROCITY = "Ferocity";

    public SkillAttributes(ConfigManager configManager, PlayerDataManager playerDataManager, LevelingManager levelingManager ) {
        this.configManager = configManager;
        this.playerDataManager = playerDataManager;
        this.levelingManager = levelingManager;
    }

    public double getAttributeValue(String configKey, int level) {
        return configManager.getConfig().getDouble(configKey, 0.0) * level;
    }
    public double getModifiedValue(String attributeName, int level) {
        switch (attributeName) {
            case "Life_Force":
                return getAttributeValue("skill_attributes.life_force", level);
            case "Strength":
                return getAttributeValue("skill_attributes.strength", level);
            case "Tenacity":
                return getAttributeValue("skill_attributes.tenacity.toughness", level)
                        + getAttributeValue("skill_attributes.tenacity.knock_back_resistance", level);
            case "Haste":
                return getAttributeValue("skill_attributes.haste.attack_speed", level)
                        + getAttributeValue("skill_attributes.haste.movement_speed", level);
            case "Precision":
                return getAttributeValue("skill_attributes.precision.critical_chance", level) / 100;
            case "Ferocity":
                return getAttributeValue("skill_attributes.ferocity.critical_damage", level) / 100;
            default:
                return 0.0;
        }
    }
    private static void resetAttribute(Player player, Attribute attribute, double value) {
        AttributeInstance attributeInstance = player.getAttribute(attribute);
        if (attributeInstance != null) {
            attributeInstance.setBaseValue(value);
        }
    }

    public static void resetAllAttributesToDefault(Player player) {
        resetAttribute(player, Attribute.GENERIC_MAX_HEALTH, 20.0); // Default max health
        resetAttribute(player, Attribute.GENERIC_ATTACK_DAMAGE, 2.0); // Default attack damage
        resetAttribute(player, Attribute.GENERIC_ARMOR_TOUGHNESS, 0.0); // Default armor toughness
        resetAttribute(player, Attribute.GENERIC_KNOCKBACK_RESISTANCE, 0.0); // Default knockback resistance
        resetAttribute(player, Attribute.GENERIC_MOVEMENT_SPEED, 0.1); // Default movement speed
        resetAttribute(player, Attribute.GENERIC_ATTACK_SPEED, 4.0); // Default attack speed
    }

    public void resetSkillPoints(Player player) {
        UUID playerUUID = player.getUniqueId();
        int defaultLevel = 0; // Assuming the default level is 0

        // Get the player's current level
        int playerLevel = playerDataManager.getPlayerLevel(playerUUID);

        // Calculate skill points based on level
        int skillPoints = levelingManager.calculateSkillPointsBasedOnLevel(playerLevel);

        // Set the calculated skill points
        playerDataManager.setPlayerSkillPoints(playerUUID, skillPoints);

        // Reset the attribute levels in the player data manager
        playerDataManager.setAttributeLevel(playerUUID, SkillAttributes.LIFE_FORCE, defaultLevel);
        playerDataManager.setAttributeLevel(playerUUID, SkillAttributes.STRENGTH, defaultLevel);
        playerDataManager.setAttributeLevel(playerUUID, SkillAttributes.TENACITY, defaultLevel);
        playerDataManager.setAttributeLevel(playerUUID, SkillAttributes.HASTE, defaultLevel);
        playerDataManager.setAttributeLevel(playerUUID, SkillAttributes.PRECISION, defaultLevel);
        playerDataManager.setAttributeLevel(playerUUID, SkillAttributes.FEROCITY, defaultLevel);

        EndlessCore.getInstance().getAttributeManager().applyAttributeModifiers(player);
    }

    public void useSkillPoint(UUID playerUUID, String attributeName) {
        // Retrieve the current skill points for the player
        int currentSkillPoints = playerDataManager.getPlayerSkillPoints(playerUUID);

        // Check if the player has enough skill points to level up the attribute
        if (currentSkillPoints > 0) {
            // Subtract one skill point and increase the attribute level
            updatePlayerSkillPoints(playerUUID, currentSkillPoints - 1);
            increaseAttributeLevel(playerUUID, attributeName);

            // Send a message to the player indicating the attribute level has increased
            Player player = Bukkit.getPlayer(playerUUID);
            if (player != null) {
                EndlessCore.getInstance().getAttributeManager().applyAttributeModifiers(player);
                sendLevelUpMessage(player, attributeName, playerDataManager.getAttributeLevel(playerUUID, attributeName));
            }
        } else {
            // Send a message to the player indicating they don't have enough skill points
            Player player = Bukkit.getPlayer(playerUUID);
            if (player != null) {
                sendInsufficientSkillPointsMessage(player, attributeName);
            }
        }
    }

    private void updatePlayerSkillPoints(UUID playerUUID, int newSkillPoints) {
        playerDataManager.setPlayerSkillPoints(playerUUID, newSkillPoints);
    }

    private void increaseAttributeLevel(UUID playerUUID, String attributeName) {
        int currentAttributeLevel = playerDataManager.getAttributeLevel(playerUUID, attributeName);
        playerDataManager.setAttributeLevel(playerUUID, attributeName, currentAttributeLevel + 1);
    }

    private void sendLevelUpMessage(Player player, String attributeName, int newLevel) {
        player.sendMessage(Component.text("Leveled ", NamedTextColor.GREEN)
                .append(Component.text(attributeName, NamedTextColor.AQUA))
                .append(Component.text(" to ", NamedTextColor.GREEN))
                .append(Component.text(String.valueOf(newLevel), NamedTextColor.AQUA))
                .append(Component.text("!", NamedTextColor.GREEN)));
    }

    private void sendInsufficientSkillPointsMessage(Player player, String attributeName) {
        player.sendMessage(Component.text("Not enough skill points to level up ", NamedTextColor.RED)
                .append(Component.text(attributeName, NamedTextColor.AQUA))
                .append(Component.text(".", NamedTextColor.RED)));
    }

    public List<String> getSkillValueString(String attributeName, int level) {
        List<String> skillValues = new ArrayList<>();
        switch (attributeName) {
            case "Tenacity":
                double toughnessValue = getAttributeValue("skill_attributes.tenacity.toughness", level);
                double knockBackResistanceValue = getAttributeValue("skill_attributes.tenacity.knock_back_resistance", level);
                skillValues.add("Toughness Value: " + String.format("%.2f", toughnessValue));
                skillValues.add("Knockback Resistance Value: " + String.format("%.2f", knockBackResistanceValue));
                break;
            case "Haste":
                double attackSpeedValue = getAttributeValue("skill_attributes.haste.attack_speed", level);
                double movementSpeedValue = getAttributeValue("skill_attributes.haste.movement_speed", level);
                skillValues.add("Attack Speed Value: " + String.format("%.2f", attackSpeedValue));
                skillValues.add("Movement Speed Value: " + String.format("%.2f", movementSpeedValue));
                break;
            case "Precision":
                double precisionValue = getAttributeValue("skill_attributes.precision.critical_chance", level);
                skillValues.add("Critical Chance Value: " + String.format("%.2f", precisionValue) + "%");
                break;
            case "Ferocity":
                double ferocityValue = getAttributeValue("skill_attributes.ferocity.critical_damage", level);
                skillValues.add("Critical Damage Value: " + "+" + String.format("%.2f", ferocityValue) + "%");
                break;
            default:
                double modifiedValue = getModifiedValue(attributeName, level);
                skillValues.add("Skill Value: " + String.format("%.2f", modifiedValue));
                break;
        }
        return skillValues;
    }

    public String getAttributeDescription(String attributeName) {
        switch (attributeName) {
            case "Life_Force":
                return "Increases max health by " + getAttributeValue("skill_attributes.life_force", 1) + " per level.";
            case "Strength":
                return "Increases attack damage by " + getAttributeValue("skill_attributes.strength", 1) + " per level.";
            case "Tenacity":
                return "Increases armor toughness by " + getAttributeValue("skill_attributes.tenacity.toughness", 1) + " and knockback resistance by " + getAttributeValue("skill_attributes.tenacity.knock_back_resistance", 1) + " per level.";
            case "Haste":
                return "Increases attack speed by " + getAttributeValue("skill_attributes.haste.attack_speed", 1) + " and movement speed by " + getAttributeValue("skill_attributes.haste.movement_speed", 1) + " per level.";
            case "Precision":
                return "Increase critical chance by " + getAttributeValue("skill_attributes.precision.critical_chance", 1) + "% per level.";
            case "Ferocity":
                return "Increase critical damage by " + getAttributeValue("skill_attributes.ferocity.critical_damage", 1) + "% per level.";
            default:
                return "Description not found.";
        }
    }
}
