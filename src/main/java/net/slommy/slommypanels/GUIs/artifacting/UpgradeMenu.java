package net.slommy.slommypanels.GUIs.artifacting;

import net.kyori.adventure.text.Component;
import net.slommy.essentials.SlommyItems.Artifacting.ArtifactSeries;
import net.slommy.essentials.SlommyItems.Item;
import net.slommy.essentials.SlommyItems.Items;
import net.slommy.essentials.SlommyItems.PersistentDataUtils;
import net.slommy.essentials.SlommyItems.SlommyItem;
import net.slommy.essentials.util.GetSlommyItem;
import net.slommy.essentials.util.Msg;
import net.slommy.slommypanels.SlommyPanels;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class UpgradeMenu implements InventoryHolder {
    @Override
    public Inventory getInventory() {
        // Do not call this method!
        return null;
    }

    public ArtifactSeries series = null;

    public void openUpgradeMenu(Player player, ItemStack item, ArtifactSeries series) {
        this.series = series;
        Inventory inventory = SlommyPanels.getInstance().getServer().createInventory(this, 5*9, Msg.JSONcolor("Upgrade Artifacts"));
        // fill with blank panes
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, Items.get("gui/blank_pane").build(player));
        }
        // Add the item to the slot
        if (item != null) {
            inventory.setItem(20, item);
            Inventory playerInventory = player.getInventory();
            // Remove the item from the player's inventory
            for (int i = 0; i < playerInventory.getSize(); i++) {
                if (playerInventory.getItem(i) != null && playerInventory.getItem(i).equals(item)) {
                    playerInventory.setItem(i, null);
                    break;
                }
            }

            // Open up the 5 slots for upgrades
            for (int i = 0; i < 5; i++) {
                if (PersistentDataUtils.getArtifactUpgraded(item) > i) {
                    ItemStack upgradeItem = series.getUpgradeItem().build(player);
                    ItemMeta meta = upgradeItem.getItemMeta();
                    meta.displayName(Msg.JSONcolor("&a&lUpgrade #" + (i + 1) + ": " + series.getUpgradeItem().getDisplayName()));
                    upgradeItem.setItemMeta(meta);
                    inventory.setItem(7 + (i * 9), upgradeItem);
                    continue;
                }
                ItemStack blankItem = Items.get("gui/add_artifact_upgrade").build(player);
                ItemMeta meta = blankItem.getItemMeta();
                meta.displayName(Msg.JSONcolor("&aUpgrade Slot #" + (i + 1)));
                blankItem.setItemMeta(meta);
                inventory.setItem(7 + (i * 9), blankItem);
            }

            if (PersistentDataUtils.getArtifactUpgraded(item) < 5 && PersistentDataUtils.getArtifactFidelity(item) < 100) {
                ItemStack upgradeItem = series.getUpgradeItem().build(player);
                ItemMeta meta = upgradeItem.getItemMeta();
                meta.displayName(Msg.JSONcolor("&aAdd " + series.getUpgradeItem().getDisplayName() + " &ato " + item.getItemMeta().getDisplayName()));
                List<Component> lore = new ArrayList<>();
                meta.lore(lore);
                upgradeItem.setItemMeta(meta);
                inventory.setItem(22, upgradeItem);
            }

        } else {
            ItemStack addArtifactItem = Items.get("gui/add_artifact_item").build(player);
            ItemMeta meta = addArtifactItem.getItemMeta();
            meta.displayName(Msg.JSONcolor("&aInsert item from the " + series.getName() + " Artifact Series"));
            addArtifactItem.setItemMeta(meta);
            inventory.setItem(20, addArtifactItem);
            inventory.setItem(22, Items.get("gui/blank_red").build(player));
            for (int i = 0; i < 5; i++) {
                inventory.setItem(7 + (i * 9), Items.get("gui/blank_red").build(player));
            }
        }

        player.openInventory(inventory);


    }
}
