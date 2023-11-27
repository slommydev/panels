package net.slommy.slommypanels.cmds;

import net.slommy.essentials.SlommyItems.recipes.Recipes;
import net.slommy.essentials.util.Msg;
import net.slommy.slommypanels.GUIs.recipes.RecipeCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class RecipeEditor implements TabExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            Msg.send(sender, "&cYou must be a player to use this command");
            return true;
        }

        Player player = (Player) sender;
        if (args.length == 0) {
            Msg.send(player, "&cUsage: /sr <edit/create> <category> <name>");
            return true;
        }

        if (args.length < 3) {
            Msg.send(player, "&cUsage: /sr <edit/create> <category> <name>");
            return true;
        }

        String category = args[1];
        String name = args[2];
        String option = args[0];
        if (option.equalsIgnoreCase("create")) {
            // Check if recipe exists
            if (Recipes.recipes.containsKey(category + "/" + name)) {
                Msg.send(player, "&cRecipe already exists. Opening recipe editor");
                RecipeCreator recipeCreator = new RecipeCreator(category, name);
                recipeCreator.openRecipe(player, category, name);
            } else {
                RecipeCreator recipeCreator = new RecipeCreator(category, name);
                recipeCreator.openMenu(player);
            }
            return true;
        } else if (option.equalsIgnoreCase("edit")) {
            // Check if recipe exists
            if (Recipes.recipes.containsKey(category + "/" + name)) {
                RecipeCreator recipeCreator = new RecipeCreator(category, name);
                recipeCreator.openRecipe(player, category, name);
            } else {
                Msg.send(player, "&cRecipe does not exist. Creating new recipe");
                RecipeCreator recipeCreator = new RecipeCreator(category, name);
                recipeCreator.openMenu(player);
            }
            return true;
        } else {
            Msg.send(player, "&cUsage: /sr <edit/create> <category> <name>");
            return true;
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        List<String> options = new ArrayList<>();
        if (strings.length == 1) {
            options.add("edit");
            options.add("create");
            options.removeIf(option -> !option.toLowerCase().startsWith(strings[0].toLowerCase()));
            return options;
        } else
        if (strings.length == 2) {
            options.addAll(Recipes.getRecipeCategories());
            options.removeIf(option -> !option.toLowerCase().startsWith(strings[1].toLowerCase()));
            return options;
        } else
        if (strings.length == 3) {
            if (strings[0].equalsIgnoreCase("edit")) {
                options.addAll(Recipes.getRecipeIDs(strings[1]));
            } else {
                options.add("<name>");
            }
            options.removeIf(option -> !option.startsWith(strings[2]));
            return options;
        }
        return options;
    }
}
