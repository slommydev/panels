package net.slommy.slommypanels.GUIs.enrichment;

import net.slommy.essentials.SlommyItems.Item;
import net.slommy.essentials.SlommyItems.Items;
import net.slommy.essentials.SlommyItems.SlommyItem;
import net.slommy.essentials.util.GetSlommyItem;
import net.slommy.essentials.util.Msg;
import net.slommy.slommypanels.SlommyPanels;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class SelectItemToEnrichMenu implements InventoryHolder {
    @Override
    public Inventory getInventory() {
        return SlommyPanels.getInstance().getServer().createInventory(this, 6*9, "Select Item to Enrich");
    }

    public void openEnrichableItemsMenu(Player player) {
        Inventory inventory = SlommyPanels.getInstance().getServer().createInventory(this, 6*9, Msg.JSONcolor("Enrichable Items"));
        // Fill perimeter with blank panes
        List<Integer> takenSlots = new ArrayList<>();
        for (int i = 0; i < inventory.getSize(); i++) {
            if (i < 9 || i > 44 || i % 9 == 0 || i % 9 == 8) {
                inventory.setItem(i, Items.items.get("gui/blank_pane").build(player));
                takenSlots.add(i);
            }
        }
        // Get a list of enrichable items from the player
        Inventory playerInventory = player.getInventory();
        List<ItemStack> enrichableItems = new ArrayList<>();
        for (int i = 0; i < playerInventory.getSize(); i++) {
            // DO NOT INCLUDE ARMOR SLOTS
            if (i == 36 || i == 37 || i == 38 || i == 39) {
                continue;
            }
            ItemStack item = playerInventory.getItem(i);
            if (item == null) {
                continue;
            }
            String itemID = GetSlommyItem.getFromItemTag(item);
            SlommyItem slommyItem = Items.items.get(itemID);
            if (slommyItem == null) {
                continue;
            }
            if (Item.type.canEnrich(Item.findType(slommyItem.getType()))) {
                enrichableItems.add(item);
            }
        }
        // Populate the empty spaces with enrichable items
        if (enrichableItems.size() > 28) {
            // Remove the last items from the list until it is 27 items long
            while (enrichableItems.size() > 28) {
                enrichableItems.remove(enrichableItems.size() - 1);
            }
        }
        int slot = 10;
        for (ItemStack item : enrichableItems) {
            while (takenSlots.contains(slot)) {
                slot++;
            }
            inventory.setItem(slot, item);
            slot++;
        }
        player.openInventory(inventory);
    }
}
