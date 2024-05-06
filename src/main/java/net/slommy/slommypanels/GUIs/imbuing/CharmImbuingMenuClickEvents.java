package net.slommy.slommypanels.GUIs.imbuing;

import net.slommy.essentials.SlommyItems.Item;
import net.slommy.essentials.SlommyItems.Items;
import net.slommy.essentials.SlommyItems.SlommyItem;
import net.slommy.essentials.player.PersistentPlayerData;
import net.slommy.essentials.player.PersistentPlayerDataUtil;
import net.slommy.essentials.util.GetSlommyItem;
import net.slommy.essentials.util.SoundLibrary;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class CharmImbuingMenuClickEvents implements Listener {
    @EventHandler
    public static void onClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof SelectCharmToImbueMenu)) {
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
        if (slommyItem.getTypeEnum() != Item.type.CHARM) {
            if (itemID.equalsIgnoreCase("gui/back")) {
                Player player = (Player) event.getWhoClicked();
                CharmImbuingMenu menu = new CharmImbuingMenu();
                menu.openCharmImbuingMenu(player);
            }
            return;
        } else {
            PersistentPlayerData data = PersistentPlayerDataUtil.getPersistentPlayerData((Player) event.getWhoClicked());
            List<String> imbued_charm_ids = data.getImbuedCharms();
            imbued_charm_ids.add(itemID);
            data.setImbuedCharms(imbued_charm_ids);
            List<ItemStack> imbued_charm_items = data.getImbuedCharmsItems();
            imbued_charm_items.add(event.getCurrentItem());
            data.setImbuedCharmsItems(imbued_charm_items);
            // take away the charm from the player
            event.getWhoClicked().getInventory().remove(event.getCurrentItem());
            // Return to the imbuement menu
            Player player = (Player) event.getWhoClicked();
            SoundLibrary.imbueSlot(player);
            CharmImbuingMenu menu = new CharmImbuingMenu();
            menu.openCharmImbuingMenu(player);
            data.save();
        }
    }
}
