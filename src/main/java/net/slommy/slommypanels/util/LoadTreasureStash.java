package net.slommy.slommypanels.util;

import net.slommy.slommypanels.GUIs.lootbags.TreasureStash;
import net.slommy.slommypanels.GUIs.lootbags.TreasureStashUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class LoadTreasureStash {
    public static void load(Player p) {
        TreasureStash stash = new TreasureStash(p, TreasureStashUtil.getFile(p));
        TreasureStashUtil.setTreasureStash(p, stash);
    }

    public static void save(Player p) {
        TreasureStash stash = TreasureStashUtil.getTreasureStash(p);
        stash.save();
        TreasureStashUtil.setTreasureStash(p, null);
    }

    public static void saveAll() {
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            save(p);
        }
    }

    public static void loadAll() {
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            load(p);
        }
    }
}
