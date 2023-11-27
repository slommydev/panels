package net.slommy.slommypanels.GUIs.recipes;

import net.slommy.essentials.SlommyItems.Item;
import net.slommy.essentials.SlommyItems.Items;
import net.slommy.essentials.SlommyItems.SlommyItem;
import net.slommy.essentials.SlommyItems.recipes.Recipe;
import net.slommy.essentials.util.Msg;
import net.slommy.slommypanels.SlommyPanels;
import net.slommy.slommypanels.util.Centerer;
import net.slommy.slommypanels.util.CommonButtons;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import xyz.janboerman.guilib.api.menu.ItemButton;
import xyz.janboerman.guilib.api.menu.MenuHolder;

import java.util.ArrayList;
import java.util.List;

public class RecipeBook {
    public static void openCategorySelectorMenu(Player player) {
        MenuHolder<SlommyPanels> panel = new MenuHolder<SlommyPanels>(SlommyPanels.getInstance(), 6*9, "Recipe Book");
        for (int i = 0; i < 6*9; i++) {
            panel.setButton(i, CommonButtons.blankButton(player));
        }
        List<Item.recipeCategory> categories = Item.recipeCategory.getCategories();
        for (int i = 0; i < categories.size(); i++) {
            Item.recipeCategory category = categories.get(i);
            panel.setButton(category.getSlot(), new ItemButton(category.getCategoryItem().build(player)) {
                @Override
                public void onClick(MenuHolder menuHolder, InventoryClickEvent event) {
                    openCategoryMenu(player, category, 0);
                }
            });
        }
        panel.setButton(4, new ItemButton(Items.get("gui/recipe_book").build(player)));
        panel.setButton(49, new ItemButton(Items.get("gui/close").build(player)) {
            @Override
            public void onClick(MenuHolder menuHolder, InventoryClickEvent event) {
                player.closeInventory();
            }
        });
        player.openInventory(panel.getInventory());
    }

    public static void openCategoryMenu(Player player, Item.recipeCategory category, int page) {
        MenuHolder<SlommyPanels> panel;
        List<Integer> takenSlots = new ArrayList<>();
        List<Recipe> recipes = category.getRecipes();
        int pages = (int) Math.ceil(recipes.size() / 28.0);
        if (pages == 0) {
            Msg.send(player, "&cThere are no recipes of this type.");
            return;
        }
        if (page < 0 || page >= pages) {
            Msg.send(player, "&cInvalid page number");
            return;
        }
        String singularizedName = category.getName();
        if (singularizedName.endsWith("s")) {
            singularizedName = singularizedName.substring(0, singularizedName.length() - 1);
        }
        panel = new MenuHolder<>(SlommyPanels.getInstance(), 6*9, singularizedName + " Recipes (" + (page + 1) + "/" + pages + ")");
        // make a border of empty panes around the menu
        for (int i = 0; i < 54; i++) {
            if (i < 9 || i > 44 || i % 9 == 0 || i % 9 == 8) {
                panel.setButton(i, new ItemButton<>(Items.items.get("gui/blank_pane").build(player)));
                takenSlots.add(i);
            }
        }
        // sort recipes by rarity first, then by name
        recipes.sort((o1, o2) -> {
            SlommyItem item1 = Items.get(o1.getResult().getItemID());
            SlommyItem item2 = Items.get(o2.getResult().getItemID());
            if (item1 == null || item2 == null) {
                return 0;
            }
            if (item1.getRarityEnum().value() == item2.getRarityEnum().value()) {
                return o1.getName().compareTo(o2.getName());
            }
            return item1.getRarityEnum().value() - item2.getRarityEnum().value();
        });

        // add the recipes to the menu
        List<Recipe> recipesOnPage = new ArrayList<>();
        for (int i = page * 28; i < recipes.size(); i++) {
            recipesOnPage.add(recipes.get(i));
            if (recipesOnPage.size() == 28) {
                break;
            }
        }
        int i = 10;
        for (Recipe recipe : recipesOnPage) {
            while (takenSlots.contains(i)) {
                i++;
            }
            if (i > 44) break;
            if (Items.get(recipe.getResult().getItemID()) == null) {
                if (player.hasPermission("dev.true")) {
                    Msg.send(player, "&cRecipe has a null result! &7" + recipe.getResult().getItemID());
                }
                continue;
            }
            panel.setButton(i, new ItemButton(Items.get(recipe.getResult().getItemID()).build(player)) {
                @Override
                public void onClick(MenuHolder menuHolder, InventoryClickEvent event) {
                    openRecipe(player, recipe, category, page);
                }
            });
            takenSlots.add(i);
        }
        // add the page buttons
        if (page > 0) {
            panel.setButton(45, new ItemButton(Items.get("gui/prev_page").build(player)) {
                @Override
                public void onClick(MenuHolder menuHolder, InventoryClickEvent event) {
                    openCategoryMenu(player, category, page - 1);
                }
            });
        }
        if (page < pages - 1) {
            panel.setButton(53, new ItemButton(Items.get("gui/next_page").build(player)) {
                @Override
                public void onClick(MenuHolder menuHolder, InventoryClickEvent event) {
                    openCategoryMenu(player, category, page + 1);
                }
            });
        }
        // add the close button
        panel.setButton(49, new ItemButton(Items.get("gui/go_back").build(player)) {
            @Override
            public void onClick(MenuHolder menuHolder, InventoryClickEvent event) {
                openCategorySelectorMenu(player);
            }
        });
        // add the icon
        panel.setButton(4, new ItemButton(category.getCategoryItem().build(player)));
        player.openInventory(panel.getInventory());
    }

