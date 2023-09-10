package net.slommy.slommypanels.GUIs.shops;

import net.kyori.adventure.text.Component;
import net.slommy.essentials.SlommyItems.Items;
import net.slommy.essentials.util.Msg;
import net.slommy.essentials.util.SlommyItemExchange;
import net.slommy.slommypanels.SlommyPanels;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.janboerman.guilib.api.menu.CloseButton;
import xyz.janboerman.guilib.api.menu.ItemButton;
import xyz.janboerman.guilib.api.menu.MenuHolder;

import java.util.ArrayList;
import java.util.List;

public class ShopPanel {
    public static MenuHolder<SlommyPanels> open(Player player, Shop shop, int page) {
        MenuHolder<SlommyPanels> panel;
        if (shop.getPages() > 1) {
            panel = new MenuHolder<>(SlommyPanels.getInstance(), 54, shop.getTitle() + " - (" + page + "/" + shop.getPages() + ")");
        } else {
            panel = new MenuHolder<>(SlommyPanels.getInstance(), 54, shop.getTitle());
        }
        List<Integer> takenSlots = new ArrayList<>();
        // make a border of empty panes around the shop
        for (int i = 0; i < 54; i++) {
            if (i < 9 || i > 44 || i % 9 == 0 || i % 9 == 8) {
                panel.setButton(i, new ItemButton<>(Items.items.get("gui/blank_pane").build(player)));
                takenSlots.add(i);
            }
        }
        if (page != 1) {
            ItemButton previousPageButton = new ItemButton(Items.items.get("gui/prev_page").build(player)) {
                @Override
                public void onClick(MenuHolder menuHolder, InventoryClickEvent event) {
                    MenuHolder<SlommyPanels> panel = ShopPanel.open(player, shop, page - 1);
                    player.openInventory(panel.getInventory());
                }
            };
            panel.setButton(45, previousPageButton);
        }
        if (shop.getPages() > page) {
            ItemButton nextPageButton = new ItemButton(Items.items.get("gui/next_page").build(player)) {
                @Override
                public void onClick(MenuHolder menuHolder, InventoryClickEvent event) {
                    MenuHolder<SlommyPanels> panel = ShopPanel.open(player, shop, page + 1);
                    player.openInventory(panel.getInventory());
                }
            };
            panel.setButton(53, nextPageButton);
        }

        if (SlommyItemExchange.checkInventory(player, Items.items.get("items/money_printer")) >= 1) {
            ItemStack moneyPrinter = Items.items.get("items/money_printer").build(player);
            ItemMeta meta = moneyPrinter.getItemMeta();
            meta.displayName(Msg.JSONcolor("&6Access Bank"));
            List<Component> lore = new ArrayList<>();
            lore.add(Msg.JSONcolor("&7Feel so clean! Your Money Machine"));
            lore.add(Msg.JSONcolor("&7allows you to access your Bank Account"));
            lore.add(Msg.JSONcolor("&7while shopping!"));
            lore.add(Msg.JSONcolor("&eClick to access Bank!"));
            meta.lore(lore);
            moneyPrinter.setItemMeta(meta);
            ItemButton moneyPrinterButton = new ItemButton(moneyPrinter);
            panel.setButton(50, moneyPrinterButton);
        }

        panel.setButton(49, new CloseButton<>(Items.items.get("gui/close").build(player)));
        int i = 10;
        for (ShopItem shopItem : shop.getItems(page)) {
            while (takenSlots.contains(i)) {
                i++;
            }
            if (i > 44) {
                break;
            }
            ItemButton ShopItemButton = new ItemButton(shopItem.renderItem(player)) {
                @Override
                public void onClick(MenuHolder menuHolder, InventoryClickEvent event) {
                    if (player.hasPermission(shopItem.getRequirement())) {
                        if (shopItem.getDisplayItem() == Items.items.get("gui/nothing")) {
                            return;
                        }
                        shopItem.exchange(player);
                    } else if (!player.hasPermission(shopItem.getRequirement())) {
                        Msg.send(player, "&cYou have not unlocked this item.");
                        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 0);
                    }
                }
            };
            panel.setButton(i, ShopItemButton);
            i++;
        }
        return panel;
    }
}
