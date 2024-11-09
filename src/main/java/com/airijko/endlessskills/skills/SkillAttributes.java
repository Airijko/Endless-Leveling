package com.airijko.endlessskills.skills;

import com.airijko.endlesscore.EndlessCore;
import com.airijko.endlesscore.managers.AttributeManager;
import com.airijko.endlessskills.leveling.LevelingManager;
import com.airijko.endlessskills.managers.ConfigManager;
import com.airijko.endlessskills.managers.PlayerDataManager;
import com.airijko.endlessskills.settings.Config;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.ArrayList;
import java.util.List;

public class SkillAttributes {
    private final ConfigManager configManager;
    private final PlayerDataManager playerDataManager;
    private final LevelingManager levelingManager;
    private final AttributeManager attributeManager;
    private static final String LIFE_FORCE = "Life_Force";
    private static final String STRENGTH = "Strength";
    private static final String TENACITY = "Tenacity";
    private static final String HASTE = "Haste";
    private static final String PRECISION = "Precision";
    private static final String FEROCITY = "Ferocity";
    private static final String LIFE_FORCE_PATH = Config.LIFE_FORCE.getPath();
    private static final String STRENGTH_PATH = Config.STRENGTH.getPath();
    private static final String TOUGHNESS_PATH = Config.TENACITY_TOUGHNESS.getPath();
    private static final String KNOCK_BACK_RESISTANCE_PATH = Config.TENACITY_KNOCK_BACK_RESISTANCE.getPath();
    private static final String ATTACK_SPEED_PATH = Config.HASTE_ATTACK_SPEED.getPath();
    private static final String MOVEMENT_SPEED_PATH = Config.HASTE_MOVEMENT_SPEED.getPath();
    private static final String PRECISION_PATH = Config.PRECISION_CRITICAL_CHANCE.getPath();
    private static final String FEROCITY_PATH = Config.FEROCITY_CRITICAL_DAMAGE.getPath();

    public SkillAttributes(ConfigManager configManager, PlayerDataManager playerDataManager, LevelingManager levelingManager) {
        this.configManager = configManager;
        this.playerDataManager = playerDataManager;
        this.levelingManager = levelingManager;
        this.attributeManager = EndlessCore.getInstance().getAttributeManager();
    }

    public double getAttributeValue(String configKey, int level) {
        return configManager.getConfig().getDouble(configKey, 0.0) * level;
    }

    public double getModifiedValue(String attributeName, int level) {
        switch (attributeName) {
            case "Life_Force":
                return getAttributeValue(LIFE_FORCE_PATH, level);
            case "Strength":
                return getAttributeValue(STRENGTH_PATH, level);
            case "Tenacity":
                return getAttributeValue(TOUGHNESS_PATH, level)
                        + getAttributeValue(KNOCK_BACK_RESISTANCE_PATH, level);
            case "Haste":
                return getAttributeValue(ATTACK_SPEED_PATH, level)
                        + getAttributeValue(MOVEMENT_SPEED_PATH, level);
            case "Precision":
                return getAttributeValue(PRECISION_PATH, level) / 100;
            case "Ferocity":
                return getAttributeValue(FEROCITY_PATH, level) / 100;
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
        EndlessCore.getInstance().getAttributeManager().applyAttributeModifiers(player);
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
            assert player != null;
            sendLevelUpMessage(player, attributeName, playerDataManager.getAttributeLevel(playerUUID, attributeName));
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
        Player player = Bukkit.getPlayer(playerUUID);
        switch (attributeName) {
            case LIFE_FORCE:
                attributeManager.applyLifeForce(player);
                break;
            case TENACITY:
                attributeManager.getDamageReduction(player);
                attributeManager.applyKnockbackResistance(player);
                break;
            case HASTE:
                attributeManager.applyMovementSpeed(player);
                attributeManager.applyAttackSpeed(player);
                break;
        }
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
                double knockBackResistanceValue = getAttributeValue(KNOCK_BACK_RESISTANCE_PATH, level);
                skillValues.add("Damage Reduction Value: " + String.format("%.2f%%", attributeManager.getDamageReductionPercentage()));
                skillValues.add("Knockback Resistance Value: " + String.format("%.2f%%", knockBackResistanceValue * 100));
                break;
            case "Haste":
                double attackSpeedValue = getAttributeValue(ATTACK_SPEED_PATH, level);
                double movementSpeedValue = getAttributeValue(MOVEMENT_SPEED_PATH, level);
                skillValues.add("Attack Speed Value: " + String.format("%.2f", attackSpeedValue));
                skillValues.add("Movement Speed Value: " + String.format("%.2f", movementSpeedValue));
                break;
            case "Precision":
                double precisionValue = getAttributeValue(PRECISION_PATH, level);
                skillValues.add("Critical Chance Value: " + String.format("%.2f", precisionValue) + "%");
                break;
            case "Ferocity":
                double ferocityValue = getAttributeValue(FEROCITY_PATH, level);
                skillValues.add("Critical Damage Value: " + "+" + String.format("%.2f", ferocityValue) + "%");
                break;
            default:
                double modifiedValue = getModifiedValue(attributeName, level);
                skillValues.add("Skill Value: " + String.format("%.2f", modifiedValue));
                break;
        }
        return skillValues;
    }

    public double getDamageReductionValueForNextLevel(Player player) {
        Map<String, Double> toughnessData = attributeManager.getProviderDataForAttribute(player, "Toughness");
        double currentToughnessValue = toughnessData.values().stream().mapToDouble(Double::doubleValue).sum();
        double nextToughnessValue = currentToughnessValue + getAttributeValue(TOUGHNESS_PATH, 1);

        double currentDamageReduction = attributeManager.calculateDamageReduction(currentToughnessValue);
        double nextDamageReduction = attributeManager.calculateDamageReduction(nextToughnessValue);

//        Bukkit.getLogger().info("Current Toughness Value: " + currentToughnessValue);
//        Bukkit.getLogger().info("Next Toughness Value: " + nextToughnessValue);
//        Bukkit.getLogger().info("Current Damage Reduction: " + currentDamageReduction);
//        Bukkit.getLogger().info("Next Damage Reduction: " + nextDamageReduction);
//        Bukkit.getLogger().info("Damage Reduction Difference: " + (nextDamageReduction - currentDamageReduction));

        return nextDamageReduction - currentDamageReduction;
    }

    public String getAttributeDescription(String attributeName, Player player) {
        switch (attributeName) {
            case "Life_Force":
                return "Increases max health by " + getAttributeValue(LIFE_FORCE_PATH, 1) + " per level.";
            case "Strength":
                return "Increases attack damage by " + getAttributeValue(STRENGTH_PATH, 1) + " per level.";
            case "Tenacity":
                return "Increases damage reduction by " + String.format("%.2f%%", getDamageReductionValueForNextLevel(player)) + " and knockback resistance by " + String.format("%.2f%%", getAttributeValue(KNOCK_BACK_RESISTANCE_PATH, 1) * 100) + " per level.";
            case "Haste":
                return "Increases attack speed by " + getAttributeValue(ATTACK_SPEED_PATH, 1) + " and movement speed by " + getAttributeValue(MOVEMENT_SPEED_PATH, 1) + " per level.";
            case "Precision":
                return "Increase critical chance by " + getAttributeValue(PRECISION_PATH, 1) + "% per level.";
            case "Ferocity":
                return "Increase critical damage by " + getAttributeValue(FEROCITY_PATH, 1) + "% per level.";
            default:
                return "Description not found.";
        }
    }
}
