package net.slommy.slommypanels.GUIs.lootbags;

import net.kyori.adventure.text.Component;
import net.slommy.essentials.SlommyItems.Items;
import net.slommy.essentials.SlommyItems.SlommyItem;
import net.slommy.essentials.util.Msg;
import net.slommy.slommypanels.SlommyPanels;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
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
        Inventory inventory = SlommyPanels.getInstance().getServer().createInventory(this,36, Msg.JSONcolor(displayName));

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
            try {
                ItemStack itemStack = drop.getSlommyItem().build(player);
                itemStack.setAmount(drop.getAmount());
                inventory.setItem(slot, itemStack);
                takenSlots.add(slot);
            } catch (Exception e) {
                e.printStackTrace();
                Bukkit.getLogger().warning("Could not add item " + drop.getId());
                Msg.send(player, "&c&lERROR! &7Could not add item &e" + drop.getId() + "&7 to your treasure bag. :( At least you could marvel at what could've been...");
            }

        }
        Msg.send(player, "&e&lTREASURE! &7You opened " + item.getDisplayName() + ":");
        List<String> sentDrops = new ArrayList<>();
        for (LootItem drop : drops) {
            // group all the drops of the same item together, and then send a message
            int amount = 0;
            for (LootItem drop2 : drops) {
                if (drop2.getId().equals(drop.getId())) {
                    amount += drop2.getAmount();
                }
            }
            if (sentDrops.contains(drop.getId())) {
                continue;
            }
            sentDrops.add(drop.getId());
            Msg.send(player, "&7- &e" + amount + "x " + drop.getSlommyItem().getDisplayName());
        }

        for (int i = 27; i < 36; i++) {
            inventory.setItem(i, Items.items.get("gui/blank_pane").build(player));
        }
        inventory.setItem(31, Items.items.get("gui/close").build(player));
        ItemStack treasureStash = Items.items.get("gui/treasure_stash").build(player);
        ItemMeta meta = treasureStash.getItemMeta();
        List<Component> lore = new ArrayList<>();
        lore.add(Msg.JSONcolor("&7Click to return to your &6stash&7."));
        meta.lore(lore);
        treasureStash.setItemMeta(meta);
        inventory.setItem(32, treasureStash);
        ItemStack bag = item.build(player);
        TreasureStash stash = TreasureStashUtil.getTreasureStash(player);
        if (stash.getBagAmount(this.name) > 0) {
            ItemMeta meta2 = bag.getItemMeta();
            List<Component> lore2 = new ArrayList<>();
            lore2.add(Msg.JSONcolor("&7You have &e" + stash.getBagAmount(this.name) + " &7left."));
            lore2.add(Msg.JSONcolor("&eClick to open another!"));
            meta2.lore(lore2);
            bag.setItemMeta(meta2);
            inventory.setItem(33, bag);
        }

        player.playSound(player.getLocation(), Sound.ITEM_TOTEM_USE, 1, 2f);
        player.openInventory(inventory);
    }

    @Override
    public Inventory getInventory() {
        return SlommyPanels.getInstance().getServer().createInventory(this,27, Msg.JSONcolor(displayName));
    }
}
