package net.slommy.slommypanels.GUIs.recipes;

import net.slommy.essentials.SlommyItems.Items;
import net.slommy.essentials.SlommyItems.PersistentDataUtils;
import net.slommy.essentials.SlommyItems.PersistentItemData;
import net.slommy.essentials.SlommyItems.recipes.Recipe;
import net.slommy.essentials.SlommyItems.recipes.RecipeItem;
import net.slommy.essentials.SlommyItems.recipes.Recipes;
import net.slommy.essentials.util.GetSlommyItem;
import net.slommy.essentials.util.Msg;
import net.slommy.slommypanels.SlommyPanels;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Objects;

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

        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                if (player.getOpenInventory().getTopInventory().getHolder() instanceof CraftingTable) {
                    RecipeItem[][] craftItems = getCraftItems(player.getOpenInventory().getTopInventory());
                    Recipe recipe = Recipes.getRecipeFromPattern(craftItems);
                    if (recipe != null) {
                        ItemStack result = Items.get(recipe.getResult().getItemID()).build(player);
                        result.setAmount(recipe.getResult().getAmount());
                        for (int i = 0; i < 9; i++) {
                            int x = i % 3;
                            int y = i / 3;
                            if (recipe.getRecipeItem(x, y) == null) {
                                continue;
                            }
                            if (recipe.getRecipeItem(x, y).doCarryOverPersistentData()) {
                                List<PersistentItemData> data = GetSlommyItem.getPersistentDataValues(Objects.requireNonNull(player.getOpenInventory().getTopInventory().getItem(getCraftingSlotFromCoordinates(x, y))));
                                for (PersistentItemData datum : data) {
                                    PersistentDataUtils.addPersistentDataTag(result, datum);
                                }
                                break;
                            }
                        }
                        player.getOpenInventory().getTopInventory().setItem(24, result);
                    } else {
                        player.getOpenInventory().getTopInventory().setItem(24, null);
                    }
            } else {
                    this.cancel();
                }
        };
        };
        task.runTaskTimer(SlommyPanels.getInstance(), 1, 1);
    }

    public static RecipeItem[][] getCraftItems(Inventory inventory) {
        RecipeItem[][] items = new RecipeItem[3][3];
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                ItemStack item = inventory.getItem(getCraftingSlotFromCoordinates(x, y));
                if (item == null) {
                    items[x][y] = new RecipeItem("gui/empty_crafting_slot", 1);
                    items[x][y].setEmptySlot(true);
                    continue;
                }
                String itemID = GetSlommyItem.getFromItemTag(item);
                if (itemID == null) {
                    items[x][y] = new RecipeItem("gui/empty_crafting_slot", 1);
                    items[x][y].setEmptySlot(true);
                    continue;
                }
                items[x][y] = new RecipeItem(itemID, item.getAmount());
            }
        }
        return items;
    }

    public static int getCraftingSlotFromCoordinates(int x, int y) {
        return 10 + (y * 9) + x;
    }
}
