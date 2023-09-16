package net.slommy.slommypanels.GUIs.lootbags;

import net.slommy.essentials.SlommyItems.Items;
import net.slommy.essentials.SlommyItems.SlommyItem;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class TreasureBagLoader {
    public static void loadBags() {
        for (File file : new File(getFilePath()).listFiles()) {
            if (file.getName().endsWith(".yml")) {
                String name = file.getName().replace(".yml", "");
                FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
                String displayName = cfg.getString("display-name");
                String lootTable = cfg.getString("loot-table");
                String itemID = cfg.getString("item");
                SlommyItem item = Items.items.get(itemID);
                // if any of these are null, do not load this bag
                if (name == null || displayName == null || lootTable == null) {
                    Bukkit.getLogger().warning("Could not load treasure bag " + name + " because it is missing a required field.");
                    continue;
                }
                LootTable table = GlobalLootbagManagers.getTable(lootTable);
                if (table == null) {
                    Bukkit.getLogger().warning("Could not load treasure bag " + name + " because its loot table does not exist.");
                    continue;
                }
                TreasureBag bag = new TreasureBag(item, name, displayName, table);
                if (cfg.contains("lockpicker")) {
                    String lockpicker = cfg.getString("lockpicker");
                    LockPicker picker = GlobalLootbagManagers.getLockPicker(lockpicker);
                    if (picker == null) {
                        Bukkit.getLogger().warning("Could not load treasure bag " + name + " because its lockpicker does not exist.");
                        continue;
                    }
                    bag.setLockPicker(picker);
                }
                GlobalLootbagManagers.bags.add(bag);
            }
        }
    }

    public static String getFilePath() {
        return Bukkit.getPluginsFolder() + "/SlommyPanels/lootbags/bags/";
    }
}
