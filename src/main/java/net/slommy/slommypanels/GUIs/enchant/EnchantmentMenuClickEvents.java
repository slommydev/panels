package net.slommy.slommypanels.GUIs.enchant;

import net.slommy.essentials.SlommyItems.Items;
import net.slommy.essentials.SlommyItems.PersistentDataUtils;
import net.slommy.essentials.SlommyItems.SlommyItem;
import net.slommy.essentials.util.GetSlommyItem;
import net.slommy.essentials.util.Msg;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class EnchantmentMenuClickEvents implements Listener {
    @EventHandler
    private static void onClickItem(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof EnchantmentTableMenu tableMenu)) return;
        event.setCancelled(true); // Never let the player take anything from the menu
        Player player = (Player) event.getWhoClicked();
        Msg.debug(player, event.getRawSlot() + "");
        if (event.getRawSlot() >= 9*6 || event.getRawSlot() == -999) {
            Msg.debug(player, "Invalid click slot");
            return;
        } // Do not apply logic if clicked item is in player's inventory
        SlommyItem clickedSlommyItem = Items.get(GetSlommyItem.getFromItemTag(event.getCurrentItem()));
        ItemStack clickedItemStack = event.getCurrentItem();
        Msg.debug(player, clickedSlommyItem.getItemID());

        if (clickedSlommyItem.getItemID().contains("blank")) return; // Do not do anything if item clicked is a border block (all of them have blank in their item ID)

        if (event.getRawSlot() == 29) {
            if (!tableMenu.getSlot1().isEmpty()) {
                addItem(player, tableMenu.getSlot1().getItem());
                tableMenu.getSlot1().setItem(tableMenu.ADD_ENCHANT_ITEM);
            }
            tableMenu.openSelectionScreen(1);
        } else if (event.getRawSlot() == 33) {
            if (!tableMenu.getSlot2().isEmpty()) {
                addItem(player, tableMenu.getSlot2().getItem());
                tableMenu.getSlot2().setItem(tableMenu.ADD_ENCHANT_ITEM);
            }
            tableMenu.openSelectionScreen(2);
        }

        if (event.getRawSlot() == 22 || event.getRawSlot() == 22 + 9) {
            ItemStack resultItem = event.getInventory().getItem(22);
            if (resultItem.equals(tableMenu.INVALID_COMBINATION_ITEM)) return;

            addItem(player, resultItem);
            tableMenu.getSlot1().setItem(tableMenu.ADD_ENCHANT_ITEM);
            tableMenu.getSlot2().setItem(tableMenu.ADD_ENCHANT_ITEM);
            tableMenu.openEnchantmentTableMenu();
            player.playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0f, 1.0f);
        }
    }

    @EventHandler
    private static void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        Msg.debug(player, event.getReason().toString());
        List<InventoryCloseEvent.Reason> validRefundReasons = List.of(InventoryCloseEvent.Reason.DEATH, InventoryCloseEvent.Reason.DISCONNECT, InventoryCloseEvent.Reason.PLAYER);
        if (!validRefundReasons.contains(event.getReason())) return;
        if (event.getInventory().getHolder() instanceof EnchantmentTableMenu tableMenu) {
            if (!tableMenu.getSlot1().isEmpty()) addItem(player, tableMenu.getSlot1().getItem());
            if (!tableMenu.getSlot2().isEmpty()) addItem(player, tableMenu.getSlot2().getItem());
        }
        if (event.getInventory().getHolder() instanceof  EnchantmentSlotSelectionMenu selectionMenu) {
            EnchantmentTableMenu tableMenu = selectionMenu.getTableMenu();
            if (!tableMenu.getSlot1().isEmpty()) addItem(player, tableMenu.getSlot1().getItem());
            if (!tableMenu.getSlot2().isEmpty()) addItem(player, tableMenu.getSlot2().getItem());
        }
    }

    @EventHandler
    private static void onSlotSelectClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof EnchantmentSlotSelectionMenu slotSelectionMenu)) return;

        event.setCancelled(true);
        Player player =  (Player) event.getWhoClicked();

        if (event.getRawSlot() >= 9*6 || event.getRawSlot() == -999) return;

        ItemStack clickedItemStack = event.getCurrentItem();
        SlommyItem clickedSlommyItem = Items.get(GetSlommyItem.getFromItemTag(clickedItemStack));

        if (clickedSlommyItem.getItemID().contains("blank")) return;
        if (clickedSlommyItem.getItemID().equalsIgnoreCase("gui/back")) {
            slotSelectionMenu.getTableMenu().openEnchantmentTableMenu();
        }
        if (clickedSlommyItem.getItemID().contains("gui/")) return; // Do not allow ANY items beginning with category gui to be put into the menu


        slotSelectionMenu.getEnchantmentSlot().setItem(clickedItemStack);
        removeItem(player, clickedItemStack);
        slotSelectionMenu.getTableMenu().openEnchantmentTableMenu();
    }

    private static void addItem(Player player, ItemStack item) {
        player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_ITEM_PICKUP, 1.0f, 1.0f);
        player.getInventory().addItem(item);
    }

    private static void removeItem(Player player, ItemStack item) {
        player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_ITEM_PICKUP, 1.0f, 1.0f);
        player.getInventory().remove(item);
    }
}
