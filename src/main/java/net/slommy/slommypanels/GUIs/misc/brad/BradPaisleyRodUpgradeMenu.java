package net.slommy.slommypanels.GUIs.misc.brad;

import net.kyori.adventure.text.Component;
import net.slommy.essentials.SlommyItems.Artifacting.ArtifactSeries;
import net.slommy.essentials.SlommyItems.Items;
import net.slommy.essentials.SlommyItems.PersistentDataUtils;
import net.slommy.essentials.SlommyItems.SlommyItem;
import net.slommy.essentials.util.Msg;
import net.slommy.slommypanels.SlommyPanels;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class BradPaisleyRodUpgradeMenu implements InventoryHolder {
    public void openUpgradeMenu(Player player, ItemStack item) {
        Inventory inventory = SlommyPanels.getInstance().getServer().createInventory(this, 5*9, Msg.JSONcolor("Upgrade Brad Paisley's Rod"));
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
            SlommyItem upgradeSlommyItem = Items.get("fish/bass_pro");
            for (int i = 0; i < 5; i++) {
                if (PersistentDataUtils.getBradPaisleyUpgrade(item) > i) {
                    ItemStack upgradeItem = upgradeSlommyItem.build(player);
                    ItemMeta meta = upgradeItem.getItemMeta();
                    meta.displayName(Msg.JSONcolor("&a&lUpgrade #" + (i + 1) + ": " + upgradeSlommyItem.getDisplayName()));
                    List<Component> lore = new ArrayList<>();
                    meta.lore(lore);
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
            for (int i = 0; i < 5; i++) {
                if (PersistentDataUtils.getBradPaisleyUpgrade(item) > i + 5) {
                    ItemStack upgradeItem = upgradeSlommyItem.build(player);
                    ItemMeta meta = upgradeItem.getItemMeta();
                    meta.displayName(Msg.JSONcolor("&a&lUpgrade #" + (i + 6) + ": " + upgradeSlommyItem.getDisplayName()));
                    List<Component> lore = new ArrayList<>();
                    meta.lore(lore);
                    upgradeItem.setItemMeta(meta);
                    inventory.setItem(8 + (i * 9), upgradeItem);
                    continue;
                }
                ItemStack blankItem = Items.get("gui/add_artifact_upgrade").build(player);
                ItemMeta meta = blankItem.getItemMeta();
                meta.displayName(Msg.JSONcolor("&aUpgrade Slot #" + (i + 6)));
                blankItem.setItemMeta(meta);
                inventory.setItem(8 + (i * 9), blankItem);
            }

            if (PersistentDataUtils.getBradPaisleyUpgrade(item) < 10) {
                ItemStack upgradeItem = upgradeSlommyItem.build(player);
                ItemMeta meta = upgradeItem.getItemMeta();
                meta.displayName(Msg.JSONcolor("&aAdd " + upgradeSlommyItem.getDisplayName() + " &ato " + item.getItemMeta().getDisplayName()));
                List<Component> lore = new ArrayList<>();
                meta.lore(lore);
                upgradeItem.setItemMeta(meta);
                inventory.setItem(22, upgradeItem);
            }

        } else {
            ItemStack addArtifactItem = Items.get("gui/add_artifact_item").build(player);
            ItemMeta meta = addArtifactItem.getItemMeta();
            meta.displayName(Msg.JSONcolor("You shouldn't be here!"));
            addArtifactItem.setItemMeta(meta);
            inventory.setItem(20, addArtifactItem);
            inventory.setItem(22, Items.get("gui/blank_red").build(player));
            for (int i = 0; i < 5; i++) {
                inventory.setItem(7 + (i * 9), Items.get("gui/blank_red").build(player));
            }
        }

        player.openInventory(inventory);


    }

    @Override
    public @NotNull Inventory getInventory() {
        return null;
    }
}
