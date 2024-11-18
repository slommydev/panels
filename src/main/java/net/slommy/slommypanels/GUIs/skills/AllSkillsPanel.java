package net.slommy.slommypanels.GUIs.skills;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextDecoration;
import net.slommy.essentials.PlayerStats.skills.LoadedSkills;
import net.slommy.essentials.PlayerStats.skills.PlayerSkills;
import net.slommy.essentials.PlayerStats.skills.Skill;
import net.slommy.essentials.SlommyItems.Items;
import net.slommy.essentials.SlommyItems.SlommyItem;
import net.slommy.essentials.util.IntegerTruncator;
import net.slommy.essentials.util.Msg;
import net.slommy.essentials.util.PlayerDataUtil;
import net.slommy.essentials.util.RomanNumeral;
import net.slommy.slommypanels.SlommyPanels;
import net.slommy.slommypanels.util.Centerer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import xyz.janboerman.guilib.api.GuiListener;
import xyz.janboerman.guilib.api.mask.Shape;
import xyz.janboerman.guilib.api.menu.CloseButton;
import xyz.janboerman.guilib.api.menu.ItemButton;
import xyz.janboerman.guilib.api.menu.MenuHolder;

import java.util.ArrayList;
import java.util.List;

public class AllSkillsPanel {
    private GuiListener listener;
    private MenuHolder<SlommyPanels> panel;
    Player player;

    public GuiListener getListener() {
        return listener;
    }

    public AllSkillsPanel(SlommyPanels plugin, Player player) {
        this.player = player;
        int skillCount = LoadedSkills.getSkills().size();
        int sizeOffset = 0;
        if (skillCount > 9) {
            sizeOffset = 9;
        }
        panel = new MenuHolder<SlommyPanels>(plugin, 45, "Your Skills");
        listener = plugin.getGuiLibrary().getGuiListener();
        List<Skill> skills = LoadedSkills.getSkills();
        List<Integer> slots = Centerer.placeItemsEvenly(45, skills.size());
        List<Integer> takenSlots = new ArrayList<>();
        int i = 0;
        for (Skill skill : skills) {
            ItemStack item = skill.getItemIcon().build(player);
            PlayerSkills playerSkills = PlayerDataUtil.getPlayerSkills(player);
            Skill playerSkill = playerSkills.getSkill(skill.getInternalName());
            ItemMeta meta1 = item.getItemMeta();
            meta1.setMaxStackSize(99);
            item.setItemMeta(meta1);
            item.setAmount(playerSkill.getLevel());
            if (playerSkill.getLevel() == 0) {
                item.setAmount(1);
            }
            if (playerSkill.getLevel() > 99) {
                item.setAmount(playerSkill.getLevel() / 10);
            }
            List<Component> lore = item.getItemMeta().lore();
            lore.add(Msg.JSONcolor(""));
            lore.add(Msg.JSONcolor("&eClick to view!"));
            ItemMeta meta = item.getItemMeta();
            meta.lore(lore);
            item.setItemMeta(meta);
            ItemButton button = new ItemButton(item) {
                @Override
                public void onClick(MenuHolder menuHolder, InventoryClickEvent inventoryClickEvent) {
                    MenuHolder<SlommyPanels> panel = IndividualSkillPanel.open(player, skill.getInternalName());
                    Inventory panelInventory = panel.getInventory();
                    player.openInventory(panelInventory);
                }
            };
            panel.setButton(slots.get(i), button);
            takenSlots.add(slots.get(i));
            i++;
        }
        for (int j = 0; j < 45; j++) {
            if (!takenSlots.contains(j)) {
                panel.setButton(j, new ItemButton<>(Items.getItems().get("gui/blank_pane").build(player)));
            }
        }
        panel.setButton(40, new CloseButton<>(Items.getItems().get("gui/close").build(player)));
        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
        // set the player head to the player's skin
        SkullMeta meta = (SkullMeta) playerHead.getItemMeta();
        meta.setOwningPlayer(player);
        TextComponent displayName = Msg.JSONcolor("&6" + player.getName() + "'s Skills");
        displayName = displayName.decoration(TextDecoration.ITALIC, false);
        meta.displayName(displayName);
        List<Component> lore = new ArrayList<>();
        PlayerSkills playerSkills = PlayerDataUtil.getPlayerSkills(player);
        int totalSkillLevel = 0;
        long totalXP = 0;
        for (Skill skill : skills) {
            Skill playerSkill = playerSkills.getSkill(skill.getInternalName());
            totalSkillLevel += playerSkill.getLevel();
            totalXP += playerSkill.getXp();
            TextComponent skillLore = Msg.JSONcolor(skill.getColor() + skill.getIcon() + " " + skill.getName() + ": " + RomanNumeral.convert(playerSkill.getLevel()) + " &7(" + IntegerTruncator.truncate(playerSkill.getXp()) + "&7 xp)");
            // make it not italics
            skillLore = skillLore.decoration(TextDecoration.ITALIC, false);
            lore.add(skillLore);
        }
        lore.add(Msg.JSONcolor(""));
        TextComponent totalLevel = Msg.JSONcolor("&7Total Skill Level: &6" + RomanNumeral.convert(totalSkillLevel) + " &7(" + totalSkillLevel + "&7)");
        totalLevel = totalLevel.decoration(TextDecoration.ITALIC, false);
        lore.add(totalLevel);
        TextComponent totalXPComponent = Msg.JSONcolor("&7Total XP: &e" + IntegerTruncator.truncate(totalXP));
        totalXPComponent = totalXPComponent.decoration(TextDecoration.ITALIC, false);
        lore.add(totalXPComponent);
        meta.lore(lore);
        playerHead.setItemMeta(meta);
        panel.setButton(4, new ItemButton<>(playerHead) {
            @Override
            public void onClick(MenuHolder menuHolder, InventoryClickEvent inventoryClickEvent) {
                    MenuHolder<SlommyPanels> totalSkillPanel = TotalSkillsPanel.open(player);
                    player.openInventory(totalSkillPanel.getInventory());
            }
        });
    }


