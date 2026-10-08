package net.slommy.slommypanels.GUIs.enchant;

import net.slommy.essentials.SlommyItems.ItemUpdateUtils;
import net.slommy.essentials.SlommyItems.Items;
import net.slommy.essentials.util.Msg;
import net.slommy.slommypanels.SlommyPanels;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class EnchantmentTableMenu implements InventoryHolder {
    public final ItemStack ADD_ENCHANT_ITEM;
    private final Player player;
    public final ItemStack INVALID_COMBINATION_ITEM;
    private EnchantmentSlot slot1;
    private EnchantmentSlot slot2;
    private final ItemStack BLANK_PURPLE;
    private final ItemStack BLANK_PINK;
    private final ItemStack BLANK_MAGENTA;
    public ItemStack result;


    @Override
    public @NotNull Inventory getInventory() {
        return SlommyPanels.getInstance().getServer().createInventory(this,54, Msg.JSONcolor("Enchantment Table"));
    }

    public EnchantmentTableMenu(Player player) {
        this.player = player;
        ADD_ENCHANT_ITEM = Items.get("gui/add_enchant_item").build(player);
        INVALID_COMBINATION_ITEM = Items.get("gui/invalid_combination").build(player);
        BLANK_MAGENTA = Items.items.get("gui/blank_magenta").build(player);
        BLANK_PURPLE = Items.items.get("gui/blank_purple").build(player);
        BLANK_PINK = Items.items.get("gui/blank_pink").build(player);
        result = INVALID_COMBINATION_ITEM;
        slot1 = new EnchantmentSlot(ADD_ENCHANT_ITEM);
        slot2 = new EnchantmentSlot(ADD_ENCHANT_ITEM);
    }

    public void openEnchantmentTableMenu() {
        Inventory inv = getInventory();

        inv = populateMainMenuBorder(inv);
        calculateResultItem(inv);
        inv = populateEnchantingArea(inv);


        player.openInventory(inv);
        ItemUpdateUtils.updateItemInContainer(player, 22);
    }

    private Inventory populateMainMenuBorder(Inventory inv) {
        List<Integer> takenSlots = new ArrayList<>();
        for (int i = 0; i < inv.getSize(); i++) {
            if (i < 9 || i > 44 || i % 9 == 0 || i % 9 == 8) {
                inv.setItem(i, BLANK_PINK);
                takenSlots.add(i);
            }
            if (i >= 9 && i <= 44 && (i < 18 || i > 35 || i % 9 == 1 || i % 9 == 7)) {
                // This code runs ONLY for the inner border slots
                if (!takenSlots.contains(i)) {
                    inv.setItem(i, BLANK_MAGENTA);
                    takenSlots.add(i);
                }
            }
        }
        return inv;
    }

    private Inventory populateEnchantingArea(Inventory inv) {
        ItemStack[] row1Defaults = {BLANK_PURPLE, BLANK_PURPLE, result, BLANK_PURPLE, BLANK_PURPLE};
        int[] row1Indices = {20,21,22,23,24};
        ItemStack[] row2Defaults = {slot1.getItem(), BLANK_PURPLE, Items.get("gui/enchant_icon").build(player), BLANK_PURPLE, slot2.getItem()};
        for (int i = 0; i < 5; i++) {
            inv.setItem(row1Indices[i], row1Defaults[i]);
            inv.setItem(row1Indices[i] + 9, row2Defaults[i]);
        }

        return inv;
    }
    private static List<Integer> calculateTakenSlots(Inventory inv) {
        List<Integer> takenSlots = new ArrayList<>();
        for (int i = 0; i < inv.getSize(); i++) {
            if (inv.getItem(i) == null) continue;
            if (!inv.getItem(i).isEmpty()) takenSlots.add(i);
        }
        return takenSlots;
    }

    public void openSelectionScreen(int slot) {
        if (slot > 2) return;

        EnchantmentSlotSelectionMenu slotSelectionMenu;

        if (slot == 1) {
            slotSelectionMenu = new EnchantmentSlotSelectionMenu(slot1, this);
        } else {
            slotSelectionMenu = new EnchantmentSlotSelectionMenu(slot2, this);
        }


        player.closeInventory();
        slotSelectionMenu.openSelectSlotMenu();
    }

    private void calculateResultItem(Inventory inv) {
        ItemStack result = slot1.getCombinedResult(slot2);
        if (result == null) result = INVALID_COMBINATION_ITEM;
        this.result = result;
    }

    public Player getPlayer() {
        return player;
    }

    public EnchantmentSlot getSlot2() {
        return slot2;
    }

    public EnchantmentSlot getSlot1() {
        return slot1;
    }
}
