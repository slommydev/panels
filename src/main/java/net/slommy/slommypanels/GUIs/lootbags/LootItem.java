package net.slommy.slommypanels.GUIs.lootbags;

import net.slommy.essentials.SlommyItems.Items;
import net.slommy.essentials.SlommyItems.SlommyItem;
import org.bukkit.Bukkit;

public class LootItem {
    private SlommyItem slommyItem;
    private int amount;
    private String id;
    public LootItem(SlommyItem slommyItem, int amount, String id) {
        this.slommyItem = slommyItem;
        this.amount = amount;
        this.id = id;
    }

    public SlommyItem getSlommyItem() {
        if (slommyItem == null) {
            Bukkit.getLogger().info("SlommyItem is null for some reason");
            return Items.items.get("item_generation_failed");
        }
        return slommyItem;
    }

    public int getAmount() {
        return amount;
    }

    public String getId() {
        return id;
    }
}
