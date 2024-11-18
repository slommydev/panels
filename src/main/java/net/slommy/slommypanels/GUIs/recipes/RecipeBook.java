package net.slommy.slommypanels.GUIs.recipes;

import net.kyori.adventure.text.Component;
import net.slommy.essentials.SlommyItems.Item;
import net.slommy.essentials.SlommyItems.ItemUpdateUtils;
import net.slommy.essentials.SlommyItems.Items;
import net.slommy.essentials.SlommyItems.SlommyItem;
import net.slommy.essentials.SlommyItems.recipes.Recipe;
import net.slommy.essentials.SlommyItems.recipes.Recipes;
import net.slommy.essentials.requirements.Requirement;
import net.slommy.essentials.util.Msg;
import net.slommy.slommypanels.SlommyPanels;
import net.slommy.slommypanels.util.Centerer;
import net.slommy.slommypanels.util.CommonButtons;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
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
        for (int j = 0; j < 6*9; j++) {
            ItemUpdateUtils.updateItemInContainer(player, j);
        }
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
            boolean meetsRequirements = true;
            for (Requirement requirement : Items.get(recipe.getResult().getItemID()).getRequirements()) {
                if (!requirement.check(player)) meetsRequirements = false;
            }
            if (meetsRequirements) {
                panel.setButton(i, new ItemButton(Items.get(recipe.getResult().getItemID()).build(player)) {
                    @Override
                    public void onClick(MenuHolder menuHolder, InventoryClickEvent event) {
                        openRecipe(player, recipe, panel, page);
                    }
                });
            } else {
                ItemStack item = new ItemStack(Material.GRAY_DYE);
                ItemMeta meta = item.getItemMeta();
                List<Component> lore = new ArrayList<>();
                meta.displayName(Msg.JSONcolor("&7" + Items.get(recipe.getResult().getItemID()).getRawDisplayName() + " &8(locked)"));
                for (Requirement requirement : Items.get(recipe.getResult().getItemID()).getRequirements()) {
                    if (!requirement.check(player)) {
                        lore.add(Msg.JSONcolor("&4✘ &c" + requirement.getLockedMessage()));
                    } else {
                        lore.add(Msg.JSONcolor("&2✔ &a" + requirement.getLockedMessage()));
                    }
                }
                meta.lore(lore);
                item.setItemMeta(meta);
                panel.setButton(i, new ItemButton<>(item));
            }

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
        for (int j = 0; i < 6*9; i++) {
            ItemUpdateUtils.updateItemInContainer(player, i);
        }
    }

    public static void openRecipe(Player player, Recipe recipe, MenuHolder previousMenu, int categoryPage) {
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
        ItemStack[][] slots = new ItemStack[3][3];
        SlommyItem[][] slommyItems = new SlommyItem[3][3];
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                if (recipe.getRecipeItem(x, y) == null) {
                    slots[x][y] = new ItemStack(Material.AIR);
                    slommyItems[x][y] = Items.get("gui/error");
                    Msg.send(player, "&cRecipe has a null item at x: &7" + x + " &cy: &7" + y);
                    continue;
                }
                if (Items.get(recipe.getRecipeItem(x, y).getItemID()) == null) {
                    continue;
                }
                ItemStack item = Items.get(recipe.getRecipeItem(x, y).getItemID()).build(player);
                item.setAmount(recipe.getRecipeItem(x, y).getAmount());
                slots[x][y] = item;
                slommyItems[x][y] = Items.get(recipe.getRecipeItem(x, y).getItemID());
            }
        }
        panel.setButton(10, new ItemButton(slots[0][0]) {
            @Override
            public void onClick(MenuHolder menuHolder, InventoryClickEvent event) {
                if (event.getClick() == ClickType.RIGHT) {
                    searchForUsages(player, slommyItems[0][0], panel);
                } else if (event.getClick() == ClickType.LEFT) {
                    searchRecipes(player, slommyItems[0][0], panel);
                }
            }
        });
        panel.setButton(11, new ItemButton(slots[1][0]) {
            @Override
            public void onClick(MenuHolder menuHolder, InventoryClickEvent event) {
                if (event.getClick() == ClickType.RIGHT) {
                    searchForUsages(player, slommyItems[1][0], panel);
                } else if (event.getClick() == ClickType.LEFT) {
                    searchRecipes(player, slommyItems[1][0], panel);
                }
            }
        });
        panel.setButton(12, new ItemButton(slots[2][0]) {
            @Override
            public void onClick(MenuHolder menuHolder, InventoryClickEvent event) {
                if (event.getClick() == ClickType.RIGHT) {
                    searchForUsages(player, slommyItems[2][0], panel);
                } else if (event.getClick() == ClickType.LEFT) {
                    searchRecipes(player, slommyItems[2][0], panel);
                }
            }
        });
        panel.setButton(19, new ItemButton(slots[0][1]) {
            @Override
            public void onClick(MenuHolder menuHolder, InventoryClickEvent event) {
                if (event.getClick() == ClickType.RIGHT) {
                    searchForUsages(player, slommyItems[0][1], panel);
                } else if (event.getClick() == ClickType.LEFT) {
                    searchRecipes(player, slommyItems[0][1], panel);
                }
            }
        });
        panel.setButton(20, new ItemButton(slots[1][1]) {
            @Override
            public void onClick(MenuHolder menuHolder, InventoryClickEvent event) {
                if (event.getClick() == ClickType.RIGHT) {
                    searchForUsages(player, slommyItems[1][1], panel);
                } else if (event.getClick() == ClickType.LEFT) {
                    searchRecipes(player, slommyItems[1][1], panel);
                }
            }
        });
        panel.setButton(21, new ItemButton(slots[2][1]) {
            @Override
            public void onClick(MenuHolder menuHolder, InventoryClickEvent event) {
                if (event.getClick() == ClickType.RIGHT) {
                    searchForUsages(player, slommyItems[2][1], panel);
                } else if (event.getClick() == ClickType.LEFT) {
                    searchRecipes(player, slommyItems[2][1], panel);
                }
            }
        });
        panel.setButton(28, new ItemButton(slots[0][2]) {
            @Override
            public void onClick(MenuHolder menuHolder, InventoryClickEvent event) {
                if (event.getClick() == ClickType.RIGHT) {
                    searchForUsages(player, slommyItems[0][2], panel);
                } else if (event.getClick() == ClickType.LEFT) {
                    searchRecipes(player, slommyItems[0][2], panel);
                }
            }
        });
        panel.setButton(29, new ItemButton(slots[1][2]) {
            @Override
            public void onClick(MenuHolder menuHolder, InventoryClickEvent event) {
                if (event.getClick() == ClickType.RIGHT) {
                    searchForUsages(player, slommyItems[1][2], panel);
                } else if (event.getClick() == ClickType.LEFT) {
                    searchRecipes(player, slommyItems[1][2], panel);
                }
            }
        });
        panel.setButton(30, new ItemButton(slots[2][2]) {
            @Override
            public void onClick(MenuHolder menuHolder, InventoryClickEvent event) {
                if (event.getClick() == ClickType.RIGHT) {
                    searchForUsages(player, slommyItems[2][2], panel);
                } else if (event.getClick() == ClickType.LEFT) {
                    searchRecipes(player, slommyItems[2][2], panel);
                }
            }
        });

        panel.setButton(24, new ItemButton(Items.get(recipe.getResult().getItemID()).build(player)) {
            @Override
            public void onClick(MenuHolder menuHolder, InventoryClickEvent event) {
                searchForUsages(player, Items.get(recipe.getResult().getItemID()), panel);
            }
        });
        panel.setButton(49, new ItemButton(Items.get("gui/go_back").build(player)) {
            @Override
            public void onClick(MenuHolder menuHolder, InventoryClickEvent event) {
                player.openInventory(previousMenu.getInventory());
            }
        });
        panel.setButton(53, new ItemButton(Items.get("gui/recipe_book_useful_controls").build(player)));
        player.openInventory(panel.getInventory());
        for (int j = 0; j < 6*9; j++) {
            ItemUpdateUtils.updateItemInContainer(player, j);
        }
    }

    public static void searchRecipes(Player player, SlommyItem result, MenuHolder previousMenu) {
        List<Recipe> recipes = Recipes.searchRecipes(result);
        if (recipes.size() == 0) {
            return;
        }
        if (recipes.size() > 1) {
            openSearchMenu(player, result, 0, previousMenu);
        }
        else {
            openRecipe(player, recipes.get(0), previousMenu, 0);
        }
    }

    public static void openSearchMenu(Player player, SlommyItem result, int page, MenuHolder previousMenu) {
        MenuHolder<SlommyPanels> panel;
        List<Integer> takenSlots = new ArrayList<>();
        List<Recipe> recipes = Recipes.searchRecipes(result);
        int pages = (int) Math.ceil(recipes.size() / 28.0);
        if (pages == 0) {
            Msg.send(player, "&cThere are no recipes for that item.");
            return;
        }
        if (page < 0 || page >= pages) {
            Msg.send(player, "&cInvalid page number");
            return;
        }
        panel = new MenuHolder<>(SlommyPanels.getInstance(), 6*9, "Recipes for " + result.getRawDisplayName() + " (" + (page + 1) + "/" + pages + ")");
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
                    openRecipe(player, recipe, panel, 0);
                }
            });
            takenSlots.add(i);
        }
        // add the page buttons
        if (page > 0) {
            panel.setButton(45, new ItemButton(Items.get("gui/prev_page").build(player)) {
                @Override
                public void onClick(MenuHolder menuHolder, InventoryClickEvent event) {
                    openSearchMenu(player, result, page - 1, previousMenu);
                }
            });
        }
        if (page < pages - 1) {
            panel.setButton(53, new ItemButton(Items.get("gui/next_page").build(player)) {
                @Override
                public void onClick(MenuHolder menuHolder, InventoryClickEvent event) {
                    openSearchMenu(player, result, page + 1, previousMenu);
                }
            });
        }
        // add the close button
        panel.setButton(49, new ItemButton(Items.get("gui/go_back").build(player)) {
            @Override
            public void onClick(MenuHolder menuHolder, InventoryClickEvent event) {
                player.openInventory(previousMenu.getInventory());
            }
        });
        // add the icon
        panel.setButton(4, new ItemButton(result.build(player)));
        player.openInventory(panel.getInventory());
        for (int j = 0; i < 6*9; i++) {
            ItemUpdateUtils.updateItemInContainer(player, i);
        }
    }

    public static void searchForUsages(Player player, SlommyItem ingredient, MenuHolder previousMenu) {
        List<Recipe> recipes = Recipes.searchRecipesForUsages(ingredient);
        if (recipes.size() == 0) {
            return;
        }
        openSearchForUsagesMenu(player, ingredient, 0, previousMenu);
    }

    public static void openSearchForUsagesMenu(Player player, SlommyItem ingredient, int page, MenuHolder previousMenu) {
        MenuHolder<SlommyPanels> panel;
        List<Integer> takenSlots = new ArrayList<>();
        List<Recipe> recipes = Recipes.searchRecipesForUsages(ingredient);
        int pages = (int) Math.ceil(recipes.size() / 28.0);
        if (pages == 0) {
            Msg.send(player, "&cThere are no recipes for that item.");
            return;
        }
        if (page < 0 || page >= pages) {
            Msg.send(player, "&cInvalid page number");
            return;
        }
        panel = new MenuHolder<>(SlommyPanels.getInstance(), 6*9, "Uses for " + ingredient.getRawDisplayName() + " (" + (page + 1) + "/" + pages + ")");
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
                    openRecipe(player, recipe, panel, 0);
                }
            });
            takenSlots.add(i);
        }
        // add the page buttons
        if (page > 0) {
            panel.setButton(45, new ItemButton(Items.get("gui/prev_page").build(player)) {
                @Override
                public void onClick(MenuHolder menuHolder, InventoryClickEvent event) {
                    openSearchForUsagesMenu(player, ingredient, page - 1, previousMenu);
                }
            });
        }
        if (page < pages - 1) {
            panel.setButton(53, new ItemButton(Items.get("gui/next_page").build(player)) {
                @Override
                public void onClick(MenuHolder menuHolder, InventoryClickEvent event) {
                    openSearchForUsagesMenu(player, ingredient, page + 1, previousMenu);
                }
            });
        }
        // add the close button
        panel.setButton(49, new ItemButton(Items.get("gui/go_back").build(player)) {
            @Override
            public void onClick(MenuHolder menuHolder, InventoryClickEvent event) {
                player.openInventory(previousMenu.getInventory());
            }
        });
        // add the icon
        panel.setButton(4, new ItemButton(ingredient.build(player)));
        player.openInventory(panel.getInventory());
        for (int j = 0; i < 6*9; i++) {
            ItemUpdateUtils.updateItemInContainer(player, i);
        }
    }
}
