package net.slommy.slommypanels.GUIs.lootbags;

import net.slommy.essentials.SlommyItems.SlommyItem;
import net.slommy.essentials.util.Msg;
import net.slommy.slommypanels.SlommyPanels;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import xyz.janboerman.guilib.api.menu.MenuHolder;

import java.util.ArrayList;
import java.util.List;

public class TreasureBag implements InventoryHolder {
    private SlommyItem item;
    private String name;
    private String displayName;
    private LootTable lootTable;

    public TreasureBag(SlommyItem item, String name, String displayName, LootTable lootTable) {
        this.item = item;
        this.name = name;
        this.displayName = displayName;
        this.lootTable = lootTable;
    }

    public SlommyItem getItem() {
        return item;
    }

    public void setItem(SlommyItem item) {
        this.item = item;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public LootTable getLootTable() {
        return lootTable;
    }

    public void setLootTable(LootTable lootTable) {
        this.lootTable = lootTable;
    }

    public void openTreasureBag(Player player) {
        Inventory inventory = SlommyPanels.getInstance().getServer().createInventory(this,27, Msg.JSONcolor(displayName));

        // Fill inventory with items from loot table
        List<LootItem> drops = lootTable.getListOfDrops();
        List<Integer> takenSlots = new ArrayList<>();
        for (LootItem drop : drops) {
            int slot = (int) (Math.random() * 27);
            while (takenSlots.contains(slot)) {
                slot = (int) (Math.random() * 27);
            }
            if (lootTable.getMainDrops().containsKey(drop)) {
                if (lootTable.getMainDrops().get(drop) < 0.01) {
                    // broadcast to server
                    List<Player> onlinePlayers = new ArrayList<>(SlommyPanels.getInstance().getServer().getOnlinePlayers());
                    for (Player onlinePlayer : onlinePlayers) {
                        Msg.send(onlinePlayer, "&d&lWOAH! &b" + player.getName() + " &ejust got a &d" + drop.getSlommyItem().getDisplayName() + "&e from a " + item.getDisplayName() + "&e!");
                    }
                }
            }
            ItemStack itemStack = drop.getSlommyItem().build(player);
            itemStack.setAmount(drop.getAmount());
            inventory.setItem(slot, itemStack);
            takenSlots.add(slot);
        }

        player.playSound(player.getLocation(), Sound.ITEM_TOTEM_USE, 1, 2f);
        player.openInventory(inventory);
    }

    @Override
    public Inventory getInventory() {
        return SlommyPanels.getInstance().getServer().createInventory(this,27, Msg.JSONcolor(displayName));
    }
}
