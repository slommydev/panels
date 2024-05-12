package net.slommy.slommypanels.GUIs.recipes;

import net.slommy.essentials.SlommyItems.Items;
import net.slommy.essentials.SlommyItems.recipes.Recipe;
import net.slommy.essentials.SlommyItems.recipes.Recipes;
import net.slommy.essentials.util.Msg;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class RecipeCreator implements InventoryHolder {
    Inventory inventory;
    private String category;
    private String name;

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public RecipeCreator(String category, String name) {
        this.category = category;
        this.name = name;
    }

    public void openMenu(Player player) {
        inventory = Bukkit.getServer().createInventory(this, 6*9, Msg.JSONcolor("Recipe: " + category + "/" + name));
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, Items.items.get("gui/blank_pane").build(player));
        }
        // Open up the slots for items
        inventory.setItem(10, null);
        inventory.setItem(11, null);
        inventory.setItem(12, null);
        inventory.setItem(19, null);
        inventory.setItem(20, null);
        inventory.setItem(21, null);
        inventory.setItem(28, null);
        inventory.setItem(29, null);
        inventory.setItem(30, null);

        inventory.setItem(24, null);

        inventory.setItem(48, Items.get("gui/save_and_close").build(player));
        inventory.setItem(50, Items.get("gui/close_without_saving").build(player));

        player.openInventory(inventory);
    }

    public void openRecipe(Player player, String category, String name) {
        inventory = Bukkit.getServer().createInventory(this, 6*9, Msg.JSONcolor("Recipe: " + category + "/" + name));
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, Items.items.get("gui/blank_pane").build(player));
        }
        // Open up the slots for items
        Recipe recipe = Recipes.recipes.get(category + "/" + name);
        if (recipe == null) {
            Msg.send(player, "&cRecipe not found");
            return;
        }

        for (int i = 0; i < 9; i++) {
            int x = i % 3;
            int y = i / 3;
            if (recipe.getRecipeItem(x, y) == null) {
                inventory.setItem(RecipeCreatorEvents.getCraftingSlotFromSlotIndex(i+1), null);
                continue;
            }
            ItemStack item = Items.get(recipe.getRecipeItem(x, y).getItemID()).build(player);
            item.setAmount(recipe.getRecipeItem(x, y).getAmount());
            inventory.setItem(RecipeCreatorEvents.getCraftingSlotFromSlotIndex(i+1), item);
        }

        if (recipe.getResult() != null) {
            ItemStack item = Items.get(recipe.getResult().getItemID()).build(player);
            item.setAmount(recipe.getResult().getAmount());
            inventory.setItem(24, item);
        } else {
            inventory.setItem(24, null);
        }

        inventory.setItem(48, Items.get("gui/save_and_close").build(player));
        inventory.setItem(50, Items.get("gui/close_without_saving").build(player));

        player.openInventory(inventory);
    }

    public String getCategory() {
        return category;
    }

    public String getName() {
        return name;
    }

    public String getFilePath() {
        return Bukkit.getPluginsFolder() + "/SlommyItems/recipes/" + category + "/" + name + ".yml";
    }
}
