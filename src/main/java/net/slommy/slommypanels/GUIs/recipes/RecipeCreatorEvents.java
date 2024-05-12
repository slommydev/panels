package net.slommy.slommypanels.GUIs.recipes;

import net.slommy.essentials.SlommyItems.recipes.Recipe;
import net.slommy.essentials.SlommyItems.recipes.RecipeItem;
import net.slommy.essentials.util.GetSlommyItem;
import net.slommy.essentials.util.Msg;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class RecipeCreatorEvents implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof RecipeCreator)) {
            return;
        }

        if (event.getRawSlot() > 53) {
            return;
        }
        ItemStack item = event.getCurrentItem();
        String itemID = GetSlommyItem.getFromItemTag(item);
        RecipeCreator holder = (RecipeCreator) event.getInventory().getHolder();
        if (itemID == null) {
            return;
        }
        if (itemID.split("/")[0].equalsIgnoreCase("gui")) {
            event.setCancelled(true);

            if (itemID.split("/")[1].equalsIgnoreCase("save_and_close")) {
                // Save the recipe and close the menu
                String path = holder.getFilePath();
                Msg.send(event.getWhoClicked(), "&7Attempting to save recipe " + holder.getCategory() + "/" + holder.getName());
                Recipe recipe = new Recipe(holder.getCategory(), holder.getName());
                int nullSlots = 0;
                if (event.getInventory().getItem(24) == null) {
                    Msg.send(event.getWhoClicked(), "&cRecipe cannot be saved because the result slot is empty");
                    return;
                }
                for (int i = 0; i < 9; i++) {
                    int x = i % 3;
                    int y = i / 3;
                    if (event.getInventory().getItem(getCraftingSlotFromSlotIndex(i)) == null) {
                        nullSlots++;
                        recipe.setRecipeItem(x, y, null);
                        continue;
                    }
                    Msg.send(event.getWhoClicked(), "&7Setting slot " + (i + 1)  + " to " + GetSlommyItem.getFromItemTag(event.getInventory().getItem(getCraftingSlotFromSlotIndex(i))));
                    ItemStack itemStack = event.getInventory().getItem(getCraftingSlotFromSlotIndex(i));
                    if (GetSlommyItem.getFromItemTag(itemStack) == null) continue;
                    RecipeItem recipeItem = new RecipeItem(GetSlommyItem.getFromItemTag(itemStack), itemStack.getAmount());
                    recipe.setRecipeItem(x, y, recipeItem);
                }
                if (nullSlots == 9) {
                    Msg.send(event.getWhoClicked(), "&cRecipe cannot be saved because all slots are empty");
                    return;
                }
                RecipeItem result = new RecipeItem(GetSlommyItem.getFromItemTag(event.getInventory().getItem(24)), event.getInventory().getItem(24).getAmount());
                recipe.setResult(result);
                boolean success = recipe.save();
                if (success) {
                    Msg.send(event.getWhoClicked(), "&aRecipe saved successfully :3");
                } else {
                    Msg.send(event.getWhoClicked(), "&cRecipe failed to save for some reason :(");
                }
            }
            if (itemID.split("/")[1].equalsIgnoreCase("close_without_saving")) {
                // Close the menu without saving
                event.getWhoClicked().closeInventory();
            }
        }
    }

    public static int getCraftingSlotFromSlotIndex(int slot) {
        switch (slot) {
            case 0:
                return 10;
            case 1:
                return 11;
            case 2:
                return 12;
            case 3:
                return 19;
            case 4:
                return 20;
            case 5:
                return 21;
            case 6:
                return 28;
            case 7:
                return 29;
            case 8:
                return 30;
            default:
                return 0;
        }
    }
}
