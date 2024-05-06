package net.slommy.slommypanels.GUIs.imbuing;

import net.slommy.essentials.SlommyItems.ItemUpdateUtils;
import net.slommy.essentials.SlommyItems.Items;
import net.slommy.essentials.SlommyItems.PersistentDataUtils;
import net.slommy.essentials.SlommyItems.PersistentItemData;
import net.slommy.essentials.player.PersistentPlayerData;
import net.slommy.essentials.player.PersistentPlayerDataUtil;
import net.slommy.essentials.util.IntegerCommaParser;
import net.slommy.essentials.util.Msg;
import net.slommy.essentials.util.PlayerTrackerUtil;
import net.slommy.essentials.util.SoundLibrary;
import net.slommy.slommypanels.SlommyPanels;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.janboerman.guilib.api.menu.CloseButton;
import xyz.janboerman.guilib.api.menu.ItemButton;
import xyz.janboerman.guilib.api.menu.MenuHolder;

import java.util.List;

public class CharmImbuingMenu implements InventoryHolder {
    @Override
    public Inventory getInventory() {
        return SlommyPanels.getInstance().getServer().createInventory(this,27, Msg.JSONcolor("Imbue Charm"));
    }

    public void openCharmImbuingMenu(Player player) {
        PersistentPlayerData pData = PersistentPlayerDataUtil.getPersistentPlayerData(player);
        int imbuementSlots = pData.getCharmImbueSlots();
        if (imbuementSlots == 0) {
            MenuHolder<SlommyPanels> panel = new MenuHolder<>(SlommyPanels.getInstance(), InventoryType.DISPENSER, "Imbue Charm");

            for (int i = 0; i < 9; i++) {
                panel.setButton(i, new ItemButton<>(Items.get("gui/blank_pane").build(player)));
            }

            ItemStack purchaseImbueSlot = Items.get("gui/purchase_imbuement_slot").build(player);
            int cost = 10000;
            PersistentItemData slotNumber = new PersistentItemData("imbuement_slot_number", PersistentItemData.Application.PLACEHOLDER, "1");
            PersistentDataUtils.addPersistentDataTag(purchaseImbueSlot, slotNumber);
            PersistentItemData slotCost = new PersistentItemData("imbuement_slot_cost", PersistentItemData.Application.PLACEHOLDER, IntegerCommaParser.parse(cost));
            PersistentDataUtils.addPersistentDataTag(purchaseImbueSlot, slotCost);
            panel.setButton(4, new ItemButton(purchaseImbueSlot) {
                @Override
                public void onClick(MenuHolder menuHolder, InventoryClickEvent event) {
                    long PlayerBankBalance = PlayerTrackerUtil.getPlayerTracker(player).player_bank_balance;
                    if (PlayerBankBalance >= cost) {
                        PlayerTrackerUtil.getPlayerTracker(player).player_bank_balance -= cost;
                        pData.setCharmImbueSlots(1);
                        player.closeInventory();
                        openCharmImbuingMenu(player);
                        SoundLibrary.imbueSlot(player);
                    } else {
                        Msg.send(player, "&cYou do not have enough money to purchase an imbuement slot.");
                    }
                }
            });
            player.openInventory(panel.getInventory());
            ItemUpdateUtils.updateItemInContainer(player, 4);
        } else if (imbuementSlots == 1 || imbuementSlots == 2) {
            MenuHolder<SlommyPanels> panel = new MenuHolder<>(SlommyPanels.getInstance(), 4*9, "Imbue Charm");

            for (int i = 3*9; i < 4*9; i++) {
                panel.setButton(i, new ItemButton<>(Items.get("gui/blank_pane").build(player)));
            }

            if (imbuementSlots == 1) {
                ItemStack purchaseImbueSlot = Items.get("gui/purchase_imbuement_slot").build(player);
                int cost = 100000;
                PersistentItemData slotNumber = new PersistentItemData("imbuement_slot_number", PersistentItemData.Application.PLACEHOLDER, "2");
                PersistentDataUtils.addPersistentDataTag(purchaseImbueSlot, slotNumber);
                PersistentItemData slotCost = new PersistentItemData("imbuement_slot_cost", PersistentItemData.Application.PLACEHOLDER, IntegerCommaParser.parse(cost));
                PersistentDataUtils.addPersistentDataTag(purchaseImbueSlot, slotCost);
                panel.setButton(32, new ItemButton(purchaseImbueSlot) {
                    @Override
                    public void onClick(MenuHolder menuHolder, InventoryClickEvent event) {
                        long PlayerBankBalance = PlayerTrackerUtil.getPlayerTracker(player).player_bank_balance;
                        if (PlayerBankBalance >= cost) {
                            PlayerTrackerUtil.getPlayerTracker(player).player_bank_balance -= cost;
                            pData.setCharmImbueSlots(2);
                            player.closeInventory();
                            openCharmImbuingMenu(player);
                            SoundLibrary.imbueSlot(player);
                        } else {
                            Msg.send(player, "&cYou do not have enough money to purchase an imbuement slot.");
                        }
                    }
                });
            }

            CloseButton closeButton = new CloseButton(Items.get("gui/close").build(player));
            panel.setButton(31, closeButton);
            List<String> imbued_item_ids = pData.getImbuedCharms();
            List<ItemStack> imbued_items = pData.getImbuedCharmsItems();
            for (int i = 0; i < imbuementSlots; i++) {
                ItemButton emptySlot = new ItemButton(Items.get("gui/empty_imbuement_slot").build(player)) {
                    @Override
                    public void onClick(MenuHolder menuHolder, InventoryClickEvent event) {
                        SelectCharmToImbueMenu selectCharmToImbueMenu = new SelectCharmToImbueMenu();
                        selectCharmToImbueMenu.openSelectCharmToImbueMenu(player);
                    }
                };
                panel.setButton(i, emptySlot);
            }
            for (int i = 0; i < imbued_item_ids.size(); i++) {
                ItemStack imbuedItem = imbued_items.get(i);
                int finalI = i;
                ItemButton imbuedSlot = new ItemButton(imbuedItem) {
                    @Override
                    public void onClick(MenuHolder menuHolder, InventoryClickEvent event) {
                        PersistentPlayerData data = PersistentPlayerDataUtil.getPersistentPlayerData(player);
                        List<String> imbued_charm_ids = data.getImbuedCharms();
                        imbued_charm_ids.remove(finalI);
                        data.setImbuedCharms(imbued_charm_ids);
                        List<ItemStack> imbued_charm_items = data.getImbuedCharmsItems();
                        imbued_charm_items.remove(finalI);
                        data.setImbuedCharmsItems(imbued_charm_items);
                        player.getInventory().addItem(imbuedItem);
                        player.closeInventory();
                        openCharmImbuingMenu(player);
                        SoundLibrary.imbueSlot(player);
                        data.save();
                    }
                };
                panel.setButton(i, imbuedSlot);
            }


            player.openInventory(panel.getInventory());
            ItemUpdateUtils.updateItemInContainer(player, 32);
            for (int i = 0; i < imbued_items.size(); i++) {
                ItemUpdateUtils.updateItemInContainer(player, i);
            }
        }
    }

}
