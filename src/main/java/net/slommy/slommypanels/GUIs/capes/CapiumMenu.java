package net.slommy.slommypanels.GUIs.capes;

import net.kyori.adventure.text.Component;
import net.slommy.essentials.SlommyItems.*;
import net.slommy.essentials.player.PersistentPlayerData;
import net.slommy.essentials.player.PersistentPlayerDataUtil;
import net.slommy.essentials.util.GetSlommyItem;
import net.slommy.essentials.util.Msg;
import net.slommy.essentials.util.PlayerTrackerUtil;
import net.slommy.slommypanels.SlommyPanels;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.janboerman.guilib.api.menu.CloseButton;
import xyz.janboerman.guilib.api.menu.ItemButton;
import xyz.janboerman.guilib.api.menu.MenuHolder;

import java.util.ArrayList;
import java.util.List;

public class CapiumMenu {
    public static void openCapiumMenu(Player player) {
        PersistentPlayerData data = PersistentPlayerDataUtil.getPersistentPlayerData(player);
        int capiumBarterScore = data.getCapiumBarterScore();
        List<ItemStack> originalCapes = new ArrayList<>();
        if (capiumBarterScore < 4) {
            MenuHolder<SlommyPanels> panel = new MenuHolder<>(SlommyPanels.getInstance(), InventoryType.DISPENSER, "Capium");
            for (int i = 0; i < 9; i++) {
                panel.setButton(i, new ItemButton(Items.get("gui/blank_pane").build(player)));
            }
            if (!player.hasPermission("capium.barter.cooldown")) {
                panel.setButton(4, new ItemButton(Items.get("gui/buy_cape_swapper_" + capiumBarterScore).build(player)) {
                    @Override
                    public void onClick(MenuHolder menuHolder, InventoryClickEvent event) {
                        int cost = 0;
                        switch (capiumBarterScore) {
                            case 0:
                                cost = 1000000;
                                break;
                            case 1:
                                cost = 10000;
                                break;
                            case 2:
                                cost = 1000;
                                break;
                            case 3:
                                cost = 1;
                                break;
                            default:
                                cost = 1;
                                break;
                        }
                        if (event.isLeftClick()) {
                            long playerBankBalance = PlayerTrackerUtil.getPlayerTracker(player).player_bank_balance;
                            if (playerBankBalance >= cost) {
                                PlayerTrackerUtil.getPlayerTracker(player).player_bank_balance -= cost;
                                data.setCapiumBarterScore(4);
                                player.closeInventory();
                                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_YES, 1, 1);
                                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
                                openCapiumMenu(player);
                            }
                        } else if (event.isRightClick()) {
                            if (capiumBarterScore == 3) return;
                            SlommyPanels.getInstance().getServer().dispatchCommand(SlommyPanels.getInstance().getServer().getConsoleSender(), "lp user " + player.getName() + " permission settemp capium.barter.cooldown true 10m");
                            data.setCapiumBarterScore(capiumBarterScore + 1);
                            // Make the console execute a command
                            player.closeInventory();
                            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_AMBIENT, 1, 1);
                        }
                    }
                });
            } else {
                panel.setButton(4, new ItemButton(Items.get("gui/capium_is_thinking").build(player)));
            }
            player.openInventory(panel.getInventory());
        } else {
            MenuHolder<SlommyPanels> panel = new MenuHolder<>(SlommyPanels.getInstance(), 6 * 9, "Cape Locker");
            List<Integer> takenSlots = new ArrayList<>();
            for (int i = 0; i < panel.getInventory().getSize(); i++) {
                if (i < 9 || i > 44 || i % 9 == 0 || i % 9 == 8) {
                    panel.setButton(i, new ItemButton(Items.get("gui/blank_pane").build(player)));
                    takenSlots.add(i);
                }
            }

            CloseButton closeButton = new CloseButton(Items.get("gui/close").build(player));
            panel.setButton(49, closeButton);
            ItemButton addCapeToCollectionButton = new ItemButton(Items.get("gui/add_cape_to_collection").build(player)) {
                @Override
                public void onClick(MenuHolder menuHolder, InventoryClickEvent event) {
                    player.closeInventory();
                    SelectCapeToAddMenu selectCapeToAddMenu = new SelectCapeToAddMenu();
                    selectCapeToAddMenu.openSelectCapeToAddMenu(player);
                }
            };
            panel.setButton(48, addCapeToCollectionButton);


            int slot = 0;
            for (int i = 0; i < data.getCapeCollectionIDs().size(); i++) {
                String capeID = data.getCapeCollectionIDs().get(i);
                ItemStack capeOriginal = data.getCapeCollectionItems().get(i);
                originalCapes.add(capeOriginal);
                ItemStack cape = capeOriginal.clone();
                ItemMeta meta = cape.getItemMeta();
                List<Component> lore = meta.lore();
                assert lore != null;
                lore.add(Msg.JSONcolor(""));
                if (data.getEquippedCapeID().equalsIgnoreCase(capeID)) {
                    lore.add(Msg.JSONcolor("&eLeft Click to unequip!"));
                } else {
                    lore.add(Msg.JSONcolor("&eLeft Click to equip!"));
                }
                lore.add(Msg.JSONcolor("&eRight Click to take out!"));
                meta.lore(lore);
                cape.setItemMeta(meta);
                if (data.getEquippedCapeID().equalsIgnoreCase(capeID)) {
                    cape.setType(Material.LIME_DYE);
                }
                ItemButton capeButton = new ItemButton(cape) {
                    @Override
                    public void onClick(MenuHolder menuHolder, InventoryClickEvent event) {
                        if (event.isLeftClick()) {
                            if (data.getEquippedCapeItem() == null) {
                                data.setEquippedCapeItem(cape);
                                data.setEquippedCapeID(capeID);
                                player.closeInventory();
                                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
                                player.playSound(player.getLocation(), Sound.ENTITY_HORSE_SADDLE, 1, 1);
                                openCapiumMenu(player);
                                data.save();
                                return;
                            }
                            if (!data.getEquippedCapeID().equalsIgnoreCase(capeID)) {
                                data.setEquippedCapeItem(cape);
                                data.setEquippedCapeID(capeID);
                                player.closeInventory();
                                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
                                player.playSound(player.getLocation(), Sound.ENTITY_HORSE_SADDLE, 1, 1);
                                openCapiumMenu(player);
                                data.save();
                            } else {
                                // unequip the cape
                                data.setEquippedCapeItem(null);
                                data.setEquippedCapeID("");
                                player.closeInventory();
                                player.playSound(player.getLocation(), Sound.ENTITY_HORSE_SADDLE, 1, 0);
                                openCapiumMenu(player);
                                data.save();
                            }
                        } else if (event.isRightClick()) {
                            // Make sure the player's inventory is not full
                            if (player.getInventory().firstEmpty() == -1) {
                                Msg.send(player, "&cYour inventory is full!");
                                return;
                            }
                            // take out the cape
                            data.getCapeCollectionIDs().remove(capeID);
                            data.getCapeCollectionItems().remove(capeOriginal);
                            // If the cape is equipped, unequip it
                            if (data.getEquippedCapeID().equalsIgnoreCase(capeID)) {
                                data.setEquippedCapeItem(null);
                                data.setEquippedCapeID("");
                            }
                            player.getInventory().addItem(capeOriginal);
                            player.closeInventory();
                            player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1, 1);
                            openCapiumMenu(player);
                            data.save();
                        }
                    }
                };
                while (takenSlots.contains(slot)) {
                    slot++;
                }
                panel.setButton(slot, capeButton);
                slot++;
            }

            ItemStack capeCollection = Items.get("gui/your_cape_collection").build(player);
            PersistentItemData yourCapes = new PersistentItemData("your_capes", PersistentItemData.Application.PLACEHOLDER, data.getCapeCollectionIDs().size() + "");
            PersistentItemData totalCapes = new PersistentItemData("total_capes", PersistentItemData.Application.PLACEHOLDER, Items.getItemsInCategory("capes").size() + "");
            PersistentDataUtils.addPersistentDataTag(capeCollection, yourCapes);
            PersistentDataUtils.addPersistentDataTag(capeCollection, totalCapes);
            panel.setButton(4, new ItemButton(capeCollection));

            player.openInventory(panel.getInventory());
            ItemUpdateUtils.updateItemInContainer(player, 4);
        }
    }
}
