package net.slommy.slommypanels.GUIs.enchant;

import net.slommy.essentials.SlommyItems.Item;
import net.slommy.essentials.SlommyItems.Items;
import net.slommy.essentials.SlommyItems.SlommyItem;
import net.slommy.essentials.util.GetSlommyItem;
import net.slommy.essentials.util.Msg;
import net.slommy.slommypanels.SlommyPanels;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnchantmentSlotSelectionMenu implements InventoryHolder {
    private final EnchantmentTableMenu tableMenu;
    private final EnchantmentSlot slot;
    private final Player player;

    @Override
    public @NotNull Inventory getInventory() {
        return SlommyPanels.getInstance().getServer().createInventory(this,54, Msg.JSONcolor("Select Enchantment Slot Item"));
    }

    public EnchantmentSlotSelectionMenu(EnchantmentSlot slot, EnchantmentTableMenu tableMenu) {
        this.tableMenu = tableMenu;
        this.slot = slot;
        this.player = tableMenu.getPlayer();
    }

    public void openSelectSlotMenu() {
        Inventory inv = getInventory();
        List<Integer> takenSlots = new ArrayList<>();
        populateBorder(inv, takenSlots);
        populateEnchantableItems(inv, takenSlots);


        player.openInventory(inv);
    }

    private void populateBorder(Inventory inv, List<Integer> takenSlots) {
        for (int i = 0; i < inv.getSize(); i++) {
            if (i < 9 || i > 44 || i % 9 == 0 || i % 9 == 8) {
                inv.setItem(i, Items.items.get("gui/blank_pane").build(player));
                takenSlots.add(i);
            }
        }
    }

    private void populateEnchantableItems(Inventory inv, List<Integer> takenSlots) {
        List<ItemStack> items = new ArrayList<>();
        Inventory playerInventory = player.getInventory();
        for (int i = 0; i < playerInventory.getSize(); i++) {
            ItemStack potentialItem = playerInventory.getItem(i);
            if (potentialItem == null) continue;

            String itemID = GetSlommyItem.getFromItemTag(potentialItem);

            if (itemID == null) continue;

            SlommyItem slommyItem = Items.get(itemID);
            if (slommyItem == null) continue;

            if (slommyItem.getTypeEnum().isEnchantable()) {
                items.add(potentialItem);
            }
        }
        int inventorySlot = 10;
        for (ItemStack enchantableItem : items) {
            while (takenSlots.contains(inventorySlot)) {
                inventorySlot++;
            }
            inv.setItem(inventorySlot, enchantableItem);
            inventorySlot++;
        }
    }

    public EnchantmentSlot getEnchantmentSlot() {
        return slot;
    }

    public EnchantmentTableMenu getTableMenu() {
        return tableMenu;
    }


}
