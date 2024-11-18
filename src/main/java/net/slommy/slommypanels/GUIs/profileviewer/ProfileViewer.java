package net.slommy.slommypanels.GUIs.profileviewer;

import net.kyori.adventure.text.TextComponent;
import net.slommy.essentials.PlayerStats.PlayerData;
import net.slommy.essentials.PlayerStats.Stat;
import net.slommy.essentials.SlommyItems.ItemUpdateUtils;
import net.slommy.essentials.SlommyItems.Items;
import net.slommy.essentials.util.*;
import net.slommy.slommypanels.GUIs.skills.AllSkillsPanel;
import net.slommy.slommypanels.SlommyPanels;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import xyz.janboerman.guilib.api.menu.ItemButton;
import xyz.janboerman.guilib.api.menu.MenuHolder;

import java.text.DecimalFormat;
import java.util.*;

public class ProfileViewer {
    public static void openProfileViewer(Player opener, Player target) {
        MenuHolder<SlommyPanels> panel = new MenuHolder<SlommyPanels>(SlommyPanels.getInstance(), 6*9, target.getName() + "'s Profile");
        for(int i = 0; i < 6*9; i++) {
            panel.setButton(i, new ItemButton<>(Items.get("gui/blank_pane").build(opener)));
        }

        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
        skullMeta.setOwningPlayer(target);
        head.setItemMeta(skullMeta);
        ItemMeta meta = head.getItemMeta();
        meta.displayName(Msg.JSONcolor(Msg.PAPI(target, "%luckperms_prefix%%player_name%%luckperms_suffix%")));
        List<TextComponent> lore = new ArrayList<>();
        PlayerData data = PlayerDataUtil.getPlayerData(target);
        List<Stat> stats = data.getStats();
        for(Stat stat : stats) {
            if (data.getStatValue(stat.getInternalName(), target) == 0) {
                continue;
            }
            DecimalFormat df = new DecimalFormat("#,###.##");
            lore.add(Msg.JSONcolor("&7" + stat.getName() + ": " + stat.getColor() + df.format(data.getStatValue(stat.getInternalName(), target)) + stat.getSuffix() +  " " + stat.getIcon()));
        }
        meta.lore(lore);
        head.setItemMeta(meta);
        panel.setButton(22, new ItemButton<>(head));
        ItemStack SkillsIcon = AllSkillsPanel.getPlayerHeadItem(target);
        SkillsIcon.setType(Material.DIAMOND_PICKAXE);
        ItemMeta SkillsIconMeta = SkillsIcon.getItemMeta();
        SkillsIconMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        SkillsIcon.setItemMeta(SkillsIconMeta);
        panel.setButton(23, new ItemButton<>(SkillsIcon));

        // Get the player's equipment
        Map<SlommyEquipmentSlot, ItemStack> equipment = GetEquippedItems.getAllValidated(target);
        Map<SlommyEquipmentSlot, ItemStack> equippedItems = new HashMap<>();
        for (int i = 0; i < SlommyEquipmentSlot.values().length; i++) {
            if (equipment.get(SlommyEquipmentSlot.values()[i]) != null) equippedItems.put(SlommyEquipmentSlot.values()[i], equipment.get(SlommyEquipmentSlot.values()[i]));
            else {
                ItemStack missingItem = new ItemStack(Material.GRAY_DYE);
                ItemMeta missingItemMeta = missingItem.getItemMeta();
                missingItemMeta.displayName(Msg.JSONcolor("&c" + SlommyEquipmentSlot.values()[i].getName()));
                missingItem.setItemMeta(missingItemMeta);
                equippedItems.put(SlommyEquipmentSlot.values()[i], missingItem);
            }
        }
        // If any of these items are null, replace with a Gray Dye item, with the name &a[Slot]
        try {
            panel.setButton(11, new ItemButton<>(equippedItems.get(SlommyEquipmentSlot.HEAD)));
            panel.setButton(20, new ItemButton<>(equippedItems.get(SlommyEquipmentSlot.CHEST)));
            panel.setButton(29, new ItemButton<>(equippedItems.get(SlommyEquipmentSlot.LEGS)));
            panel.setButton(38, new ItemButton<>(equippedItems.get(SlommyEquipmentSlot.FEET)));

            panel.setButton(19, new ItemButton<>(equippedItems.get(SlommyEquipmentSlot.CAPE)));
            panel.setButton(2, new ItemButton<>(equippedItems.get(SlommyEquipmentSlot.HAND)));

            panel.setButton(16, new ItemButton<>(equippedItems.get(SlommyEquipmentSlot.OFF_HAND)));
            panel.setButton(25, new ItemButton<>(equippedItems.get(SlommyEquipmentSlot.IMBUED_CHARM_1)));
            panel.setButton(34, new ItemButton<>(equippedItems.get(SlommyEquipmentSlot.IMBUED_CHARM_2)));
        } catch (NullPointerException e) {
            for (int i = 0; i < 6*9; i++) {
                if (panel.getButton(i) == null) {
                    panel.setButton(i, new ItemButton<>(Items.get("gui/empty").build(opener)));
                }
            }
        }

        for (int i = 0; i < 6*9; i++) {
            if (panel.getButton(i) == null) {
                panel.setButton(i, new ItemButton<>(Items.get("gui/empty").build(opener)));
            }
        }

        opener.openInventory(panel.getInventory());
        for (int i = 0; i < 6*9; i++) {
            if (GetSlommyItem.getFromItemTag(Objects.requireNonNull(panel.getInventory().getItem(i))) == null) {
                continue;
            }
            ItemUpdateUtils.updateItemInContainer(opener, i);
        }
    }
}
