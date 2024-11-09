package com.airijko.endlessskills;

import com.airijko.endlesscore.EndlessCore;
import com.airijko.endlesscore.interfaces.RespawnInterface;
import com.airijko.endlesscore.managers.AttributeManager;
import com.airijko.endlesscore.permissions.Permissions;

import com.airijko.endlessskills.commands.*;
import com.airijko.endlessskills.leveling.LevelingManager;
import com.airijko.endlessskills.listeners.*;
import com.airijko.endlessskills.managers.ConfigManager;
import com.airijko.endlessskills.managers.PlayerDataManager;
import com.airijko.endlessskills.leveling.XPConfiguration;
import com.airijko.endlessskills.leveling.LevelConfiguration;
import com.airijko.endlessskills.managers.PluginManager;
import com.airijko.endlessskills.mechanics.SoloLevelingMechanic;
import com.airijko.endlessskills.providers.EndlessSkillsModifierProvider;
import com.airijko.endlessskills.skills.SkillAttributes;

import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class EndlessSkills extends JavaPlugin {
    private Permissions permissions;
    private PluginManager pluginManager;
    private PlayerDataManager playerDataManager;
    private EndlessSkillsModifierProvider endlessSkillsModifierProvider;
    private LevelConfiguration levelConfiguration;
    private SkillAttributes skillAttributes;
    private LevelingManager levelingManager;
    private XPConfiguration xpConfiguration;
    private ReloadCMD reloadCMD;
    private DefaultResetVanillaCMD resetAttributesCommand;
    private LevelCMD levelCMD;
    private ResetSkillPointsCMD resetSkillPointsCMD;
    private SoloLevelingMechanic soloLevelingMechanic;
    private SkillsGUI skillsGUI;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        ConfigManager configManager = new ConfigManager(this);

        permissions = new Permissions();
        pluginManager = new PluginManager(this);
        pluginManager.initializePlugin();

        playerDataManager = new PlayerDataManager(this);
        levelConfiguration = new LevelConfiguration(this);
        levelingManager = new LevelingManager(this, playerDataManager, levelConfiguration);
        skillAttributes = new SkillAttributes(configManager, playerDataManager, levelingManager);
        endlessSkillsModifierProvider = new EndlessSkillsModifierProvider(playerDataManager, skillAttributes);
        xpConfiguration = new XPConfiguration(this);
        skillsGUI = new SkillsGUI(playerDataManager, skillAttributes);
        levelCMD = new LevelCMD(playerDataManager, levelingManager);
        resetAttributesCommand = new DefaultResetVanillaCMD();
        resetSkillPointsCMD = new ResetSkillPointsCMD(skillAttributes);
        soloLevelingMechanic = new SoloLevelingMechanic(configManager, permissions);
        reloadCMD = new ReloadCMD(configManager, skillsGUI, xpConfiguration, levelConfiguration);

        configManager.loadConfig();
        levelConfiguration.loadLevelingConfiguration();

        // Registering the EndlessSkillsModifierProvider with the EndlessCore AttributeManager
        EndlessCore endlessCore = EndlessCore.getInstance();
        AttributeManager attributeManager = endlessCore.getAttributeManager();
        attributeManager.registerProvider(endlessSkillsModifierProvider);

        getServer().getPluginManager().registerEvents(skillsGUI, this);

        getServer().getServicesManager().register(RespawnInterface.class, soloLevelingMechanic, this, ServicePriority.Normal);
        getServer().getPluginManager().registerEvents(new MobEventListener(configManager, permissions, xpConfiguration, levelingManager), this);
        getServer().getPluginManager().registerEvents(new BlockActivityListener(configManager, permissions, xpConfiguration, levelingManager), this);
        getServer().getPluginManager().registerEvents(new SkillsGUI(playerDataManager, skillAttributes), this);

        SkillsCMD skillsCMD = new SkillsCMD(skillsGUI);
        Objects.requireNonNull(getCommand("endless")).setExecutor(new EndlessCMD(skillsCMD, levelCMD, resetAttributesCommand, resetSkillPointsCMD, reloadCMD));
        Objects.requireNonNull(getCommand("skills")).setExecutor(skillsCMD);
        Objects.requireNonNull(getCommand("level")).setExecutor(levelCMD);
        Objects.requireNonNull(getCommand("resetattributes")).setExecutor(resetAttributesCommand);
        Objects.requireNonNull(getCommand("resetskillpoints")).setExecutor(resetSkillPointsCMD);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        if (skillsGUI != null) {
            skillsGUI.closeForAllPlayers();
        }
    }
}