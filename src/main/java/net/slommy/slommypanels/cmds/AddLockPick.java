package net.slommy.slommypanels.cmds;

import net.slommy.essentials.util.Msg;
import net.slommy.slommypanels.GUIs.lootbags.GlobalLootbagManagers;
import net.slommy.slommypanels.GUIs.lootbags.LockPicker;
import net.slommy.slommypanels.GUIs.lootbags.TreasureStash;
import net.slommy.slommypanels.GUIs.lootbags.TreasureStashUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class AddLockPick implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length != 3) {
            sender.sendMessage("Usage: /addlockpick <player> <lockpick> <amount>");
            return true;
        }
        Player player = Bukkit.getPlayer(args[0]);
        if (player == null) {
            sender.sendMessage("Player not found");
            return true;
        }
        LockPicker picker = GlobalLootbagManagers.getLockPicker(args[1]);
        if (picker == null) {
            sender.sendMessage("Lockpick not found");
            return true;
        }
        int amount = Integer.parseInt(args[2]);
        TreasureStash stash = TreasureStashUtil.getTreasureStash(player);
        stash.giveLockpicker(picker, amount);
        sender.sendMessage("Gave " + player.getName() + " " + amount + " " + picker.getName());
        Msg.send(player, "&7+" + amount + " " + picker.getItem().getDisplayName() + "&7!");
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        List<String> options = new ArrayList<>();
        if (args.length == 1) {
            return null;
        }
        if (args.length == 2) {
            for (LockPicker picker : GlobalLootbagManagers.lockpicks) {
                options.add(picker.getName());
            }
            // remove all options that don't start with the arg
            options.removeIf(option -> !option.startsWith(args[1]));
        }
        if (args.length == 3) {
            options.add("<amount>");
        }

        return options;
    }
}
