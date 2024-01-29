package net.slommy.slommypanels.GUIs.artifacting.events;

import net.slommy.essentials.SlommyItems.ItemUpdateUtils;
import net.slommy.essentials.SlommyItems.PersistentDataUtils;
import net.slommy.essentials.SlommyItems.PersistentItemData;
import net.slommy.essentials.SlommyItems.SlommyItem;
import net.slommy.essentials.util.GetSlommyItem;
import net.slommy.essentials.util.Msg;
import net.slommy.essentials.util.SlommyItemExchange;
import net.slommy.essentials.util.SoundLibrary;
import net.slommy.slommypanels.GUIs.artifacting.SelectItemToUpgradeMenu;
import net.slommy.slommypanels.GUIs.artifacting.UpgradeMenu;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Objects;

public class UpgradeMenuEventManagers implements Listener {

    // Handlers for Upgrade Menu
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof UpgradeMenu)) {
            return;
        }

        event.setCancelled(true);

        ItemStack clickedItem = event.getCurrentItem();
        int slot = event.getSlot();

        if (clickedItem == null) {
            return;
        }

        String itemID = GetSlommyItem.getFromItemTag(clickedItem);

        if (itemID == null) {
            return;
        }

        if (slot == 20) {
            if (itemID.equalsIgnoreCase("gui/add_artifact_item")) {
                SelectItemToUpgradeMenu menu = new SelectItemToUpgradeMenu();
                menu.openUpgradableItemsMenu((Player) event.getWhoClicked(), ((UpgradeMenu) event.getInventory().getHolder()).series);
            } else {
                // Open the upgrade menu again
                UpgradeMenu menu = new UpgradeMenu();
                menu.openUpgradeMenu((Player) (event.getWhoClicked()), null, ((UpgradeMenu) event.getInventory().getHolder()).series);
            }
        }

        if (slot == 22) {
            if (!itemID.equalsIgnoreCase("gui/blank_pane")) {
                ItemStack item = event.getInventory().getItem(20);
                if (item == null) {
                    return;
                }
                if (GetSlommyItem.getFromItemTag(item).equalsIgnoreCase("gui/add_artifact_item")) {
                    return;
                }
                List<PersistentItemData> data = GetSlommyItem.getPersistentDataValues(item);
                if (data.size() == 0) {
                    return;
                }
                // Check if the player has the required item in their inventory
                SlommyItem requiredItem = ((UpgradeMenu) event.getInventory().getHolder()).series.getUpgradeItem();
                if (!SlommyItemExchange.checkInventory((Player) event.getWhoClicked(), requiredItem, 1)) {
                    Msg.send((Player) event.getWhoClicked(), "&cYou do not have the required item to upgrade this artifact!");
                    return;
                }
                // Remove the required item from the player's inventory
                SlommyItemExchange.takeItems((Player) event.getWhoClicked(), requiredItem, 1);
                for (PersistentItemData dataTag : data) {
                    if (dataTag.getApplication() == PersistentItemData.Application.ARTIFACT_STATMOD) {
                        int value = Integer.parseInt(dataTag.getValue());
                        value = value + (20/(value/10) + 1);
                        if (value > 100) {
                            value = 100;
                        }
                        dataTag.setValue(String.valueOf(value));
                        PersistentDataUtils.addPersistentDataTag(item, dataTag);
                    }
                }
                PersistentItemData artifactUpgraded = new PersistentItemData("artifact_upgraded", PersistentItemData.Application.ARTIFACT_UPGRADED, String.valueOf(PersistentDataUtils.getArtifactUpgraded(item) + 1));
                PersistentDataUtils.addPersistentDataTag(item, artifactUpgraded);
                DecimalFormat df = new DecimalFormat("#.##");
                PersistentItemData artifactFidelity = new PersistentItemData("artifact_fidelity", PersistentItemData.Application.PLACEHOLDER, df.format(PersistentDataUtils.getArtifactFidelity(item)));
                PersistentDataUtils.addPersistentDataTag(item, artifactFidelity);
                event.getInventory().setItem(20, item);
                ItemUpdateUtils.updateItemInContainer((Player) (event.getWhoClicked()), 20);
                item = event.getInventory().getItem(20);
                UpgradeMenu menu = new UpgradeMenu();
                event.getInventory().setItem(20, null);
                menu.openUpgradeMenu((Player) (event.getWhoClicked()), item, ((UpgradeMenu) event.getInventory().getHolder()).series);
                Player player = (Player) event.getWhoClicked();
                SoundLibrary.upgradeArtifactItem(player);
            }
        }

    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (!(event.getInventory().getHolder() instanceof UpgradeMenu)) {
            return;
        }

        // Give the item back to the player
        if (event.getInventory().getItem(20) != null) {
            if (!GetSlommyItem.getFromItemTag(Objects.requireNonNull(event.getInventory().getItem(20))).equalsIgnoreCase("gui/add_artifact_item")) {
                event.getPlayer().getInventory().addItem(Objects.requireNonNull(event.getInventory().getItem(20)));
                Player player = (Player) event.getPlayer();
                player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1, 1);
            }
        }
    }


    // Handlers for SelectItemToUpgradeMenu
    @EventHandler
    public void onSelectItemUpgradeMenuClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof SelectItemToUpgradeMenu)) {
            return;
        }

        event.setCancelled(true);
        String itemID = GetSlommyItem.getFromItemTag(event.getCurrentItem());
        int slot = event.getSlot();
        if (itemID == null) {
            return;
        }
        if (!itemID.startsWith("gui/")) {
            // Make sure the slot is in the GUI and NOT the player's inventory
            if (slot < 0 || slot > 35) {
                return;
            }
            UpgradeMenu menu = new UpgradeMenu();
            Player player = (Player) event.getWhoClicked();
            player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1, 1);
            menu.openUpgradeMenu((Player) event.getWhoClicked(), event.getCurrentItem(), ((SelectItemToUpgradeMenu) event.getInventory().getHolder()).series);
        }

        event.setCancelled(true);
    }
}
