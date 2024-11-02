package com.airijko.endlessskills;

import com.airijko.endlesscore.EndlessCore;
import com.airijko.endlesscore.interfaces.RespawnInterface;
import com.airijko.endlesscore.managers.AttributeManager;
import com.airijko.endlesscore.permissions.Permissions;

import com.airijko.endlessskills.commands.EndlessCMD;
import com.airijko.endlessskills.commands.DefaultResetVanillaCMD;
import com.airijko.endlessskills.gui.EndlessSkillsGUI;
import com.airijko.endlessskills.leveling.LevelingManager;
import com.airijko.endlessskills.commands.LevelCMD;
import com.airijko.endlessskills.listeners.*;
import com.airijko.endlessskills.managers.ConfigManager;
import com.airijko.endlessskills.managers.PlayerDataManager;
import com.airijko.endlessskills.leveling.XPConfiguration;
import com.airijko.endlessskills.leveling.LevelConfiguration;
import com.airijko.endlessskills.commands.ReloadCMD;
import com.airijko.endlessskills.managers.PluginManager;
import com.airijko.endlessskills.mechanics.SoloLevelingMechanic;
import com.airijko.endlessskills.providers.EndlessSkillsModifierProvider;
import com.airijko.endlessskills.skills.SkillAttributes;
import com.airijko.endlessskills.commands.ResetSkillPointsCMD;

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
    private EndlessSkillsGUI endlessSkillsGUI;
    private LevelingManager levelingManager;
    private XPConfiguration xpConfiguration;
    private ReloadCMD reloadCMD;
    private DefaultResetVanillaCMD resetAttributesCommand;
    private LevelCMD levelCMD;
    private ResetSkillPointsCMD resetSkillPointsCMD;
    private SoloLevelingMechanic soloLevelingMechanic;

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
        endlessSkillsGUI = new EndlessSkillsGUI(playerDataManager, skillAttributes);
        xpConfiguration = new XPConfiguration(this);
        reloadCMD = new ReloadCMD(configManager, endlessSkillsGUI, xpConfiguration, levelConfiguration);
        levelCMD = new LevelCMD(playerDataManager, levelingManager);
        resetAttributesCommand = new DefaultResetVanillaCMD();
        resetSkillPointsCMD = new ResetSkillPointsCMD(skillAttributes);
        soloLevelingMechanic = new SoloLevelingMechanic(configManager, permissions);

        configManager.loadConfig();
        levelConfiguration.loadLevelingConfiguration();

        // Registering the EndlessSkillsModifierProvider with the EndlessCore AttributeManager
        EndlessCore endlessCore = EndlessCore.getInstance();
        AttributeManager attributeManager = endlessCore.getAttributeManager();
        attributeManager.registerProvider(endlessSkillsModifierProvider, "EndlessLeveling");

        getServer().getServicesManager().register(RespawnInterface.class, soloLevelingMechanic, this, ServicePriority.Normal);
        getServer().getPluginManager().registerEvents(new MobEventListener(configManager, permissions, xpConfiguration, levelingManager), this);
        getServer().getPluginManager().registerEvents(new BlockActivityListener(configManager, permissions, xpConfiguration, levelingManager), this);
        getServer().getPluginManager().registerEvents(new EndlessGUIListener(endlessSkillsGUI, skillAttributes), this);

        LevelCMD levelCMD = new LevelCMD(playerDataManager, levelingManager);
        Objects.requireNonNull(getCommand("endless")).setExecutor(new EndlessCMD(endlessSkillsGUI, levelCMD));
        Objects.requireNonNull(getCommand("level")).setExecutor(levelCMD);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        if (endlessSkillsGUI != null) {
            endlessSkillsGUI.closeForAllPlayers();
        }
    }
}