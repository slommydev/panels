package net.slommy.slommypanels.GUIs.artifacting;

import net.slommy.essentials.SlommyItems.Artifacting.ArtifactSeries;
import net.slommy.essentials.SlommyItems.Items;
import net.slommy.essentials.SlommyItems.PersistentDataUtils;
import net.slommy.slommypanels.SlommyPanels;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class SelectItemToUpgradeMenu implements InventoryHolder {
    @Override
    public org.bukkit.inventory.Inventory getInventory() {
        // Do not call this method!
        return null;
    }

    public ArtifactSeries series = null;

    public void openUpgradableItemsMenu(Player player, ArtifactSeries series) {
        this.series = series;
        Inventory inventory = SlommyPanels.getInstance().getServer().createInventory(this, 6*9, "Select Item to Upgrade");
        List<Integer> takenSlots = new ArrayList<>();
        // Fill perimeter with blank panes
        for (int i = 0; i < inventory.getSize(); i++) {
            if (i < 9 || i > 44 || i % 9 == 0 || i % 9 == 8) {
                inventory.setItem(i, Items.get("gui/blank_pane").build(player));
                takenSlots.add(i);
            }
        }

        // Get a list of items with the specified ArtifactSeries from the player's inventory
        Inventory playerInventory = player.getInventory();
        List<ItemStack> validUpgradableItems = new ArrayList<>();
        for (int i = 0; i < playerInventory.getSize(); i++) {
            // DO NOT INCLUDE ARMOR SLOTS
            if (i == 36 || i == 37 || i == 38 || i == 39) {
                continue;
            }
            ItemStack item = playerInventory.getItem(i);
            if (item == null) {
                continue;
            }
            if (PersistentDataUtils.isIdentifiedArtifact(item) && PersistentDataUtils.getArtifactSeries(item) != null) {
                if (!PersistentDataUtils.getArtifactSeries(item).equals(series)) {
                    continue;
                }
                validUpgradableItems.add(item);
            }
        }

        if (validUpgradableItems.size() > 28) {
            while (validUpgradableItems.size() > 28) {
                validUpgradableItems.remove(validUpgradableItems.size() - 1);
            }
        }
        int slot = 10;
        for (ItemStack item : validUpgradableItems) {
            while (takenSlots.contains(slot)) {
                slot++;
            }
            inventory.setItem(slot, item);
            slot++;
        }

        player.openInventory(inventory);

    }
}
