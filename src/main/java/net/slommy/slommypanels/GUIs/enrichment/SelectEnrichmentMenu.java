package net.slommy.slommypanels.GUIs.enrichment;

import net.kyori.adventure.text.Component;
import net.slommy.essentials.SlommyItems.Items;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectEnrichmentMenu implements InventoryHolder {
    @Override
    public Inventory getInventory() {
        return SlommyPanels.getInstance().getServer().createInventory(this, 6*9, Msg.JSONcolor("Select Enrichment"));
    }

    public void openEnrichmentSelectMenu(Player player, ItemStack item, int enrichmentSlot) {
        Inventory inv = SlommyPanels.getInstance().getServer().createInventory(this, 6*9, Msg.JSONcolor("Select Enrichment"));
        // Fill the perimeter of the inventory with blank panes
        List<Integer> takenSlots = new ArrayList<>();
        for (int i = 0; i < inv.getSize(); i++) {
            if (i < 9 || i > 44 || i % 9 == 0 || i % 9 == 8) {
                inv.setItem(i, Items.items.get("gui/blank_pane").build(player));
                takenSlots.add(i);
            }
        }
        // Place the item that is being enriched in the top center of the inventory
        inv.setItem(4, item);
        // Place the enrichment options in the inventory
        Map<String, Integer> enrichmentOptions = new HashMap<>();
        Inventory playerInventory = player.getInventory();
        for (int i = 0; i < playerInventory.getSize(); i++) {
            ItemStack enrichmentItem = playerInventory.getItem(i);
            if (enrichmentItem == null) {
                continue;
            }
            String enrichmentItemID = GetSlommyItem.getFromItemTag(enrichmentItem);
            if (enrichmentItemID == null) {
                continue;
            }
            SlommyItem slommyItem = Items.items.get(enrichmentItemID);
            if (!slommyItem.getType().equalsIgnoreCase("enrichment")) {
                continue;
            }
            int currentAmount = enrichmentOptions.getOrDefault(enrichmentItemID, 0);
            enrichmentOptions.put(enrichmentItemID, currentAmount + 1);
        }
        int slot = 10;
        for (String enrichmentItemID : enrichmentOptions.keySet()) {
            while(takenSlots.contains(slot)) {
                slot++;
            }
            ItemStack enrichmentItem = Items.items.get(enrichmentItemID).build(player);
            enrichmentItem.setAmount(enrichmentOptions.get(enrichmentItemID));
            inv.setItem(slot, enrichmentItem);
            slot++;
        }
        inv.setItem(49, Items.items.get("gui/back").build(player));
        ItemStack itemWithSlotName = inv.getItem(0);
        ItemMeta meta = itemWithSlotName.getItemMeta();
        meta.displayName(Component.text(enrichmentSlot));
        itemWithSlotName.setItemMeta(meta);
        inv.setItem(0, itemWithSlotName);
        player.openInventory(inv);
    }
}
