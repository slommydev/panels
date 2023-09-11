package net.slommy.slommypanels.GUIs.lootbags;

import net.kyori.adventure.text.TextComponent;
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
            LootItem item = getDrop(mainDrops);
            if (item != null) {
                drops.add(item);
            }
        }
        for (int i = 0; i < secondaryDropAmount; i++) {
            LootItem item = getDrop(secondaryDrops);
            if (item != null) {
                drops.add(item);
            }
        }
        return drops;
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
}
