package net.slommy.slommypanels.GUIs.misc.brad;

import net.slommy.essentials.SlommyItems.*;
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

public class BradUpgradeMenuEvents implements Listener {

        // Handlers for Upgrade Menu
        @EventHandler
        public void onClick(InventoryClickEvent event) {
            if (!(event.getInventory().getHolder() instanceof BradPaisleyRodUpgradeMenu)) {
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
                    Msg.send((Player) event.getWhoClicked(), "&cYou can't do that here!");
                } else {
                    // Close the menu
                    event.getWhoClicked().closeInventory();
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
                    SlommyItem requiredItem = Items.get("fish/bass_pro");
                    if (!SlommyItemExchange.checkInventory((Player) event.getWhoClicked(), requiredItem, 1)) {
                        Msg.send((Player) event.getWhoClicked(), "&cYou do not have the required item to upgrade this rod!");
                        return;
                    }
                    // Remove the required item from the player's inventory
                    SlommyItemExchange.takeItems((Player) event.getWhoClicked(), requiredItem, 1);
                    int upgradeNum = PersistentDataUtils.getBradPaisleyUpgrade(item);
                    PersistentDataUtils.addBradPaisleyUpgrade(item, upgradeNum + 1);
                    event.getInventory().setItem(20, item);
                    ItemUpdateUtils.updateItemInContainer((Player) (event.getWhoClicked()), 20);
                    item = event.getInventory().getItem(20);
                    BradPaisleyRodUpgradeMenu menu = new BradPaisleyRodUpgradeMenu();
                    event.getInventory().setItem(20, null);
                    menu.openUpgradeMenu((Player) event.getWhoClicked(), item);
                    Player player = (Player) event.getWhoClicked();
                    SoundLibrary.upgradBradPaisleyRod(player);
                }
            }

        }

        @EventHandler
        public void onClose(InventoryCloseEvent event) {
            if (!(event.getInventory().getHolder() instanceof BradPaisleyRodUpgradeMenu)) {
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

}
