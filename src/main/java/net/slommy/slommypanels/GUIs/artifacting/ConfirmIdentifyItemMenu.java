package net.slommy.slommypanels.GUIs.artifacting;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextDecoration;
import net.slommy.essentials.PlayerStats.GlobalStats;
import net.slommy.essentials.PlayerStats.PlayerData;
import net.slommy.essentials.SlommyItems.Item;
import net.slommy.essentials.SlommyItems.Items;
import net.slommy.essentials.SlommyItems.SlommyItem;
import net.slommy.essentials.util.*;
import net.slommy.slommypanels.SlommyPanels;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.janboerman.guilib.api.menu.ItemButton;
import xyz.janboerman.guilib.api.menu.MenuHolder;

import java.util.ArrayList;
import java.util.List;

public class ConfirmIdentifyItemMenu {
    public static void open(Player player, SlommyItem item) {
        if (item == null) {
            Msg.send(player, "&cThat item does not exist!");
            return;
        }
        if (item.getArtifactInfo() == null || item.getTypeEnum() != Item.type.ARTIFACT) {
            Msg.send(player, "&cYou cannot identify non-artifact items.");
            return;
        }

        MenuHolder<SlommyPanels> panel = new MenuHolder<>(SlommyPanels.getInstance(), 54, "Identify " + item.getRawDisplayName());
        for(int i = 0; i < 54; i++) {
            panel.setButton(i, new ItemButton<>(item.getArtifactInfo().getSeries().getBackgroundGlass().build(player)));
        }
        ItemStack itemStack = item.build(player);
        ItemMeta meta = itemStack.getItemMeta();
        List<TextComponent> lore = new ArrayList<>();
        meta.lore(lore);
        itemStack.setItemMeta(meta);
        panel.setButton(13, new ItemButton<>(itemStack));
        panel.setButton(22, new ItemButton<>(Items.get("gui/down_arrow").build(player)));
        ItemStack returnItem = Items.get(item.getArtifactInfo().getReturnItemID()).build(player);
        ItemMeta returnMeta = returnItem.getItemMeta();
        List<Component> lore2 = returnMeta.lore();
        for (int i = 0; i < lore2.size(); i++) {
            // Make the lore line obfuscated
            lore2.set(i, lore2.get(i).asComponent().decoration(TextDecoration.OBFUSCATED, true));
        }
        // remove the last 2 lore lines
        lore2.remove(lore2.size() - 1);
        lore2.remove(lore2.size() - 1);
        // remove any lore lines that are not obfuscated
        for (int i = 0; i < lore2.size(); i++) {
            if (!lore2.get(i).asComponent().hasDecoration(TextDecoration.OBFUSCATED)) {
                lore2.remove(i);
                i--;
            }
        }
        returnMeta.lore(lore2);
        returnItem.setItemMeta(returnMeta);
        panel.setButton(31, new ItemButton<>(returnItem));

        ItemStack identifyItem = item.getArtifactInfo().getSeries().getIDItem().build(player);
        ItemMeta identifyMeta = identifyItem.getItemMeta();
        List<Component> lore3 = new ArrayList<>();
        lore3.add(Msg.JSONcolor("&7Cost: &6" + IntegerCommaParser.parse(item.getArtifactInfo().getCostToIdentify()) + " \uD83C\uDD6D"));
        PlayerData data = PlayerDataUtil.getPlayerData(player);
        double artifactingFortuity = data.getStatValue("ArtifactFortuity", player);
        lore3.add(Msg.JSONcolor("&7Artifact Fortuity: &6" + IntegerCommaParser.parsedouble(artifactingFortuity) + "% " + GlobalStats.getStat("ArtifactFortuity").getIcon()));
        lore3.add(Msg.JSONcolor(""));
        lore3.add(Msg.JSONcolor("&eClick to Identify!"));
        identifyMeta.lore(lore3);
        identifyItem.setItemMeta(identifyMeta);
        panel.setButton(25, new ItemButton(identifyItem) {
            @Override
            public void onClick(MenuHolder menuHolder, InventoryClickEvent event) {
                if (!SlommyItemExchange.checkInventory(player, item, 1)) {
                    Msg.send(player, "&cYou do not have any " + item.getDisplayName() + "&c!");
                    player.playSound(player.getLocation(), Sound.BLOCK_GLASS_BREAK, 1, 0);
                    return;
                }
                // Make sure player has enough money
                long PlayerBankBalance = PlayerTrackerUtil.getPlayerTracker(player).player_bank_balance;
                if (PlayerBankBalance < item.getArtifactInfo().getCostToIdentify()) {
                    Msg.send(player, "&cYou do not have enough money in the bank to identify this artifact!");
                    player.playSound(player.getLocation(), Sound.BLOCK_GLASS_BREAK, 1, 0);
                    return;
                }
                SlommyItemExchange.takeItems(player, item, 1);
                // Take the money
                PlayerTrackerUtil.getPlayerTracker(player).player_bank_balance -= item.getArtifactInfo().getCostToIdentify();
                IdentificationMenu menu = new IdentificationMenu();
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
                menu.open(player, Items.get(item.getArtifactInfo().getReturnItemID()), item.getArtifactInfo().getSeries());
            }
        });

        // add a back button
        ItemStack backButton = Items.get("gui/back").build(player);
        panel.setButton(49, new ItemButton(backButton) {
            @Override
            public void onClick(MenuHolder menuHolder, InventoryClickEvent event) {
                ArtifactMenu.openArtifactMenu(player, item.getArtifactInfo().getSeries(), 1);
            }
        });

        player.openInventory(panel.getInventory());

    }
}
