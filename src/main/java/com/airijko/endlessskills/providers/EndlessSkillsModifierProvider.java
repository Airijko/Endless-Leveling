package com.airijko.endlessskills.providers;

import com.airijko.endlesscore.interfaces.AttributeModifierInterface;
import com.airijko.endlessskills.managers.PlayerDataManager;
import com.airijko.endlessskills.skills.SkillAttributes;
import org.bukkit.entity.Player;

import java.util.*;


public class EndlessSkillsModifierProvider implements AttributeModifierInterface {
    private final PlayerDataManager playerDataManager;
    private final SkillAttributes skillAttributes;

    public EndlessSkillsModifierProvider(PlayerDataManager playerDataManager, SkillAttributes skillAttributes) {
        this.playerDataManager = playerDataManager;
        this.skillAttributes = skillAttributes;
    }


    @Override
    public Map<String, Double> getModifiers(String attributeName, Player player) {
        int level = playerDataManager.getAttributeLevel(player.getUniqueId(), attributeName);
        Map<String, Double> attributeModifiers = new HashMap<>();

        switch (attributeName) {
            case "Tenacity":
                attributeModifiers.put("Toughness", skillAttributes.getAttributeValue("skill_attributes.tenacity.toughness", level));
                attributeModifiers.put("Knockback_Resistance", skillAttributes.getAttributeValue("skill_attributes.tenacity.knock_back_resistance", level));
                break;
            case "Haste":
                attributeModifiers.put("Attack_Speed", skillAttributes.getAttributeValue("skill_attributes.haste.attack_speed", level));
                attributeModifiers.put("Movement_Speed", skillAttributes.getAttributeValue("skill_attributes.haste.movement_speed", level));
                break;
            default:
                attributeModifiers.put(attributeName, skillAttributes.getModifiedValue(attributeName, level));
                break;
        }

        return attributeModifiers;
    }

    public Set<String> getAttributeNames() {
        return new HashSet<>(Arrays.asList("Life_Force", "Strength",  "Tenacity", "Haste",  "Precision", "Ferocity"));
    }
}