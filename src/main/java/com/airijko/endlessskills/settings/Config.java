package com.airijko.endlessskills.settings;

import org.bukkit.configuration.file.FileConfiguration;

public enum Config {
    LIFE_FORCE("skill_attributes.life_force", OptionType.DOUBLE),
    STRENGTH("skill_attributes.strength", OptionType.DOUBLE),
    TENACITY_KNOCK_BACK_RESISTANCE("skill_attributes.tenacity.knockback_resistance", OptionType.DOUBLE),
    TENACITY_TOUGHNESS("skill_attributes.tenacity.toughness", OptionType.DOUBLE),
    HASTE_ATTACK_SPEED("skill_attributes.haste.attack_speed", OptionType.DOUBLE),
    HASTE_MOVEMENT_SPEED("skill_attributes.haste.movement_speed", OptionType.DOUBLE),
    PRECISION_CRITICAL_CHANCE("skill_attributes.precision.critical_chance", OptionType.DOUBLE),
    FEROCITY_CRITICAL_DAMAGE("skill_attributes.ferocity.critical_damage", OptionType.DOUBLE),
    GAIN_XP_FROM_BLOCKS("gain_xp_from_blocks", OptionType.BOOLEAN),
    ENABLE_SOLO_LEVELING("enable_solo_leveling", OptionType.BOOLEAN);

    private final String path;
    private final OptionType type;

    Config(String path, OptionType type) {
        this.path = path;
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public OptionType getType() {
        return type;
    }
}
