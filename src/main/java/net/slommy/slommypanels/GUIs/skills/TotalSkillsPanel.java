package net.slommy.slommypanels.GUIs.skills;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextDecoration;
import net.slommy.essentials.PlayerStats.skills.LoadedSkills;
import net.slommy.essentials.PlayerStats.skills.PlayerSkills;
import net.slommy.essentials.PlayerStats.skills.Skill;
import net.slommy.essentials.SlommyItems.Items;
import net.slommy.essentials.requirements.RequirementManager;
import net.slommy.essentials.requirements.TotalSkillRequirement;
import net.slommy.essentials.util.*;
import net.slommy.slommypanels.SlommyPanels;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.janboerman.guilib.api.menu.ItemButton;
import xyz.janboerman.guilib.api.menu.MenuHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TotalSkillsPanel {
    public static MenuHolder<SlommyPanels> open(Player player) {
        SlommyPanels plugin = SlommyPanels.getInstance();
        PlayerSkills skills = PlayerDataUtil.getPlayerSkills(player);
        int maxTotalSkillLevel = 0;
        for (Skill skill : LoadedSkills.getSkills()) {
            maxTotalSkillLevel += skill.getMaxLevel();
        }
        MenuHolder<SlommyPanels> panel = new MenuHolder<>(plugin, 5*9, "Total Skill Level");

        // organize the panel
        final int[] scrollPosition = {skills.getTotalLevel() + 4};
        if (skills.getTotalLevel() == 0) {
            scrollPosition[0]++;
        }
        if (skills.getTotalLevel() == 0) {
            scrollPosition[0] = 8;
        }
        if (skills.getTotalLevel() > (LoadedSkills.getSkills().size() * maxTotalSkillLevel) - 5) {
            scrollPosition[0] = (LoadedSkills.getSkills().size() * maxTotalSkillLevel) - 5;
        }
        int startPosition = scrollPosition[0] - 8;
        if (scrollPosition[0] > LoadedSkills.getSkills().size() * maxTotalSkillLevel) {
            scrollPosition[0] = LoadedSkills.getSkills().size() * maxTotalSkillLevel;
        }
        for (int i = 0; i < 5*9; i++) {
            ItemButton button = new ItemButton<>(Items.getItems().get("gui/light_blank").build(player));
            panel.setButton(i, button);
        }
        setLevelItems(panel, player, scrollPosition[0], startPosition);

        // add buttons
        ItemButton backButton = new ItemButton(Items.getItems().get("gui/back").build(player)) {
            @Override
            public void onClick(MenuHolder menuHolder, InventoryClickEvent inventoryClickEvent) {
                MenuHolder<SlommyPanels> panel = new AllSkillsPanel(plugin, player).getPanel();
                player.openInventory(panel.getInventory());
            }
        };
        panel.setButton(40, backButton);
        ItemStack skillItem = AllSkillsPanel.getPlayerHeadItem(player);
        ItemMeta skillItemMeta = skillItem.getItemMeta();
        if (skills.getTotalLevel() > 0) {
            skillItemMeta.setMaxStackSize(99);
            skillItem.setItemMeta(skillItemMeta);
            skillItem.setAmount(skills.getTotalLevel());
        }
        if (skills.getTotalLevel() > 99) {
            skillItem.setAmount(skills.getTotalLevel() / 10);
        }
        panel.setButton(4, new ItemButton<>(skillItem));

        setScrollingButtons(panel, player, scrollPosition[0]);


        return panel;
    }

    private static void setLevelItems(MenuHolder<SlommyPanels> panel, Player player, int scrollPosition, int startPosition) {
        for (int i = 0; i < 9; i++) {
            if (startPosition + 9 < 0) {
                ItemButton button = new ItemButton<>(Items.getItems().get("gui/blank_pane").build(player));
                panel.setButton(i, button);
            } else {
                ItemButton button = new ItemButton<>(getLevelItem(startPosition + i, player, panel.getInventory()));
                panel.setButton(i+18, button);
            }
        }
    }

    private static ItemStack getLevelItem(int level, Player player, Inventory panelInv) {
        PlayerSkills skills = PlayerDataUtil.getPlayerSkills(player);
        ItemStack item = new ItemStack(Material.BARRIER);
        if (level < 0) {
            return Items.getItems().get("gui/blank_pane").build(player);
        }
        int stackSize = level;
        if (stackSize == 0) {
            stackSize = 1;
        }
        if (stackSize > 99) {
            stackSize = level / 10;
        }
        if (skills.getTotalLevel() > level) {
            item.setType(Material.LIME_STAINED_GLASS_PANE);
            ItemMeta meta = item.getItemMeta();
            meta.setMaxStackSize(99);
            meta.displayName(Msg.JSONcolor("&aTotal Skill Level " + RomanNumeral.convert(level)));
            List<Component> lore = new ArrayList<>();
            addRewardLoreLines(lore, player, level);
            meta.lore(lore);
            item.setItemMeta(meta);
            item.setAmount(stackSize);
            return item;
        }
        if (skills.getTotalLevel() < level && level != skills.getTotalLevel() + 1) {
            item.setType(Material.RED_STAINED_GLASS_PANE);
            ItemMeta meta = item.getItemMeta();
            meta.setMaxStackSize(99);
            meta.displayName(Msg.JSONcolor("&cTotal Skill Level " + RomanNumeral.convert(level)));
            List<Component> lore = new ArrayList<>();
            addRewardLoreLines(lore, player, level);
            meta.lore(lore);
            item.setItemMeta(meta);
            item.setAmount(stackSize);
            return item;
        }
        if (level == skills.getTotalLevel() + 1) {
            item.setType(Material.YELLOW_STAINED_GLASS_PANE);
            ItemMeta meta = item.getItemMeta();
            meta.setMaxStackSize(99);
            meta.displayName(Msg.JSONcolor("&eTotal Skill Level " + RomanNumeral.convert(level)));
            List<Component> lore = new ArrayList<>();
            addRewardLoreLines(lore, player, level);
            meta.lore(lore);
            item.setItemMeta(meta);
            item.setAmount(stackSize);
            return item;
        }
        if (level == skills.getTotalLevel()) {
            item = AllSkillsPanel.getPlayerHeadItem(player);
            ItemMeta meta = item.getItemMeta();
            meta.setMaxStackSize(99);
            meta.displayName(Msg.JSONcolor("&6Total Skill Level " + RomanNumeral.convert(level)));
            List<Component> lore = new ArrayList<>();
            addRewardLoreLines(lore, player, level);
            meta.lore(lore);
            item.setItemMeta(meta);
            item.setAmount(stackSize);
            return item;
        }
        return item;
    }
    private static List<Component> addRewardLoreLines(List<Component> lore, Player player, int level) {
        Set<TotalSkillRequirement> totalSkillRewards = RequirementManager.getTotalSkillRequirements();
        TextComponent RewardHeader = Msg.JSONcolor("&7Rewards:");
        lore.add(RewardHeader);
        int rewardsForThisLevel = 0;
        for (TotalSkillRequirement requirement : totalSkillRewards) {
            if (requirement.getLevel() == level) {
                TextComponent rewardComponent = Msg.JSONcolor("&7- " + Msg.PAPI(player, requirement.getUnlockMessage()));
                lore.add(rewardComponent);
                rewardsForThisLevel++;
            }
        }
        if (rewardsForThisLevel == 0) {
            lore.clear();
        }
        return lore;
    }

    private static void setScrollingButtons(MenuHolder<SlommyPanels> panel, Player player, int scrollPosition) {
        final int[] newScrollPosition = {scrollPosition};
        int maxTotalSkillLevel = 0;
        for (Skill skill : LoadedSkills.getSkills()) {
            maxTotalSkillLevel += skill.getMaxLevel();
        }
        int finalMaxTotalSkillLevel = maxTotalSkillLevel;
        ItemButton ScrollForwardButton = new ItemButton(Items.getItems().get("gui/individual_skill_menu_scroller").build(player)) {
            @Override
            public void onClick(MenuHolder menuHolder, InventoryClickEvent event) {
                if (event.isLeftClick()) {
                    newScrollPosition[0]++;
                    if (newScrollPosition[0] > finalMaxTotalSkillLevel) {
                        newScrollPosition[0] = finalMaxTotalSkillLevel;
                    }
                    int startPosition = newScrollPosition[0] - 8;
                    setLevelItems(panel, player, newScrollPosition[0], startPosition);
                } else if (event.isRightClick()) {
                    newScrollPosition[0] += 10;
                    if (newScrollPosition[0] > finalMaxTotalSkillLevel) {
                        newScrollPosition[0] = finalMaxTotalSkillLevel;
                    }
                    int startPosition = newScrollPosition[0] - 8;
                    setLevelItems(panel, player, scrollPosition, startPosition);
                }
            }
        };
        ItemButton ScrollBackwardButton = new ItemButton(Items.getItems().get("gui/individual_skill_menu_scroller").build(player)) {
            @Override
            public void onClick(MenuHolder holder, InventoryClickEvent event) {
                if (event.isLeftClick()) {
                    newScrollPosition[0]--;
                    if (newScrollPosition[0] < 9) {
                        newScrollPosition[0] = 9;
                    }
                    int startPosition = newScrollPosition[0] - 8;
                    setLevelItems(panel, player, newScrollPosition[0], startPosition);
                } else if (event.isRightClick()) {
                    newScrollPosition[0] -= 10;
                    if (newScrollPosition[0] < 9) {
                        newScrollPosition[0] = 9;
                    }
                    int startPosition = newScrollPosition[0] - 8;
                    setLevelItems(panel, player, scrollPosition, startPosition);
                }
            }
        };

        panel.setButton(35, ScrollForwardButton);
        panel.setButton(27, ScrollBackwardButton);
    }
}
