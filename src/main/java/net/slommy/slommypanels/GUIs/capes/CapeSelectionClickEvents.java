package net.slommy.slommypanels.GUIs.capes;

import net.slommy.essentials.SlommyItems.Item;
import net.slommy.essentials.SlommyItems.Items;
import net.slommy.essentials.SlommyItems.SlommyItem;
import net.slommy.essentials.player.PersistentPlayerData;
import net.slommy.essentials.player.PersistentPlayerDataUtil;
import net.slommy.essentials.util.GetSlommyItem;
import net.slommy.essentials.util.SoundLibrary;
import net.slommy.slommypanels.GUIs.imbuing.CharmImbuingMenu;
import net.slommy.slommypanels.GUIs.imbuing.SelectCharmToImbueMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class CapeSelectionClickEvents implements Listener {
    @EventHandler
    public static void onClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof SelectCapeToAddMenu)) {
            return;
        }

        event.setCancelled(true);
        if (event.getCurrentItem() == null) {
            return;
        }
        // Check if the item's slot is in the player's inventory
        if (event.getRawSlot() > 9*6 - 1) {
            event.setCancelled(true);
            return;
        }

        String itemID = GetSlommyItem.getFromItemTag(event.getCurrentItem());
        if (itemID == null) {
            return;
        }
        SlommyItem slommyItem = Items.get(itemID);
        if (slommyItem == null) {
            return;
        }
        if (slommyItem.getTypeEnum() != Item.type.CAPE) {
            if (itemID.equalsIgnoreCase("gui/back")) {
                Player player = (Player) event.getWhoClicked();
                CapiumMenu.openCapiumMenu(player);
            }
        } else {
            PersistentPlayerData data = PersistentPlayerDataUtil.getPersistentPlayerData((Player) event.getWhoClicked());
            List<String> cape_collection_ids = data.getCapeCollectionIDs();
            cape_collection_ids.add(itemID);
            data.setCapeCollectionIDs(cape_collection_ids);
            List<ItemStack> cape_collection_items = data.getCapeCollectionItems();
            cape_collection_items.add(event.getCurrentItem());
            data.setCapeCollectionItems(cape_collection_items);
            // take away the cape from the player
            event.getWhoClicked().getInventory().remove(event.getCurrentItem());
            // Return to the cape menu
            Player player = (Player) event.getWhoClicked();
            SoundLibrary.capiumJingle(player);
            CapiumMenu.openCapiumMenu(player);
            data.save();
        }
    }
}
