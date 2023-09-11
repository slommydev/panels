package net.slommy.slommypanels.GUIs.lootbags;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class TreasureStashUtil {
    public static Map<String, TreasureStash> treasureStash = new HashMap<>();

    public static TreasureStash getTreasureStash(Player p) {
        if (!treasureStash.containsKey(p.getUniqueId().toString())) {
            TreasureStash m = new TreasureStash(p, getFile(p));
            treasureStash.put(p.getUniqueId().toString(), m);
            return m;
        }
        return treasureStash.get(p.getUniqueId().toString());
    }

    public static void setTreasureStash(Player p, TreasureStash data) {
        if (data == null) treasureStash.remove(p.getUniqueId().toString());
        treasureStash.put(p.getUniqueId().toString(), data);
    }

    private static File getFile(Player p) {
        return new File(Bukkit.getPluginsFolder().getAbsolutePath() + "/SlommyEssentials/player/" + p.getUniqueId() + "/treasurestash.yml");
    }
}
