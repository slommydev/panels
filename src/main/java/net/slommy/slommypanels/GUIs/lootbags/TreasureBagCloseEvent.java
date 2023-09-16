package net.slommy.slommypanels.GUIs.lootbags;

import net.slommy.essentials.PlayerStats.skills.PlayerSkills;
import net.slommy.essentials.SlommyItems.Items;
import net.slommy.essentials.util.GetSlommyItem;
import net.slommy.essentials.util.Msg;
import net.slommy.essentials.util.PlayerDataUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;

public class TreasureBagCloseEvent implements Listener {
    @EventHandler
    public void onBagClose(InventoryCloseEvent event) {
        if (event.getInventory().getHolder() instanceof TreasureBag) {
            Inventory inv = event.getInventory();
            TreasureBag bag = GlobalLootbagManagers.getBag(((TreasureBag) event.getInventory().getHolder()).getName());
            bag.lockpickAllowed.put(event.getPlayer().getName(), true);
            for (int i = 0; i < inv.getSize(); i++) {
                if (inv.getItem(i) != null) {
                    String itemID = GetSlommyItem.getFromItemTag(inv.getItem(i));
                    if (itemID.split("/")[0].equalsIgnoreCase("clickables") || itemID.split("/")[0].equalsIgnoreCase("gui") || itemID.split("/")[0].equalsIgnoreCase("bags") || itemID.split("/")[0].equalsIgnoreCase("lockpicks")) {
                        if (itemID.split("/")[0].equalsIgnoreCase("clickables")) {
                            runClickableItem(inv.getItem(i), (Player) event.getPlayer());
                        }
                        continue;
                    }
                    // drop the item at the player's location
                    event.getPlayer().getWorld().dropItem(event.getPlayer().getLocation(), Objects.requireNonNull(inv.getItem(i)));
                }
            }
        }
    }

    @EventHandler
    public void onItemClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof TreasureBag) {
            if (event.getCurrentItem() == null) {
                return;
            }
            String itemID = GetSlommyItem.getFromItemTag(event.getCurrentItem());
            if (itemID.split("/")[0].equalsIgnoreCase("clickables") || itemID.split("/")[0].equalsIgnoreCase("gui") || itemID.split("/")[0].equalsIgnoreCase("bags") || itemID.split("/")[0].equalsIgnoreCase("lockpicks")) {
                event.setCancelled(true);

                if (itemID.split("/")[0].equalsIgnoreCase("clickables")) {
                    runClickableItem(event.getCurrentItem(), (Player) event.getWhoClicked());
                    // remove the item from the inventory
                    event.getInventory().setItem(event.getSlot(), null);
                    TreasureBag bag = GlobalLootbagManagers.getBag(((TreasureBag) event.getInventory().getHolder()).getName());
                    bag.lockpickAllowed.put(event.getWhoClicked().getName(), false);
                    Player player = (Player) event.getWhoClicked();
                    event.getInventory().setItem(30, Items.items.get("gui/blank_pane").build(player));
                }
                if (itemID.split("/")[0].equalsIgnoreCase("gui")) {
                    if (itemID.split("/")[1].equalsIgnoreCase("close")) {
                        event.getWhoClicked().closeInventory();
                    }
                    else if (itemID.split("/")[1].equalsIgnoreCase("treasure_stash")) {
                        Player player = (Player) event.getWhoClicked();
                        player.closeInventory();
                        Bukkit.getServer().dispatchCommand(player, "stash");
                    }
                }
                if (itemID.split("/")[0].equalsIgnoreCase("bags")) {
                    TreasureBag bag = GlobalLootbagManagers.getBag(((TreasureBag) event.getInventory().getHolder()).getName());
                    if (itemID.split("/")[1].equalsIgnoreCase(bag.getItem().getItemID().split("/")[1])) {
                        event.setCancelled(true);
                        Player player = (Player) event.getWhoClicked();
                        TreasureStash stash = TreasureStashUtil.getTreasureStash(player);
                        stash.useBag(bag);
                    }
                }
                if (itemID.split("/")[0].equalsIgnoreCase("lockpicks")) {
                    TreasureBag bag = GlobalLootbagManagers.getBag(((TreasureBag) event.getInventory().getHolder()).getName());
                    TreasureStash stash = TreasureStashUtil.getTreasureStash((Player) event.getWhoClicked());
                    LockPicker picker = bag.getLockPicker();
                    if (picker == null) {
                        return;
                    }
                    if (stash.getLockpickAmount(picker.getName()) <= 0) {
                        Msg.send((Player) event.getWhoClicked(), "&cYou don't have any " + picker.getName() + "s!");
                        Msg.send((Player) event.getWhoClicked(), "&cCHEATING CHEATER!! >:(");
                        return;
                    }
                    if (bag.lockpickAllowed.get(event.getWhoClicked().getName()) == null) {
                        return;
                    }
                    if (!bag.lockpickAllowed.get(event.getWhoClicked().getName())) {
                        return;
                    }
                    Player player = (Player) event.getWhoClicked();
                    // remove all items from the first 27 slots
                    for (int i = 0; i < 27; i++) {
                        if (event.getInventory().getItem(i) != null) {
                            event.getInventory().setItem(i, null);
                        }
                    }
                    stash.useLockpick(picker);
                    bag.openTreasureBag(player);
                }
            } else {
                TreasureBag bag = GlobalLootbagManagers.getBag(((TreasureBag) event.getInventory().getHolder()).getName());
                bag.lockpickAllowed.put(event.getWhoClicked().getName(), false);
                Player player = (Player) event.getWhoClicked();
                event.getInventory().setItem(30, Items.items.get("gui/blank_pane").build(player));
            }
        }
    }

    public void runClickableItem(ItemStack item, Player player) {
        String itemID = GetSlommyItem.getFromItemTag(item);
        String type = itemID.split("/")[1];

        if (type.startsWith("essence")) {
            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 2);
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "scoreboard players add " + player.getName() + " masteryEssence " + type.split("_")[1]);
            player.sendActionBar(Msg.JSONcolor("&5+" + type.split("_")[1] + " Mastery Essence"));
        }
        if (type.startsWith("skill")) {
            String skillName = type.split("_")[1];
            PlayerSkills skills = PlayerDataUtil.getPlayerSkills(player);
            skills.getSkill(skillName).addXP(Long.parseLong(type.split("_")[2]));
        }
    }
}
