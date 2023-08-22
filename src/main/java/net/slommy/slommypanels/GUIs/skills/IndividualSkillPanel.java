package net.slommy.slommypanels.GUIs.skills;

import com.comphenix.protocol.utility.MinecraftReflection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextDecoration;
import net.slommy.essentials.PlayerStats.skills.LoadedSkills;
import net.slommy.essentials.PlayerStats.skills.PlayerSkills;
import net.slommy.essentials.PlayerStats.skills.Skill;
import net.slommy.essentials.SlommyItems.Items;
import net.slommy.essentials.util.IntegerTruncator;
import net.slommy.essentials.util.Msg;
import net.slommy.essentials.util.PlayerDataUtil;
import net.slommy.essentials.util.RomanNumeral;
import net.slommy.slommypanels.SlommyPanels;
import net.slommy.essentials.util.ProgressBar;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.janboerman.guilib.api.ItemBuilder;
import xyz.janboerman.guilib.api.menu.ItemButton;
import xyz.janboerman.guilib.api.menu.MenuHolder;
import xyz.janboerman.guilib.api.menu.ToggleButton;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class IndividualSkillPanel {
    public static MenuHolder<SlommyPanels> open(Player player, String skillName) {
        if (LoadedSkills.getSkill(skillName) == null) {
            return null;
        }
        SlommyPanels plugin = SlommyPanels.getInstance();
        PlayerSkills skills = PlayerDataUtil.getPlayerSkills(player);
        Skill skill = skills.getSkill(skillName);
        MenuHolder<SlommyPanels> panel = new MenuHolder<>(plugin, 5*9, skill.getName());

        // organize the panel
        final int[] scrollPosition = {skill.getLevel() + 4};
        if (skill.getLevel() == 0) {
            scrollPosition[0]++;
        }
        if (skill.getLevel() == 0) {
            scrollPosition[0] = 8;
        }
        if (skill.getLevel() > 94) {
            scrollPosition[0] = 99;
        }
        int startPosition = scrollPosition[0] - 8;
        if (scrollPosition[0] > 99) {
            scrollPosition[0] = 99;
        }
        for (int i = 0; i < 5*9; i++) {
            ItemButton button = new ItemButton<>(Items.getItems().get("gui/light_blank").build(player));
            panel.setButton(i, button);
        }
        setLevelItems(panel, skill, player, scrollPosition[0], startPosition);

        // add buttons
        ItemButton backButton = new ItemButton(Items.getItems().get("gui/back").build(player)) {
            @Override
            public void onClick(MenuHolder menuHolder, InventoryClickEvent inventoryClickEvent) {
                MenuHolder<SlommyPanels> panel = new AllSkillsPanel(plugin, player).getPanel();
                player.openInventory(panel.getInventory());
            }
        };
        panel.setButton(40, backButton);
        ItemStack skillItem = LoadedSkills.getSkill(skill.getInternalName()).getItemIcon().build(player);
        if (skill.getLevel() > 0) {
            panel.getInventory().setMaxStackSize(127);
            skillItem.setAmount(skill.getLevel());
            // set the max stack size to 127 so that the item can be stacked up to 127 times
        }
        if (skill.getLevel() > 64) {
            // use NMS to get a nms item stack and then set the max stack size to 100
            skillItem.setAmount(skill.getLevel() / 10);
        }
        panel.setButton(4, new ItemButton<>(skillItem));

        setScrollingButtons(panel, skill, player, scrollPosition[0]);


        return panel;
    }

    private static void setLevelItems(MenuHolder<SlommyPanels> panel, Skill skill, Player player, int scrollPosition, int startPosition) {
        for (int i = 0; i < 9; i++) {
            if (startPosition + 9 < 0) {
                ItemButton button = new ItemButton<>(Items.getItems().get("gui/blank_pane").build(player));
                panel.setButton(i, button);
            } else {
                ItemButton button = new ItemButton<>(getLevelItem(skill, startPosition + i, player, panel.getInventory()));
                panel.setButton(i+18, button);
            }
        }
    }

    private static ItemStack getLevelItem(Skill skill, int level, Player player, Inventory panelInv) {
        ItemStack item = new ItemStack(Material.BARRIER);
        if (level < 0) {
            return Items.getItems().get("gui/blank_pane").build(player);
        }
        panelInv.setMaxStackSize(127);
        int stackSize = level;
        if (stackSize == 0) {
            stackSize = 1;
        }
        if (stackSize > 64) {
            stackSize = level / 10;
        }
        if (skill.getLevel() > level) {
            item.setType(Material.LIME_STAINED_GLASS_PANE);
            ItemMeta meta = item.getItemMeta();
            meta.displayName(Msg.JSONcolor("&a" + skill.getName() + " Level " + RomanNumeral.convert(level)));
            List<Component> lore = new ArrayList<>();
            addRewardLoreLines(lore, player, skill, level);
            meta.lore(lore);
            item.setItemMeta(meta);
            item.setAmount(stackSize);
            return item;
        }
        if (skill.getLevel() < level && level != skill.getLevel() + 1) {
            item.setType(Material.RED_STAINED_GLASS_PANE);
            ItemMeta meta = item.getItemMeta();
            meta.displayName(Msg.JSONcolor("&c" + skill.getName() + " Level " + RomanNumeral.convert(level)));
            List<Component> lore = new ArrayList<>();
            TextComponent ProgressHeader = Msg.JSONcolor("&7Progress:");
            TextComponent progressBar = Msg.JSONcolor(ProgressBar.skillBar(level, skill) + "&e " + IntegerTruncator.truncate(skill.getXp()) + "&6/&e" + IntegerTruncator.truncate(Skill.level_required_xp(level)));
            lore.add(ProgressHeader);
            lore.add(progressBar);
            addRewardLoreLines(lore, player, skill, level);
            meta.lore(lore);
            item.setItemMeta(meta);
            item.setAmount(stackSize);
            return item;
        }
        if (level == skill.getLevel() + 1) {
            item.setType(Material.YELLOW_STAINED_GLASS_PANE);
            ItemMeta meta = item.getItemMeta();
            meta.displayName(Msg.JSONcolor("&e" + skill.getName() + " Level " + RomanNumeral.convert(level)));
            List<Component> lore = new ArrayList<>();
            TextComponent progressToNextLevelLine = Msg.JSONcolor("&7Progress:");
            progressToNextLevelLine.decoration(TextDecoration.ITALIC, false);
            lore.add(progressToNextLevelLine);
            String progressbar = Msg.PAPI(player, "%slommy_skill_" + skill.getInternalName() + "_progressbar% &e%slommy_skill_" + skill.getInternalName() + "_xp_comma%&6/&e%slommy_skill_" + skill.getInternalName() + "_xp_nextlevel-comma%");
            TextComponent progressbarComponent = Msg.JSONcolor(progressbar);
            progressbarComponent = progressbarComponent.decoration(TextDecoration.ITALIC, false);
            lore.add(progressbarComponent);
            addRewardLoreLines(lore, player, skill, level);
            meta.lore(lore);
            item.setItemMeta(meta);
            item.setAmount(stackSize);
            return item;
        }
        if (level == skill.getLevel()) {
            item = LoadedSkills.getSkill(skill.getInternalName()).getItemIcon().build(player);
            ItemMeta meta = item.getItemMeta();
            meta.displayName(Msg.JSONcolor("&6" + skill.getName() + " Level " + RomanNumeral.convert(level)));
            List<Component> lore = new ArrayList<>();
            TextComponent TotalXP = Msg.JSONcolor("&7Total XP: &e" + IntegerTruncator.truncate(skill.getXp()));
            lore.add(TotalXP);
            addRewardLoreLines(lore, player, skill, level);
            meta.lore(lore);
            item.setItemMeta(meta);
            item.setAmount(stackSize);
            return item;
        }
        return item;
    }
    private static List<Component> addRewardLoreLines(List<Component> lore, Player player, Skill skill, int level) {
        if (skill.getRewardsDescriptions(level).size() == 0) {
            return lore;
        }
        TextComponent RewardHeader = Msg.JSONcolor("&7Rewards:");
        lore.add(RewardHeader);
        for (String reward : skill.getRewardsDescriptions(level)) {
            TextComponent rewardComponent = Msg.JSONcolor("&7- " + Msg.PAPI(player, reward));
            lore.add(rewardComponent);
        }
        return lore;
    }

    private static void setScrollingButtons(MenuHolder<SlommyPanels> panel, Skill skill, Player player, int scrollPosition) {
        final int[] newScrollPosition = {scrollPosition};
        ItemButton ScrollForwardButton = new ItemButton(Items.getItems().get("gui/individual_skill_menu_scroller").build(player)) {
            @Override
            public void onClick(MenuHolder menuHolder, InventoryClickEvent event) {
                if (event.isLeftClick()) {
                    newScrollPosition[0]++;
                    if (newScrollPosition[0] > 99) {
                        newScrollPosition[0] = 99;
                    }
                    int startPosition = newScrollPosition[0] - 8;
                    setLevelItems(panel, skill, player, newScrollPosition[0], startPosition);
                } else if (event.isRightClick()) {
                    newScrollPosition[0] += 10;
                    if (newScrollPosition[0] > 99) {
                        newScrollPosition[0] = 99;
                    }
                    int startPosition = newScrollPosition[0] - 8;
                    setLevelItems(panel, skill, player, scrollPosition, startPosition);
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
                    setLevelItems(panel, skill, player, newScrollPosition[0], startPosition);
                } else if (event.isRightClick()) {
                    newScrollPosition[0] -= 10;
                    if (newScrollPosition[0] < 9) {
                        newScrollPosition[0] = 9;
                    }
                    int startPosition = newScrollPosition[0] - 8;
                    setLevelItems(panel, skill, player, scrollPosition, startPosition);
                }
            }
        };

        panel.setButton(35, ScrollForwardButton);
        panel.setButton(27, ScrollBackwardButton);
    }
}
