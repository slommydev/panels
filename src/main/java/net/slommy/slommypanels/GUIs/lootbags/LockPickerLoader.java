package net.slommy.slommypanels.GUIs.lootbags;

import net.slommy.essentials.SlommyItems.Items;
import net.slommy.essentials.SlommyItems.SlommyItem;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class LockPickerLoader {
    public static void load() {
        for (File file : new File(getFilePath()).listFiles()) {
            if (file.getName().endsWith(".yml")) {
                String name = file.getName().replace(".yml", "");
                FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
                String item = cfg.getString("item");
                // if any of these are null, do not load this bag
                if (name == null || item == null) {
                    Bukkit.getLogger().warning("Could not load lock picker " + name + " because it is missing a required field.");
                    continue;
                }
                try {
                    SlommyItem slommyItem = Items.items.get(item);
                    LockPicker lockPicker = new LockPicker(name, slommyItem);
                    GlobalLootbagManagers.lockpicks.add(lockPicker);
                }
                catch (NullPointerException e) {
                    Bukkit.getLogger().warning("Could not load lock picker " + name + " because its item does not exist.");
                }
            }
        }
    }

    public static String getFilePath() {
        return Bukkit.getPluginsFolder() + "/SlommyPanels/lootbags/lockpicks/";
    }
}
