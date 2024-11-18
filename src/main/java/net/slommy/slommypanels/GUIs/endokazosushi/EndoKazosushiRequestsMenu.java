package net.slommy.slommypanels.GUIs.endokazosushi;

import net.kyori.adventure.text.Component;
import net.slommy.essentials.PlayerStats.PlayerTrackers;
import net.slommy.essentials.PlayerStats.skills.PlayerSkills;
import net.slommy.essentials.SlommyItems.ItemUpdateUtils;
import net.slommy.essentials.SlommyItems.Items;
import net.slommy.essentials.SlommyItems.SlommyItem;
import net.slommy.essentials.player.PersistentPlayerData;
import net.slommy.essentials.player.PersistentPlayerDataUtil;
import net.slommy.essentials.util.*;
import net.slommy.slommypanels.SlommyPanels;
import net.slommy.slommypanels.util.CommonButtons;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.janboerman.guilib.api.menu.ItemButton;
import xyz.janboerman.guilib.api.menu.MenuHolder;

import java.text.DecimalFormat;
import java.util.List;

public class EndoKazosushiRequestsMenu {
    public static void openRequestsMenu(Player player) {
        MenuHolder<SlommyPanels> panel = new MenuHolder<>(SlommyPanels.getInstance(), 9*5, "Endo Kazosushi's Requests");

        for (int i = 0; i < 9*5; i++) {
            panel.setButton(i, CommonButtons.blankButton(player));
        }

        DecimalFormat df = new DecimalFormat("#,###.##");
        PersistentPlayerData data = PersistentPlayerDataUtil.getPersistentPlayerData(player);
        if (data.getLastEndoKazosushiSale() < EndoKazosushi.getLastRequestTime()) {
            data.setEndoKazosushiFilletsSold(0);
            data.setEndoKazosushiFishSold(0);
        }

        panel.setButton(4, new ItemButton<>(Items.get("gui/endo_kazosushis_requests_board").build(player)));
        ItemButton backButton = new ItemButton(Items.get("gui/back").build(player)) {
            @Override
            public void onClick(MenuHolder menuHolder, InventoryClickEvent event) {
                EndoKazosushiMainMenu.openMainMenu((Player) event.getWhoClicked());
            }
        };
        panel.setButton(40, backButton);
        panel.setButton(39, new ItemButton(Items.get("gui/endo_kazosushis_silver_platter").build(player)));
        panel.setButton(44, new ItemButton(Items.get("gui/endo_subscribe_to_my_newsletter").build(player)) {
            @Override
            public void onClick(MenuHolder menuHolder, InventoryClickEvent event) {
                Msg.send(player, "&cThis is not currently available.");
            }
        });

        ItemStack fish = EndoKazosushi.getRequestedFish().build(player);
        fish = ItemUpdateUtils.updateItemStack(fish, player);
        ItemMeta fishMeta = fish.getItemMeta();
        List<Component> lore = fish.lore();
        // add the displayname to the beginning of the lore
        lore.add(0, Msg.JSONcolor(EndoKazosushi.getRequestedFish().getDisplayName(fish) + " &2x" + EndoKazosushi.getRequestedFishAmount()));
        fishMeta.displayName(Msg.JSONcolor("&aRequested Fish:"));
        lore.add(Msg.JSONcolor("&7&l&m---------------------"));
        lore.add(Msg.JSONcolor("&7Total Rewards:"));
        lore.add(Msg.JSONcolor("&7- &6" + df.format(EndoKazosushi.getRequestedFishValuePer() * EndoKazosushi.getRequestedFishAmount()) + " \uD83C\uDD6D"));
        lore.add(Msg.JSONcolor("&7- &3" + df.format(EndoKazosushi.getRequestedFishXP() * EndoKazosushi.getRequestedFishAmount()) + " Fishing XP"));
        lore.add(Msg.JSONcolor("&7Your Progress: &a" + data.getEndoKazosushiFishSold() + "&7/&2" + EndoKazosushi.getRequestedFishAmount()));
        lore.add(Msg.JSONcolor("&eClick to sell fish!"));
        fishMeta.lore(lore);
        fish.setItemMeta(fishMeta);
        if (EndoKazosushi.getRequestedFishAmount() <= data.getEndoKazosushiFishSold()) {
            fish = Items.get("gui/endo_order_complete").build(player);
            ItemMeta completeMeta = fish.getItemMeta();
            completeMeta.displayName(Msg.JSONcolor("&aRequested Fish: &2&lORDER FILLED!"));
            lore.remove(lore.size() -1);
            lore.remove(lore.size() -1);
            completeMeta.lore(lore);
            fish.setItemMeta(completeMeta);
        }
        panel.setButton(20, new ItemButton<>(fish) {
            @Override
            public void onClick(MenuHolder menuHolder, InventoryClickEvent event) {
                sellItem(EndoKazosushi.getRequestedFish(), EndoKazosushiRequestFillReason.FISH, (Player) event.getWhoClicked(), EndoKazosushi.getRequestedFishValuePer(), EndoKazosushi.getRequestedFishXP());
            }
        });

        ItemStack fillet = Items.get(EndoKazosushi.getRequestedFillet().getAssociatedItemID()).build(player);
        fillet = ItemUpdateUtils.updateItemStack(fillet, player);
        ItemMeta filletMeta = fillet.getItemMeta();
        List<Component> filletLore = fillet.lore();
        // add the displayname to the beginning of the lore
        filletLore.add(0, Msg.JSONcolor(Items.get(EndoKazosushi.getRequestedFillet().getAssociatedItemID()).getDisplayName() + " &2x" + EndoKazosushi.getRequestedFilletAmount()));
        filletMeta.displayName(Msg.JSONcolor("&aRequested Fillet:"));
        filletLore.add(Msg.JSONcolor("&7&l&m---------------------"));
        filletLore.add(Msg.JSONcolor("&7Total Rewards:"));
        filletLore.add(Msg.JSONcolor("&7- &6" + df.format(EndoKazosushi.getRequestedFilletValuePer() * EndoKazosushi.getRequestedFilletAmount()) + " \uD83C\uDD6D"));
        filletLore.add(Msg.JSONcolor("&7- &3" + df.format(EndoKazosushi.getRequestedFilletXP() * EndoKazosushi.getRequestedFilletAmount()) + " Fishing XP"));
        filletLore.add(Msg.JSONcolor("&7Your Progress: &a" + data.getEndoKazosushiFilletsSold() + "&7/&2" + EndoKazosushi.getRequestedFilletAmount()));
        filletLore.add(Msg.JSONcolor("&eClick to sell fillets!"));
        filletMeta.lore(filletLore);
        fillet.setItemMeta(filletMeta);
        if (EndoKazosushi.getRequestedFilletAmount() <= data.getEndoKazosushiFilletsSold()) {
            fillet = Items.get("gui/endo_order_complete").build(player);
            ItemMeta completeMeta = fillet.getItemMeta();
            completeMeta.displayName(Msg.JSONcolor("&aRequested Fillet: &2&lORDER FILLED!"));
            filletLore.remove(filletLore.size() -1);
            filletLore.remove(filletLore.size() -1);
            completeMeta.lore(filletLore);
            fillet.setItemMeta(completeMeta);
        }
        panel.setButton(22, new ItemButton<>(fillet) {
            @Override
            public void onClick(MenuHolder menuHolder, InventoryClickEvent event) {
                sellItem(Items.get(EndoKazosushi.getRequestedFillet().getAssociatedItemID()), EndoKazosushiRequestFillReason.FILLET, (Player) event.getWhoClicked(), EndoKazosushi.getRequestedFilletValuePer(), EndoKazosushi.getRequestedFilletXP());
            }
        });

        ItemStack globalRequest = EndoKazosushi.getRequestedGlobalItem().build(player);
        globalRequest = ItemUpdateUtils.updateItemStack(globalRequest, player);
        ItemMeta globalRequestMeta = globalRequest.getItemMeta();
        List<Component> globalRequestLore = globalRequest.lore();
        // add the displayname to the beginning of the lore
        globalRequestLore.add(0, Msg.JSONcolor(EndoKazosushi.getRequestedGlobalItem().getDisplayName(globalRequest) + " &2x" + EndoKazosushi.getRequestedGlobalItemAmount()));
        globalRequestMeta.displayName(Msg.JSONcolor("&aSpecial Item:"));
        globalRequestLore.add(Msg.JSONcolor("&7&l&m---------------------"));
        globalRequestLore.add(Msg.JSONcolor("&7Total Rewards:"));
        globalRequestLore.add(Msg.JSONcolor("&7- &6" + df.format(EndoKazosushi.getRequestedGlobalItemValuePer() * EndoKazosushi.getRequestedGlobalItemAmount()) + " \uD83C\uDD6D"));
        globalRequestLore.add(Msg.JSONcolor("&7- &3" + df.format(EndoKazosushi.getRequestedGlobalItemXP() * EndoKazosushi.getRequestedGlobalItemAmount()) + " Fishing XP"));
        globalRequestLore.add(Msg.JSONcolor("&bServer-Wide &7Progress: &a" + EndoKazosushi.getGlobalItemsFilled() + "&7/&2" + EndoKazosushi.getRequestedGlobalItemAmount()));
        globalRequestLore.add(Msg.JSONcolor("&eClick to sell!"));
        globalRequestMeta.lore(globalRequestLore);
        globalRequest.setItemMeta(globalRequestMeta);
        if (EndoKazosushi.getRequestedGlobalItemAmount() <= EndoKazosushi.getGlobalItemsFilled()) {
            globalRequest = Items.get("gui/endo_order_complete").build(player);
            ItemMeta completeMeta = globalRequest.getItemMeta();
            completeMeta.displayName(Msg.JSONcolor("&aSpecial Item: &2&lORDER FILLED!"));
            globalRequestLore.remove(globalRequestLore.size() -1);
            globalRequestLore.remove(globalRequestLore.size() -1);
            completeMeta.lore(globalRequestLore);
            globalRequest.setItemMeta(completeMeta);
        }
        panel.setButton(24, new ItemButton(globalRequest) {
            @Override
            public void onClick(MenuHolder menuHolder, InventoryClickEvent event) {
                sellItem(EndoKazosushi.getRequestedGlobalItem(), EndoKazosushiRequestFillReason.SPECIAL_ITEM, (Player) event.getWhoClicked(), EndoKazosushi.getRequestedGlobalItemValuePer(), EndoKazosushi.getRequestedGlobalItemXP());
            }
        });

        player.openInventory(panel.getInventory());
    }

