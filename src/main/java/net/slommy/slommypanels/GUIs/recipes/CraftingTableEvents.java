package net.slommy.slommypanels.GUIs.recipes;

import net.slommy.essentials.SlommyItems.recipes.Recipe;
import net.slommy.essentials.SlommyItems.recipes.Recipes;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

public class CraftingTableEvents implements Listener {
    @EventHandler
    public void onClickCraftingTable(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) {
            return;
        }
        if (event.getClickedBlock().getType() == Material.CRAFTING_TABLE && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            event.setCancelled(true);
            CraftingTable.openMenu(event.getPlayer());
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof CraftingTable) {
            List<Integer> validClickableSlots = List.of(10, 11, 12, 19, 20, 21, 28, 29, 30);
            if (!validClickableSlots.contains(event.getRawSlot()) && event.getRawSlot() != 24) {
                // if the slot is in the player's inventory do not cancel it
                event.setCancelled(true);
                if (event.getRawSlot() >= 5 * 9) event.setCancelled(false);
            }
            if (event.getRawSlot() == 24) {
                Recipe craftedRecipe = Recipes.getRecipeFromPattern(CraftingTable.getCraftItems(event.getInventory()));
                if (craftedRecipe != null) {
                    // Remove the items from the crafting table
                    for (int i = 0; i < 9; i++) {
                        int x = i % 3;
                        int y = i / 3;
                        if (craftedRecipe.getRecipeItem(x, y).getItemID().equalsIgnoreCase("gui/empty_crafting_slot")) {
                            continue;
                        }
                        else {
                            if (event.getInventory().getItem(CraftingTable.getCraftingSlotFromCoordinates(x, y)) == null) continue;
                            event.getInventory().getItem(CraftingTable.getCraftingSlotFromCoordinates(x, y)).setAmount(event.getInventory().getItem(CraftingTable.getCraftingSlotFromCoordinates(x, y)).getAmount() - craftedRecipe.getRecipeItem(x, y).getAmount());
                        }
                    }
                }
            }
        }
    }
    @EventHandler
    public void onCloseInventory(InventoryCloseEvent event) {
        if (event.getInventory().getHolder() instanceof CraftingTable) {
            for (int i = 0; i < 9; i++) {
                int x = i % 3;
                int y = i / 3;
                if (event.getInventory().getItem(CraftingTable.getCraftingSlotFromCoordinates(x, y)) == null) {
                    continue;
                }
                // If the item is not an empty crafting slot, drop it
                if (event.getInventory().getItem(CraftingTable.getCraftingSlotFromCoordinates(x, y)) != null) {
                    event.getPlayer().getWorld().dropItem(event.getPlayer().getLocation(), event.getInventory().getItem(CraftingTable.getCraftingSlotFromCoordinates(x, y)));
                }
            }
        }
    }
}
