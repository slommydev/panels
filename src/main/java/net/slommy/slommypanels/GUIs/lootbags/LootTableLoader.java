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
        File[] files = new File(getFilePath()).listFiles();

        if (files == null) {
            Bukkit.getLogger().warning("Could not find loot table directory: " + getFilePath());
            return;
        }

        for (File file : files) {
            if (!file.getName().endsWith(".yml")) {
                continue;
            }

            try {
                loadLootTable(file);
            } catch (Exception e) {
                Bukkit.getLogger().warning(
                        "Could not load loot table " + file.getName() + " due to an error."
                );
                e.printStackTrace();
            }
        }
    }

    private static void loadLootTable(File file) {
        String name = file.getName().replace(".yml", "");
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);

        ConfigurationSection mainDrops = cfg.getConfigurationSection("main-drops");
        ConfigurationSection secondaryDrops = cfg.getConfigurationSection("secondary-drops");

        int mainDropAmount = cfg.getInt("main-drop-amount");
        int secondaryDropAmount = cfg.getInt("secondary-drop-amount");

        // Validate required fields before trying to access them
        if (mainDrops == null
                || secondaryDrops == null
                || mainDropAmount == 0
                || secondaryDropAmount == 0) {

            Bukkit.getLogger().warning(
                    "Could not load loot table " + name +
                            " because it is missing a required field."
            );
            return;
        }

        Map<LootItem, Double> mainDropsMap = loadDrops(mainDrops);
        Map<LootItem, Double> secondaryDropsMap = loadDrops(secondaryDrops);

        LootTable lootTable = new LootTable(
                name,
                mainDropsMap,
                secondaryDropsMap,
                mainDropAmount,
                secondaryDropAmount
        );

        GlobalLootbagManagers.tables.add(lootTable);
    }

    private static Map<LootItem, Double> loadDrops(ConfigurationSection drops) {
        Map<LootItem, Double> dropsMap = new HashMap<>();

        for (String key : drops.getKeys(false)) {
            ConfigurationSection item = drops.getConfigurationSection(key);

            if (item == null) {
                continue;
            }

            String itemKey = item.getString("item");

            if (itemKey == null) {
                continue;
            }

            String enchantmentKey = item.getString("enchantment_id", "");

            SlommyItem slommyItem = Items.items.get(itemKey);

            if (slommyItem == null) {
                Bukkit.getLogger().warning(
                        "Could not find SlommyItem '" + itemKey + "' in loot table."
                );
                continue;
            }

            double chance = item.getDouble("chance");
            int amount = item.getInt("amount", 1);
            int enchantmentLevel = item.getInt("enchantment_level", 1);

            LootItem lootItem = new LootItem(
                    slommyItem,
                    amount,
                    itemKey,
                    enchantmentKey,
                    enchantmentLevel
            );

            dropsMap.put(lootItem, chance);

            Bukkit.getLogger().info(
                    "Loaded drop " + slommyItem.getDisplayName() +
                            " with chance " + chance
            );
        }

        return dropsMap;
    }

    public static String getFilePath() {
        return Bukkit.getPluginsFolder() + "/SlommyPanels/lootbags/tables/";
    }
}