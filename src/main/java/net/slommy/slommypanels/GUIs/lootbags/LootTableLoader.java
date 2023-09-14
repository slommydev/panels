package net.slommy.slommypanels.GUIs.lootbags;

import net.slommy.essentials.SlommyItems.Items;
import net.slommy.essentials.SlommyItems.SlommyItem;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class LootTableLoader {
    public static void loadLootTables() {
        for (File file : new File(getFilePath()).listFiles()) {
            try {
                if (file.getName().endsWith(".yml")) {
                    String name = file.getName().replace(".yml", "");
                    FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
                    ConfigurationSection mainDrops = cfg.getConfigurationSection("main-drops");
                    Map<LootItem, Double> mainDropsMap = new HashMap<>();
                    ConfigurationSection secondaryDrops = cfg.getConfigurationSection("secondary-drops");
                    Map<LootItem, Double> secondaryDropsMap = new HashMap<>();
                    for (String key : mainDrops.getKeys(false)) {
                        ConfigurationSection item = mainDrops.getConfigurationSection(key);
                        String itemKey = item.getString("item");
                        if (itemKey == null) {
                            continue;
                        }
                        SlommyItem slommyItem = Items.items.get(itemKey);
                        double chance = item.getDouble("chance");
                        int amount = item.getInt("amount");
                        if (amount == 0) {
                            amount = 1;
                        }
                        LootItem lootItem = new LootItem(slommyItem, amount, itemKey);
                        mainDropsMap.put(lootItem, chance);
                    }
                    for (String key : secondaryDrops.getKeys(false)) {
                        ConfigurationSection item = secondaryDrops.getConfigurationSection(key);
                        String itemKey = item.getString("item");
                        if (itemKey == null) {
                            continue;
                        }
                        SlommyItem slommyItem = Items.items.get(itemKey);
                        double chance = item.getDouble("chance");
                        int amount = item.getInt("amount");
                        if (amount == 0) {
                            amount = 1;
                        }
                        LootItem lootItem = new LootItem(slommyItem, amount, itemKey);
                        secondaryDropsMap.put(lootItem, chance);
                        Bukkit.getLogger().info("Loaded secondary drop " + slommyItem.getDisplayName() + " with chance " + chance);
                    }
                    int mainDropAmount = cfg.getInt("main-drop-amount");
                    int secondaryDropAmount = cfg.getInt("secondary-drop-amount");
                    // if any of these are null, do not load this table
                    if (name == null || mainDrops == null || secondaryDrops == null || mainDropAmount == 0 || secondaryDropAmount == 0) {
                        Bukkit.getLogger().warning("Could not load loot table " + name + " because it is missing a required field.");
                        continue;
                    }
                    LootTable lootTable = new LootTable(name, mainDropsMap, secondaryDropsMap, mainDropAmount, secondaryDropAmount);
                    GlobalLootbagManagers.tables.add(lootTable);
                }
            } catch (Exception e) {
                Bukkit.getLogger().warning("Could not load loot table " + file.getName() + " due to an error.");
                e.printStackTrace();
                continue;
            }
        }
    }

    public static String getFilePath() {
        return Bukkit.getPluginsFolder() + "/SlommyPanels/lootbags/tables/";
    }
}