    public static void openRecipe(Player player, Recipe recipe, Item.recipeCategory category, int categoryPage) {
        MenuHolder<SlommyPanels> panel = new MenuHolder<>(SlommyPanels.getInstance(), 6*9, "Recipe: " + Items.get(recipe.getResult().getItemID()).getRawDisplayName());
        List<Integer> takenSlots = new ArrayList<>();
        takenSlots.add(10);
        takenSlots.add(11);
        takenSlots.add(12);
        takenSlots.add(19);
        takenSlots.add(20);
        takenSlots.add(21);
        takenSlots.add(28);
        takenSlots.add(29);
        takenSlots.add(30);
        takenSlots.add(24);
        for (int i = 0; i < 6*9; i++) {
            while (takenSlots.contains(i)) {
                i++;
            }
            panel.setButton(i, CommonButtons.blankButton(player));
        }
        List<ItemStack> slots = new ArrayList<>();
        for (int i = 1; i < 10; i++) {
            if (recipe.getSlot(i) == null) {
                slots.add(new ItemStack(Material.AIR));
                continue;
            }
            ItemStack item = Items.get(recipe.getSlot(i).getItemID()).build(player);
            item.setAmount(recipe.getSlot(i).getAmount());
            if (item == null) {
                item = new ItemStack(Material.AIR);
            }
            slots.add(item);
        }
        panel.setButton(10, new ItemButton(slots.get(0)));
        panel.setButton(11, new ItemButton(slots.get(1)));
        panel.setButton(12, new ItemButton(slots.get(2)));
        panel.setButton(19, new ItemButton(slots.get(3)));
        panel.setButton(20, new ItemButton(slots.get(4)));
        panel.setButton(21, new ItemButton(slots.get(5)));
        panel.setButton(28, new ItemButton(slots.get(6)));
        panel.setButton(29, new ItemButton(slots.get(7)));
        panel.setButton(30, new ItemButton(slots.get(8)));
        panel.setButton(24, new ItemButton(Items.get(recipe.getResult().getItemID()).build(player)));
        panel.setButton(49, new ItemButton(Items.get("gui/go_back").build(player)) {
            @Override
            public void onClick(MenuHolder menuHolder, InventoryClickEvent event) {
                openCategoryMenu(player, category, categoryPage);
            }
        });
        player.openInventory(panel.getInventory());
    }
}