    public static void sellItem(SlommyItem item, EndoKazosushiRequestFillReason reason, Player player, double moneyPerSale, long xpPerSale) {
        // Use the request fill reason to check if the player has already filled their order
        PersistentPlayerData data = PersistentPlayerDataUtil.getPersistentPlayerData(player);
        if (reason == EndoKazosushiRequestFillReason.FISH) {
            if (data.getEndoKazosushiFishSold() >= EndoKazosushi.getRequestedFishAmount()) {
                Msg.send(player, "&cYou have already filled this order!");
                player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 0);
                return;
            }
        }
        if (reason == EndoKazosushiRequestFillReason.FILLET) {
            if (data.getEndoKazosushiFilletsSold() >= EndoKazosushi.getRequestedFilletAmount()) {
                Msg.send(player, "&cYou have already filled this order!");
                player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 0);
                return;
            }
        }
        if (reason == EndoKazosushiRequestFillReason.SPECIAL_ITEM) {
            if (EndoKazosushi.getGlobalItemsFilled() >= EndoKazosushi.getRequestedGlobalItemAmount()) {
                Msg.send(player, "&cThis order has already been filled!");
                player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 0);
                return;
            }
        }

        // Check if the player has the item
        if (!SlommyItemExchange.checkInventory(player, item, 1)) {
            Msg.send(player, "&cYou do not have the required item to fill this order!");
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 0);
            return;
        }

        int amount = SlommyItemExchange.checkInventory(player, item);
        PlayerTrackers trackers = PlayerTrackerUtil.getPlayerTracker(player);
        if (reason == EndoKazosushiRequestFillReason.FISH) {
            if (amount > EndoKazosushi.getRequestedFishAmount() - data.getEndoKazosushiFishSold()) {
                amount = (int) (EndoKazosushi.getRequestedFishAmount() - data.getEndoKazosushiFishSold());
            }
            data.setEndoKazosushiFishSold(data.getEndoKazosushiFishSold() + amount);
            trackers.endo_kazosushi_total_fish_sold += amount;
        } else if (reason == EndoKazosushiRequestFillReason.FILLET) {
            if (amount > EndoKazosushi.getRequestedFilletAmount() - data.getEndoKazosushiFilletsSold()) {
                amount = (int) (EndoKazosushi.getRequestedFilletAmount() - data.getEndoKazosushiFilletsSold());
            }
            data.setEndoKazosushiFilletsSold(data.getEndoKazosushiFilletsSold() + amount);
            trackers.endo_kazosushi_total_fillets_sold += amount;
        } else if (reason == EndoKazosushiRequestFillReason.SPECIAL_ITEM) {
            if (amount > EndoKazosushi.getRequestedGlobalItemAmount() - EndoKazosushi.getGlobalItemsFilled()) {
                amount = (int) (EndoKazosushi.getRequestedGlobalItemAmount() - EndoKazosushi.getGlobalItemsFilled());
            }
            EndoKazosushi.setGlobalItemsFilled(EndoKazosushi.getGlobalItemsFilled() + amount);
            trackers.endo_kazosushi_global_items_contributed += amount;
        }
        data.setLastEndoKazosushiSale(System.currentTimeMillis());
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
        SlommyItemExchange.takeItems(player, item, amount);
        trackers.player_bank_balance += (long) (moneyPerSale * amount);
        PlayerSkills skills = PlayerDataUtil.getPlayerSkills(player);
        DecimalFormat df = new DecimalFormat("#,###.##");
        skills.getSkill("Fishing").addXP(xpPerSale * amount);
        Msg.send(player, "&6+" + (df.format(moneyPerSale * amount)) + " \uD83C\uDD6D!");
        Msg.send(player, "&3+" + (df.format(xpPerSale * amount)) + " Fishing XP!");
        trackers.endo_kazosushi_coins_earned += (long) (moneyPerSale * amount);
        trackers.endo_kazosushi_experience_earned += xpPerSale * amount;
        player.closeInventory();
        openRequestsMenu(player);
    }
}
