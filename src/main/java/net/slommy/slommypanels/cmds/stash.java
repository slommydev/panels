package net.slommy.slommypanels.cmds;

import net.slommy.slommypanels.GUIs.lootbags.TreasureStash;
import net.slommy.slommypanels.GUIs.lootbags.TreasureStashUtil;
import net.slommy.slommypanels.SlommyPanels;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.janboerman.guilib.api.menu.MenuHolder;

public class stash implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("You must be a player to use this command.");
            return true;
        }
        Player player = (Player) sender;
        TreasureStash stash = TreasureStashUtil.getTreasureStash(player);
        MenuHolder<SlommyPanels> panel = stash.getStashMenu(1);
        player.openInventory(panel.getInventory());
        player.playSound(player.getLocation(), Sound.ENTITY_HORSE_SADDLE, 1, 0);
        return true;
    }
}
