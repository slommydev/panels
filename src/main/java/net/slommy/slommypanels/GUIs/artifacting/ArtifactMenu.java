package net.slommy.slommypanels.GUIs.artifacting;

import net.kyori.adventure.text.TextComponent;
import net.slommy.essentials.PlayerStats.skills.PlayerSkills;
import net.slommy.essentials.SlommyItems.Artifacting.ArtifactSeries;
import net.slommy.essentials.SlommyItems.Items;
import net.slommy.essentials.SlommyItems.SlommyItem;
import net.slommy.essentials.util.*;
import net.slommy.slommypanels.SlommyPanels;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.janboerman.guilib.api.menu.ItemButton;
import xyz.janboerman.guilib.api.menu.MenuHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ArtifactMenu {
    public static void openArtifactMenu(Player player, ArtifactSeries series, int page) {
        MenuHolder<SlommyPanels> panel;
        List<SlommyItem> seriesItems = series.getItemsInSeries();
        PlayerSkills skills = PlayerDataUtil.getPlayerSkills(player);
        // remove any items with null artifact info
        for (int i = 0; i < seriesItems.size(); i++) {
            if (seriesItems.get(i).getArtifactInfo() == null) {
                if (player.hasPermission("dev.true")) {
                    Msg.send(player, "&cArtifact " + seriesItems.get(i).getItemID() + " has null &8(invalid?) &cartifact info!");
                }
                seriesItems.remove(i);
                i--;
            }
        }
        // Sort seriesItems by skill level required
        seriesItems.sort((item1, item2) -> {
            if (item1.getArtifactInfo() == null || item2.getArtifactInfo() == null) {
                return 0;
            }
            return item1.getArtifactInfo().getSkillLevelRequired() - item2.getArtifactInfo().getSkillLevelRequired();
        });
        if (seriesItems.size() > 28) {
            seriesItems = seriesItems.subList((page - 1) * 28, seriesItems.size());
        }
        if (seriesItems.size() > 28) {
            seriesItems = seriesItems.subList(0, 28);
        }
        panel = new MenuHolder<>(SlommyPanels.getInstance(), 54, series.getName() + " Artifacts");
        List<Integer> takenSlots = new ArrayList<>();
        // Add blank pane items
        for (int i = 0; i < 54; i++) {
            if (i < 9 || i > 44 || i % 9 == 0 || i % 9 == 8) {
                panel.setButton(i, new ItemButton<>(series.getBackgroundGlass().build(player)));
                takenSlots.add(i);
            }
        }
        // Add the items in the series
        int slot = 0;
        for (int i = 0; i < seriesItems.size(); i++) {
            while (takenSlots.contains(slot)) {
                slot++;
            }
            SlommyItem item = seriesItems.get(i);
            if (skills.getSkill(item.getArtifactInfo().getSkillRequired()).getLevel() < item.getArtifactInfo().getSkillLevelRequired()) {
                ItemStack hiddenItem = new ItemStack(Material.GRAY_DYE);
                ItemMeta meta = hiddenItem.getItemMeta();
                meta.displayName(Msg.JSONcolor("&c???"));
                TextComponent lore = Msg.JSONcolor("&cRequired " + item.getArtifactInfo().getSkillRequired().toString() + " Level: " + RomanNumeral.convert(item.getArtifactInfo().getSkillLevelRequired()));
                List<TextComponent> loreList = new ArrayList<>();
                loreList.add(lore);
                meta.lore(loreList);
                hiddenItem.setItemMeta(meta);
                panel.setButton(slot, new ItemButton<>(hiddenItem));
                takenSlots.add(slot);
            } else {
                ItemStack itemStack = item.build(player);
                ItemMeta meta = itemStack.getItemMeta();
                List<TextComponent> lore = new ArrayList<>();
                lore.add(Msg.JSONcolor("&7Cost to Identify: &6" + IntegerCommaParser.parse(item.getArtifactInfo().getCostToIdentify()) + " \uD83C\uDD6D"));
                lore.add(Msg.JSONcolor("&7Identifies Into: " + Items.get(item.getArtifactInfo().getReturnItemID()).getDisplayName()));
                lore.add(Msg.JSONcolor("&7" + item.getArtifactInfo().getSkillRequired() + " Level Required: " + skills.getSkill(item.getArtifactInfo().getSkillRequired()).getColor() + RomanNumeral.convert(item.getArtifactInfo().getSkillLevelRequired())));
                lore.add(Msg.JSONcolor("&7Times Identified: &d" + "N/A (Not yet implemented)"));
                meta.lore(lore);
                itemStack.setItemMeta(meta);
                panel.setButton(slot, new ItemButton(itemStack) {
                    @Override
                    public void onClick(MenuHolder menuHolder, InventoryClickEvent event) {
                        ConfirmIdentifyItemMenu.open(player, Items.get(GetSlommyItem.getFromItemTag(Objects.requireNonNull(event.getCurrentItem()))));
                    }
                });
                takenSlots.add(slot);
            }
            slot++;
        }
        // Add the page buttons
        if (page != 1) {
            ItemButton previousPageButton = new ItemButton(Items.items.get("gui/prev_page").build(player)) {
                @Override
                public void onClick(MenuHolder menuHolder, InventoryClickEvent event) {
                    openArtifactMenu(player, series, page - 1);
                }
            };
            panel.setButton(45, previousPageButton);
        }
        if (series.getItemsInSeries().size() > page * 28) {
            ItemButton nextPageButton = new ItemButton(Items.items.get("gui/next_page").build(player)) {
                @Override
                public void onClick(MenuHolder menuHolder, InventoryClickEvent event) {
                    openArtifactMenu(player, series, page + 1);
                }
            };
            panel.setButton(53, nextPageButton);
        }

        // Add close button
        ItemButton closeButton = new ItemButton((Items.get("gui/close").build(player))) {
            @Override
            public void onClick(MenuHolder menuHolder, InventoryClickEvent event) {
                ArtifactCollectionMenu.open(player, series);
            }
        };

        panel.setButton(49, closeButton);

        player.openInventory(panel.getInventory());
    }
}
