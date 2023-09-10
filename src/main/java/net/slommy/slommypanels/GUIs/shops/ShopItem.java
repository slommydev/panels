package net.slommy.slommypanels.GUIs.shops;

import net.kyori.adventure.text.Component;
import net.slommy.essentials.SlommyItems.Items;
import net.slommy.essentials.SlommyItems.SlommyItem;
import net.slommy.essentials.util.GetInventoryValue;
import net.slommy.essentials.util.Msg;
import net.slommy.essentials.util.PlayerTrackerUtil;
import net.slommy.essentials.util.SlommyItemExchange;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ShopItem {
    private SlommyItem item;

    List<ShopExchange> exchanges;
    private String requirement;
    private String requirementMessage;
    private int amount = 1;
    private SlommyItem displayItem;

    public ShopItem(SlommyItem item, List<ShopExchange> exchanges) {
        this.item = item;
        this.exchanges = exchanges;
    }

    public SlommyItem getItem() {
        return item;
    }

    public void setItem(SlommyItem item) {
        this.item = item;
    }

    public String getRequirement() {
        return requirement;
    }

    public void setRequirement(String requirement) {
        this.requirement = requirement;
    }

    public String getRequirementMessage() {
        return requirementMessage;
    }

    public void setRequirementMessage(String requirementMessage) {
        this.requirementMessage = requirementMessage;
    }

    public void exchange(Player player) {
        int exchangesValid = 0;
        if (player.getInventory().firstEmpty() == -1) {
            Msg.send(player, "&cYour inventory is full!");
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 0);
            return;
        }
        for (ShopExchange exchange : exchanges) {
            if (exchange.type == ShopExchange.ExchangeType.BUY) {
                double PlayerInventoryValue = GetInventoryValue.getInventoryValue(player);
                long PlayerBankBalance = PlayerTrackerUtil.getPlayerTracker(player).player_bank_balance;
                double playerTotalValue = PlayerInventoryValue + PlayerBankBalance;
                if (playerTotalValue >= exchange.exchangeAmount) {
                    exchangesValid++;
                }
            }
            if (exchange.type == ShopExchange.ExchangeType.TRADE) {
                if (SlommyItemExchange.checkInventory(player, exchange.tradeItem, exchange.exchangeAmount)) {
                    exchangesValid++;
                }
            }
        }
        if (exchangesValid == exchanges.size()) {
            Msg.send(player, "&aYou have purchased " + item.getDisplayName() + "&a!");
            for (ShopExchange exchange1 : exchanges) {
                if (exchange1.type == ShopExchange.ExchangeType.BUY) {
                    if (GetInventoryValue.getInventoryValue(player) >= exchange1.exchangeAmount) {
                        GetInventoryValue.takeInventoryValue(player, exchange1.exchangeAmount);
                    } else {
                        PlayerTrackerUtil.getPlayerTracker(player).player_bank_balance -= exchange1.exchangeAmount - GetInventoryValue.getInventoryValue(player);
                        GetInventoryValue.takeInventoryValue(player, GetInventoryValue.getInventoryValue(player));
                    }
                }
                if (exchange1.type == ShopExchange.ExchangeType.TRADE) {
                    SlommyItemExchange.takeItems(player, exchange1.tradeItem, exchange1.exchangeAmount);
                }
            }
            ItemStack item = this.item.build(player);
            item.setAmount(amount);
            player.getInventory().addItem(item);
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
        } else {
            Msg.send(player, "&cYou do not have enough to purchase this!");
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 0);
        }
    }

    public ItemStack renderItem(Player player) {
        if (player.hasPermission(requirement)) {
            if (displayItem == null) {
                displayItem = item;
            }
            ItemStack item = this.item.build(player);
            item.setAmount(amount);
            ItemMeta meta = item.getItemMeta();
            List<Component> lore = meta.lore();
            lore.add(Msg.JSONcolor("&7&l&m---------------------"));
            lore.add(Msg.JSONcolor("&7Cost: "));
            for (ShopExchange exchange : exchanges) {
                lore.add(exchange.getDisplayLine());
            }
            lore.add(Msg.JSONcolor(""));
            lore.add(Msg.JSONcolor("&eClick to purchase!"));
            meta.lore(lore);
            item.setItemMeta(meta);
            if (displayItem == Items.items.get("gui/nothing")) {
                item = new ItemStack(Material.AIR);
            }
            return item;
        }
        if (!player.hasPermission(requirement)) {
            ItemStack item = Items.items.get("gui/shop_not_unlocked").build(player);
            ItemMeta meta = item.getItemMeta();
            List<Component> lore = new ArrayList<>();
            lore.add(Msg.JSONcolor("&7" + requirementMessage));
            meta.lore(lore);
            item.setItemMeta(meta);
            return item;
        }
        return displayItem.build(player);
    }

    public List<ShopExchange> getExchanges() {
        return exchanges;
    }

    public void setExchanges(List<ShopExchange> exchanges) {
        this.exchanges = exchanges;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public SlommyItem getDisplayItem() {
        return displayItem;
    }

    public void setDisplayItem(SlommyItem displayItem) {
        this.displayItem = displayItem;
    }
}
