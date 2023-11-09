package net.slommy.slommypanels.GUIs.recipes;

import net.slommy.essentials.SlommyItems.Items;
import net.slommy.essentials.util.Msg;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class CraftingTable implements InventoryHolder {
    @Override
    public org.bukkit.inventory.Inventory getInventory() {
        return null;
    }

    public static void openMenu(Player player) {
        Inventory table = Bukkit.getServer().createInventory(new CraftingTable(), 5*9, Msg.JSONcolor("Crafting Table"));
        for (int i = 0; i < table.getSize(); i++) {
            table.setItem(i, Items.items.get("gui/blank_pane").build(player));
        }
        // Open up the slots for items
        table.setItem(10, null);
        table.setItem(11, null);
        table.setItem(12, null);
        table.setItem(19, null);
        table.setItem(20, null);
        table.setItem(21, null);
        table.setItem(28, null);
        table.setItem(29, null);
        table.setItem(30, null);

        table.setItem(24, null);

        player.openInventory(table);
    }
}
