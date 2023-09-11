package net.slommy.slommypanels.GUIs.lootbags;

import net.slommy.essentials.SlommyItems.SlommyItem;

public class LootItem {
    private SlommyItem slommyItem;
    private int amount;
    public LootItem(SlommyItem slommyItem, int amount) {
        this.slommyItem = slommyItem;
        this.amount = amount;
    }

    public SlommyItem getSlommyItem() {
        return slommyItem;
    }

    public int getAmount() {
        return amount;
    }
}