    public MenuHolder<SlommyPanels> getPanel() {
        return panel;
    }

    public static ItemStack getPlayerHeadItem(Player player) {
        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
        // set the player head to the player's skin
        SkullMeta meta = (SkullMeta) playerHead.getItemMeta();
        meta.setOwningPlayer(player);
        TextComponent displayName = Msg.JSONcolor("&6" + player.getName() + "'s Skills");
        displayName = displayName.decoration(TextDecoration.ITALIC, false);
        meta.displayName(displayName);
        List<Component> lore = new ArrayList<>();
        PlayerSkills playerSkills = PlayerDataUtil.getPlayerSkills(player);
        int totalSkillLevel = 0;
        long totalXP = 0;
        List<Skill> skills = LoadedSkills.getSkills();
        for (Skill skill : skills) {
            Skill playerSkill = playerSkills.getSkill(skill.getInternalName());
            totalSkillLevel += playerSkill.getLevel();
            totalXP += playerSkill.getXp();
            TextComponent skillLore = Msg.JSONcolor(skill.getColor() + skill.getIcon() + " " + skill.getName() + ": " + RomanNumeral.convert(playerSkill.getLevel()) + " &7(" + IntegerTruncator.truncate(playerSkill.getXp()) + "&7 xp)");
            // make it not italics
            skillLore = skillLore.decoration(TextDecoration.ITALIC, false);
            lore.add(skillLore);
        }
        lore.add(Msg.JSONcolor(""));
        TextComponent totalLevel = Msg.JSONcolor("&7Total Skill Level: &6" + RomanNumeral.convert(totalSkillLevel) + " &7(" + totalSkillLevel + "&7)");
        totalLevel = totalLevel.decoration(TextDecoration.ITALIC, false);
        lore.add(totalLevel);
        TextComponent totalXPComponent = Msg.JSONcolor("&7Total XP: &e" + IntegerTruncator.truncate(totalXP));
        totalXPComponent = totalXPComponent.decoration(TextDecoration.ITALIC, false);
        lore.add(totalXPComponent);
        meta.lore(lore);
        playerHead.setItemMeta(meta);
        return playerHead;
    }

}
