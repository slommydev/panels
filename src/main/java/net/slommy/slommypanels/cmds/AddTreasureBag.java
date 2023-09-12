package net.slommy.slommypanels.cmds;

import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.slommy.essentials.util.Msg;
import net.slommy.slommypanels.GUIs.lootbags.GlobalLootbagManagers;
import net.slommy.slommypanels.GUIs.lootbags.TreasureBag;
import net.slommy.slommypanels.GUIs.lootbags.TreasureStash;
import net.slommy.slommypanels.GUIs.lootbags.TreasureStashUtil;
import net.slommy.slommypanels.SlommyPanels;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class AddTreasureBag implements TabExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        Player player = Bukkit.getPlayer(args[0]);
        if (player == null) {
            sender.sendMessage("Player not found");
            return true;
        }
        TreasureBag bag = GlobalLootbagManagers.getBag(args[1]);
        if (bag == null) {
            sender.sendMessage("Bag not found");
            return true;
        }
        int amount = Integer.parseInt(args[2]);
        TreasureStash stash = TreasureStashUtil.getTreasureStash(player);
        stash.giveBag(bag, amount);
        Msg.send(player, "&7+" + amount + " " + bag.getItem().getDisplayName() + "&7!");
        TextComponent msg = Msg.JSONcolor("&7Check your &6/stash&7! &8(or click this message)");
        msg = msg.clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/stash"));
        msg = msg.hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, Msg.JSONcolor("&7Click to open your stash!")));
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 0.84f);
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1.12f);
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 0.56f);
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 0.42f);
        // after 2 ticks, play the sound again
        Bukkit.getScheduler().runTaskLater(SlommyPanels.getInstance(), () -> {
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 0.84f);
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1.12f);
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 0.56f);
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 0.71f);
        }, 5);
        // after 4 ticks, play the sound for the last time
        Bukkit.getScheduler().runTaskLater(SlommyPanels.getInstance(), () -> {
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 0.84f);
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1.12f);
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 0.56f);
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 0.71f);
        }, 10);
        player.sendMessage(msg);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        List<String> options = new ArrayList<>();
        if (args.length == 1) {
            options = null;
        }
        if (args.length == 2) {
            for (TreasureBag bag : GlobalLootbagManagers.bags) {
                options.add(bag.getName());
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
