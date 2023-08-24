package net.slommy.slommypanels.cmds;

import net.slommy.essentials.util.Msg;
import net.slommy.slommypanels.GUIs.shops.Shop;
import net.slommy.slommypanels.GUIs.shops.ShopPanel;
import net.slommy.slommypanels.SlommyPanels;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.janboerman.guilib.api.menu.MenuHolder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class shop implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        Player player = Bukkit.getPlayer(args[0]);
        if (player == null) {
            Msg.send(sender, "&cNo player found");
            return true;
        }
        if (args.length != 2) {
            Msg.send(sender, "&cInvalid arguments");
            return true;
        }

        Shop shop = Shop.getShop(Shop.getShopPath(args[1]));
        MenuHolder<SlommyPanels> shopPanel = ShopPanel.open(player, shop, 1);
        player.openInventory(shopPanel.getInventory());

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length == 1) {
            return null;
        }
        if (args.length == 2) {
            // get all the shops in the shops folder
            File directory = new File(Shop.getShopPath());
            File[] files = directory.listFiles();
            List<String> fileNames = new ArrayList<>();
            for (File file : files) {
                fileNames.add(file.getName().replace(".yml", ""));
            }
            return fileNames;
        }
        return new ArrayList<>();
    }
}
