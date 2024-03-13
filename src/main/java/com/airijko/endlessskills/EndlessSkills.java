package com.airijko.endlessskills;

import com.airijko.endlesscore.EndlessCore;
import com.airijko.endlesscore.managers.AttributeManager;
import com.airijko.endlessskills.commands.EndlessCMD;
import com.airijko.endlessskills.commands.DefaultResetVanillaCMD;
import com.airijko.endlessskills.gui.EndlessSkillsGUI;
import com.airijko.endlessskills.leveling.LevelingManager;
import com.airijko.endlessskills.commands.LevelCMD;
import com.airijko.endlessskills.listeners.*;
import com.airijko.endlessskills.managers.PlayerDataManager;
import com.airijko.endlessskills.managers.ConfigManager;
import com.airijko.endlessskills.leveling.XPConfiguration;
import com.airijko.endlessskills.leveling.LevelConfiguration;
import com.airijko.endlessskills.commands.ReloadCMD;
import com.airijko.endlessskills.managers.PluginManager;
import com.airijko.endlessskills.providers.EndlessSkillsModifierProvider;
import com.airijko.endlessskills.skills.SkillAttributes;
import com.airijko.endlessskills.commands.ResetSkillPointsCMD;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class EndlessSkills extends JavaPlugin {
    private PluginManager pluginManager;
    private PlayerDataManager playerDataManager;
    private EndlessSkillsModifierProvider endlessSkillsModifierProvider;
    private ConfigManager configManager;
    private LevelConfiguration levelConfiguration;
    private SkillAttributes skillAttributes;
    private EndlessSkillsGUI endlessSkillsGUI;
    private LevelingManager levelingManager;
    private XPConfiguration xpConfiguration;
    private ReloadCMD reloadCMD;
    private DefaultResetVanillaCMD resetAttributesCommand;
    private LevelCMD levelCMD;
    private ResetSkillPointsCMD resetSkillPointsCMD;

    @Override
    public void onEnable() {
        pluginManager = new PluginManager(this);
        pluginManager.initializePlugin();

        configManager = new ConfigManager(this);
        playerDataManager = new PlayerDataManager(this);
        levelConfiguration = new LevelConfiguration(this);
        levelingManager = new LevelingManager(this, configManager, playerDataManager, levelConfiguration);
        skillAttributes = new SkillAttributes(this, configManager, playerDataManager, levelingManager);
        endlessSkillsModifierProvider = new EndlessSkillsModifierProvider(playerDataManager, skillAttributes);
        endlessSkillsGUI = new EndlessSkillsGUI(playerDataManager, skillAttributes);
        xpConfiguration = new XPConfiguration(this);
        reloadCMD = new ReloadCMD(configManager, endlessSkillsGUI, xpConfiguration, levelConfiguration);
        levelCMD = new LevelCMD(playerDataManager, levelingManager);
        resetAttributesCommand = new DefaultResetVanillaCMD();
        resetSkillPointsCMD = new ResetSkillPointsCMD(skillAttributes, levelingManager);

        levelConfiguration.loadLevelingConfiguration();
        playerDataManager.loadPlayerDataFolder();
        skillAttributes.applyModifiersToAllPlayers();

        EndlessCore endlessCore = EndlessCore.getInstance();
        AttributeManager attributeManager = endlessCore.getAttributeManager();
        attributeManager.registerProvider(endlessSkillsModifierProvider);

        getServer().getPluginManager().registerEvents(new PlayerEventListener(playerDataManager), this);
        getServer().getPluginManager().registerEvents(new PlayerCombatListener(this, configManager, skillAttributes, playerDataManager), this);
        getServer().getPluginManager().registerEvents(new MobEventListener(xpConfiguration, levelingManager), this);
        getServer().getPluginManager().registerEvents(new BlockActivityListener(configManager, xpConfiguration, levelingManager), this);
        getServer().getPluginManager().registerEvents(new EndlessGUIListener(endlessSkillsGUI, skillAttributes), this);

        Objects.requireNonNull(getCommand("endless")).setExecutor(new EndlessCMD(endlessSkillsGUI, reloadCMD, resetAttributesCommand, levelCMD, resetSkillPointsCMD));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        if (endlessSkillsGUI != null) {
            endlessSkillsGUI.closeForAllPlayers();
        }
    }
}