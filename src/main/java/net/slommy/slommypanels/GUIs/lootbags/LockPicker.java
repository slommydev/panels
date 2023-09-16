package net.slommy.slommypanels.GUIs.lootbags;

import net.slommy.essentials.SlommyItems.SlommyItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class LockPicker {
    private String name;
    private SlommyItem item;

    public LockPicker(String name, SlommyItem item) {
        this.name = name;
        this.item = item;
    }

    public String getName() {
        return name;
    }
    public SlommyItem getItem() {
        return item;
    }

}
