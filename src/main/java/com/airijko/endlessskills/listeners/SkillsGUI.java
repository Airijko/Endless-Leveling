package com.airijko.endlessskills.listeners;

import com.airijko.endlessskills.skills.SkillAttributes;
import com.airijko.endlessskills.managers.PlayerDataManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class SkillsGUI implements Listener {
    private final PlayerDataManager playerDataManager;
    private final SkillAttributes skillAttributes;
    private final Inventory gui;

    public SkillsGUI(PlayerDataManager playerDataManager, SkillAttributes skillAttributes) {
        this.playerDataManager = playerDataManager;
        this.skillAttributes = skillAttributes;

        gui = Bukkit.createInventory(null, 27, Component.text("Endless Skills"));
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getInventory().equals(gui)) return;
        if (event.getClickedInventory() != event.getInventory()) return;

        event.setCancelled(true);
        final ItemStack clickedItem = event.getCurrentItem();

        if (clickedItem == null || clickedItem.getType().isAir()) return;
        if (clickedItem.getType() == Material.WHITE_STAINED_GLASS_PANE) return;

        Player player = (Player) event.getWhoClicked();
        Map<String, Runnable> actionMap = getStringRunnableMap(player, skillAttributes);

        handleAction(event, actionMap);

        skillAttributesGUI(player);
    }

    @NotNull
    private static Map<String, Runnable> getStringRunnableMap(Player player, SkillAttributes skillAttributes) {
        UUID playerUUID = player.getUniqueId();

        Map<String, Runnable> actionMap = new HashMap<>();
        actionMap.put("Life_Force", () -> skillAttributes.useSkillPoint(playerUUID, "Life_Force"));
        actionMap.put("Strength", () -> skillAttributes.useSkillPoint(playerUUID, "Strength"));
        actionMap.put("Tenacity", () -> skillAttributes.useSkillPoint(playerUUID, "Tenacity"));
        actionMap.put("Haste", () -> skillAttributes.useSkillPoint(playerUUID, "Haste"));
        actionMap.put("Precision", () -> skillAttributes.useSkillPoint(playerUUID, "Precision"));
        actionMap.put("Ferocity", () -> skillAttributes.useSkillPoint(playerUUID, "Ferocity"));
        return actionMap;
    }

    private void handleAction(InventoryClickEvent event, Map<String, Runnable> actionMap) {
        if (event.getCurrentItem() != null) {
            ItemMeta itemMeta = event.getCurrentItem().getItemMeta();

            if (itemMeta != null) {
                String displayName = GsonComponentSerializer.gson().serialize(Objects.requireNonNull(itemMeta.displayName()));
                Gson gson = new Gson();
                JsonObject jsonObject = gson.fromJson(displayName, JsonObject.class);
                String text = jsonObject.get("text").getAsString().replace(" ", "_");

                Runnable action = actionMap.get(text);
                if (action != null) {
                    action.run();
                }
            }
        }
    }

    public void skillAttributesGUI(Player player) {
        UUID playerUUID = player.getUniqueId();
        int totalSkillPoints = playerDataManager.getPlayerSkillPoints(player.getUniqueId());

        List<String> attributes = Arrays.asList("Life_Force", "Strength", "Tenacity", "Haste", "Precision", "Ferocity");
        List<Material> woolColors = Arrays.asList(Material.RED_WOOL, Material.ORANGE_WOOL, Material.YELLOW_WOOL, Material.LIME_WOOL, Material.LIGHT_BLUE_WOOL, Material.BLUE_WOOL);
        List<NamedTextColor> textColors = Arrays.asList(NamedTextColor.RED, NamedTextColor.GOLD, NamedTextColor.YELLOW, NamedTextColor.GREEN, NamedTextColor.AQUA, NamedTextColor.AQUA);

        int slotIndex = 10;
        for (int i = 0; i < attributes.size(); i++) {
            if (slotIndex == 13) slotIndex++;
            String attribute = attributes.get(i);
            String attributeWithSpace = attributes.get(i).replace("_", " ");
            int level = playerDataManager.getAttributeLevel(playerUUID, attribute);
            String description = skillAttributes.getAttributeDescription(attribute, player);
            gui.setItem(slotIndex, createWoolItem(woolColors.get(i), textColors.get(i), attributeWithSpace, String.valueOf(level), description, attribute));
            slotIndex++;
        }

        ItemStack netherStar = createNetherStarItem(playerUUID, playerDataManager, totalSkillPoints);
        gui.setItem(13, netherStar);

        fillEmptySlots(gui);

        player.openInventory(gui);
    }

    private ItemStack createNetherStarItem(UUID playerUUID, PlayerDataManager playerDataManager, int totalSkillPoints) {
        ItemStack netherStar = new ItemStack(Material.NETHER_STAR);
        ItemMeta netherStarMeta = netherStar.getItemMeta();
        if (netherStarMeta != null) {
            Player player = Bukkit.getPlayer(playerUUID);
            String playerName = player != null ? player.getName() : "Unknown Player";

            netherStarMeta.displayName(Component.text((playerName + " Stats").toUpperCase(), NamedTextColor.GOLD).decoration(TextDecoration.BOLD, true));

            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("Level " + playerDataManager.getPlayerLevel(playerUUID), NamedTextColor.AQUA));

            String[] attributes = {"Life Force", "Strength", "Tenacity", "Haste", "Precision", "Ferocity"};
            for (String attribute : attributes) {
                String attributeWithUnderscores = attribute.replace(" ", "_");
                int level = playerDataManager.getAttributeLevel(playerUUID, attributeWithUnderscores);
                lore.add(Component.text(attribute + ": " + level, NamedTextColor.GRAY));
            }

            lore.add(Component.text("Available Skill Points: " + totalSkillPoints, NamedTextColor.YELLOW));
            netherStarMeta.lore(lore);

            netherStar.setItemMeta(netherStarMeta);
        }
        return netherStar;
    }

    private ItemStack createWoolItem(Material woolType, NamedTextColor color, String displayName, String attributeLevel, String description, String attributeName) {
        ItemStack woolItem = new ItemStack(woolType);
        ItemMeta meta = woolItem.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text(displayName, color));
            int level = Integer.parseInt(attributeLevel);
            List<String> additionalLines = skillAttributes.getSkillValueString(attributeName, level);
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("Level: " + attributeLevel, NamedTextColor.LIGHT_PURPLE));
            lore.add(Component.text(description, NamedTextColor.GRAY));
            for (String line : additionalLines) {
                lore.add(Component.text(line, NamedTextColor.DARK_GRAY));
            }
            meta.lore(lore);
            woolItem.setItemMeta(meta);
        }
        return woolItem;
    }

    private void fillEmptySlots(Inventory inventory) {
        ItemStack invisibleItem = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);
        ItemMeta meta = invisibleItem.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text(" "));
            invisibleItem.setItemMeta(meta);
        }

        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, invisibleItem);
            }
        }
    }

    public void closeForAllPlayers() {
        List<HumanEntity> viewers = new ArrayList<>(gui.getViewers());
        for (HumanEntity viewer : viewers) {
            viewer.closeInventory();
        }
    }

    public void openInventory(Player player) {
        skillAttributesGUI(player);
    }
}