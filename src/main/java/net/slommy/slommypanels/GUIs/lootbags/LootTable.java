package net.slommy.slommypanels.GUIs.lootbags;

import net.kyori.adventure.text.TextComponent;
import net.slommy.essentials.SlommyItems.Item;
import net.slommy.essentials.SlommyItems.SlommyItem;
import net.slommy.essentials.util.Msg;
import org.bukkit.Bukkit;

import java.util.*;
import java.util.List;

public class LootTable {
    private Map<LootItem, Double> mainDrops = new HashMap<>();
    private Map<LootItem, Double> secondaryDrops = new HashMap<>();
    private String name;
    private int mainDropAmount;
    private int secondaryDropAmount;
    private int slayingLevelForDrops = 50;

    public LootTable(String name, Map<LootItem, Double> mainDrops, Map<LootItem, Double> secondaryDrops, int mainDropAmount, int secondaryDropAmount) {
        this.name = name;
        this.mainDrops = mainDrops;
        this.secondaryDrops = secondaryDrops;
        this.mainDropAmount = mainDropAmount;
        this.secondaryDropAmount = secondaryDropAmount;

    }

    public Map<LootItem, Double> getMainDrops() {
        return mainDrops;
    }

    public Map<LootItem, Double> getSecondaryDrops() {
        return secondaryDrops;
    }

    public String getName() {
        return name;
    }

    public int getMainDropAmount() {
        return mainDropAmount;
    }

    public int getSecondaryDropAmount() {
        return secondaryDropAmount;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMainDropAmount(int mainDropAmount) {
        this.mainDropAmount = mainDropAmount;
    }

    public void setSecondaryDropAmount(int secondaryDropAmount) {
        this.secondaryDropAmount = secondaryDropAmount;
    }

    public int getSlayingLevelForDrops() {
        return slayingLevelForDrops;
    }

    public void setSlayingLevelForDrops(int slayingLevelForDrops) {
        this.slayingLevelForDrops = slayingLevelForDrops;
    }

    public List<LootItem> getListOfDrops() {
        // generate a list of drops for the player to get
        List<LootItem> drops = new ArrayList<>();
        for (int i = 0; i < mainDropAmount; i++) {
            try {
                LootItem item = getDrop(mainDrops);
                if (item != null) {
                    drops.add(item);
                }
            }
            catch (Exception e) {
                Bukkit.getLogger().warning("Could not get main drop for loot table " + name);
            }
        }
        for (int i = 0; i < secondaryDropAmount; i++) {
            try {
                LootItem item = getDrop(secondaryDrops);
                if (item != null) {
                    drops.add(item);
                }
            }
            catch (Exception e) {
                Bukkit.getLogger().warning("Could not get secondary drop for loot table " + name);
            }
        }
        return drops;
    }

    public List<LootItem> getAllDropItems() {
        List<LootItem> items = new ArrayList<>();
        items.addAll(mainDrops.keySet());
        items.addAll(secondaryDrops.keySet());
        return items;
    }

    public LootItem getDrop(Map<LootItem, Double> drops) {
        // get a drop from the map of drops
        double total = 0;
        for (double chance : drops.values()) {
            total += chance;
        }
        double random = Math.random() * total;
        for (LootItem item : drops.keySet()) {
            random -= drops.get(item);
            if (random <= 0) {
                return item;
            }
        }
        return null;
    }

    public List<TextComponent> getDropsForLore() {
        List<TextComponent> drops = new ArrayList<>();
        drops.add(Msg.JSONcolor("&7Main Drops:"));
        for (LootItem item : mainDrops.keySet()) {
            TextComponent textComponent = Msg.JSONcolor("&7- " + item.getSlommyItem().getDisplayName());
            drops.add(textComponent);
        }
        drops.add(Msg.JSONcolor("&7Secondary Drops:"));
        for (LootItem item : secondaryDrops.keySet()) {
            TextComponent textComponent = Msg.JSONcolor("&7- " + item.getSlommyItem().getDisplayName());
            drops.add(textComponent);
        }
        return drops;
    }

    public double getChance(LootItem item) {
        if (mainDrops.containsKey(item)) {
            return mainDrops.get(item) * 100.0;
        }
        else if (secondaryDrops.containsKey(item)) {
            return secondaryDrops.get(item) * 100.0;
        }
        return 0;
    }

    public enum Rarity {
        TOKEN, COMMON, UNCOMMON, RARE, EXTREMELY_RARE, CRAZY_RARE, MYTHIC;

        public static String color(LootTable.Rarity rarity) {
            switch (rarity) {
                case COMMON:
                    return "&f";
                case UNCOMMON:
                    return "&a";
                case RARE:
                    return "&9";
                case EXTREMELY_RARE:
                    return "&3";
                case CRAZY_RARE:
                case MYTHIC:
                    return "&d";
                default:
                    return "&7";
            }
        }

        public static Rarity getRarityByChance(double chance) {
            chance = chance * 100;
            if (chance < 0.001) {
                return MYTHIC;
            }
            else if (chance < 0.1) {
                return CRAZY_RARE;
            }
            else if (chance < 1) {
                return EXTREMELY_RARE;
            }
            else if (chance < 10) {
                return RARE;
            }
            else if (chance < 30) {
                return UNCOMMON;
            }
            else if (chance < 100) {
                return COMMON;
            } else {
                return TOKEN;
            }
        }

        public static String getRarityString(LootTable.Rarity rarity) {
            switch (rarity) {
                case TOKEN:
                    return color(rarity) + "&lTOKEN DROP";
                case COMMON:
                    return color(rarity) + "&lCOMMON DROP";
                case UNCOMMON:
                    return color(rarity) + "&lUNCOMMON DROP";
                case RARE:
                    return color(rarity) + "&lRARE DROP";
                case EXTREMELY_RARE:
                    return color(rarity) + "&lEXTREMELY RARE DROP";
                case CRAZY_RARE:
                    return color(rarity) + "&lCRAZY RARE DROP";
                case MYTHIC:
                    return color(rarity) + "&lMYTHIC DROP";
                default:
                    return "&7&lSO-RARE-IT'S-UNKNOWN DROP";
            }
        }

    };
}
