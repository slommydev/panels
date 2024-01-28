package net.slommy.slommypanels.GUIs.artifacting.events;

import net.slommy.essentials.util.GetSlommyItem;
import net.slommy.slommypanels.GUIs.artifacting.IdentificationMenu;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.Objects;

public class IdentificationMenuEventManagers implements Listener {
    @EventHandler
    public void onInvClick(InventoryClickEvent event) {
        if (!(event.getClickedInventory().getHolder() instanceof IdentificationMenu)) {
            return;
        }

        if (GetSlommyItem.getFromItemTag(Objects.requireNonNull(event.getCurrentItem())).startsWith("gui/")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInvClose(InventoryCloseEvent event) {
        if (!(event.getInventory().getHolder() instanceof IdentificationMenu)) {
            return;
        }

        // check if slot number 13 is empty
        if (event.getInventory().getItem(13) == null) {
            return;
        } else {
            // Drop item on the floor at player, make sure only player can pick it up.
            event.getPlayer().getWorld().dropItemNaturally(event.getPlayer().getLocation(), Objects.requireNonNull(event.getInventory().getItem(13)));
        }
    }
}
