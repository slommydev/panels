package net.slommy.slommypanels.GUIs.shops;

import net.kyori.adventure.text.Component;
import net.slommy.essentials.SlommyItems.Items;
import net.slommy.essentials.SlommyItems.SlommyItem;
import net.slommy.essentials.util.Msg;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ShopItem {
    private SlommyItem item;
    public enum ExchangeType {
        BUY,
        SELL,
        TRADE
    }
    private ExchangeType exchangeType;
    private int amount;
    private SlommyItem tradeItem;
    private String requirement;
    private String requirementMessage;

    public ShopItem(SlommyItem item, ExchangeType exchangeType, int amount) {
        this.item = item;
        this.exchangeType = exchangeType;
        this.amount = amount;
    }

    public ShopItem(SlommyItem item, ExchangeType exchangeType, int amount, SlommyItem tradeItem) {
        this.item = item;
        this.exchangeType = exchangeType;
        this.amount = amount;
        if (exchangeType.equals(ExchangeType.TRADE)) {
            this.tradeItem = tradeItem;
        }
    }

    public SlommyItem getItem() {
        return item;
    }

    public ExchangeType getExchangeType() {
        return exchangeType;
    }

    public void setItem(SlommyItem item) {
        this.item = item;
    }

    public void setExchangeType(ExchangeType exchangeType) {
        this.exchangeType = exchangeType;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public SlommyItem getTradeItem() {
        return tradeItem;
    }

    public void setTradeItem(SlommyItem tradeItem) {
        this.tradeItem = tradeItem;
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

    public void exchange() {
        if (exchangeType.equals(ExchangeType.BUY)) {

        }
    }

    public ItemStack renderItem(Player player) {
        if (player.hasPermission(requirement)) {
            return item.build(player);
        } else {
            ItemStack item = Items.items.get("gui/shop_not_unlocked").build(player);
            ItemMeta meta = item.getItemMeta();
            List<Component> lore = new ArrayList<>();
            lore.add(Msg.JSONcolor("&7" + requirementMessage));
            meta.lore(lore);
            item.setItemMeta(meta);
            return item;
        }
    }
}
